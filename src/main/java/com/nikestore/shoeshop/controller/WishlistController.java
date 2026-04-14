package com.nikestore.shoeshop.controller;

import com.nikestore.shoeshop.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String toggle(@RequestParam Long productId, HttpSession session, @RequestHeader(value = "Referer", required = false) String referer, RedirectAttributes redirectAttributes) {
        boolean wasAdded = wishlistService.contains(session, productId);
        wishlistService.toggle(session, productId);
        if (wasAdded) {
            redirectAttributes.addFlashAttribute("info", "Removed from wishlist.");
        } else {
            redirectAttributes.addFlashAttribute("success", "Added to wishlist!");
        }
        return "redirect:" + (referer != null && !referer.isBlank() ? referer : "/wishlist");
    }

    @PostMapping("/wishlist/{productId}/remove")
    public String remove(@PathVariable Long productId, HttpSession session, RedirectAttributes redirectAttributes) {
        wishlistService.remove(session, productId);
        redirectAttributes.addFlashAttribute("info", "Removed from wishlist.");
        return "redirect:/wishlist";
    }

    @PostMapping("/wishlist/clear")
    public String clear(HttpSession session, RedirectAttributes redirectAttributes) {
        wishlistService.clear(session);
        redirectAttributes.addFlashAttribute("success", "Wishlist cleared!");
        return "redirect:/wishlist";
    }
}
