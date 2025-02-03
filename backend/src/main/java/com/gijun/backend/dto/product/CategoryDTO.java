package com.gijun.backend.dto.product;

import com.gijun.backend.domain.sis.product.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class CategoryDTO {

    @Data
    public static class CategoryCreateRequest {
        @NotBlank(message = "카테고리 코드는 필수입니다")
        @Size(max = 20)
        private String code;

        @NotBlank(message = "카테고리명은 필수입니다")
        @Size(max = 50)
        private String name;

        private Long parentId;

        @NotNull(message = "정렬순서는 필수입니다")
        private Integer orderNum;

        @Size(max = 500)
        private String description;
    }

    @Data
    public static class CategoryUpdateRequest {
        @NotBlank(message = "카테고리명은 필수입니다")
        @Size(max = 50)
        private String name;

        private Long parentId;

        @NotNull(message = "정렬순서는 필수입니다")
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
}