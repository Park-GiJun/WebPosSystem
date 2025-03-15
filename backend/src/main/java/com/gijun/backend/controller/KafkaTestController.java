package com.gijun.backend.controller;

import com.gijun.backend.dto.CommonResponseDto;
import com.gijun.backend.service.KafkaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test/kafka")
@Tag(name = "Kafka Test", description = "Kafka 연결 테스트 API")
@RequiredArgsConstructor
@Slf4j
public class KafkaTestController {

    private final KafkaService kafkaService;

    @Operation(summary = "Kafka 메시지 전송 테스트", description = "지정된 토픽으로 테스트 메시지를 전송합니다.")
    @PostMapping("/send")
    public ResponseEntity<CommonResponseDto<Map<String, Object>>> sendMessage(
            @RequestParam String topic,
            @RequestParam String eventType,
            @RequestBody(required = false) Map<String, Object> payload
    ) {
        try {
            if (payload == null) {
                payload = new HashMap<>();
                payload.put("message", "Test message");
                payload.put("timestamp", System.currentTimeMillis());
            }

            kafkaService.sendMessage(topic, eventType, payload);

            Map<String, Object> response = new HashMap<>();
            response.put("topic", topic);
            response.put("eventType", eventType);
            response.put("payload", payload);
            response.put("status", "sent");

            return ResponseEntity.ok(CommonResponseDto.success(response, "메시지 전송 성공"));
        } catch (Exception e) {
            log.error("Kafka 메시지 전송 실패", e);
            return ResponseEntity.ok(CommonResponseDto.error("KAFKA_ERROR", "메시지 전송 실패: " + e.getMessage()));
        }
    }

    @Operation(summary = "시스템 알림 전송 테스트", description = "Slack으로 시스템 알림을 전송합니다.")
    @PostMapping("/notification")
    public ResponseEntity<CommonResponseDto<Map<String, Object>>> sendNotification(
            @RequestParam String status,
            @RequestParam String message
    ) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("status", status);
            notification.put("message", message);
            notification.put("timestamp", System.currentTimeMillis());

            kafkaService.sendNotification(notification);

            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            response.put("message", message);
            response.put("sent", true);

            return ResponseEntity.ok(CommonResponseDto.success(response, "알림 전송 성공"));
        } catch (Exception e) {
            log.error("알림 전송 실패", e);
            return ResponseEntity.ok(CommonResponseDto.error("NOTIFICATION_ERROR", "알림 전송 실패: " + e.getMessage()));
        }
    }

    @Operation(summary = "재고 이벤트 전송 테스트", description = "재고 관련 이벤트를 전송합니다.")
    @PostMapping("/stock-event")
    public ResponseEntity<CommonResponseDto<Map<String, Object>>> sendStockEvent(
            @RequestParam Long productId,
            @RequestParam String type,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String note
    ) {
        try {
            Map<String, Object> stockEvent = new HashMap<>();
            stockEvent.put("productId", productId);
            stockEvent.put("type", type);
            stockEvent.put("quantity", quantity);
            stockEvent.put("note", note != null ? note : "테스트 이벤트");
            stockEvent.put("timestamp", System.currentTimeMillis());

            kafkaService.sendStockEvent(stockEvent);

            return ResponseEntity.ok(CommonResponseDto.success(stockEvent, "재고 이벤트 전송 성공"));
        } catch (Exception e) {
            log.error("재고 이벤트 전송 실패", e);
            return ResponseEntity.ok(CommonResponseDto.error("STOCK_EVENT_ERROR", "재고 이벤트 전송 실패: " + e.getMessage()));
        }
    }

    @Operation(summary = "전체 카프카 기능 테스트", description = "모든 카프카 토픽에 테스트 메시지를 전송합니다.")
    @PostMapping("/test-all")
    public ResponseEntity<CommonResponseDto<Map<String, Object>>> testAllTopics() {
        Map<String, Object> results = new HashMap<>();
        Map<String, Object> testPayload = new HashMap<>();
        testPayload.put("message", "Test message");
        testPayload.put("timestamp", System.currentTimeMillis());

        try {
            // 인증 이벤트 테스트
            kafkaService.sendAuthenticationEvent("Test authentication event");
            results.put("authentication", "success");

            // API 요청 테스트
            kafkaService.sendApiRequest("Test API request");
            results.put("api-requests", "success");

            // 주문 이벤트 테스트
            kafkaService.sendOrderEvent(testPayload);
            results.put("orders", "success");

            // 결제 이벤트 테스트
            kafkaService.sendPaymentEvent(testPayload);
            results.put("payments", "success");

            // 재고 이벤트 테스트
            kafkaService.sendStockEvent(testPayload);
            results.put("stock-events", "success");

            // 알림 이벤트 테스트
            kafkaService.sendNotification(testPayload);
            results.put("notifications", "success");

            return ResponseEntity.ok(CommonResponseDto.success(results, "모든 카프카 토픽 테스트 성공"));
        } catch (Exception e) {
            log.error("카프카 토픽 테스트 실패", e);
            results.put("error", e.getMessage());
            return ResponseEntity.ok(CommonResponseDto.error("KAFKA_TEST_ERROR", "카프카 토픽 테스트 실패: " + e.getMessage()));
        }
    }
}