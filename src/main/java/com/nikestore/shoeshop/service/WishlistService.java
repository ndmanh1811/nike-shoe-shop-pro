package com.nikestore.shoeshop.service;

import com.nikestore.shoeshop.entity.Product;
import com.nikestore.shoeshop.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class WishlistService {

    private static final String WISHLIST_KEY = "WISHLIST";

    private final ProductRepository productRepository;

    public WishlistService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @SuppressWarnings("unchecked")
    private Set<Long> wishlist(HttpSession session) {
        Object raw = session.getAttribute(WISHLIST_KEY);
        if (raw instanceof Set<?> set) {
            return (Set<Long>) set;
        }
        Set<Long> created = new LinkedHashSet<>();
        session.setAttribute(WISHLIST_KEY, created);
        return created;
    }

    public void toggle(HttpSession session, Long productId) {
        Set<Long> wishlist = wishlist(session);
        if (!wishlist.add(productId)) {
            wishlist.remove(productId);
        }
    }

    public void add(HttpSession session, Long productId) {
        wishlist(session).add(productId);
    }

    public void remove(HttpSession session, Long productId) {
        wishlist(session).remove(productId);
    }

    public void clear(HttpSession session) {
        wishlist(session).clear();
    }

    public boolean contains(HttpSession session, Long productId) {
        return wishlist(session).contains(productId);
    }

    public int count(HttpSession session) {
        return wishlist(session).size();
    }

    public List<Product> products(HttpSession session) {
        return wishlist(session).stream()
                .map(productRepository::findById)
                .flatMap(Optional::stream)
                .filter(Product::isActive)
                .toList();
    }
}
