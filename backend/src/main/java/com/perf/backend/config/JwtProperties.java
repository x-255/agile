package com.perf.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey = "mySecretKey123456789012345678901234567890";
    private long expiration = 86400000; // 24小时
}