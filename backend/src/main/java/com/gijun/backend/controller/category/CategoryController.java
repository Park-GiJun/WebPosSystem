package com.gijun.backend.controller.category;

import com.gijun.backend.dto.CommonRequestDto;
import com.gijun.backend.dto.CommonResponseDto;
import com.gijun.backend.dto.category.CategoryDTO;
import com.gijun.backend.service.sis.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "Category Management API")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create Category")
    public ResponseEntity<CommonResponseDto<CategoryDTO.CategoryResponse>> createCategory(
            @Valid @RequestBody CommonRequestDto<CategoryDTO.CategoryCreateRequest> request) {
        CategoryDTO.CategoryResponse response = categoryService.createCategory(request.getData());
        return ResponseEntity.ok(CommonResponseDto.success(response, "Category created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get Category List")
    public ResponseEntity<CommonResponseDto<Page<CategoryDTO.CategoryResponse>>> getCategories(
            @PageableDefault(size = 20, sort = "orderNum") Pageable pageable) {
        Page<CategoryDTO.CategoryResponse> response = categoryService.getCategories(pageable);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }

    @GetMapping("/{id}/subcategories")
    @Operation(summary = "Get Subcategory List")
    public ResponseEntity<CommonResponseDto<List<CategoryDTO.CategoryResponse>>> getSubcategories(@PathVariable Long id) {
        List<CategoryDTO.CategoryResponse> response = categoryService.getSubcategories(id);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Category")
    public ResponseEntity<CommonResponseDto<CategoryDTO.CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CommonRequestDto<CategoryDTO.CategoryUpdateRequest> request) {
        CategoryDTO.CategoryResponse response = categoryService.updateCategory(id, request.getData());
        return ResponseEntity.ok(CommonResponseDto.success(response, "Category updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Category")
    public ResponseEntity<CommonResponseDto<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(CommonResponseDto.success(null, "Category deleted successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search Categories")
    public ResponseEntity<CommonResponseDto<List<CategoryDTO.CategoryResponse>>> searchCategories(
            @RequestParam String keyword) {
        List<CategoryDTO.CategoryResponse> response = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }
}