package com.nikestore.shoeshop.repository;

import com.nikestore.shoeshop.entity.CustomerOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    @EntityGraph(attributePaths = "items")
    List<CustomerOrder> findTop10ByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "items")
    List<CustomerOrder> findByEmailOrderByCreatedAtDesc(String email);

    @EntityGraph(attributePaths = "items")
    Optional<CustomerOrder> findById(Long id);

    Optional<CustomerOrder> findByOrderCode(String orderCode);
}
