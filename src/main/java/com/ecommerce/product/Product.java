package com.ecommerce.product;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Indexed
@Table(name = "products")
public class Product extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @FullTextField
    @Column(nullable = false)
    public String name;

    @FullTextField
    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal price;

    public String category;

    @Column(name = "stock_quantity", nullable = false)
    public int stockQuantity = 0;

    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();

    public static List<Product> findByCategory(String category) {
        return list("category", category);
    }

    public static List<Product> searchByName(String keyword) {
        return list("lower(name) like ?1", "%" + keyword.toLowerCase() + "%");
    }
}
