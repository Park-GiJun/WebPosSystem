package com.gijun.backend.controller.product;

import com.gijun.backend.domain.sis.category.ProductType;
import com.gijun.backend.dto.CommonRequestDto;
import com.gijun.backend.dto.CommonResponseDto;
import com.gijun.backend.dto.PageResponseDto;
import com.gijun.backend.dto.product.ProductDTO;
import com.gijun.backend.service.sis.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "Product Management API")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create Product")
    public ResponseEntity<CommonResponseDto<ProductDTO.ProductResponse>> createProduct(
            @Valid @RequestBody CommonRequestDto<ProductDTO.ProductCreateRequest> request) {
        ProductDTO.ProductResponse response = productService.createProduct(request.getData());
        return ResponseEntity.ok(CommonResponseDto.success(response, "Product created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Product Detail")
    public ResponseEntity<CommonResponseDto<ProductDTO.ProductResponse>> getProduct(@PathVariable Long id) {
        ProductDTO.ProductResponse response = productService.getProduct(id);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }

    @GetMapping
    @Operation(summary = "Get Product List")
    public ResponseEntity<CommonResponseDto<Page<ProductDTO.ProductResponse>>> getProducts(
            @ModelAttribute ProductDTO.ProductSearchRequest searchRequest,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<ProductDTO.ProductResponse> page = productService.getProducts(searchRequest, pageable);
        return ResponseEntity.ok(CommonResponseDto.success(page));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Product")
    public ResponseEntity<CommonResponseDto<ProductDTO.ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CommonRequestDto<ProductDTO.ProductUpdateRequest> request) {
        ProductDTO.ProductResponse response = productService.updateProduct(id, request.getData());
        return ResponseEntity.ok(CommonResponseDto.success(response, "Product updated successfully"));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update Product Stock")
    public ResponseEntity<CommonResponseDto<ProductDTO.ProductResponse>> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody CommonRequestDto<ProductDTO.StockUpdateRequest> request) {
        ProductDTO.ProductResponse response = productService.updateStock(id, request.getData());
        return ResponseEntity.ok(CommonResponseDto.success(response, "Stock updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Product")
    public ResponseEntity<CommonResponseDto<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(CommonResponseDto.success(null, "Product deleted successfully"));
    }

    @GetMapping("/types/{type}")
    @Operation(summary = "Get Products by Type")
    public ResponseEntity<CommonResponseDto<List<ProductDTO.ProductResponse>>> getProductsByType(
            @PathVariable ProductType type) {
        List<ProductDTO.ProductResponse> responses = productService.getProductsByType(type);
        return ResponseEntity.ok(CommonResponseDto.success(responses));
    }

    @GetMapping("/raw-materials")
    @Operation(summary = "Get Raw Materials")
    public ResponseEntity<CommonResponseDto<List<ProductDTO.ProductResponse>>> getRawMaterials() {
        List<ProductDTO.ProductResponse> responses = productService.getProductsByType(ProductType.RAW_MATERIAL);
        return ResponseEntity.ok(CommonResponseDto.success(responses));
    }

    @GetMapping("/recipe-products")
    @Operation(summary = "Get Recipe Products")
    public ResponseEntity<CommonResponseDto<List<ProductDTO.ProductResponse>>> getRecipeProducts() {
        List<ProductDTO.ProductResponse> responses = productService.getProductsByType(ProductType.RECIPE_PRODUCT);
        return ResponseEntity.ok(CommonResponseDto.success(responses));
    }

    @GetMapping("/set-products")
    @Operation(summary = "Get Set Products")
    public ResponseEntity<CommonResponseDto<List<ProductDTO.ProductResponse>>> getSetProducts() {
        List<ProductDTO.ProductResponse> responses = productService.getProductsByType(ProductType.SET_PRODUCT);
        return ResponseEntity.ok(CommonResponseDto.success(responses));
    }
}