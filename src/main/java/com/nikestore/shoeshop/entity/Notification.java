package com.nikestore.shoeshop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user; // null nếu là thông báo cho admin (tất cả admin)

    private String title;

    private String message;

    private String type; // ORDER_NEW, ORDER_STATUS, SYSTEM

    private Long relatedId; // orderId nếu là thông báo về order

    private boolean isRead = false;

    private boolean isAdmin = false; // true nếu là thông báo cho admin

    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Notification() {}

    public Notification(String title, String message, String type, Long relatedId, AppUser user, boolean isAdmin) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedId = relatedId;
        this.user = user;
        this.isAdmin = isAdmin;
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
