package com.gijun.backend.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

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

    private void send(String topic, Object message) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[{}] with offset=[{}]",
                        message,
                        result.getRecordMetadata().offset()
                );
            } else {
                log.error("Unable to send message=[{}] due to : {}",
                        message,
                        ex.getMessage()
                );
            }
        });
    }

    @KafkaListener(topics = "authentication", groupId = "auth-group")
    public void consumeAuthenticationEvents(String event) {
        log.info("Received authentication event: {}", event);
        // 인증 이벤트 처리 로직
    }

    @KafkaListener(topics = "api-requests", groupId = "api-group")
    public void consumeApiRequests(String request) {
        log.info("Received API request: {}", request);
        // API 요청 처리 로직
    }

    @KafkaListener(topics = "orders", groupId = "order-group")
    public void consumeOrders(String orderJson) {
        log.info("Received order: {}", orderJson);
        // 주문 처리 로직
    }

    @KafkaListener(topics = "payments", groupId = "payment-group")
    public void consumePayments(String paymentJson) {
        log.info("Received payment: {}", paymentJson);
        // 결제 처리 로직
    }
}