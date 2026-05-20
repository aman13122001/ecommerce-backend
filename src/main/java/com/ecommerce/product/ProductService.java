package com.ecommerce.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ProductService {

    public List<Product> getAllProducts() {
        return Product.listAll();
    }

    public Product getProductById(Long id) {
        return Product.findById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        return Product.findByCategory(category);
    }

    public List<Product> searchProducts(String keyword) {
        return Product.searchByName(keyword);
    }

    @Transactional
    public Product createProduct(String name, String description,
                                  BigDecimal price, String category, int stock) {
        Product p = new Product();
        p.name = name;
        p.description = description;
        p.price = price;
        p.category = category;
        p.stockQuantity = stock;
        p.persist();
        return p;
    }

    @Transactional
    public Product updateProduct(Long id, String name, String description,
                                  BigDecimal price, String category, int stock) {
        Product p = Product.findById(id);
        if (p == null) throw new RuntimeException("Product not found: " + id);
        if (name != null) p.name = name;
        if (description != null) p.description = description;
        if (price != null) p.price = price;
        if (category != null) p.category = category;
        if (stock >= 0) p.stockQuantity = stock;
        return p;
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        return Product.deleteById(id);
    }
}
