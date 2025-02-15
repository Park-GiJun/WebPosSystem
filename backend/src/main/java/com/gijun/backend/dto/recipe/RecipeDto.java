package com.gijun.backend.dto.recipe;

import com.gijun.backend.domain.sis.product.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class RecipeDto {

    @Getter
    @Setter
    public static class CreateRequest {
        @NotBlank(message = "레시피 코드는 필수입니다")
        @Size(max = 20, message = "레시피 코드는 20자를 초과할 수 없습니다")
        private String code;

        @NotBlank(message = "레시피명은 필수입니다")
        @Size(max = 100, message = "레시피명은 100자를 초과할 수 없습니다")
        private String name;

        @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
        private String description;

        @NotNull(message = "판매가는 필수입니다")
        private BigDecimal price;

        @NotNull(message = "원가는 필수입니다")
        private BigDecimal costPrice;

        @NotNull(message = "재료 목록은 필수입니다")
        private List<IngredientRequest> ingredients;
    }

    @Getter
    @Setter
    public static class UpdateRequest {
        @NotBlank(message = "레시피명은 필수입니다")
        @Size(max = 100, message = "레시피명은 100자를 초과할 수 없습니다")
        private String name;

        @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
        private String description;

        @NotNull(message = "판매가는 필수입니다")
        private BigDecimal price;

        @NotNull(message = "원가는 필수입니다")
        private BigDecimal costPrice;

        @NotNull(message = "상태는 필수입니다")
        private ProductStatus status;

        @NotNull(message = "재료 목록은 필수입니다")
        private List<IngredientRequest> ingredients;
    }

    @Getter
    @Setter
    public static class IngredientRequest {
        @NotNull(message = "상품 ID는 필수입니다")
        private Long productId;

        @NotNull(message = "수량은 필수입니다")
        private Integer quantity;
    }

    @Getter
    @Setter
    public static class Response {
        private Long id;
        private String code;
        private String name;
        private String description;
        private BigDecimal price;
        private BigDecimal costPrice;
        private ProductStatus status;
        private List<IngredientResponse> ingredients;
        private String createdAt;
        private String updatedAt;

        // 생성자 추가 예정
    }

    @Getter
    @Setter
    public static class IngredientResponse {
        private Long productId;
        private String productCode;
        private String productName;
        private Integer quantity;

        // 생성자 추가 예정
    }

    @Getter
    @Setter
    public static class SearchRequest {
        private String keyword;
        private ProductStatus status;
        private Integer page = 0;
        private Integer size = 10;
    }

    @Getter
    @Setter
    public static class SearchResponse {
        private List<Response> content;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
    }
}