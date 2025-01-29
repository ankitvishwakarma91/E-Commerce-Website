package com.softworkshub.ecom.services.impl;

import com.softworkshub.ecom.model.Category;
import com.softworkshub.ecom.repository.CategoryRepository;
import com.softworkshub.ecom.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Boolean deleteCategory(int id) {

        Category category = categoryRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(category)) {
            categoryRepository.delete(category);
            return true;
        }
        return false ;
    }

    @Override
    public Boolean existCategory(String categoryName) {
        return categoryRepository.existsByName(categoryName);
    }

    @Override
    public Category getCategoryById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAllByIsActiveTrue();
    }
}
