package com.pawtime.productservice.repository;

import com.pawtime.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Get all products by category (DOG or CAT)
    List<Product> findByCategory(String category);

    // Get all products by food type
    List<Product> findByFoodType(String foodType);

    // Get products by category and food type
    List<Product> findByCategoryAndFoodType(String category, String foodType);

    // Search products by name
    List<Product> findByNameContainingIgnoreCase(String name);
}