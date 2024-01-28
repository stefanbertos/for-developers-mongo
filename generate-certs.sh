#!/bin/bash
set -e

mkdir -p tls

# Generate CA key and certificate
openssl genpkey -algorithm RSA -out tls/ca.key
openssl req -x509 -new -key tls/ca.key -out tls/ca.crt -subj "/CN=MongoDB-CA"

# Generate server key and certificate
openssl genpkey -algorithm RSA -out tls/server.key
openssl req -new -key tls/server.key -out tls/server.csr -subj "/CN=mongodb1"
openssl x509 -req -in tls/server.csr -CA tls/ca.crt -CAkey tls/ca.key -out tls/server.crt -days 365 -CAcreateserial
cat tls/server.crt tls/server.key > tls/server.pem

# Generate client key and certificate
openssl genpkey -algorithm RSA -out tls/client.key
openssl req -new -key tls/client.key -out tls/client.csr -subj "/CN=MongoDB-Client"
openssl x509 -req -in tls/client.csr -CA tls/ca.crt -CAkey tls/ca.key -out tls/client.crt -days 365 -CAcreateserial
cat tls/client.crt tls/client.key > tls/client.pem

# Generate JKS for client
keytool -importcert -file tls/ca.crt -keystore tls/client-truststore.jks -storepass password -alias mongoCA -noprompt
keytool -importkeystore -srckeystore tls/client.pem -srcstoretype PEM -destkeystore tls/client-keystore.jks -deststorepass password -noprompt
