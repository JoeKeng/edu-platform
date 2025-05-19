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

import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(
                                        "/user/register",
                                        "/user/login",
                                        "/doc.html",
                                        "/doc.html/**",
                                        "/webjars/**",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/swagger-ui/**"
                                ).permitAll()
                                // 允许所有OPTIONS请求（预检请求）确保前端可以正常访问API：
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            // 允许匿名访问
//                       .requestMatchers("/**").permitAll()// 允许匿名访问
                         .requestMatchers("/student/**","/wrong-question","/student-analysis","/student-data").hasRole("STUDENT")  // 仅学生访问
                         .requestMatchers("/teacher/**", "/students/**","upload/**","neo4j/**").hasRole("TEACHER")  // 仅教师访问
                         .requestMatchers("/common/**","/upload/**","/question/**","/assignment/**","/exam/**","/student/knowledge/**").hasAnyRole("STUDENT", "TEACHER")  // 共享访问
                         .anyRequest().authenticated() //测试暂时关闭
                )
                .csrf(csrf -> csrf.disable())  // 关闭 CSRF 保护（需要时可以开启）
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS)) // 关闭 session 机制，使用 JWT
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // 添加 JWT 过滤器,测试暂时关闭

        return http.build();
    }

    // 提供默认的 AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
