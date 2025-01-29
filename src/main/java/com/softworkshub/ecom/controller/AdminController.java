package com.softworkshub.ecom.controller;


import com.softworkshub.ecom.model.Category;
import com.softworkshub.ecom.model.Product;
import com.softworkshub.ecom.model.ProductOrder;
import com.softworkshub.ecom.model.UserDetails;
import com.softworkshub.ecom.services.impl.*;
import com.softworkshub.ecom.utils.MailUtils;
import com.softworkshub.ecom.utils.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;



@Controller
@RequestMapping("/admin")
public class AdminController {

    Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private CartServiceImpl cartService;
    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private MailUtils mailUtils;

    @GetMapping("/profile")
    public String adminPage(){
        return "admin/index";
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

    @GetMapping("/add-product")
    public String addProduct(Model model){
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/add_product";
    }

    @GetMapping("/category")
    public String category(Model model){
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(
            @ModelAttribute Category category,
            @RequestParam("file")MultipartFile file,
            @RequestParam("isActive") boolean isActive,
            HttpSession session
    ) throws IOException {

        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";

        category.setImageName(imageName);

        Boolean existed = categoryService.existCategory(category.getName());

        if(existed){
            session.setAttribute("error", "Category already exists");
        }else {
            category.setActive(isActive);
            Category saveCategory = categoryService.saveCategory(category);

            if(ObjectUtils.isEmpty(saveCategory)){
                session.setAttribute("error", " Not Saved  ! Internal Error");
            }else {

                File savedFile = new ClassPathResource("static/img").getFile();


                Path path = Paths.get(savedFile.getAbsolutePath() + File.separator + "savedFormWeb" + File.separator + file.getOriginalFilename());

                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                session.setAttribute("success", "Saved Successfully");
            }
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable("id") int id, HttpSession session){

        log.info("Delete Category id : {}", id);

        Boolean deleteCategory = categoryService.deleteCategory(id);

        log.info(deleteCategory.toString());

        if(deleteCategory){
            session.setAttribute("success", "Deleted Successfully");
        }else {
            session.setAttribute("error", "Not Deleted ! Something Went Wrong !");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable("id") int id, Model model){
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }


    @PostMapping("/updateCategory")
    public String updateCategory(
            @ModelAttribute Category category,
            @RequestParam("file") MultipartFile file,
            @RequestParam("isActive") boolean isActive,
            HttpSession session
    ) throws IOException {

        Category oldCategoryById = categoryService.getCategoryById(category.getId());

        String imageName = file.isEmpty() ? oldCategoryById.getImageName() : file.getOriginalFilename();

        if(!ObjectUtils.isEmpty(oldCategoryById)){
            oldCategoryById.setName(category.getName());
            oldCategoryById.setActive(isActive);
            oldCategoryById.setImageName(imageName);
        }

        Category updatedCategory = categoryService.saveCategory(oldCategoryById);

        if(!ObjectUtils.isEmpty(updatedCategory)){

            if (!file.isEmpty()){
                File saveFile = new ClassPathResource("/static/img/").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "savedFormWeb" + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            session.setAttribute("success", "Updated Successfully");
        }else{
            session.setAttribute("error", "Not Updated ! Something Went Wrong !");
        }

        return "redirect:/admin/loadEditCategory/" + category.getId();
    }


    @PostMapping("/create-product")
    public String createProduct(
            @ModelAttribute Product product,
            @RequestParam("file") MultipartFile image,
            @RequestParam("isActive") boolean isActive,
            HttpSession session
    ) throws IOException {

        String file = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        product.setImage(file);
        product.setDiscount(0);
        product.setActive(isActive);
        product.setDiscountPrice(product.getPrice());
        Product saveProduct = productService.saveProduct(product);

        if(!ObjectUtils.isEmpty(saveProduct)){

            File savedFile = new ClassPathResource("static/img").getFile();

            Path path = Paths.get(savedFile.getAbsolutePath() + File.separator + "productFromWeb" + File.separator + image.getOriginalFilename());

            log.info(path.toString());

            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            session.setAttribute("success", "Saved Successfully");
        }else {
            session.setAttribute("error", "Not Saved ! Something Went Wrong !");
        }
        return "admin/add_product";
    }


    @GetMapping("/viewProducts")
    public String viewProducts(Model model){
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }


    @GetMapping("/deleteProducts/{id}")
    public String deleteViewProducts( @PathVariable("id") int id , HttpSession session){

        Boolean deleteProduct = productService.deleteProduct(id);

        if (deleteProduct){
            session.setAttribute("success", "Deleted Successfully");
        }else {
            session.setAttribute("error", "Not Deleted ! Something Went Wrong !");
        }
        return "redirect:/admin/viewProducts";
    }

    @GetMapping("/editViewProducts/{id}")
    public String editViewProducts(Model model, @PathVariable("id") int id){
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("category", categoryService.getAllCategories());
        return "admin/edit_products";
    }

    @PostMapping("/updateProducts")
    public String updateViewProducts(
            @ModelAttribute Product product,
            @RequestParam("file") MultipartFile image,
            @RequestParam("isActive") boolean isActive,
            HttpSession session
    ){

        if (product.getDiscount() < 0 || product.getDiscount() > 100 ){
            session.setAttribute("error", " Invalid Discount !");
        }else {
            product.setActive(isActive);
            Product updated = productService.updateProduct(product, image);

            if(!ObjectUtils.isEmpty(updated)){
                session.setAttribute("success", "Updated Successfully");
            }else {
                session.setAttribute("error", "Not Updated ! Something Went Wrong !");
            }
        }

        return "redirect:/admin/editViewProducts/" + product.getId();
    }


    @GetMapping("/view-users")
    public String viewUsers(Model model){
        model.addAttribute("users", userService.getAllUsers("ROLE_USER"));
        return "admin/user";
    }

    @GetMapping("/update")
    public String updateAccountStatus(@RequestParam Boolean isEnabled, @RequestParam Integer id, HttpSession session){
        Boolean updated = userService.updateAccountStatus(id, isEnabled);
        if(updated){
            session.setAttribute("success", "Updated Successfully");
        }else{
            session.setAttribute("error", "Not Updated ! Something Went Wrong !");
        }
        return "redirect:/admin/view-users";
    }

    @GetMapping("/orders")
    public String orders(Model model){
        List<ProductOrder> allOrder = orderService.getAllOrder();
        model.addAttribute("orders", allOrder);
        return "admin/orders";
    }

    @PostMapping("/update-order-status")
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
        return "redirect:/admin/orders";
    }

}
