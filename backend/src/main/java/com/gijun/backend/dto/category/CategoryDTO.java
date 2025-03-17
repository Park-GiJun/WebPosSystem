package com.gijun.backend.dto.category;

import com.gijun.backend.domain.sis.category.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class CategoryDTO {

    @Data
    public static class CategoryCreateRequest {
        @NotBlank(message = "Category code is required")
        @Size(max = 20)
        private String code;

        @NotBlank(message = "Category name is required")
        @Size(max = 50)
        private String name;

        private Long parentId;

        @NotNull(message = "Order number is required")
        private Integer orderNum;

        @Size(max = 500)
        private String description;
    }

    @Data
    public static class CategoryUpdateRequest {
        @NotBlank(message = "Category name is required")
        @Size(max = 50)
        private String name;

        private Long parentId;

        @NotNull(message = "Order number is required")
        private Integer orderNum;

        @Size(max = 500)
        private String description;
    }

    @Data
    @Builder
    public static class CategoryResponse {
        private Long id;
        private String code;
        private String name;
        private Long parentId;
        private String parentName;
        private Integer level;
        private Integer orderNum;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static CategoryResponse from(Category category) {
            return CategoryResponse.builder()
                    .id(category.getId())
                    .code(category.getCode())
                    .name(category.getName())
                    .parentId(category.getParent() != null ? category.getParent().getId() : null)
                    .parentName(category.getParent() != null ? category.getParent().getName() : null)
                    .level(category.getLevel())
                    .orderNum(category.getOrderNum())
                    .description(category.getDescription())
                    .createdAt(category.getCreatedAt())
                    .updatedAt(category.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    public static class CategoryHierarchyDTO {
        private Long id;
        private String code;
        private String name;
        private Integer level;
        private Integer orderNum;
        private String description;
        private Long productCount;
        private List<CategoryHierarchyDTO> children;
    }
}