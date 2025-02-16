package com.gijun.backend.controller.recipe;

import com.gijun.backend.domain.sis.recipe.Recipe;
import com.gijun.backend.dto.CommonResponseDto;
import com.gijun.backend.dto.recipe.RecipeDto;
import com.gijun.backend.service.sis.recipe.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "레시피", description = "레시피 관리 API")
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "레시피 등록", description = "새로운 레시피를 등록합니다.")
    @PostMapping
    public ResponseEntity<CommonResponseDto<RecipeDto.Response>> createRecipe(
            @RequestBody RecipeDto.CreateRequest request
    ) {
        Recipe recipe = recipeService.createRecipe(request);
        return ResponseEntity.ok(CommonResponseDto.success(RecipeDto.Response.from(recipe)));
    }

    @Operation(summary = "레시피 수정", description = "기존 레시피를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponseDto<RecipeDto.Response>> updateRecipe(
            @Parameter(description = "레시피 ID") @PathVariable Long id,
            @RequestBody RecipeDto.UpdateRequest request
    ) {
        Recipe recipe = recipeService.updateRecipe(id, request);
        return ResponseEntity.ok(CommonResponseDto.success(RecipeDto.Response.from(recipe)));
    }

    @Operation(summary = "레시피 삭제", description = "레시피를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDto<Void>> deleteRecipe(
            @Parameter(description = "레시피 ID") @PathVariable Long id
    ) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok(CommonResponseDto.success(null));
    }

    @Operation(summary = "레시피 조회", description = "특정 레시피를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDto<RecipeDto.Response>> getRecipe(
            @Parameter(description = "레시피 ID") @PathVariable Long id
    ) {
        Recipe recipe = recipeService.getRecipe(id);
        return ResponseEntity.ok(CommonResponseDto.success(RecipeDto.Response.from(recipe)));
    }

    @Operation(summary = "레시피 목록 조회", description = "레시피 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonResponseDto<Page<RecipeDto.Response>>> getRecipes(
            @Parameter(description = "페이지 정보") Pageable pageable
    ) {
        Page<Recipe> recipes = recipeService.getRecipes(pageable);
        return ResponseEntity.ok(CommonResponseDto.success(
                recipes.map(RecipeDto.Response::from)
        ));
    }

    @Operation(summary = "레시피 검색", description = "레시피를 이름 또는 코드로 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<CommonResponseDto<List<RecipeDto.Response>>> searchRecipes(
            @Parameter(description = "검색어") @RequestParam String keyword
    ) {
        List<Recipe> recipes = recipeService.searchRecipes(keyword);
        return ResponseEntity.ok(CommonResponseDto.success(
                recipes.stream()
                        .map(RecipeDto.Response::from)
                        .toList()
        ));
    }
}