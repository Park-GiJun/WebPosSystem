package com.gijun.backend.dto.product;

import com.gijun.backend.domain.sis.product.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        @NotNull(message = "최소재고는 필수입니다")
        @Min(0)
        private Integer minStock;

        private Integer maxStock;

        private boolean isTaxable = true;

        private ProductUnit unit = ProductUnit.EA;

        @NotNull(message = "상품유형은 필수입니다")
        private ProductType productType;

        private String barcode;

        private String imageUrl;

        // 세트 상품인 경우 구성 상품 ID 목록
        private List<Long> setItemIds;

        // 레시피 상품인 경우 재료 목록
        private List<RecipeIngredientRequest> recipeIngredients;
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

        private ProductUnit unit;

        private ProductStatus status;

        private ProductType productType;

        private String barcode;

        private String imageUrl;

        // 세트 상품인 경우 구성 상품 ID 목록
        private List<Long> setItemIds;

        // 레시피 상품인 경우 재료 목록
        private List<RecipeIngredientRequest> recipeIngredients;
    }

    @Data
    public static class RecipeIngredientRequest {
        @NotNull(message = "재료 ID는 필수입니다")
        private Long ingredientId;

        @NotNull(message = "수량은 필수입니다")
        @Min(1)
        private Integer quantity;

        private ProductUnit unit;
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
        private ProductUnit unit;
        private ProductType productType;
        private String barcode;
        private String imageUrl;
        private Boolean isTaxable;
        private List<ProductSimpleResponse> setItems;
        private List<RecipeIngredientResponse> recipeIngredients;

        public static ProductResponse from(Product product) {
            List<ProductSimpleResponse> setItems = product.getSetItems() != null
                    ? product.getSetItems().stream()
                    .map(ProductSimpleResponse::from)
                    .collect(Collectors.toList())
                    : new ArrayList<>();

            List<RecipeIngredientResponse> recipeIngredients = product.getRecipeIngredients() != null
                    ? product.getRecipeIngredients().stream()
                    .map(RecipeIngredientResponse::from)
                    .collect(Collectors.toList())
                    : new ArrayList<>();

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
                    .unit(product.getUnit())
                    .productType(product.getProductType())
                    .barcode(product.getBarcode())
                    .imageUrl(product.getImageUrl())
                    .isTaxable(product.isTaxable())
                    .setItems(setItems)
                    .recipeIngredients(recipeIngredients)
                    .build();
        }
    }

    @Data
    @Builder
    public static class ProductSimpleResponse {
        private Long id;
        private String code;
        private String name;
        private BigDecimal price;
        private ProductType productType;
        private String imageUrl;

        public static ProductSimpleResponse from(Product product) {
            return ProductSimpleResponse.builder()
                    .id(product.getId())
                    .code(product.getCode())
                    .name(product.getName())
                    .price(product.getPrice())
                    .productType(product.getProductType())
                    .imageUrl(product.getImageUrl())
                    .build();
        }
    }

    @Data
    @Builder
    public static class RecipeIngredientResponse {
        private Long id;
        private ProductSimpleResponse ingredient;
        private Integer quantity;
        private ProductUnit unit;

        public static RecipeIngredientResponse from(RecipeIngredient recipeIngredient) {
            return RecipeIngredientResponse.builder()
                    .id(recipeIngredient.getId())
                    .ingredient(ProductSimpleResponse.from(recipeIngredient.getIngredient()))
                    .quantity(recipeIngredient.getQuantity())
                    .unit(recipeIngredient.getUnit())
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
        private ProductStatus status;
        private ProductType productType;
    }
}