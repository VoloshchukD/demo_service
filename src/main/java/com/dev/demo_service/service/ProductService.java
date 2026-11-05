package com.dev.demo_service.service;

import com.dev.demo_service.entity.Product;
import com.dev.demo_service.metrics.ProductMetrics;
import com.dev.demo_service.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMetrics productMetrics;

    public ProductService(ProductRepository productRepository, ProductMetrics productMetrics) {
        this.productRepository = productRepository;
        this.productMetrics = productMetrics;
    }

    public Product create(Product product) {
        Product newProduct = new Product(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity());
        Product savedProduct = productRepository.save(newProduct);
        productMetrics.countCreated();
        return savedProduct;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product update(Long id, Product product) {
        Product existingProduct = findById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        Product savedProduct = productRepository.save(existingProduct);
        productMetrics.countUpdated();
        return savedProduct;
    }

    public void delete(Long id) {
        Product existingProduct = findById(id);
        productRepository.delete(existingProduct);
        productMetrics.countDeleted();
    }

}