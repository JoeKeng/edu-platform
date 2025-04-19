package com.edusystem.util;

import jakarta.servlet.http.HttpServletRequest;

// 创建RequestUtil工具类
// 用于获取客户端IP地址和设备信息
public class RequestUtil {
    
    public static String getClientIP(HttpServletRequest request) {
        // 原有逻辑保持不变
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getDeviceInfo(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
