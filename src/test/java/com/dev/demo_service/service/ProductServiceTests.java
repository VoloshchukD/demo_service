package com.dev.demo_service.service;

import com.dev.demo_service.entity.Product;
import com.dev.demo_service.metrics.ProductMetrics;
import com.dev.demo_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMetrics productMetrics;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("Keyboard", "Mechanical keyboard", new BigDecimal("89.99"), 5);
    }

    @Test
    void createsProductAndIncrementsMetric() {
        Product savedProduct = new Product("Keyboard", "Mechanical keyboard", new BigDecimal("89.99"), 5);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.create(product);

        assertSame(savedProduct, result);
        verify(productRepository).save(any(Product.class));
        verify(productMetrics).countCreated();
    }

    @Test
    void findsAllProducts() {
        List<Product> products = List.of(product);
        when(productRepository.findAll()).thenReturn(products);

        assertSame(products, productService.findAll());

        verify(productRepository).findAll();
        verifyNoInteractions(productMetrics);
    }

    @Test
    void findsProductById() {
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));

        assertSame(product, productService.findById(7L));
    }

    @Test
    void throwsNotFoundWhenProductDoesNotExist() {
        when(productRepository.findById(7L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.findById(7L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product not found", exception.getReason());
    }

    @Test
    void updatesProductAndIncrementsMetric() {
        Product updates = new Product("Updated keyboard", "Updated description",
                new BigDecimal("99.99"), 4);
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.update(7L, updates);

        assertSame(product, result);
        assertEquals("Updated keyboard", product.getName());
        assertEquals("Updated description", product.getDescription());
        assertEquals(new BigDecimal("99.99"), product.getPrice());
        assertEquals(4, product.getQuantity());
        verify(productMetrics).countUpdated();
    }

    @Test
    void doesNotSaveOrIncrementMetricWhenUpdatingMissingProduct() {
        when(productRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.update(7L, product));

        verify(productRepository, never()).save(any(Product.class));
        verifyNoInteractions(productMetrics);
    }

    @Test
    void deletesProductAndIncrementsMetric() {
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));

        productService.delete(7L);

        verify(productRepository).delete(product);
        verify(productMetrics).countDeleted();
    }

    @Test
    void doesNotDeleteOrIncrementMetricWhenProductIsMissing() {
        when(productRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.delete(7L));

        verify(productRepository, never()).delete(any(Product.class));
        verifyNoInteractions(productMetrics);
    }

}