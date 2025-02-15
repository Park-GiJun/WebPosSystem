package com.gijun.backend.service.sis.recipe;

import com.gijun.backend.dto.ErrorCode;
import com.gijun.backend.dto.recipe.RecipeDto;
import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.sis.recipe.Recipe;
import com.gijun.backend.repository.sis.product.ProductRepository;
import com.gijun.backend.repository.sis.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Recipe createRecipe(RecipeDto.CreateRequest request) {
        if (recipeRepository.existsByCode(request.getCode())) {
            throw new RuntimeException(ErrorCode.DUPLICATE_CODE.getMessage());
        }

        Recipe recipe = Recipe.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .costPrice(request.getCostPrice())
                .build();

        // 재료 추가
        request.getIngredients().forEach(ingredient -> {
            Product product = productRepository.findById(ingredient.getProductId())
                    .orElseThrow(() -> new RuntimeException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
            recipe.addIngredient(product, ingredient.getQuantity());
        });

        return recipeRepository.save(recipe);
    }

    @Transactional
    public Recipe updateRecipe(Long id, RecipeDto.UpdateRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ErrorCode.RECIPE_NOT_FOUND.getMessage()));

        recipe.updateInfo(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCostPrice(),
                request.getStatus()
        );

        // 재료 업데이트
        recipe.getIngredients().clear();
        request.getIngredients().forEach(ingredient -> {
            Product product = productRepository.findById(ingredient.getProductId())
                    .orElseThrow(() -> new RuntimeException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
            recipe.addIngredient(product, ingredient.getQuantity());
        });

        return recipe;
    }

    @Transactional(readOnly = true)
    public Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ErrorCode.RECIPE_NOT_FOUND.getMessage()));
    }

    @Transactional
    public void deleteRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ErrorCode.RECIPE_NOT_FOUND.getMessage()));
        recipe.delete();
    }
}