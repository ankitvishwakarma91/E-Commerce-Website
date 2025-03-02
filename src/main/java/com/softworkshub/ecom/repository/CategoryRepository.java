package com.softworkshub.ecom.repository;

import com.softworkshub.ecom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Boolean existsByName(String categoryName);

    List<Category> findAllByIsActiveTrue();

}
