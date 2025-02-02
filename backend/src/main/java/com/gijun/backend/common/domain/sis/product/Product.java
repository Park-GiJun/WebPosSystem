package com.gijun.backend.common.domain.sis.product;

import com.gijun.backend.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;  // 상품 코드

    @Column(nullable = false, length = 100)
    private String name;  // 상품명

    @Column(nullable = false, length = 500)
    private String description;  // 상품 설명

    @Column(length = 255)
    private String imageUrl;  // 상품 이미지 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;  // 카테고리

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;  // 판매가

    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;  // 원가

    @Column(nullable = false)
    private Integer stock;  // 현재 재고량

    @Column(name = "min_stock", nullable = false)
    private Integer minStock;  // 최소 재고량

    @Column(name = "max_stock")
    private Integer maxStock;  // 최대 재고량

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;  // 상품 상태

    @Column(name = "barcode", length = 30)
    private String barcode;  // 바코드

    @Column(name = "is_taxable", nullable = false)
    private boolean isTaxable;  // 과세 여부

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;  // 세율

    @Column(name = "point_rate", precision = 5, scale = 2)
    private BigDecimal pointRate;  // 포인트 적립률

    @Column(name = "sale_start_date")
    private LocalDateTime saleStartDate;  // 판매 시작일

    @Column(name = "sale_end_date")
    private LocalDateTime saleEndDate;  // 판매 종료일

    @Builder
    public Product(String code, String name, String description, Category category,
                   BigDecimal price, BigDecimal costPrice, Integer stock, Integer minStock,
                   Integer maxStock, ProductStatus status, boolean isTaxable) {
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
        this.taxRate = isTaxable ? new BigDecimal("0.10") : BigDecimal.ZERO;  // 기본 세율 10%
        this.pointRate = new BigDecimal("0.01");  // 기본 포인트 적립률 1%
    }

    // 재고 관리 메서드
    public void addStock(int quantity) {
        this.stock += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stock - quantity;
        if (restStock < 0) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stock = restStock;
    }

    // 상품 상태 변경
    public void updateStatus(ProductStatus status) {
        this.status = status;
    }

    // 가격 변경
    public void updatePrice(BigDecimal newPrice) {
        this.price = newPrice;
    }

    // 상품 정보 업데이트
    public void updateInfo(String name, String description, Category category,
                           BigDecimal price, BigDecimal costPrice,
                           Integer minStock, Integer maxStock) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.costPrice = costPrice;
        this.minStock = minStock;
        this.maxStock = maxStock;
    }

    // 판매 기간 설정
    public void setSalePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("종료일이 시작일보다 빠를 수 없습니다.");
        }
        this.saleStartDate = startDate;
        this.saleEndDate = endDate;
    }

    // 판매 가능 여부 확인
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

