package com.gijun.backend.domain.sis.product;

import com.gijun.backend.domain.BaseEntity;
import com.gijun.backend.domain.sis.category.Category;
import com.gijun.backend.domain.sis.category.ProductStatus;
import com.gijun.backend.domain.sis.category.ProductType;
import com.gijun.backend.domain.sis.category.ProductUnit;
import com.gijun.backend.domain.sis.recipe.RecipeComponent;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "min_stock", nullable = false)
    private Integer minStock;

    @Column(name = "max_stock")
    private Integer maxStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(name = "is_taxable", nullable = false)
    private boolean isTaxable = true;

    @Column(name = "sale_start_date")
    private LocalDateTime saleStartDate;

    @Column(name = "sale_end_date")
    private LocalDateTime saleEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private ProductUnit unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;

    @Column(name = "barcode", length = 30)
    private String barcode;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    // Recipe components (for recipe products)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeComponent> recipeComponents = new ArrayList<>();

    // Set product items (for set products)
    @OneToMany(mappedBy = "setProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SetProductItem> setProductItems = new ArrayList<>();

    @Builder
    public Product(String code, String name, String description, Category category,
                   BigDecimal price, BigDecimal costPrice, Integer stock, Integer minStock,
                   Integer maxStock, ProductStatus status, boolean isTaxable, ProductUnit unit,
                   ProductType productType, String barcode, String imageUrl) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.costPrice = costPrice;
        this.stock = stock;
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.status = status != null ? status : ProductStatus.ON_SALE;
        this.isTaxable = isTaxable;
        this.unit = unit != null ? unit : ProductUnit.EA;
        this.productType = productType;
        this.barcode = barcode;
        this.imageUrl = imageUrl;
    }

    public void updateStock(int quantity) {
        this.stock += quantity;
        if (this.stock < 0) {
            throw new IllegalStateException("Stock cannot be negative");
        }

        // Update product status based on stock
        if (this.stock == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        } else if (this.status == ProductStatus.OUT_OF_STOCK) {
            this.status = ProductStatus.ON_SALE;
        }
    }

    public void update(String name, String description, Category category,
                       BigDecimal price, BigDecimal costPrice, Integer minStock,
                       Integer maxStock, ProductUnit unit, ProductStatus status,
                       ProductType productType, String barcode, String imageUrl) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (category != null) this.category = category;
        if (price != null) this.price = price;
        if (costPrice != null) this.costPrice = costPrice;
        if (minStock != null) this.minStock = minStock;
        if (maxStock != null) this.maxStock = maxStock;
        if (unit != null) this.unit = unit;
        if (status != null) this.status = status;
        if (productType != null) this.productType = productType;
        if (barcode != null) this.barcode = barcode;
        if (imageUrl != null) this.imageUrl = imageUrl;
    }

    public void addRecipeComponent(Product ingredient, int quantity, ProductUnit unit) {
        if (this.productType != ProductType.RECIPE_PRODUCT) {
            throw new IllegalStateException("Only recipe products can have recipe components");
        }
        RecipeComponent component = new RecipeComponent(this, ingredient, quantity, unit);
        this.recipeComponents.add(component);
    }

    public void removeRecipeComponent(Long ingredientId) {
        this.recipeComponents.removeIf(component -> component.getIngredient().getId().equals(ingredientId));
    }

    public void clearRecipeComponents() {
        this.recipeComponents.clear();
    }

    public void addSetProductItem(Product item, int quantity) {
        if (this.productType != ProductType.SET_PRODUCT) {
            throw new IllegalStateException("Only set products can have set product items");
        }
        SetProductItem setItem = new SetProductItem(this, item, quantity);
        this.setProductItems.add(setItem);
    }

    public void removeSetProductItem(Long itemId) {
        this.setProductItems.removeIf(item -> item.getItem().getId().equals(itemId));
    }

    public void clearSetProductItems() {
        this.setProductItems.clear();
    }
}