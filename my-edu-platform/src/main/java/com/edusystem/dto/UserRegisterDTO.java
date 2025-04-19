package com.edusystem.dto;

import lombok.Data;

import java.util.Map;

@Data
public class UserRegisterDTO {
    private String username;
    private String email;
    private String phone;
    private String password;
    private String role;
    private Map<String, Object> roleInfo; // 用于存储学生或教师的特定信息
}