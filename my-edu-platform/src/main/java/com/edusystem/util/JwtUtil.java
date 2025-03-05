package com.edusystem.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    private static final String SECRET_KEY = "cGxhdGZyb20K"; // 密钥
    private static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000; // 令牌过期时间：12小时

    /**
     * 生成JWT令牌
     *
     * @param dataMap 要添加到令牌中的自定义数据
     * @return 生成的JWT令牌
     */
    public static String generateToken(Map<String, Object> dataMap) {
        Date now = new Date(); // 当前时间
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME); // 过期时间

        // 使用JWT构建器生成令牌
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 指定加密算法和密钥
                .addClaims(dataMap) // 添加自定义数据
                .setExpiration(expiration) // 设置过期时间
                .compact(); // 生成紧凑的JWT字符串

        return jwt;
    }

    /**
     * 校验解析JWT令牌
     *
     * @param token 要解析的JWT令牌
     * @return 解析后的Claims对象，包含令牌中的数据
     * @throws Exception 如果令牌无效或过期，会抛出异常
     */
    public static Claims parseJwt(String token) throws Exception {
        try {
            // 解析JWT令牌
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY) // 指定密钥
                    .parseClaimsJws(token) // 解析令牌
                    .getBody(); // 获取Claims对象

            return claims;
        } catch (Exception e) {
            // 如果解析失败，抛出异常
            throw new Exception("Invalid JWT token", e);
        }
    }


    /**
     * 从JWT中获取角色，并转换为Spring Security格式
     */
    public static String getRoleFromToken(String token) throws Exception {
        Claims claims = parseJwt(token);
        String role = claims.get("role", String.class); // 获取角色
        log.info("从JWT中获取到的角色: {}", role);
        if (role != null) {
            return "ROLE_" + role.toUpperCase(); // 确保与Spring Security匹配
        }
        return null;
    }

    /**
     * 从JWT中获取用户ID
     */
    public static Integer getUserIdFromToken(String token) throws Exception {
        Claims claims = parseJwt(token);
        Integer userId = claims.get("userId", Integer.class); // 获取用户ID
        log.info("从JWT中获取到的用户ID: {}", userId);
        return userId;
    }
    /**
     * 获取token
     */



}
