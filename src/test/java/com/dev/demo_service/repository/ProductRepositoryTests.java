package com.dev.demo_service.repository;

import com.dev.demo_service.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void clearProducts() {
        productRepository.deleteAll();
    }

    @Test
    void savesAndFindsProductById() {
        Product product = productRepository.saveAndFlush(
                new Product("Keyboard", "Mechanical keyboard", new BigDecimal("89.99"), 5));

        Optional<Product> foundProduct = productRepository.findById(product.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals("Keyboard", foundProduct.get().getName());
        assertEquals("Mechanical keyboard", foundProduct.get().getDescription());
        assertEquals(new BigDecimal("89.99"), foundProduct.get().getPrice());
        assertEquals(5, foundProduct.get().getQuantity());
    }

    @Test
    void findsAllProductsAndDeletesById() {
        Product firstProduct = productRepository.save(
                new Product("Mouse", "Wireless mouse", new BigDecimal("29.99"), 12));
        Product secondProduct = productRepository.save(
                new Product("Monitor", "27-inch monitor", new BigDecimal("249.99"), 3));
        productRepository.flush();

        List<Product> products = productRepository.findAll();

        assertEquals(2, products.size());
        productRepository.deleteById(firstProduct.getId());
        productRepository.flush();

        assertEquals(List.of(secondProduct.getId()),
                productRepository.findAll().stream().map(Product::getId).toList());
        assertFalse(productRepository.findById(firstProduct.getId()).isPresent());
    }

    @Test
    void countsProductsBelowAndAtOrBelowQuantity() {
        productRepository.save(new Product("Low stock", "Few remaining", new BigDecimal("10.00"), 2));
        productRepository.save(new Product("Threshold", "At threshold", new BigDecimal("15.00"), 5));
        productRepository.save(new Product("In stock", "Available", new BigDecimal("20.00"), 8));
        productRepository.flush();

        assertEquals(1, productRepository.countByQuantityLessThan(5));
        assertEquals(2, productRepository.countByQuantityLessThanEqual(5));
    }

}