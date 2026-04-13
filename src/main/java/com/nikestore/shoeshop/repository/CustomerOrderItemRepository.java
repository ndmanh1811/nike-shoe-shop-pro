package com.nikestore.shoeshop.repository;

import com.nikestore.shoeshop.entity.CustomerOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderItemRepository extends JpaRepository<CustomerOrderItem, Long> {
}
