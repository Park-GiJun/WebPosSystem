package com.gijun.backend.controller.receipt;

import com.gijun.backend.dto.CommonRequestDto;
import com.gijun.backend.dto.CommonResponseDto;
import com.gijun.backend.dto.receipt.ReceiptDTO;
import com.gijun.backend.service.pos.receipt.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipts")
@Tag(name = "Receipt", description = "영수증 관리 API")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping
    @Operation(summary = "영수증 생성")
    public ResponseEntity<CommonResponseDto<ReceiptDTO.ReceiptResponse>> createReceipt(
            Authentication authentication,
            @Valid @RequestBody CommonRequestDto<ReceiptDTO.ReceiptRequest> request) {
        ReceiptDTO.ReceiptResponse response =
                receiptService.createReceipt(authentication.getName(), request.getData());
        return ResponseEntity.ok(CommonResponseDto.success(response, "Receipt created successfully"));
    }

    @GetMapping("/{receiptNumber}")
    @Operation(summary = "영수증 조회")
    public ResponseEntity<CommonResponseDto<ReceiptDTO.ReceiptResponse>> getReceipt(
            @PathVariable String receiptNumber) {
        ReceiptDTO.ReceiptResponse response = receiptService.getReceipt(receiptNumber);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }

    @PostMapping("/{receiptNumber}/cancel")
    @Operation(summary = "영수증 취소")
    public ResponseEntity<CommonResponseDto<ReceiptDTO.ReceiptResponse>> cancelReceipt(
            Authentication authentication,
            @PathVariable String receiptNumber) {
        ReceiptDTO.ReceiptResponse response =
                receiptService.cancelReceipt(authentication.getName(), receiptNumber);
        return ResponseEntity.ok(CommonResponseDto.success(response, "Receipt canceled successfully"));
    }

    @GetMapping("/user")
    @Operation(summary = "사용자별 영수증 목록")
    public ResponseEntity<CommonResponseDto<Page<ReceiptDTO.ReceiptResponse>>> getUserReceipts(
            Authentication authentication,
            @PageableDefault(sort = "saleDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReceiptDTO.ReceiptResponse> response =
                receiptService.getUserReceipts(authentication.getName(), pageable);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }
}