package com.edusystem.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.ai.openai")  // 前缀要匹配 yml 结构
public class AIConfig {

    private String baseUrl;

    private String apiKey;

    private String model;

}
