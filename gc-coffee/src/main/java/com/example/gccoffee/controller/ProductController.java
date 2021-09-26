package com.example.gccoffee.controller;

import com.example.gccoffee.model.CreateProductRequest;
import com.example.gccoffee.model.UpdateProductRequest;
import com.example.gccoffee.service.DefaultProductService;
import com.example.gccoffee.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String viewProductPage(Model model) {
        var products = productService.getAllProducts();
        model.addAttribute("products", products);

        return "product-list";
    }

    @GetMapping("/new-product")
    public String viewNewProductPage() {
        return "new-product";
    }

    @PostMapping("/products")
    public String newProduct(CreateProductRequest createProductRequest) {
        productService.createProduct(
                createProductRequest.productName(),
                createProductRequest.category(),
                createProductRequest.price(),
                createProductRequest.description());
        return "redirect:/products";
    }

    @GetMapping("/update/{productId}")
    public String viewUpdateProductPage(@PathVariable("productId") UUID productId, Model model) {
        var product = productService.getProductById(productId);
        model.addAttribute("product", product);
        return "update-product";
    }

    @PostMapping("/update/{productId}")
    public String updateProduct(@PathVariable("productId") String productId, UpdateProductRequest updateProductRequest) {
        var product = productService.getProductById(UUID.fromString(productId));
        product.setDescription(updateProductRequest.getDescription());
        productService.updateProduct(product);

        return "redirect:/products";
    }

    @GetMapping("/delete-product/{productId}")
    public String deleteProduct(@PathVariable("productId") UUID productId) {
        productService.deleteProductById(productId);

        return "redirect:/products";
    }
}
