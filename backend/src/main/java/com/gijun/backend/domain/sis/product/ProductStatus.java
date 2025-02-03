package com.gijun.backend.domain.sis.product;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ON_SALE("판매중"),
    OUT_OF_STOCK("품절"),
    DISCONTINUED("판매중지"),
    UPCOMING("판매예정");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }
}