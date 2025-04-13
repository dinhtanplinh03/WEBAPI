package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public List<Cart> getCartByUser(@PathVariable Long userId) {
        return cartService.getCartByUser(userId);
    }

    @PostMapping
    public Cart addToCart(@RequestBody Cart cart) {
        return cartService.addToCart(cart);
    }

    @DeleteMapping("/{id}")
    public void removeCartItem(@PathVariable Long id) {
        cartService.removeCartItem(id);
    }
}
