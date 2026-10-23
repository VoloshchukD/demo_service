package com.dev.demo_service.health;

import com.dev.demo_service.repository.ProductRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class InventoryHealthIndicator implements HealthIndicator {

    private final ProductRepository productRepository;

    public InventoryHealthIndicator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Health health() {
        long outOfStockProducts = productRepository.countByQuantityLessThanEqual(0);
        long invalidQuantityProducts = productRepository.countByQuantityLessThan(0);

        Health.Builder health = invalidQuantityProducts > 0 ? Health.down() : Health.up();
        return health.withDetail("outOfStockProducts", outOfStockProducts)
                .withDetail("invalidQuantityProducts", invalidQuantityProducts)
                .build();
    }

}