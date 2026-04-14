package com.nikestore.shoeshop.repository;

import com.nikestore.shoeshop.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Lấy thông báo chưa đọc của user (cho khách hàng)
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    // Lấy tất cả thông báo của user
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Lấy thông báo chưa đọc cho admin
    List<Notification> findByIsAdminTrueAndIsReadFalseOrderByCreatedAtDesc();

    // Lấy tất cả thông báo cho admin
    List<Notification> findByIsAdminTrueOrderByCreatedAtDesc();

    // Đếm số thông báo chưa đọc của user
    long countByUserIdAndIsReadFalse(Long userId);

    // Đếm số thông báo chưa đọc cho admin
    long countByIsAdminTrueAndIsReadFalse();

    // Đánh dấu đã đọc
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);

    // Đánh dấu tất cả đã đọc của user
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadForUser(@Param("userId") Long userId);

    // Đánh dấu tất cả thông báo admin đã đọc
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.isAdmin = true AND n.isRead = false")
    void markAllAdminAsRead();
}
