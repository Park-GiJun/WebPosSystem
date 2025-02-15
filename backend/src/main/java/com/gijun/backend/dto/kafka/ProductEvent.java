package com.gijun.backend.dto.kafka;

import com.gijun.backend.domain.sis.product.Product;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ProductEvent {
    private final String eventType;
    private final ProductEventData data;
    private final String timestamp;

    @Getter
    public static class ProductEventData {
        private final Long id;
        private final String code;
        private final String name;
        private final Long categoryId;
        private final String categoryName;
        private final BigDecimal price;
        private final Integer stock;
        private final String status;

        public ProductEventData(Product product) {
            this.id = product.getId();
            this.code = product.getCode();
            this.name = product.getName();
            this.categoryId = product.getCategory().getId();
            this.categoryName = product.getCategory().getName();
            this.price = product.getPrice();
            this.stock = product.getStock();
            this.status = product.getStatus().name();
        }
    }

    public ProductEvent(String eventType, Product product) {
        this.eventType = eventType;
        this.data = new ProductEventData(product);
        this.timestamp = LocalDateTime.now().toString();
    }
}