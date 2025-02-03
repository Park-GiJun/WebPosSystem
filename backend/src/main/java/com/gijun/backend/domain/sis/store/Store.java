package com.gijun.backend.domain.sis.store;

import com.gijun.backend.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;  // 매장 코드

    @Column(nullable = false, length = 100)
    private String name;  // 매장명

    @Column(length = 500)
    private String address;  // 매장 주소

    @Column(length = 20)
    private String phone;  // 매장 연락처

    @Column(name = "business_hours", length = 100)
    private String businessHours;  // 영업 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreStatus status;  // 매장 상태

    @Builder
    public Store(String code, String name, String address, String phone,
                 String businessHours, StoreStatus status) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.businessHours = businessHours;
        this.status = status;
    }

    public void updateInfo(String name, String address, String phone, String businessHours) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.businessHours = businessHours;
    }

    public void updateStatus(StoreStatus status) {
        this.status = status;
    }
}