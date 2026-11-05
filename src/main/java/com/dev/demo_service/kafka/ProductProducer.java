package com.dev.demo_service.kafka;

import com.dev.demo_service.avro.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductProducer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ProductProducer.class);

    private final KafkaTemplate<String, Product> kafkaTemplate;

    public ProductProducer(KafkaTemplate<String, Product> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        logger.info("ProductProducer initialized with KafkaTemplate: {}", kafkaTemplate);
    }

    @Override
    public void run(String... args) {
        logger.info("Producing product messages to Kafka...");
        Product product = Product.newBuilder()
                .setId(1)
                .setName("Test")
                .build();

        kafkaTemplate.send("products", String.valueOf(product.getId()), product)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        logger.error("Failed to send product: " + product, error);
                    } else {
                        logger.info("Successfully sent product: " + product + " to partition: "
                                + result.getRecordMetadata().partition() + ", offset: " + result.getRecordMetadata().offset());
                    }
                });
    }

}