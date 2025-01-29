package com.softworkshub.ecom.services.impl;

import com.softworkshub.ecom.model.Cart;
import com.softworkshub.ecom.model.OrderAddress;
import com.softworkshub.ecom.model.OrderRequest;
import com.softworkshub.ecom.model.ProductOrder;
import com.softworkshub.ecom.repository.CartRepository;
import com.softworkshub.ecom.repository.ProductOrderRepository;
import com.softworkshub.ecom.services.OrderService;
import com.softworkshub.ecom.utils.MailUtils;
import com.softworkshub.ecom.utils.OrderStatus;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MailUtils mailUtils;

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException {
        List<Cart> carts = cartRepository.findByUserId(userId);
        for (Cart cart : carts) {

            ProductOrder productOrder = new ProductOrder();

            productOrder.setOrderId(UUID.randomUUID().toString());
            productOrder.setOrderDate(LocalDate.now());

            productOrder.setProduct(cart.getProduct());
            productOrder.setQuantity(cart.getQuantity());
            productOrder.setPrice(cart.getProduct().getDiscountPrice());
            productOrder.setUser(cart.getUser());

            productOrder.setStatus(OrderStatus.IN_PROGRESS.getName());
            productOrder.setPaymentType(orderRequest.getPaymentType());

            OrderAddress orderAddress = getOrderAddress(orderRequest);

            productOrder.setOrderAddress(orderAddress);

            ProductOrder save = productOrderRepository.save(productOrder);
            mailUtils.productSendMail(save,"Success");


        }
    }

    private static OrderAddress getOrderAddress(OrderRequest orderRequest) {
        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setFirstName(orderRequest.getFirstName());
        orderAddress.setLastName(orderRequest.getLastName());
        orderAddress.setEmail(orderRequest.getEmail());
        orderAddress.setMobileNo(orderRequest.getMobileNo());
        orderAddress.setAddress(orderRequest.getAddress());
        orderAddress.setCity(orderRequest.getCity());
        orderAddress.setState(orderRequest.getState());
        orderAddress.setPincode(orderRequest.getPincode());
        return orderAddress;
    }

    @Override
    public List<ProductOrder> getOrderByUser(Integer userId) {
        return productOrderRepository.findByUserId(userId);
    }

    @Override
    public ProductOrder updateOrder(Integer userId, String status) {
        Optional<ProductOrder> byId = productOrderRepository.findById(userId);
        if (byId.isPresent()){
            ProductOrder productOrder = byId.get();
            productOrder.setStatus(status);
            ProductOrder saved = productOrderRepository.save(productOrder);
            return saved;
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrder() {
        return productOrderRepository.findAll();
    }
}
