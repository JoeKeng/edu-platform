package com.edusystem.filter;

import com.edusystem.util.CurrentHolder;
import com.edusystem.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
@WebFilter(urlPatterns = "/*")
@Slf4j
public class TokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        //1.获取请求路径
        String requestURI = request.getRequestURI();//

        //2.判断是否为登录或注册请求,如果包含login,放行,否则拦截,返回错误信息
        if(requestURI.contains("/user/login")|| requestURI.contains("/user/register")){
            log.info("登录请求,放行 ...");
            filterChain.doFilter(request,response);
            return;
        }

        //3.获取请求头中的token
        String token = request.getHeader("token");

        //4.判断token是否存在,如果不存在,否则拦截,返回错误信息(响应401状态码)
        if(token==null|| token.isEmpty()){
            log.info("令牌为空,响应401状态码 ...");
            response.setStatus(401);
            return;
        }
        //5.如果token存在,校验令牌,如果校验不通过,返回错误信息(响应401状态码)
        try {
            Claims claims = JwtUtil.parseJwt(token);
            Integer id = Integer.valueOf(claims.get("id").toString());
            CurrentHolder.setCurrentId(id);
            log.info("当前登录员工ID: {}, 将其存入ThreadLocal", id);

        } catch (Exception e) {
            log.info("令牌校验不通过,响应401状态码 ...");
            response.setStatus(401);
            return;
        }
        //6.如果校验通过,放行
        log.info("令牌校验通过,放行 ...");
        filterChain.doFilter(request,response);

        //7.将当前登录员工ID从ThreadLocal中移除
        CurrentHolder.remove();
        log.info("当前登录员工ID: {}, 从ThreadLocal中移除", CurrentHolder.getCurrentId());
    }


}
