package com.gijun.backend.dto.product;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryProductCountDto {
    private Long categoryId;
    private String categoryName;
    private Long productCount;
}