package com.softworkshub.ecom.controller;


import com.softworkshub.ecom.model.*;
import com.softworkshub.ecom.services.impl.CartServiceImpl;
import com.softworkshub.ecom.services.impl.CategoryServiceImpl;
import com.softworkshub.ecom.services.impl.OrderServiceImpl;
import com.softworkshub.ecom.services.impl.UserServiceImpl;
import com.softworkshub.ecom.utils.MailUtils;
import com.softworkshub.ecom.utils.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private CartServiceImpl cartService;
    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/home")
    public String userPage(){
        return "user/home";
    }

    @GetMapping("/profile")
    public String profilePage(){
        return "user/profile";
    }

    @ModelAttribute
    public void getUserName(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            UserDetails userByEmail = userService.findUserByEmail(email);
            model.addAttribute("user", userByEmail);
            Integer cartCount = cartService.getCartCount(userByEmail.getId());
            model.addAttribute("cartCount", cartCount);
        }

        List<Category> allActiveCategories = categoryService.getAllActiveCategories();
        model.addAttribute("categorys", allActiveCategories);
    }

    @GetMapping("/addCart")
    public String addCartPage(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session){
        Cart cart = cartService.saveCart(pid, uid);
        if (ObjectUtils.isEmpty(cart)){
            session.setAttribute("error","Not Saved !! Something went wrong");
        }else{
            session.setAttribute("success", "Saved !! ");
        }
        return "redirect:/view-products/" + pid;
    }


    @GetMapping("/cart")
    public String loadCartPage(Principal p , Model model){

        UserDetails user = getUser(p);
        List<Cart> carts = cartService.getAllCartByUserId(user.getId());
        log.info("list of carts{}",carts);
        model.addAttribute("carts", carts);
        if (!carts.isEmpty()) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            model.addAttribute("totalOrderAmount", totalOrderPrice);
        }
        return "user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid){
        cartService.updateQuantity(sy,cid);
        return "redirect:/user/cart";
    }

    private UserDetails getUser(Principal p ){
        String name = p.getName();
        return userService.findUserByEmail(name);
    }


    @GetMapping("/order")
    public String orderPage(Principal principal, Model model){
        UserDetails user = getUser(principal);
        List<Cart> carts = cartService.getAllCartByUserId(user.getId());
        model.addAttribute("carts", carts);
        if (!carts.isEmpty()) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice()+250 + 100;
            model.addAttribute("orderAmount", orderPrice);
            model.addAttribute("totalOrderAmount", totalOrderPrice);
        }
        return "user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest,Principal principal) throws MessagingException, UnsupportedEncodingException {
        UserDetails user = getUser(principal);
        orderService.saveOrder(user.getId(),orderRequest);
        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String loadSuccessPage(){
        return "user/success";
    }

    @GetMapping("/order-details")
    public String orderDetails(Model model, Principal principal){
        UserDetails user = getUser(principal);
        List<ProductOrder> orderByUser = orderService.getOrderByUser(user.getId());
        model.addAttribute("orders",orderByUser);
        return "/user/order-details";
    }


    @GetMapping("/update-status")
    public String updateStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session){

        OrderStatus[] values = OrderStatus.values();
        String statusName = null;

        for(OrderStatus s : values){
            if (s.getId().equals(st)){
                statusName = s.getName();
            }
        }

        ProductOrder productOrder = orderService.updateOrder(id, statusName);

        try {
            mailUtils.productSendMail(productOrder,statusName);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!ObjectUtils.isEmpty(productOrder)){
            session.setAttribute("success", " Status Updated !! ");
        }else {
            session.setAttribute("error", "Status Not Updated !! Something went wrong");
        }
        return "redirect:/user/order-details";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDetails user, @RequestParam MultipartFile img, HttpSession session){
        UserDetails userDetails = userService.updateUserProfile(user, img);
        if (ObjectUtils.isEmpty(userDetails)){
            session.setAttribute("error", "Profile Not Updated !! Something went wrong");
        }else{
            session.setAttribute("success", "Profile Updated !! ");
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal principal,HttpSession session){
        UserDetails user = getUser(principal);
        boolean matches = passwordEncoder.matches(currentPassword, user.getPassword());

        if (matches){
            String encode = passwordEncoder.encode(newPassword);
            user.setPassword(encode);
            UserDetails userDetails = userService.updateUser(user);
            if (ObjectUtils.isEmpty(userDetails)){
                session.setAttribute("error", "Password Not Updated !! Something went wrong");
            }else {
                session.setAttribute("success", "Password Updated !! ");
            }
        }else {
            session.setAttribute("error", "Current Password Incorrect");
        }
        return "redirect:/user/profile";
    }

}
