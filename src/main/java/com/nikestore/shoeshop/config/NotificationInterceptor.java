package com.nikestore.shoeshop.config;

import com.nikestore.shoeshop.entity.AppUser;
import com.nikestore.shoeshop.entity.Notification;
import com.nikestore.shoeshop.repository.AppUserRepository;
import com.nikestore.shoeshop.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Component
public class NotificationInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(NotificationInterceptor.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AppUserRepository userRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null || modelAndView.getModelMap() == null) {
            return;
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return;
            }

            Object principal = authentication.getPrincipal();
            if (principal == null || "anonymousUser".equals(principal)) {
                return;
            }

            String email = authentication.getName();
            if (email == null || email.isEmpty()) {
                return;
            }

            // Kiểm tra role admin
            boolean isAdmin = authentication.getAuthorities() != null && 
                    authentication.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

            Optional<AppUser> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                AppUser user = userOpt.get();
                
                // Add user name for display
                modelAndView.addObject("currentUserName", user.getFullName() != null ? user.getFullName() : user.getEmail());
                
                if (isAdmin) {
                    // Admin notifications
                    long adminUnreadCount = notificationService.countUnreadForAdmin();
                    modelAndView.addObject("adminNotificationCount", adminUnreadCount);
                    
                    List<Notification> adminNotifications = notificationService.getUnreadForAdmin();
                    if (adminNotifications != null && adminNotifications.size() > 5) {
                        adminNotifications = adminNotifications.subList(0, 5);
                    }
                    modelAndView.addObject("adminNotifications", adminNotifications);
                    modelAndView.addObject("currentUserRole", "ROLE_ADMIN");
                } else {
                    // User notifications
                    long unreadCount = notificationService.countUnreadForUser(user.getId());
                    modelAndView.addObject("notificationCount", unreadCount);
                    
                    List<Notification> notifications = notificationService.getUnreadForUser(user.getId());
                    if (notifications != null && notifications.size() > 5) {
                        notifications = notifications.subList(0, 5);
                    }
                    modelAndView.addObject("notifications", notifications);
                    modelAndView.addObject("currentUserRole", "ROLE_USER");
                }
            }
        } catch (Exception e) {
            // Log error but don't break the page
            logger.error("NotificationInterceptor error: {}", e.getMessage(), e);
        }
    }
}
