package com.nikestore.shoeshop.service;

import com.nikestore.shoeshop.dto.CartItemView;
import com.nikestore.shoeshop.dto.CheckoutForm;
import com.nikestore.shoeshop.entity.*;
import com.nikestore.shoeshop.repository.AppUserRepository;
import com.nikestore.shoeshop.repository.CustomerOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final AppUserRepository userRepository;

    public OrderService(CustomerOrderRepository orderRepository, AppUserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CustomerOrder createOrder(CheckoutForm form, Map<Long, CartItemView> cart, String email, CouponService.CouponQuote couponQuote) {
        CustomerOrder order = new CustomerOrder();
        order.setOrderCode("NK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomerName(form.getCustomerName());
        order.setPhone(form.getPhone());
        order.setAddress(form.getAddress());
        order.setEmail(email);
        order.setPaymentMethod("ONLINE".equalsIgnoreCase(form.getPaymentMethod()) ? PaymentMethod.ONLINE : PaymentMethod.COD);
        order.setStatus(OrderStatus.PENDING);

        double subtotal = 0d;
        for (CartItemView line : cart.values()) {
            CustomerOrderItem item = new CustomerOrderItem();
            item.setProductName(line.getName());
            item.setProductImage(line.getImageUrl());
            item.setUnitPrice(line.getPrice());
            item.setQuantity(line.getQuantity());
            item.setSize(line.getSize());
            item.setSubtotal(line.getSubtotal());
            order.addItem(item);
            subtotal += line.getSubtotal();
        }

        double discount = couponQuote != null ? couponQuote.getDiscountAmount() : 0d;
        order.setSubtotalAmount(subtotal);
        order.setDiscountAmount(discount);
        order.setCouponCode(couponQuote != null ? couponQuote.getCode() : null);
        order.setTotalAmount(Math.max(0d, subtotal - discount));
        return orderRepository.save(order);
    }

    public List<CustomerOrder> ordersForEmail(String email) {
        return orderRepository.findByEmailOrderByCreatedAtDesc(email);
    }

    public CustomerOrder requireOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public CustomerOrder requireOrderByCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public long countOrders() {
        return orderRepository.count();
    }

    public long countUsers() {
        return userRepository.count();
    }
}
