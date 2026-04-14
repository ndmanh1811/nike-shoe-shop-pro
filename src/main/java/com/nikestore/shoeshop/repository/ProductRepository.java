package com.nikestore.shoeshop.repository;

import com.nikestore.shoeshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.brand LEFT JOIN FETCH p.category WHERE p.slug = :slug")
    Optional<Product> findBySlug(@Param("slug") String slug);

    List<Product> findTop8ByFeaturedTrueAndActiveTrueOrderByCreatedAtDesc();
    List<Product> findTop8ByBestsellerTrueAndActiveTrueOrderByCreatedAtDesc();
    List<Product> findAllByActiveTrueOrderByCreatedAtDesc();
    boolean existsBySlug(String slug);

    List<Product> findByNameContainingIgnoreCase(String keyword);
}
