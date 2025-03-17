package com.gijun.backend.domain.sis.recipe;

import com.gijun.backend.domain.BaseEntity;
import com.gijun.backend.domain.sis.category.ProductUnit;
import com.gijun.backend.domain.sis.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_components")
@Getter
@NoArgsConstructor
public class RecipeComponent extends BaseEntity {

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
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductUnit unit;

    public RecipeComponent(Product product, Product ingredient, Integer quantity, ProductUnit unit) {
        this.product = product;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }

    public void updateQuantity(Integer quantity) {
        if (quantity != null && quantity > 0) {
            this.quantity = quantity;
        }
    }

    public void updateUnit(ProductUnit unit) {
        if (unit != null) {
            this.unit = unit;
        }
    }
}