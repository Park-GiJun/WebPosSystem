package com.gijun.backend.dto.receipt;

import com.gijun.backend.domain.pos.Receipt.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReceiptDTO {

    @Data
    public static class ReceiptRequest {
        @NotNull
        private List<ReceiptItemRequest> items;
        private List<ReceiptPaymentRequest> payments;
    }

    @Data
    public static class ReceiptItemRequest {
        @NotNull
        private Long productId;
        @NotNull
        @Min(1)
        private Integer quantity;
        private BigDecimal unitPrice;
    }

    @Data
    public static class ReceiptPaymentRequest {
        @NotNull
        private PaymentType paymentType;
        @NotNull
        private BigDecimal paymentAmount;
        private String approvalNumber;
        private String cardInfo;
    }

    @Data
    @Builder
    public static class ReceiptResponse {
        private String receiptNumber;
        private String username;
        private LocalDateTime saleDate;
        private BigDecimal totalAmount;
        private BigDecimal taxAmount;
        private BigDecimal discountAmount;
        private ReceiptStatus status;
        private List<ReceiptItemResponse> items;
        private List<ReceiptPaymentResponse> payments;

        public static ReceiptResponse from(Receipt receipt) {
            return ReceiptResponse.builder()
                    .receiptNumber(receipt.getReceiptNumber())
                    .username(receipt.getUser().getUsername())
                    .saleDate(receipt.getSaleDate())
                    .totalAmount(receipt.getTotalAmount())
                    .taxAmount(receipt.getTaxAmount())
                    .discountAmount(receipt.getDiscountAmount())
                    .status(receipt.getStatus())
                    .items(receipt.getItems().stream().map(ReceiptItemResponse::from).collect(Collectors.toList()))
                    .payments(receipt.getPayments().stream().map(ReceiptPaymentResponse::from).collect(Collectors.toList()))
                    .build();
        }
    }

    @Data
    @Builder
    public static class ReceiptItemResponse {
        private Integer lineNo;
        private String productName;
        private String productCode;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineAmount;
        private BigDecimal taxAmount;

        public static ReceiptItemResponse from(ReceiptItem item) {
            return ReceiptItemResponse.builder()
                    .lineNo(item.getLineNo())
                    .productName(item.getProduct().getName())
                    .productCode(item.getProduct().getCode())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .lineAmount(item.getLineAmount())
                    .taxAmount(item.getTaxAmount())
                    .build();
        }
    }

    @Data
    @Builder
    public static class ReceiptPaymentResponse {
        private PaymentType paymentType;
        private BigDecimal paymentAmount;
        private LocalDateTime paymentTime;
        private String approvalNumber;

        public static ReceiptPaymentResponse from(ReceiptPayment payment) {
            return ReceiptPaymentResponse.builder()
                    .paymentType(payment.getPaymentType())
                    .paymentAmount(payment.getPaymentAmount())
                    .paymentTime(payment.getPaymentTime())
                    .approvalNumber(payment.getApprovalNumber())
                    .build();
        }
    }
}