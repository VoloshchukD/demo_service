package com.dev.demo_service.service;

import com.dev.demo_service.entity.Product;
import com.dev.demo_service.metrics.ProductMetrics;
import com.dev.demo_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductMetrics productMetrics;

    public ProductService(ProductRepository productRepository, ProductMetrics productMetrics) {
        this.productRepository = productRepository;
        this.productMetrics = productMetrics;
    }

    public Product create(Product product) {
        log.info("Creating product name={}, price={}, quantity={}", product.getName(), product.getPrice(), product.getQuantity());

        Product newProduct = new Product(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity());
        Product savedProduct = productRepository.save(newProduct);
        productMetrics.countCreated();

        log.info("Created product id={}, name={}", savedProduct.getId(), savedProduct.getName());
        return savedProduct;
    }

    public List<Product> findAll() {
        log.debug("Fetching all products");
        List<Product> products = productRepository.findAll();
        log.debug("Fetched {} products", products.size());
        return products;
    }

    public Product findById(Long id) {
        log.debug("Fetching product by id={}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found for id={}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
                });
    }

    public Product update(Long id, Product product) {
        log.info("Updating product id={}", id);

        Product existingProduct = findById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        Product savedProduct = productRepository.save(existingProduct);
        productMetrics.countUpdated();

        log.info("Updated product id={}, name={}", savedProduct.getId(), savedProduct.getName());
        return savedProduct;
    }

    public void delete(Long id) {
        log.info("Deleting product id={}", id);

        Product existingProduct = findById(id);
        productRepository.delete(existingProduct);
        productMetrics.countDeleted();

        log.info("Deleted product id={}", id);
    }

}
