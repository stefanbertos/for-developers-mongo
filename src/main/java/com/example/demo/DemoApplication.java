package com.example.demo;

import com.example.demo.service.GroceryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class DemoApplication {

	@Autowired
	GroceryService groceryService;


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@EventListener
	@Async
	public void onApplicationEvent(ApplicationStartedEvent event) {
		System.out.println("1");
		groceryService.yourTransactionalMethodWithTimeout();
		System.out.println("2");
	}
}