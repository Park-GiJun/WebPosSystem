package com.gijun.backend.domain.sis.recipe;

import com.gijun.backend.domain.BaseEntity;
import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.sis.product.ProductStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
@Getter
@NoArgsConstructor
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Builder
    public Recipe(String code, String name, String description, BigDecimal price, BigDecimal costPrice) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.price = price;
        this.costPrice = costPrice;
        this.status = ProductStatus.ON_SALE;
    }

    public void updateInfo(String name, String description, BigDecimal price, BigDecimal costPrice, ProductStatus status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.costPrice = costPrice;
        this.status = status;
    }

    public void addIngredient(Product product, int quantity) {
        RecipeIngredient ingredient = new RecipeIngredient(this, product, quantity);
        ingredients.add(ingredient);
    }

    public void updateIngredient(Product product, int quantity) {
        ingredients.stream()
                .filter(i -> i.getProduct().equals(product))
                .findFirst()
                .ifPresent(i -> i.updateQuantity(quantity));
    }

    public void removeIngredient(Product product) {
        ingredients.removeIf(i -> i.getProduct().equals(product));
    }
}