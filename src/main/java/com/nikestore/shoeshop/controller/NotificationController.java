package com.nikestore.shoeshop.controller;

import com.nikestore.shoeshop.entity.Notification;
import com.nikestore.shoeshop.entity.AppUser;
import com.nikestore.shoeshop.repository.AppUserRepository;
import com.nikestore.shoeshop.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AppUserRepository userRepository;

    private Optional<AppUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String email = authentication.getName();
        if (email == null || email.isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email);
    }

    // Lấy tất cả thông báo cho user đăng nhập
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
        Optional<AppUser> user = getCurrentUser();
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Notification> notifications = notificationService.getAllForUser(user.get().getId());
        return ResponseEntity.ok(notifications);
    }

    // Lấy số lượng thông báo chưa đọc
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        Map<String, Long> response = new HashMap<>();
        Optional<AppUser> user = getCurrentUser();
        if (user.isEmpty()) {
            response.put("count", 0L);
            return ResponseEntity.ok(response);
        }
        long count = notificationService.countUnreadForUser(user.get().getId());
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Đánh dấu thông báo đã đọc
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // Đánh dấu tất cả đã đọc
    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead() {
        Optional<AppUser> user = getCurrentUser();
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        notificationService.markAllAsReadForUser(user.get().getId());
        return ResponseEntity.ok().build();
    }

    // ================= ADMIN APIs =================

    // Lấy tất cả thông báo cho admin
    @GetMapping("/admin")
    public ResponseEntity<List<Notification>> getAdminNotifications() {
        List<Notification> notifications = notificationService.getAllForAdmin();
        return ResponseEntity.ok(notifications);
    }

    // Lấy số lượng thông báo chưa đọc cho admin
    @GetMapping("/admin/count")
    public ResponseEntity<Map<String, Long>> getAdminUnreadCount() {
        Map<String, Long> response = new HashMap<>();
        long count = notificationService.countUnreadForAdmin();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Đánh dấu thông báo admin đã đọc
    @PostMapping("/admin/{id}/read")
    public ResponseEntity<Void> markAdminAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // Đánh dấu tất cả thông báo admin đã đọc
    @PostMapping("/admin/mark-all-read")
    public ResponseEntity<Void> markAllAdminAsRead() {
        notificationService.markAllAdminAsRead();
        return ResponseEntity.ok().build();
    }
}
