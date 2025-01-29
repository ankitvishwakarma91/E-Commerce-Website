package com.softworkshub.ecom.services;

import com.softworkshub.ecom.model.Cart;

import java.util.List;

public interface CartService {

    public Cart saveCart(Integer productId, Integer userId);

    public List<Cart> getAllCartByUserId(Integer userId);

    public Integer getCartCount(Integer userId);

    void updateQuantity(String sy, Integer cid);
}
