package com.gijun.backend.domain.sis.product;

import lombok.Getter;

@Getter
public enum ProductUnit {
    EA("개"),      // 개수
    KG("kg"),     // 킬로그램
    G("g"),       // 그램
    L("L"),       // 리터
    ML("ml"),     // 밀리리터
    BOX("박스"),   // 박스
    SET("세트"),   // 세트
    PKG("팩");    // 패키지

    private final String description;

    ProductUnit(String description) {
        this.description = description;
    }
}