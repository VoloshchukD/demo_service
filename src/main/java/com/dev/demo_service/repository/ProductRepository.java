package com.dev.demo_service.repository;

import com.dev.demo_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    long countByQuantityLessThan(int quantity);

    long countByQuantityLessThanEqual(int quantity);

}