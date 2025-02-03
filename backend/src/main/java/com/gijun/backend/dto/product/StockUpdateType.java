package com.gijun.backend.dto.product;

import lombok.Getter;

@Getter
public enum StockUpdateType {
    MANUAL_ADD("수동 입고"),
    MANUAL_SUBTRACT("수동 출고"),
    PURCHASE("구매 입고"),
    SALE("판매 출고"),
    RETURN("반품 입고"),
    DISCARD("폐기 출고"),
    ADJUSTMENT("재고 조정");

    private final String description;

    StockUpdateType(String description) {
        this.description = description;
    }

}