package com.nikestore.shoeshop.service;

import com.nikestore.shoeshop.dto.CartItemView;
import com.nikestore.shoeshop.entity.Product;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CartService {

    private static final String CART_KEY = "CART";

    @SuppressWarnings("unchecked")
    private Map<Long, CartItemView> cart(HttpSession session) {
        Object obj = session.getAttribute(CART_KEY);
        if (obj instanceof Map<?, ?> map) {
            return (Map<Long, CartItemView>) map;
        }
        Map<Long, CartItemView> newCart = new LinkedHashMap<>();
        session.setAttribute(CART_KEY, newCart);
        return newCart;
    }

    public void add(HttpSession session, Product product, int quantity, String size) {
        Map<Long, CartItemView> cart = cart(session);
        CartItemView item = cart.get(product.getId());
        if (item == null) {
            item = new CartItemView(
                    product.getId(),
                    product.getName(),
                    product.getSlug(),
                    product.getImageUrl(),
                    product.effectivePrice(),
                    Math.max(quantity, 1),
                    size
            );
            cart.put(product.getId(), item);
        } else {
            item.setQuantity(item.getQuantity() + Math.max(quantity, 1));
            item.setSize(size);
        }
    }

    public void update(HttpSession session, Long productId, int quantity, String size) {
        Map<Long, CartItemView> cart = cart(session);
        CartItemView item = cart.get(productId);
        if (item != null) {
            item.setQuantity(Math.max(quantity, 1));
            item.setSize(size);
        }
    }

    public void remove(HttpSession session, Long productId) {
        cart(session).remove(productId);
    }

    public void clear(HttpSession session) {
        cart(session).clear();
    }

    public Map<Long, CartItemView> items(HttpSession session) {
        return cart(session);
    }

    public double total(HttpSession session) {
        return cart(session).values().stream().mapToDouble(CartItemView::getSubtotal).sum();
    }

    public int count(HttpSession session) {
        return cart(session).values().stream().mapToInt(CartItemView::getQuantity).sum();
    }
}
