package com.gijun.backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.repository.sis.product.ProductRepository;
import com.gijun.backend.service.KafkaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventHandler {

    private final KafkaService kafkaService;
    private final ProductRepository productRepository;

    @KafkaListener(topics = "stock-events", groupId = "pos-group")
    public void handleStockEvent(String message) {
        try {
            // 재고 이벤트 처리 로직
            Map<String, Object> event = new ObjectMapper().readValue(message, Map.class);

            String eventType = (String) event.get("type");
            switch (eventType) {
                case "SALE":
                    handleSaleEvent(event);
                    break;
                case "RETURN":
                    handleReturnEvent(event);
                    break;
                case "ADJUSTMENT":
                    handleAdjustmentEvent(event);
                    break;
            }

        } catch (Exception e) {
            log.error("Failed to process stock event: {}", message, e);
        }
    }

    @KafkaListener(topics = "product-events", groupId = "pos-group")
    public void handleProductEvent(String message) {
        try {
            Map<String, Object> event = new ObjectMapper().readValue(message, Map.class);

            String eventType = (String) event.get("type");
            switch (eventType) {
                case "product.created":
                    handleProductCreated(event);
                    break;
                case "product.updated":
                    handleProductUpdated(event);
                    break;
                case "product.deleted":
                    handleProductDeleted(event);
                    break;
            }

        } catch (Exception e) {
            log.error("Failed to process product event: {}", message, e);
        }
    }

    private void handleSaleEvent(Map<String, Object> event) {
        // 판매에 의한 재고 감소 처리
        Long productId = Long.valueOf(event.get("productId").toString());
        Integer quantity = (Integer) event.get("quantity");

        // 재고 상태 체크 및 알림 발송 로직
        checkAndNotifyStockStatus(productId);
    }

    private void handleReturnEvent(Map<String, Object> event) {
        // 반품에 의한 재고 증가 처리
        Long productId = Long.valueOf(event.get("productId").toString());
        Integer quantity = (Integer) event.get("quantity");
    }

    private void handleAdjustmentEvent(Map<String, Object> event) {
        // 재고 조정 처리
        Long productId = Long.valueOf(event.get("productId").toString());
        Integer quantity = (Integer) event.get("quantity");
        String note = (String) event.get("note");
    }

    private void handleProductCreated(Map<String, Object> event) {
        // 신규 상품 생성 처리
        Product product = (Product) event.get("product");
        // POS 시스템에 상품 정보 동기화
    }

    private void handleProductUpdated(Map<String, Object> event) {
        // 상품 정보 수정 처리
        Product product = (Product) event.get("product");
        // POS 시스템의 상품 정보 업데이트
    }

    private void handleProductDeleted(Map<String, Object> event) {
        // 상품 삭제 처리
        Long productId = Long.valueOf(event.get("productId").toString());
        // POS 시스템에서 해당 상품 비활성화
    }

    private void checkAndNotifyStockStatus(Long productId) {
        // 재고 상태 체크 및 알림 발송
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (product.getStock() <= product.getMinStock()) {
            // 재고 부족 알림 발송
            Map<String, Object> notification = Map.of(
                    "type", "STOCK_ALERT",
                    "productId", productId,
                    "productName", product.getName(),
                    "currentStock", product.getStock(),
                    "minStock", product.getMinStock()
            );

            kafkaService.sendMessage("notifications", "stock.alert", notification);
        }
    }
}