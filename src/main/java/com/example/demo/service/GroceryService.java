package com.example.demo.service;

import com.example.demo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class GroceryService {

    @Autowired
    private ItemRepository itemRepository;


    @Autowired
    private MongoTransactionManager transactionManager;

    private final ScheduledExecutorService timeoutExecutor = Executors.newScheduledThreadPool(1);

    public void yourTransactionalMethodWithTimeout() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);

        CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(30000); // Timeout value in milliseconds (e.g., 60 seconds)
                if (!status.isCompleted()) {
                    transactionManager.rollback(status);
                    System.out.println("Transaction rolled back due to timeout");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        try {
            itemRepository.findAll();
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Your transactional logic here
            // If the transaction completes before the timeout, cancel the timeout task
            // If an exception occurs, ensure the timeout task is cancelled as well
        } finally {
            task.cancel(true); // Cancel the timeout task
            transactionManager.commit(status);
        }
    }
}
