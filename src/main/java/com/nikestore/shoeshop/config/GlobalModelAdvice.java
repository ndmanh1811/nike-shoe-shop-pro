package com.nikestore.shoeshop.config;

import com.nikestore.shoeshop.entity.AppUser;
import com.nikestore.shoeshop.repository.AppUserRepository;
import com.nikestore.shoeshop.repository.BrandRepository;
import com.nikestore.shoeshop.repository.CategoryRepository;
import com.nikestore.shoeshop.service.CartService;
import com.nikestore.shoeshop.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CartService cartService;
    private final WishlistService wishlistService;
    private final AppUserRepository userRepository;

    public GlobalModelAdvice(CategoryRepository categoryRepository,
                             BrandRepository brandRepository,
                             CartService cartService,
                             WishlistService wishlistService,
                             AppUserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.cartService = cartService;
        this.wishlistService = wishlistService;
        this.userRepository = userRepository;
    }

    @ModelAttribute("navCategories")
    public Object navCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAscNameAsc();
    }

    @ModelAttribute("navBrands")
    public Object navBrands() {
        return brandRepository.findAllByOrderByNameAsc();
    }

    @ModelAttribute("cartCount")
    public int cartCount(HttpSession session) {
        return cartService.count(session);
    }

    @ModelAttribute("wishlistCount")
    public int wishlistCount(HttpSession session) {
        return wishlistService.count(session);
    }

    @ModelAttribute("loggedIn")
    public boolean loggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    @ModelAttribute("currentUserName")
    public String currentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "";
        }
        String email = authentication.getName();
        AppUser user = userRepository.findByEmail(email).orElse(null);
        return user != null ? user.getFullName() : email;
    }

    @ModelAttribute("currentUserRole")
    public String currentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "";
        }
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("");
    }
}
