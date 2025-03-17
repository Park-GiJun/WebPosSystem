package com.gijun.backend.domain.sis.product;

import com.gijun.backend.domain.BaseEntity;
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
    private ProductUnit unit = ProductUnit.EA;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;

    @Column(name = "barcode", length = 30)
    private String barcode;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    // 세트 상품인 경우 구성 상품들
    @ManyToMany
    @JoinTable(
            name = "set_product_items",
            joinColumns = @JoinColumn(name = "set_product_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> setItems = new ArrayList<>();

    // 레시피 상품인 경우 사용되는 원재료들
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

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
        this.status = status;
        this.isTaxable = isTaxable;
        this.unit = unit != null ? unit : ProductUnit.EA;
        this.productType = productType;
        this.barcode = barcode;
        this.imageUrl = imageUrl;
    }

    public void updateStock(int quantity) {
        this.stock += quantity;
        if (this.stock < 0) {
            throw new IllegalStateException("재고가 부족합니다");
        }
        if (this.stock == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        }
        if (this.stock > 0 && this.status == ProductStatus.OUT_OF_STOCK) {
            this.status = ProductStatus.ON_SALE;
        }
    }

    public void update(String name, String description, Category category,
                       BigDecimal price, BigDecimal costPrice, Integer minStock,
                       Integer maxStock, ProductUnit unit, ProductType productType,
                       String barcode, String imageUrl) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (category != null) this.category = category;
        if (price != null) this.price = price;
        if (costPrice != null) this.costPrice = costPrice;
        if (minStock != null) this.minStock = minStock;
        if (maxStock != null) this.maxStock = maxStock;
        if (unit != null) this.unit = unit;
        if (productType != null) this.productType = productType;
        if (barcode != null) this.barcode = barcode;
        if (imageUrl != null) this.imageUrl = imageUrl;
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        if (status != ProductStatus.ON_SALE) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (saleStartDate != null && now.isBefore(saleStartDate)) {
            return false;
        }

        if (saleEndDate != null && now.isAfter(saleEndDate)) {
            return false;
        }

        return stock > 0;
    }

    // 세트 상품 관련 메서드
    public void addSetItem(Product product) {
        if (this.productType != ProductType.SET_PRODUCT) {
            throw new IllegalStateException("세트 상품만 구성 상품을 추가할 수 있습니다");
        }
        this.setItems.add(product);
    }

    public void removeSetItem(Product product) {
        this.setItems.remove(product);
    }

    public void clearSetItems() {
        this.setItems.clear();
    }

    // 레시피 상품 관련 메서드
    public void addRecipeIngredient(Product ingredient, int quantity) {
        if (this.productType != ProductType.RECIPE_PRODUCT) {
            throw new IllegalStateException("레시피 상품만 재료를 추가할 수 있습니다");
        }
        RecipeIngredient recipeIngredient = new RecipeIngredient(this, ingredient, quantity);
        this.recipeIngredients.add(recipeIngredient);
    }

    public void removeRecipeIngredient(Product ingredient) {
        this.recipeIngredients.removeIf(ri -> ri.getIngredient().equals(ingredient));
    }

    public void clearRecipeIngredients() {
        this.recipeIngredients.clear();
    }
}