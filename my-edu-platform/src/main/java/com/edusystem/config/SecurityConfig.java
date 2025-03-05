package com.edusystem.config;

import com.edusystem.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/register", "/user/login","/ai/**").permitAll()// 允许匿名访问
//                        .requestMatchers("/common/**").permitAll()// 允许匿名访问
                        .requestMatchers("/student/**").hasRole("STUDENT")  // 仅学生访问
                        .requestMatchers("/teacher/**", "/students/**").hasRole("TEACHER")  // 仅教师访问
                        .requestMatchers("/common/**","/upload/**","/question/**","/assignment/**").hasAnyRole("STUDENT", "TEACHER")  // 共享访问
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())  // 关闭 CSRF 保护（需要时可以开启）
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS)) // 关闭 session 机制，使用 JWT
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // 添加 JWT 过滤器

        return http.build();
    }

    // 提供默认的 AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
