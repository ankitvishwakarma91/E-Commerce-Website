package com.softworkshub.ecom.services;

import com.softworkshub.ecom.model.OrderRequest;
import com.softworkshub.ecom.model.ProductOrder;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import  java.util.List;

public interface OrderService {

    public void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException;

    public List<ProductOrder> getOrderByUser(Integer userId);

    public ProductOrder updateOrder(Integer userId, String status);

    public List<ProductOrder> getAllOrder();


}
