package com.gijun.backend.service.sis.recipe;

import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.sis.recipe.Recipe;
import com.gijun.backend.dto.recipe.RecipeDto;
import com.gijun.backend.repository.sis.product.ProductRepository;
import com.gijun.backend.repository.sis.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Recipe createRecipe(RecipeDto.CreateRequest request) {
        Recipe recipe = Recipe.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .costPrice(request.getCostPrice())
                .build();

        for (RecipeDto.IngredientRequest ingredient : request.getIngredients()) {
            Product product = productRepository.findById(ingredient.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + ingredient.getProductId()));
            recipe.addIngredient(product, ingredient.getQuantity());
        }

        return recipeRepository.save(recipe);
    }

    @Transactional
    public Recipe updateRecipe(Long id, RecipeDto.UpdateRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + id));

        recipe.updateInfo(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCostPrice(),
                request.getStatus()
        );

        recipe.getIngredients().clear();
        for (RecipeDto.IngredientRequest ingredient : request.getIngredients()) {
            Product product = productRepository.findById(ingredient.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + ingredient.getProductId()));
            recipe.addIngredient(product, ingredient.getQuantity());
        }

        return recipe;
    }

    @Transactional
    public void deleteRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + id));
        recipe.delete();
    }

    public Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + id));
    }

    public Page<Recipe> getRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    public List<Recipe> searchRecipes(String keyword) {
        return recipeRepository.findByNameContainingOrCodeContaining(keyword, keyword);
    }
}