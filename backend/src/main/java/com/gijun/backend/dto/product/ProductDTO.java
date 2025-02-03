package com.gijun.backend.dto.product;

import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.sis.product.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {

    @Data
    public static class ProductCreateRequest {
        @NotBlank(message = "상품코드는 필수입니다")
        @Size(max = 20)
        private String code;

        @NotBlank(message = "상품명은 필수입니다")
        @Size(max = 100)
        private String name;

        @Size(max = 500)
        private String description;

        @NotNull(message = "카테고리는 필수입니다")
        private Long categoryId;

        @NotNull(message = "가격은 필수입니다")
        @Min(0)
        private BigDecimal price;

        @NotNull(message = "원가는 필수입니다")
        @Min(0)
        private BigDecimal costPrice;

        @NotNull(message = "재고는 필수입니다")
        @Min(0)
        private Integer stock;

        private boolean isTaxable = true;  // default value

        @NotNull(message = "최소재고는 필수입니다")
        @Min(0)
        private Integer minStock;

        private Integer maxStock;
    }

    @Data
    public static class ProductUpdateRequest {
        @Size(max = 100)
        private String name;

        @Size(max = 500)
        private String description;

        private Long categoryId;

        @Min(0)
        private BigDecimal price;

        @Min(0)
        private BigDecimal costPrice;

        @Min(0)
        private Integer minStock;

        private Integer maxStock;
    }

    @Data
    @Builder
    public static class ProductResponse {
        private Long id;
        private String code;
        private String name;
        private String description;
        private CategoryDTO.CategoryResponse category;
        private BigDecimal price;
        private BigDecimal costPrice;
        private Integer stock;
        private Integer minStock;
        private Integer maxStock;
        private ProductStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ProductResponse from(Product product) {
            return ProductResponse.builder()
                    .id(product.getId())
                    .code(product.getCode())
                    .name(product.getName())
                    .description(product.getDescription())
                    .category(CategoryDTO.CategoryResponse.from(product.getCategory()))
                    .price(product.getPrice())
                    .costPrice(product.getCostPrice())
                    .stock(product.getStock())
                    .minStock(product.getMinStock())
                    .maxStock(product.getMaxStock())
                    .status(product.getStatus())
                    .createdAt(product.getCreatedAt())
                    .updatedAt(product.getUpdatedAt())
                    .build();
        }
    }

    @Data
    public static class StockUpdateRequest {
        @NotNull(message = "수량은 필수입니다")
        private Integer quantity;
        private String note;
    }

    @Data
    public static class ProductSearchRequest {
        private Long categoryId;
        private String keyword;
    }
}