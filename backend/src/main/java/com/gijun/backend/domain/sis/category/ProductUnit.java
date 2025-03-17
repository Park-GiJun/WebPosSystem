package com.gijun.backend.domain.sis.category;

import lombok.Getter;

public enum ProductUnit {
    EA("개"),
    KG("kg"),
    G("g"),
    L("L"),
    ML("ml"),
    BOX("박스"),
    SET("세트"),
    PKG("팩");

    @Getter
    private final String description;

    ProductUnit(String description) {
        this.description = description;
    }
}

