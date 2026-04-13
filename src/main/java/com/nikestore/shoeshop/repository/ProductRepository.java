package com.nikestore.shoeshop.repository;

import com.nikestore.shoeshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    List<Product> findTop8ByFeaturedTrueAndActiveTrueOrderByCreatedAtDesc();
    List<Product> findTop8ByBestsellerTrueAndActiveTrueOrderByCreatedAtDesc();
    List<Product> findAllByActiveTrueOrderByCreatedAtDesc();
    boolean existsBySlug(String slug);
}
