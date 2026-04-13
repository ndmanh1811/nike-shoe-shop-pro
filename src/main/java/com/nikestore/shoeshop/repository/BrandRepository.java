package com.nikestore.shoeshop.repository;

import com.nikestore.shoeshop.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByName(String name);
    List<Brand> findAllByOrderByNameAsc();
    boolean existsByName(String name);
}
