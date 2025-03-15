package com.gijun.backend.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

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
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "209715200");

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
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
                .config("min.insync.replicas", "1")
                .build();
    }

    // API 요청 토픽
    @Bean
    public NewTopic apiRequestTopic() {
        return TopicBuilder.name("api-requests")
                .partitions(1)
                .replicas(1)
                .config("min.insync.replicas", "1")
                .build();
    }

    // 주문 관련 토픽
    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name("orders")
                .partitions(1)
                .replicas(1)
                .config("min.insync.replicas", "1")
                .build();
    }

    // 결제 관련 토픽
    @Bean
    public NewTopic paymentsTopic() {
        return TopicBuilder.name("payments")
                .partitions(1)
                .replicas(1)
                .config("min.insync.replicas", "1")
                .build();
    }

    // 재고 이벤트 토픽
    @Bean
    public NewTopic stockEventsTopic() {
        return TopicBuilder.name("stock-events")
                .partitions(1)
                .replicas(1)
                .config("min.insync.replicas", "1")
                .build();
    }

    // 상품 이벤트 토픽
    @Bean
    public NewTopic productEventsTopic() {
        return TopicBuilder.name("product-events")
                .partitions(1)
                .replicas(1)
                .config("min.insync.replicas", "1")
                .build();
    }

    // 알림 토픽
    @Bean
    public NewTopic notificationsTopic() {
        return TopicBuilder.name("notifications")
                .partitions(1)
                .replicas(1)
                .config("min.insync.replicas", "1")
                .build();
    }

    // 카테고리 이벤트 토픽
    @Bean
    public NewTopic categoryEventsTopic() {
        return TopicBuilder.name("category-events")
                .partitions(1)
                .replicas(1)
                .config("min.insync.replicas", "1")
                .build();
    }
}