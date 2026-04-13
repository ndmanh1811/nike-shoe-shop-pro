package com.nikestore.shoeshop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(length = 255)
    private String note;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public OrderStatusHistory() {}

    public OrderStatusHistory(CustomerOrder order, OrderStatus status, String note) {
        this.order = order;
        this.status = status;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public CustomerOrder getOrder() {
        return order;
    }

    public void setOrder(CustomerOrder order) {
        this.order = order;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatusIcon() {
        return switch (status) {
            case PENDING -> "bi-hourglass";
            case PAID -> "bi-check-circle";
            case SHIPPED -> "bi-truck";
            case COMPLETED -> "bi-box-seam";
            case CANCELLED -> "bi-x-circle";
        };
    }

    public String getStatusColor() {
        return switch (status) {
            case PENDING -> "#fbbf24";
            case PAID -> "#60a5fa";
            case SHIPPED -> "#a78bfa";
            case COMPLETED -> "#4ade80";
            case CANCELLED -> "#f87171";
        };
    }
}
