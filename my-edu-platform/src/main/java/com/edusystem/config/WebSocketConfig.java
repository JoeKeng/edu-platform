package com.edusystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置类
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 配置消息代理
        // /topic 用于广播消息，如班级通知、系统公告
        // /queue 用于点对点消息，如个人通知
        registry.enableSimpleBroker("/topic", "/queue");
        
        // 配置应用程序目标前缀
        registry.setApplicationDestinationPrefixes("/app");
        
        // 配置用户目标前缀，用于点对点消息
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP端点，客户端通过这个端点连接WebSocket服务器
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 允许所有来源的跨域请求
                .withSockJS(); // 启用SockJS支持，兼容不支持WebSocket的浏览器
    }
}