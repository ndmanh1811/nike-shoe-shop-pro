package com.nikestore.shoeshop.controller;

import com.nikestore.shoeshop.entity.CustomerOrder;
import com.nikestore.shoeshop.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserOrderController {

    private final OrderService orderService;

    public UserOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public String orders(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/signin";
        }
        model.addAttribute("orders", orderService.ordersForEmail(authentication.getName()));
        return "shop/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/signin";
        }
        CustomerOrder order = orderService.requireOrder(id);
        if (!order.getEmail().equals(authentication.getName())) {
            return "redirect:/orders";
        }
        model.addAttribute("order", order);
        model.addAttribute("statusHistory", orderService.getOrderStatusHistory(id));
        return "shop/order-detail";
    }
}
