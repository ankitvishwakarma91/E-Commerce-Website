package com.softworkshub.ecom.services;

import com.softworkshub.ecom.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    public Product saveProduct(Product product);

    public Product getProductById(int id);

    public List<Product> getAllProducts();

    public Boolean deleteProduct(int id);

    public Product updateProduct(Product product, MultipartFile file);

    public List<Product> getProductsIsActive(String category);
}
