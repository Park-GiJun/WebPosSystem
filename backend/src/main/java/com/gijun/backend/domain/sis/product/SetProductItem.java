package com.gijun.backend.domain.sis.product;

import com.gijun.backend.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "set_product_items")
@Getter
@NoArgsConstructor
public class SetProductItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_product_id", nullable = false)
    private Product setProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Product item;

    @Column(nullable = false)
    private Integer quantity;

    public SetProductItem(Product setProduct, Product item, Integer quantity) {
        this.setProduct = setProduct;
        this.item = item;
        this.quantity = quantity;
    }

    public void updateQuantity(Integer quantity) {
        if (quantity != null && quantity > 0) {
            this.quantity = quantity;
        }
    }
}