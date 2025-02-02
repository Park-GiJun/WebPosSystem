package com.gijun.backend.common.domain.sis.store;


import lombok.Getter;

@Getter
public enum StoreStatus {
    OPEN("영업중"),
    CLOSED("영업종료"),
    TEMPORARILY_CLOSED("임시휴업"),
    PREPARING("준비중");

    private final String description;

    StoreStatus(String description) {
        this.description = description;
    }
}