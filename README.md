# Demo Service

Spring Boot product catalog service with REST APIs, gRPC, PostgreSQL, and Kafka integration.

## Overview

This project exposes product data through a Spring Boot application and supports local development with PostgreSQL, gRPC, and Kafka/Avro messaging.

## Prerequisites

- Java 17+
- Maven or the included Maven wrapper (`./mvnw`)
- PostgreSQL instance
- Docker (for Keycloak and local infrastructure)
- Kafka broker and Schema Registry (for Avro flows)

## Run locally

1. Create a PostgreSQL database named `demo_service`.
2. Copy `.env.example` to `.env` and set the database credentials.
3. Start Keycloak:
   ```bash
   docker compose up -d keycloak
   ```
4. Start the application with the local profile:
   ```bash
   ./mvnw spring-boot:run -Dspring.profiles.active=local
   ```

## gRPC

The gRPC server is enabled by Spring Boot. Its port is configured in `src/main/resources/application.properties` and defaults to `9091`.

The service contract is defined in:
- `src/main/proto/product.proto`
- `python-grpc-client/proto/product.proto`

Both files should remain in sync. The Java server and Python client use the same service name and message definitions.

### Generate Java classes

Run:
```bash
./mvnw clean install
```

This generates protobuf and Avro classes under `target/generated-sources`. Do not edit generated files; update the source schema files instead.

### Generate Python gRPC stubs

From the `python-grpc-client` directory, run:
```bash
cd python-grpc-client
python -m grpc_tools.protoc -I proto --python_out=. --grpc_python_out=. proto/product.proto
python client.py
```

Local profile gRPC calls do not require authentication. Other profiles require JWT authentication.

## Kafka and Avro

Kafka configuration is not included in the repository. Before starting the app, make sure the following are available:

- Kafka broker on port `9092`
- Topic named `products`
- Schema Registry on port `8085`

The producer and consumer use the `products` topic, and messages follow the Avro schema in `src/main/avro/Product.avsc`.