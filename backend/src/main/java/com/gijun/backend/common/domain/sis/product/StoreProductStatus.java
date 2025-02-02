package com.gijun.backend.common.domain.sis.product;

import lombok.Getter;

@Getter
public enum StoreProductStatus {
    ON_SALE("판매중"),
    OUT_OF_STOCK("품절"),
    DISCONTINUED("판매중지"),
    HIDDEN("숨김");

    private final String description;

    StoreProductStatus(String description) {
        this.description = description;
    }
}