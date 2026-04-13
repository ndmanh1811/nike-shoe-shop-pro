package com.nikestore.shoeshop.controller.admin;

import com.nikestore.shoeshop.entity.CustomerOrder;
import com.nikestore.shoeshop.entity.OrderStatus;
import com.nikestore.shoeshop.repository.CustomerOrderRepository;
import com.nikestore.shoeshop.repository.ProductRepository;
import com.nikestore.shoeshop.service.CatalogService;
import com.nikestore.shoeshop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    private final CatalogService catalogService;
    private final CustomerOrderRepository orderRepository;
    private final OrderService orderService;
    private final ProductRepository productRepository;

    public AdminController(CatalogService catalogService, CustomerOrderRepository orderRepository, OrderService orderService, ProductRepository productRepository) {
        this.catalogService = catalogService;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.productRepository = productRepository;
    }

    @GetMapping("/admin")
    public String adminRoot() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("categoryCount", catalogService.categories().size());
        model.addAttribute("brandCount", catalogService.brands().size());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("userCount", orderService.countUsers());
        model.addAttribute("revenue", orderRepository.findAll().stream().mapToDouble(o -> o.getTotalAmount() == null ? 0d : o.getTotalAmount()).sum());
        model.addAttribute("latestOrders", orderRepository.findTop10ByOrderByCreatedAtDesc());
        model.addAttribute("pendingOrders", orderRepository.findAll().stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count());
        return "admin/dashboard";
    }

    @GetMapping("/admin/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findTop10ByOrderByCreatedAtDesc());
        return "admin/orders";
    }

    @GetMapping("/admin/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        CustomerOrder order = orderRepository.findById(id).orElseThrow();
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order-detail";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        CustomerOrder order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
        return "redirect:/admin/orders/" + id;
    }
}
