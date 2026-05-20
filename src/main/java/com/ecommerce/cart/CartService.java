package com.ecommerce.cart;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CartService {

    private final Map<String, List<CartItem>> cartCache = new HashMap<>();

    public String addToCart(String userId, CartItem item) {

        List<CartItem> cart = cartCache.get(userId);

        if (cart == null) {
            cart = new ArrayList<>();
        }

        cart.add(item);

        cartCache.put(userId, cart);

        return "Item added to cart";
    }

    public List<CartItem> getCart(String userId) {

        List<CartItem> cart = cartCache.get(userId);

        if (cart == null) {
            return new ArrayList<>();
        }

        return cart;
    }

    public String clearCart(String userId) {

        cartCache.remove(userId);

        return "Cart cleared";
    }
}
