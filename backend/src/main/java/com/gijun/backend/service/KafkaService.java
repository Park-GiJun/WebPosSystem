package com.gijun.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendAuthenticationEvent(String event) {
        send("authentication", event);
    }

    public void sendApiRequest(String request) {
        send("api-requests", request);
    }

    public void sendOrderEvent(Object order) {
        send("orders", order);
    }

    public void sendPaymentEvent(Object payment) {
        send("payments", payment);
    }

    public void sendStockEvent(Object stockUpdate) {
        send("stock-events", stockUpdate);
    }

    public void sendNotification(Object notification) {
        send("notifications", notification);
    }

    public void sendMessage(String topic, String eventType, Object message) {
        try {
            Map<String, Object> eventMessage = new HashMap<>();
            eventMessage.put("type", eventType);
            eventMessage.put("timestamp", LocalDateTime.now().toString());
            eventMessage.put("data", message);

            String jsonMessage = objectMapper.writeValueAsString(eventMessage);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, jsonMessage);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent message to topic=[{}] type=[{}] offset=[{}]",
                            topic,
                            eventType,
                            result.getRecordMetadata().offset()
                    );
                } else {
                    log.error("Unable to send message to topic=[{}] type=[{}] due to: {}",
                            topic,
                            eventType,
                            ex.getMessage(),
                            ex
                    );
                }
            });
        } catch (Exception e) {
            log.error("Failed to send message to topic=[{}] type=[{}]: {}",
                    topic,
                    eventType,
                    e.getMessage(),
                    e
            );
            throw new RuntimeException("Message sending failed", e);
        }
    }

    private void send(String topic, Object message) {
        try {
            String jsonMessage = (message instanceof String) ?
                    (String) message : objectMapper.writeValueAsString(message);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, jsonMessage);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent message to topic=[{}] with offset=[{}] message=[{}]",
                            topic,
                            result.getRecordMetadata().offset(),
                            jsonMessage
                    );
                } else {
                    log.error("Unable to send message to topic=[{}] message=[{}] due to: {}",
                            topic,
                            jsonMessage,
                            ex.getMessage(),
                            ex
                    );
                }
            });
        } catch (Exception e) {
            log.error("Failed to serialize message for topic=[{}]: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Message serialization failed", e);
        }
    }

    @KafkaListener(topics = "authentication", groupId = "auth-group")
    public void consumeAuthenticationEvents(String event) {
        try {
            log.info("Received authentication event: {}", event);
            AuthenticationEvent authEvent = objectMapper.readValue(event, AuthenticationEvent.class);
            processAuthenticationEvent(authEvent);
        } catch (Exception e) {
            log.error("Failed to process authentication event: {}", event, e);
        }
    }

    @KafkaListener(topics = "api-requests", groupId = "api-group")
    public void consumeApiRequests(String request) {
        try {
            log.info("Received API request: {}", request);
            ApiRequest apiRequest = objectMapper.readValue(request, ApiRequest.class);
            processApiRequest(apiRequest);
        } catch (Exception e) {
            log.error("Failed to process API request: {}", request, e);
        }
    }

    @KafkaListener(topics = "orders", groupId = "order-group")
    public void consumeOrders(String orderJson) {
        try {
            log.info("Received order: {}", orderJson);
            Order order = objectMapper.readValue(orderJson, Order.class);
            processOrder(order);
        } catch (Exception e) {
            log.error("Failed to process order: {}", orderJson, e);
        }
    }

    @KafkaListener(topics = "payments", groupId = "payment-group")
    public void consumePayments(String paymentJson) {
        try {
            log.info("Received payment: {}", paymentJson);
            Payment payment = objectMapper.readValue(paymentJson, Payment.class);
            processPayment(payment);
        } catch (Exception e) {
            log.error("Failed to process payment: {}", paymentJson, e);
        }
    }

    @KafkaListener(topics = "stock-events", groupId = "stock-group")
    public void consumeStockEvents(String stockEventJson) {
        try {
            log.info("Received stock event: {}", stockEventJson);
            StockEvent stockEvent = objectMapper.readValue(stockEventJson, StockEvent.class);
            processStockEvent(stockEvent);
        } catch (Exception e) {
            log.error("Failed to process stock event: {}", stockEventJson, e);
        }
    }

    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void consumeNotifications(String notificationJson) {
        try {
            log.info("Received notification: {}", notificationJson);
            Notification notification = objectMapper.readValue(notificationJson, Notification.class);
            processNotification(notification);
        } catch (Exception e) {
            log.error("Failed to process notification: {}", notificationJson, e);
        }
    }

    private void processAuthenticationEvent(AuthenticationEvent event) {
        // 인증 이벤트 처리 로직
        log.info("Processing authentication event: {}", event);
    }

    private void processApiRequest(ApiRequest request) {
        // API 요청 처리 로직
        log.info("Processing API request: {}", request);
    }

    private void processOrder(Order order) {
        // 주문 처리 로직
        log.info("Processing order: {}", order);
    }

    private void processPayment(Payment payment) {
        // 결제 처리 로직
        log.info("Processing payment: {}", payment);
    }

    private void processStockEvent(StockEvent stockEvent) {
        // 재고 이벤트 처리 로직
        log.info("Processing stock event: {}", stockEvent);
    }

    private void processNotification(Notification notification) {
        // 알림 처리 로직
        log.info("Processing notification: {}", notification);
    }

    // Event Classes
    @Data
    public static class AuthenticationEvent {
        private String type;
        private String username;
        private String timestamp;
        private Map<String, Object> details;
    }

    @Data
    public static class ApiRequest {
        private String path;
        private String method;
        private String timestamp;
        private Map<String, Object> parameters;
    }

    @Data
    public static class Order {
        private String orderId;
        private String status;
        private BigDecimal totalAmount;
        private List<OrderItem> items;
        private String timestamp;
    }

    @Data
    public static class OrderItem {
        private String productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }

    @Data
    public static class Payment {
        private String paymentId;
        private String orderId;
        private String status;
        private BigDecimal amount;
        private String paymentMethod;
        private String timestamp;
    }

    @Data
    public static class StockEvent {
        private String productId;
        private String type;
        private Integer quantity;
        private String note;
        private String timestamp;
    }

    @Data
    public static class Notification {
        private String type;
        private String target;
        private String title;
        private String message;
        private Map<String, Object> data;
        private String timestamp;
    }
}