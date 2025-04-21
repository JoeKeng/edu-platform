package com.edusystem.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Configuration
public class ServerInfoConfig implements ApplicationListener<WebServerInitializedEvent> {

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        try {
            // 获取当前主机的IP地址
            String ip = InetAddress.getLocalHost().getHostAddress();
            // 获取应用的端口号
            int port = event.getWebServer().getPort();
            
            log.info("==========================================================");
            log.info("应用启动成功！");
            log.info("本地访问地址: http://localhost:{}", port);
            log.info("外部访问地址: http://{}:{}", ip, port);
            log.info("==========================================================");
        } catch (UnknownHostException e) {
            log.error("获取主机IP地址失败", e);
        }
    }
}