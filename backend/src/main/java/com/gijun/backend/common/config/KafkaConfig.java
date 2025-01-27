package com.gijun.backend.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "209715200");

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // 인증 관련 토픽
    @Bean
    public NewTopic authenticationTopic() {
        return TopicBuilder.name("authentication")
                .partitions(1)
                .replicas(1)
                .build();
    }

    // API 요청 토픽
    @Bean
    public NewTopic apiRequestTopic() {
        return TopicBuilder.name("api-requests")
                .partitions(1)
                .replicas(1)
                .build();
    }

    // 주문 관련 토픽
    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name("orders")
                .partitions(1)
                .replicas(1)
                .build();
    }

    // 결제 관련 토픽
    @Bean
    public NewTopic paymentsTopic() {
        return TopicBuilder.name("payments")
                .partitions(1)
                .replicas(1)
                .build();
    }
}