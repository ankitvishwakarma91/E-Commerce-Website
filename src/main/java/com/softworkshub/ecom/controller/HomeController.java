package com.softworkshub.ecom.controller;


import com.softworkshub.ecom.model.Category;
import com.softworkshub.ecom.model.Product;
import com.softworkshub.ecom.model.UserDetails;
import com.softworkshub.ecom.services.impl.CartServiceImpl;
import com.softworkshub.ecom.services.impl.CategoryServiceImpl;
import com.softworkshub.ecom.services.impl.ProductServiceImpl;
import com.softworkshub.ecom.services.impl.UserServiceImpl;
import com.softworkshub.ecom.utils.MailUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;


@Controller
@Slf4j
public class HomeController {

    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

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


    @GetMapping("/index")
    public String homePage(){
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @GetMapping("/product")
    public String productPage(Model model , @RequestParam(value = "category",defaultValue = "")String category){
        log.info("category:{}", category);
        List<Product> productsIsActive = productService.getProductsIsActive(category);
        List<Category> allActiveCategories = categoryService.getAllActiveCategories();
        model.addAttribute("activeProduct",productsIsActive );
        model.addAttribute("activeCategory", allActiveCategories);
        model.addAttribute("paramValue", category);
        return "product";
    }

    @GetMapping("/view-products/{id}")
    public String viewProduct(@PathVariable("id") int id, Model model){
        Product productById = productService.getProductById(id);
        model.addAttribute("product", productById);
        return "view_products";
    }

    @PostMapping("/create-user")
    public String createUser(@ModelAttribute UserDetails userDetails,
                             @RequestParam("img") MultipartFile file,
                             HttpSession session
    ) throws IOException, MessagingException {

        String image = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        userDetails.setProfileImage(image);

        UserDetails saveUser = userService.saveUser(userDetails);
        if(!ObjectUtils.isEmpty(saveUser)){

            if (!file.isEmpty()){
                File savedfile = new ClassPathResource("/static/img/").getFile();

                Path path = Paths.get(savedfile.getAbsolutePath() + File.separator + "profileFromWeb" + File.separator + file.getOriginalFilename());

                log.info("saved file:{}", path);

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("success", "User saved in db ");
            log.info("email of user {}", userDetails.getEmail());
            mailUtils.createUserMail(userDetails.getEmail(),userDetails.getName());
        }else {
            session.setAttribute("error ", "User already exist  or something went wrong");
        }
        return "redirect:/register";
    }

    @GetMapping("/forgot-password")
    public String forgetPasswordPage(){
        return "forget_password";
    }

    @PostMapping("/forgot-password")
    public String validatePassword(@RequestParam("email") String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        UserDetails userByEmail = userService.findUserByEmail(email);

        log.info("user email : {} ", userByEmail);

        if(ObjectUtils.isEmpty(userByEmail)){
            log.info("user email is empty");
            session.setAttribute("error", "User doesn't exist  or something went wrong");
        }else {

            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email,resetToken);

            //Generate Url : http://localhost:8080/reset-password?token=sdj$32js#nsufns%20!jsfdj
            String url = MailUtils.generateUrl(request) + "/reset-password?token=" + resetToken;
            log.info("url {}",url);
            Boolean mail = mailUtils.sendMail(email, url);
            if (mail){
            session.setAttribute("success", "Email verified go and check email ");
            }else {
                log.info("email not verified or something went wrong");
                session.setAttribute("error", "Email not send Verify email or Something went wrong  ");
            }
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token,  Model model){

        UserDetails userByToken = userService.getUserByToken(token);
        if (userByToken== null){
            model.addAttribute("error", "Invalid token or expired ");
            return "errorpage";
        }
        model.addAttribute("token",token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String submitResetPassword(@RequestParam String token, @RequestParam String password, HttpSession session, Model model){

        UserDetails userByToken = userService.getUserByToken(token);
        if (userByToken== null){
            model.addAttribute("error", "Invalid token or expired ");
        }else{
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            model.addAttribute("error", "Password changed successfully");
        }
        return "errorpage";
    }



}
