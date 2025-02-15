package com.gijun.backend.common.controller;

import com.gijun.backend.common.domain.dto.SlackNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    @PostMapping("/slack")
    public ResponseEntity<Void> sendSlackNotification(@RequestBody SlackNotification notification) {
        Map<String, Object> payload = new HashMap<>();

        // Slack 메시지 포맷 지정
        String message = String.format("*🚀 [%s]* %s\n\n⏰ *Timestamp:* %s",
                notification.getStatus().toUpperCase(),
                notification.getMessage(),
                notification.getTimestamp()
        );

        payload.put("text", message);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(slackWebhookUrl, payload, String.class);

        return ResponseEntity.ok().build();
    }
}