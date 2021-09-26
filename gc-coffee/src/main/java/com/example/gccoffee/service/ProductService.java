package com.example.gccoffee.service;

import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<Product> getProductsByCategory(Category category);

    List<Product> getAllProducts();

    Product createProduct(String productName, Category category, long price);

    Product createProduct(String productName, Category category, long price, String description);

    Product getProductById(UUID productId);

    Product updateProduct(Product product);

    void deleteProductById(UUID productId);
}
