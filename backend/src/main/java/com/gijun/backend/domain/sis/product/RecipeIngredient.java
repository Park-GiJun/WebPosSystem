package com.gijun.backend.domain.sis.product;

import com.gijun.backend.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_ingredients")
@Getter
@NoArgsConstructor
public class RecipeIngredient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Product ingredient;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private ProductUnit unit;

    public RecipeIngredient(Product product, Product ingredient, int quantity) {
        this.product = product;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = ingredient.getUnit();
    }

    public RecipeIngredient(Product product, Product ingredient, int quantity, ProductUnit unit) {
        this.product = product;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void updateUnit(ProductUnit unit) {
        this.unit = unit;
    }
}