package com.nikestore.shoeshop.service;

import com.nikestore.shoeshop.dto.CartItemView;
import com.nikestore.shoeshop.dto.CheckoutForm;
import com.nikestore.shoeshop.entity.*;
import com.nikestore.shoeshop.repository.AppUserRepository;
import com.nikestore.shoeshop.repository.CustomerOrderRepository;
import com.nikestore.shoeshop.repository.OrderStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final AppUserRepository userRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;

    public OrderService(CustomerOrderRepository orderRepository, AppUserRepository userRepository, OrderStatusHistoryRepository statusHistoryRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    @Transactional
    public CustomerOrder createOrder(CheckoutForm form, Map<Long, CartItemView> cart, String email, CouponService.CouponQuote couponQuote) {
        CustomerOrder order = new CustomerOrder();
        order.setOrderCode("NK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomerName(form.getCustomerName());
        order.setPhone(form.getPhone());
        order.setAddress(form.getAddress());
        order.setEmail(email);
        // Parse payment method: COD or BANK
        PaymentMethod paymentMethod = "BANK".equalsIgnoreCase(form.getPaymentMethod()) ? PaymentMethod.BANK : PaymentMethod.COD;
        order.setPaymentMethod(paymentMethod);
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
        CustomerOrder savedOrder = orderRepository.save(order);

        // Create initial status history
        String note = paymentMethod == PaymentMethod.BANK ? "Order placed, awaiting bank transfer" : "Order placed, cash on delivery";
        createStatusHistory(savedOrder, OrderStatus.PENDING, note);

        return savedOrder;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus, String note) {
        CustomerOrder order = requireOrder(orderId);
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        orderRepository.save(order);

        String statusNote = note != null ? note : "Status changed from " + oldStatus + " to " + newStatus;
        createStatusHistory(order, newStatus, statusNote);
    }

    private void createStatusHistory(CustomerOrder order, OrderStatus status, String note) {
        OrderStatusHistory history = new OrderStatusHistory(order, status, note);
        statusHistoryRepository.save(history);
    }

    public List<OrderStatusHistory> getOrderStatusHistory(Long orderId) {
        return statusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
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
