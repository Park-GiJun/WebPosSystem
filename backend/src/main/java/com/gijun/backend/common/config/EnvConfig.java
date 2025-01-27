package com.gijun.backend.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./backend/")
                .filename(".env")
                .load();

        // 공통 설정
        System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        System.setProperty("SPRING_DATASOURCE_DRIVER_CLASS_NAME", dotenv.get("SPRING_DATASOURCE_DRIVER_CLASS_NAME"));

        // Redis Configuration
        System.setProperty("REDIS_HOST", dotenv.get("REDIS_HOST"));
        System.setProperty("REDIS_PORT", dotenv.get("REDIS_PORT"));

        // Kafka Configuration
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", dotenv.get("KAFKA_BOOTSTRAP_SERVERS"));

        // 개발 환경
        System.setProperty("DEV_SERVER_PORT", dotenv.get("DEV_SERVER_PORT"));
        System.setProperty("DEV_CONTEXT_PATH", dotenv.get("DEV_CONTEXT_PATH"));
        System.setProperty("DEV_JPA_DDL_AUTO", dotenv.get("DEV_JPA_DDL_AUTO"));
        System.setProperty("DEV_JPA_SHOW_SQL", dotenv.get("DEV_JPA_SHOW_SQL"));

        // 프로덕션 환경
        System.setProperty("PROD_SERVER_PORT", dotenv.get("PROD_SERVER_PORT"));
        System.setProperty("PROD_CONTEXT_PATH", dotenv.get("PROD_CONTEXT_PATH"));
        System.setProperty("PROD_JPA_DDL_AUTO", dotenv.get("PROD_JPA_DDL_AUTO"));
        System.setProperty("PROD_JPA_SHOW_SQL", dotenv.get("PROD_JPA_SHOW_SQL"));

        // JWT 설정
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
    }
}