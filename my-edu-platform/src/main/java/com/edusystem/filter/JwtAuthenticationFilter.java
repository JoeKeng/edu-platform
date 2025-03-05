package com.edusystem.filter;

import com.edusystem.util.CurrentHolder;
import com.edusystem.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer " 前缀
            try {
                // 解析 JWT
                Claims claims = JwtUtil.parseJwt(token);
                String username = claims.get("username", String.class);
                String role = JwtUtil.getRoleFromToken(token);
                Integer userId = claims.get("id", Integer.class); // ✅ 获取 userId

                if (username != null && role != null && userId != null) {
                    // ✅ 存入 ThreadLocal，保证全局可获取
                    CurrentHolder.setCurrentId(userId);

                    // 构造 Spring Security 认证信息
                    User user = new User(username, "", Collections.singleton(() -> role));
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token 无效或过期");
                return;
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            CurrentHolder.remove(); // ✅ 防止 ThreadLocal 泄漏
        }
    }
}
