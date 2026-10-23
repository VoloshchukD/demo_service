package com.dev.demo_service.health;

import com.dev.demo_service.repository.ProductRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ProductCatalogHealthIndicator implements HealthIndicator {

    private final ProductRepository productRepository;

    public ProductCatalogHealthIndicator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Health health() {
        return Health.up()
                .withDetail("productCount", productRepository.count())
                .build();
    }

}