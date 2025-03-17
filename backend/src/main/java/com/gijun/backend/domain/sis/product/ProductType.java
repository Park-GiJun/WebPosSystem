package com.gijun.backend.domain.sis.product;

import lombok.Getter;

@Getter
public enum ProductType {
    RAW_MATERIAL("원재료"),
    PRODUCT("제품"),
    GOODS("상품"),
    RECIPE_PRODUCT("레시피 제품"),
    SET_PRODUCT("세트상품");

    private final String description;

    ProductType(String description) {
        this.description = description;
    }
}