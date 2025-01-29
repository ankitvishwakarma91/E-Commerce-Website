package com.softworkshub.ecom.services.impl;

import com.softworkshub.ecom.model.Cart;
import com.softworkshub.ecom.model.Product;
import com.softworkshub.ecom.model.UserDetails;
import com.softworkshub.ecom.repository.CartRepository;
import com.softworkshub.ecom.repository.ProductRepository;
import com.softworkshub.ecom.repository.UserRepository;
import com.softworkshub.ecom.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {

        UserDetails userById = userRepository.findById(userId).get();
        Product productRepositoryById = productRepository.findById(productId).get();

        Cart byProductIdAndUserId = cartRepository.findByProductIdAndUserId(productId, userId);

        Cart cart = null;

        if (ObjectUtils.isEmpty(byProductIdAndUserId)) {
            cart = new Cart();
            cart.setProduct(productRepositoryById);
            cart.setUser(userById);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * productRepositoryById.getDiscountPrice());

        }else {
            cart = byProductIdAndUserId;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
        }
        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> getAllCartByUserId(Integer userId) {
        List<Cart> byUserId = cartRepository.findByUserId(userId);
        double totalOrderPrice = 0.0;
        List<Cart> carts = new ArrayList<>();
        for(Cart cart : byUserId) {
             double totalPrice = (cart.getProduct().getDiscountPrice() * cart.getQuantity());
            cart.setTotalPrice(totalPrice);
            totalOrderPrice = totalOrderPrice + totalPrice;
            cart.setTotalOrderPrice(totalOrderPrice);
            carts.add(cart);
        }
        return carts;
    }

    @Override
    public Integer getCartCount(Integer userId) {
        Integer count = cartRepository.countByUserId(userId);
        return count;
    }

    @Override
    public void updateQuantity(String sy, Integer cid) {
        Cart cart = cartRepository.findById(cid).get();
        int updateQuantity;

        if (sy.equalsIgnoreCase("de")) {
            updateQuantity = cart.getQuantity() -1 ;
            if (updateQuantity < 0) {
                cartRepository.delete(cart);
            }else {
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }
        }else {
            updateQuantity = cart.getQuantity()+1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);
        }
    }
}
