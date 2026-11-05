package com.dev.demo_service.kafka;

import com.dev.demo_service.avro.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductConsumer.class);

    @KafkaListener(topics = "products")
    public void consume(Product product) {
        logger.info("Received product: {}", product);
        logger.info("ID: {}", product.getId());
        logger.info("Name: {}", product.getName());
    }

}