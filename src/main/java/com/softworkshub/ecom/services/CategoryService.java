package com.softworkshub.ecom.services;

import com.softworkshub.ecom.model.Category;

import java.util.List;

public interface CategoryService {

    public Category saveCategory(Category category);

    public List<Category> getAllCategories();

    public Boolean deleteCategory(int id);

    public Boolean existCategory(String categoryName);

    public Category getCategoryById(int id);

    public List<Category> getAllActiveCategories();
}
