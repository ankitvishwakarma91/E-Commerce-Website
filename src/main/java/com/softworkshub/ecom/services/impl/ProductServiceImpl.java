package com.softworkshub.ecom.services.impl;

import com.softworkshub.ecom.model.Product;
import com.softworkshub.ecom.repository.ProductRepository;
import com.softworkshub.ecom.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Boolean deleteProduct(int id) {
        Product product = productRepository.findById(id).orElse(null);

        if (!ObjectUtils.isEmpty(product)) {
            productRepository.delete(product);
            return true;
        }

        return false;
    }


    public Product updateProduct(Product product, MultipartFile file) {
        Product oldProductById = productRepository.findById(product.getId()).orElse(null);

        String image = file.isEmpty() ? oldProductById.getImage() : file.getOriginalFilename();

        oldProductById.setImage(image);
        oldProductById.setTitle(product.getTitle());
        oldProductById.setPrice(product.getPrice());
        oldProductById.setDescription(product.getDescription());
        oldProductById.setCategory(product.getCategory());
        oldProductById.setStock(product.getStock());
        oldProductById.setDiscount(product.getDiscount());
        oldProductById.setActive(product.getActive());

        double discount  = product.getPrice() * (product.getDiscount() / 100.0);
        double discountPrice = product.getPrice() - discount;
        oldProductById.setDiscountPrice(discountPrice);

        Product updatedProduct = productRepository.save(oldProductById);
        if (!ObjectUtils.isEmpty(updatedProduct)) {

            if (!image.isEmpty()) {
                try {
                    File saveFile = new ClassPathResource("static/img").getFile();

                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "productFromWeb" + File.separator + file.getOriginalFilename());

                    Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return product;
        }
        return null;
    }

    @Override
    public List<Product> getProductsIsActive(String category) {
        List<Product> products;
        if (ObjectUtils.isEmpty(category)){
            products =  productRepository.findByIsActiveTrue();
        }else {
            products = productRepository.findByCategory(category);
        }
        return products;
    }
}
