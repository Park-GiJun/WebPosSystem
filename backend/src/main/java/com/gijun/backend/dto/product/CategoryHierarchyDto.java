package com.gijun.backend.dto.product;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryHierarchyDto {
    private Long id;
    private String code;
    private String name;
    private Integer level;
    private Integer orderNum;
    private String description;
    private Long productCount;
    private List<CategoryHierarchyDto> children;
}