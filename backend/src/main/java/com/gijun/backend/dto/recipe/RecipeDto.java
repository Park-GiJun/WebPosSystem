package com.gijun.backend.dto.recipe;

import com.gijun.backend.domain.sis.product.ProductStatus;
import com.gijun.backend.domain.sis.recipe.Recipe;
import com.gijun.backend.domain.sis.recipe.RecipeIngredient;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RecipeDto {

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private String code;
        private String name;
        private String description;
        private BigDecimal price;
        private BigDecimal costPrice;
        private ProductStatus status;
        private List<IngredientResponse> ingredients;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(Recipe recipe) {
            Response dto = new Response();
            dto.id = recipe.getId();
            dto.code = recipe.getCode();
            dto.name = recipe.getName();
            dto.description = recipe.getDescription();
            dto.price = recipe.getPrice();
            dto.costPrice = recipe.getCostPrice();
            dto.status = recipe.getStatus();
            dto.ingredients = recipe.getIngredients().stream()
                    .map(IngredientResponse::from)
                    .toList();
            dto.createdAt = recipe.getCreatedAt();
            dto.updatedAt = recipe.getUpdatedAt();
            return dto;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class IngredientResponse {
        private Long id;
        private Long productId;
        private String productName;
        private int quantity;

        public static IngredientResponse from(RecipeIngredient ingredient) {
            IngredientResponse dto = new IngredientResponse();
            dto.id = ingredient.getId();
            dto.productId = ingredient.getProduct().getId();
            dto.productName = ingredient.getProduct().getName();
            dto.quantity = ingredient.getQuantity();
            return dto;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        private String code;
        private String name;
        private String description;
        private BigDecimal price;
        private BigDecimal costPrice;
        private List<IngredientRequest> ingredients;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private BigDecimal costPrice;
        private ProductStatus status;
        private List<IngredientRequest> ingredients;
    }

    @Getter
    @NoArgsConstructor
    public static class IngredientRequest {
        private Long productId;
        private int quantity;
    }
}