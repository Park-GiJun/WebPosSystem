package com.gijun.backend.domain.sis.product;

import com.gijun.backend.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Product.java
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

    @Column(nullable = false, length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "cost_price", nullable = false)
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

    @Column(name = "sale_start_date")
    private LocalDateTime saleStartDate;

    @Column(name = "sale_end_date")
    private LocalDateTime saleEndDate;

    @Builder
    public Product(String code, String name, String description, Category category,
                   BigDecimal price, BigDecimal costPrice, Integer stock, Integer minStock,
                   Integer maxStock, ProductStatus status) {
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
                       BigDecimal price, BigDecimal costPrice, Integer minStock, Integer maxStock) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (category != null) this.category = category;
        if (price != null) this.price = price;
        if (costPrice != null) this.costPrice = costPrice;
        if (minStock != null) this.minStock = minStock;
        if (maxStock != null) this.maxStock = maxStock;
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
}
