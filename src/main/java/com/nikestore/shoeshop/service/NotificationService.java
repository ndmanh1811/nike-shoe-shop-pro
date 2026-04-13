package com.nikestore.shoeshop.service;

import com.nikestore.shoeshop.entity.Notification;
import com.nikestore.shoeshop.entity.CustomerOrder;
import com.nikestore.shoeshop.entity.AppUser;
import com.nikestore.shoeshop.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // Tạo thông báo cho admin khi có order mới
    @Transactional
    public void notifyAdminNewOrder(CustomerOrder order) {
        Notification notification = new Notification(
            "Đơn hàng mới!",
            "Có đơn hàng mới " + order.getOrderCode() + " từ " + order.getCustomerName(),
            "ORDER_NEW",
            order.getId(),
            null, // null vì là thông báo cho admin
            true  // isAdmin = true
        );
        notificationRepository.save(notification);
    }

    // Tạo thông báo cho khách hàng khi order thay đổi status (cần user entity trong Order)
    @Transactional
    public void notifyCustomerOrderStatusChanged(CustomerOrder order, String status, AppUser user) {
        String statusMessage = getStatusMessage(status);
        Notification notification = new Notification(
            "Cập nhật đơn hàng",
            "Đơn hàng " + order.getOrderCode() + " của bạn đã " + statusMessage,
            "ORDER_STATUS",
            order.getId(),
            user,  // AppUser
            false // isAdmin = false
        );
        notificationRepository.save(notification);
    }

    // Lấy thông báo chưa đọc cho user
    public List<Notification> getUnreadForUser(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    // Lấy tất cả thông báo cho user
    public List<Notification> getAllForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Lấy thông báo chưa đọc cho admin
    public List<Notification> getUnreadForAdmin() {
        return notificationRepository.findByIsAdminTrueAndIsReadFalseOrderByCreatedAtDesc();
    }

    // Lấy tất cả thông báo cho admin
    public List<Notification> getAllForAdmin() {
        return notificationRepository.findByIsAdminTrueOrderByCreatedAtDesc();
    }

    // Đếm số thông báo chưa đọc của user
    public long countUnreadForUser(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // Đếm số thông báo chưa đọc của admin
    public long countUnreadForAdmin() {
        return notificationRepository.countByIsAdminTrueAndIsReadFalse();
    }

    // Đánh dấu đã đọc
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    // Đánh dấu tất cả đã đọc cho user
    @Transactional
    public void markAllAsReadForUser(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    // Đánh dấu tất cả thông báo admin đã đọc
    @Transactional
    public void markAllAdminAsRead() {
        notificationRepository.markAllAdminAsRead();
    }

    private String getStatusMessage(String status) {
        switch (status) {
            case "PENDING": return "đang chờ xử lý";
            case "PAID": return "đã thanh toán";
            case "PROCESSING": return "đang được xử lý";
            case "SHIPPED": return "đang giao hàng";
            case "DELIVERED": return "đã giao hàng";
            case "COMPLETED": return "đã hoàn thành";
            case "CANCELLED": return "đã bị hủy";
            default: return "được cập nhật";
        }
    }
}
