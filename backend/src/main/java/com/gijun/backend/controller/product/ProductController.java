package com.gijun.backend.controller.product;

import com.gijun.backend.dto.CommonRequestDto;
import com.gijun.backend.dto.CommonResponseDto;
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

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "상품 관리 API")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "상품 등록")
    public ResponseEntity<CommonResponseDto<ProductDTO.ProductResponse>> createProduct(
            @Valid @RequestBody CommonRequestDto<ProductDTO.ProductCreateRequest> request) {
        ProductDTO.ProductResponse response = productService.createProduct(request.getData());
        return ResponseEntity.ok(CommonResponseDto.success(response, "상품이 등록되었습니다"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "상품 상세 조회")
    public ResponseEntity<CommonResponseDto<ProductDTO.ProductResponse>> getProduct(@PathVariable Long id) {
        ProductDTO.ProductResponse response = productService.getProduct(id);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }

    @GetMapping
    @Operation(summary = "상품 목록 조회")
    public ResponseEntity<CommonResponseDto<Page<ProductDTO.ProductResponse>>> getProducts(
            @ModelAttribute ProductDTO.ProductSearchRequest searchRequest,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<ProductDTO.ProductResponse> response = productService.getProducts(searchRequest, pageable);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "상품 정보 수정")
    public ResponseEntity<CommonResponseDto<ProductDTO.ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CommonRequestDto<ProductDTO.ProductUpdateRequest> request) {
        ProductDTO.ProductResponse response = productService.updateProduct(id, request.getData());
        return ResponseEntity.ok(CommonResponseDto.success(response, "상품이 수정되었습니다"));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "상품 재고 수정")
    public ResponseEntity<CommonResponseDto<ProductDTO.ProductResponse>> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody CommonRequestDto<ProductDTO.StockUpdateRequest> request) {
        ProductDTO.ProductResponse response = productService.updateStock(id, request.getData());
        return ResponseEntity.ok(CommonResponseDto.success(response, "재고가 수정되었습니다"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "상품 삭제")
    public ResponseEntity<CommonResponseDto<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(CommonResponseDto.success(null, "상품이 삭제되었습니다"));
    }
}
