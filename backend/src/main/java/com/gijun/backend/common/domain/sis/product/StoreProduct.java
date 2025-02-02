package com.gijun.backend.common.domain.sis.product;

import com.gijun.backend.common.domain.BaseEntity;
import com.gijun.backend.common.domain.sis.store.Store;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "store_products",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"store_id", "product_id"},
                        name = "uk_store_product"
                )
        }
)
@Getter
@NoArgsConstructor
public class StoreProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;  // 매장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // 상품 마스터

    @Column(nullable = false)
    private Integer stock;  // 현재 재고량

    @Column(name = "min_stock", nullable = false)
    private Integer minStock;  // 최소 재고량

    @Column(name = "max_stock")
    private Integer maxStock;  // 최대 재고량

    @Column(name = "sale_price", precision = 10, scale = 2)
    private BigDecimal salePrice;  // 매장별 판매가 (null인 경우 마스터 가격 사용)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreProductStatus status;  // 매장별 상품 상태

    @Column(name = "display_order")
    private Integer displayOrder;  // 진열 순서

    @Column(name = "last_restocked_at")
    private LocalDateTime lastRestockedAt;  // 마지막 입고일

    @Column(name = "auto_reorder")
    private boolean autoReorder;  // 자동 발주 여부

    @Builder
    public StoreProduct(Store store, Product product, Integer stock,
                        Integer minStock, Integer maxStock, BigDecimal salePrice,
                        StoreProductStatus status, Integer displayOrder,
                        boolean autoReorder) {
        this.store = store;
        this.product = product;
        this.stock = stock;
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.salePrice = salePrice;
        this.status = status;
        this.displayOrder = displayOrder;
        this.autoReorder = autoReorder;
    }

    // 재고 관리 메서드
    public void addStock(int quantity) {
        this.stock += quantity;
        this.lastRestockedAt = LocalDateTime.now();
    }

    public void removeStock(int quantity) {
        int restStock = this.stock - quantity;
        if (restStock < 0) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stock = restStock;

        // 최소 재고 미달 시 자동 발주 체크
        if (autoReorder && stock <= minStock) {
            // TODO: 자동 발주 로직 구현
        }
    }

    // 가격 관리
    public BigDecimal getCurrentPrice() {
        return salePrice != null ? salePrice : product.getPrice();
    }

    public void updateSalePrice(BigDecimal newPrice) {
        this.salePrice = newPrice;
    }

    // 상태 관리
    public void updateStatus(StoreProductStatus status) {
        this.status = status;
    }

    // 진열 순서 변경
    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    // 자동 발주 설정
    public void setAutoReorder(boolean autoReorder) {
        this.autoReorder = autoReorder;
    }

    // 재고 설정 업데이트
    public void updateStockSettings(Integer minStock, Integer maxStock) {
        this.minStock = minStock;
        this.maxStock = maxStock;
    }

    // 판매 가능 여부 확인
    public boolean isAvailable() {
        return status == StoreProductStatus.ON_SALE &&
                stock > 0 &&
                product.isAvailable();
    }
}
