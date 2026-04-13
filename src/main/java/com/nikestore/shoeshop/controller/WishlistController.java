package com.nikestore.shoeshop.controller;

import com.nikestore.shoeshop.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WishlistController {

    private final WishlistService wishlistService;
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/wishlist")
    public String wishlist(HttpSession session, Model model) {
        model.addAttribute("products", wishlistService.products(session));
        return "shop/wishlist";
    }

    @PostMapping("/wishlist/toggle")
    public String toggle(@RequestParam Long productId, HttpSession session, @RequestHeader(value = "Referer", required = false) String referer) {
        wishlistService.toggle(session, productId);
        return "redirect:" + (referer != null && !referer.isBlank() ? referer : "/wishlist");
    }

    @PostMapping("/wishlist/{productId}/remove")
    public String remove(@PathVariable Long productId, HttpSession session) {
        wishlistService.remove(session, productId);
        return "redirect:/wishlist";
    }

    @PostMapping("/wishlist/clear")
    public String clear(HttpSession session) {
        wishlistService.clear(session);
        return "redirect:/wishlist";
    }
}
