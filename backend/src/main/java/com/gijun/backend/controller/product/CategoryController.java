package com.gijun.backend.controller.product;

import com.gijun.backend.dto.CommonRequestDto;
import com.gijun.backend.dto.CommonResponseDto;
import com.gijun.backend.dto.product.CategoryDTO;
import com.gijun.backend.service.sis.product.CategoryService;
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
@Tag(name = "Category", description = "카테고리 관리 API")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "카테고리 등록")
    public ResponseEntity<CommonResponseDto<CategoryDTO.CategoryResponse>> createCategory(
            @Valid @RequestBody CommonRequestDto<CategoryDTO.CategoryCreateRequest> request) {
        CategoryDTO.CategoryResponse response = categoryService.createCategory(request.getData());
        return ResponseEntity.ok(CommonResponseDto.success(response, "카테고리가 등록되었습니다"));
    }

    @GetMapping
    @Operation(summary = "카테고리 목록 조회")
    public ResponseEntity<CommonResponseDto<Page<CategoryDTO.CategoryResponse>>> getCategories(
            @PageableDefault(size = 20, sort = "orderNum") Pageable pageable) {
        Page<CategoryDTO.CategoryResponse> response = categoryService.getCategories(pageable);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }

    @GetMapping("/{id}/subcategories")
    @Operation(summary = "하위 카테고리 목록 조회")
    public ResponseEntity<CommonResponseDto<List<CategoryDTO.CategoryResponse>>> getSubcategories(@PathVariable Long id) {
        List<CategoryDTO.CategoryResponse> response = categoryService.getSubcategories(id);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "카테고리 수정")
    public ResponseEntity<CommonResponseDto<CategoryDTO.CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CommonRequestDto<CategoryDTO.CategoryUpdateRequest> request) {
        CategoryDTO.CategoryResponse response = categoryService.updateCategory(id, request.getData());
        return ResponseEntity.ok(CommonResponseDto.success(response, "카테고리가 수정되었습니다"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "카테고리 삭제")
    public ResponseEntity<CommonResponseDto<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(CommonResponseDto.success(null, "카테고리가 삭제되었습니다"));
    }
}