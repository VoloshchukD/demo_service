package com.dev.demo_service.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ProductMetrics {

    private final Counter createdProducts;
    private final Counter updatedProducts;
    private final Counter deletedProducts;

    public ProductMetrics(MeterRegistry meterRegistry) {
        this.createdProducts = Counter.builder("products.created")
                .description("Number of products successfully created")
                .register(meterRegistry);
        this.updatedProducts = Counter.builder("products.updated")
                .description("Number of products successfully updated")
                .register(meterRegistry);
        this.deletedProducts = Counter.builder("products.deleted")
                .description("Number of products successfully deleted")
                .register(meterRegistry);
    }

    public void countCreated() {
        createdProducts.increment();
    }

    public void countUpdated() {
        updatedProducts.increment();
    }

    public void countDeleted() {
        deletedProducts.increment();
    }

}