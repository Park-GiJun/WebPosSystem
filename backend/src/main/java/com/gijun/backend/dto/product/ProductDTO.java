package com.gijun.backend.dto.product;

import com.gijun.backend.domain.sis.category.ProductStatus;
import com.gijun.backend.domain.sis.category.ProductType;
import com.gijun.backend.domain.sis.category.ProductUnit;
import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.sis.product.SetProductItem;
import com.gijun.backend.domain.sis.recipe.RecipeComponent;
import com.gijun.backend.dto.category.CategoryDTO;
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

public class ProductDTO {

    @Data
    public static class ProductCreateRequest {
        @NotBlank(message = "Product code is required")
        @Size(max = 20)
        private String code;

        @NotBlank(message = "Product name is required")
        @Size(max = 100)
        private String name;

        @Size(max = 500)
        private String description;

        @NotNull(message = "Category is required")
        private Long categoryId;

        @NotNull(message = "Price is required")
        @Min(0)
        private BigDecimal price;

        @NotNull(message = "Cost price is required")
        @Min(0)
        private BigDecimal costPrice;

        @NotNull(message = "Stock is required")
        @Min(0)
        private Integer stock;

        @NotNull(message = "Minimum stock is required")
        @Min(0)
        private Integer minStock;

        private Integer maxStock;

        private Boolean isTaxable = true;

        private ProductUnit unit = ProductUnit.EA;

        @NotNull(message = "Product type is required")
        private ProductType productType;

        private String barcode;

        private String imageUrl;

        private List<RecipeComponentRequest> recipeComponents;

        private List<SetProductItemRequest> setProductItems;
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

        private String barcode;

        private String imageUrl;

        // For recipe products
        private List<RecipeComponentRequest> recipeComponents;

        // For set products
        private List<SetProductItemRequest> setProductItems;
    }

    @Data
    public static class RecipeComponentRequest {
        @NotNull(message = "Ingredient ID is required")
        private Long ingredientId;

        @NotNull(message = "Quantity is required")
        @Min(1)
        private Integer quantity;

        private ProductUnit unit;
    }

    @Data
    public static class SetProductItemRequest {
        @NotNull(message = "Item ID is required")
        private Long itemId;

        @NotNull(message = "Quantity is required")
        @Min(1)
        private Integer quantity;
    }

    @Data
    public static class StockUpdateRequest {
        @NotNull(message = "Quantity is required")
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
        private Boolean isTaxable;
        private ProductUnit unit;
        private ProductType productType;
        private String barcode;
        private String imageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // For recipe products
        private List<RecipeComponentResponse> recipeComponents;

        // For set products
        private List<SetProductItemResponse> setProductItems;

        public static ProductResponse from(Product product) {
            List<RecipeComponentResponse> recipeComponents = product.getRecipeComponents() != null
                    ? product.getRecipeComponents().stream()
                    .map(RecipeComponentResponse::from)
                    .collect(Collectors.toList())
                    : new ArrayList<>();

            List<SetProductItemResponse> setProductItems = product.getSetProductItems() != null
                    ? product.getSetProductItems().stream()
                    .map(SetProductItemResponse::from)
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
                    .isTaxable(product.isTaxable())
                    .unit(product.getUnit())
                    .productType(product.getProductType())
                    .barcode(product.getBarcode())
                    .imageUrl(product.getImageUrl())
                    .createdAt(product.getCreatedAt())
                    .updatedAt(product.getUpdatedAt())
                    .recipeComponents(recipeComponents)
                    .setProductItems(setProductItems)
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
        private ProductUnit unit;
        private String imageUrl;

        public static ProductSimpleResponse from(Product product) {
            return ProductSimpleResponse.builder()
                    .id(product.getId())
                    .code(product.getCode())
                    .name(product.getName())
                    .price(product.getPrice())
                    .productType(product.getProductType())
                    .unit(product.getUnit())
                    .imageUrl(product.getImageUrl())
                    .build();
        }
    }

    @Data
    @Builder
    public static class RecipeComponentResponse {
        private Long id;
        private ProductSimpleResponse ingredient;
        private Integer quantity;
        private ProductUnit unit;

        public static RecipeComponentResponse from(RecipeComponent component) {
            return RecipeComponentResponse.builder()
                    .id(component.getId())
                    .ingredient(ProductSimpleResponse.from(component.getIngredient()))
                    .quantity(component.getQuantity())
                    .unit(component.getUnit())
                    .build();
        }
    }

    @Data
    @Builder
    public static class SetProductItemResponse {
        private Long id;
        private ProductSimpleResponse item;
        private Integer quantity;

        public static SetProductItemResponse from(SetProductItem setProductItem) {
            return SetProductItemResponse.builder()
                    .id(setProductItem.getId())
                    .item(ProductSimpleResponse.from(setProductItem.getItem()))
                    .quantity(setProductItem.getQuantity())
                    .build();
        }
    }
}