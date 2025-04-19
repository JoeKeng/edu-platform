package com.edusystem.factory;

import com.edusystem.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AIGradingFactory {

    @Autowired
    private Map<String, AIService> aiServices;

    public AIService getAIService(String modelName) {
        return aiServices.getOrDefault(modelName, aiServices.get("DeepSeekAIService"));
    }
}
