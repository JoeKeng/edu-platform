package com.edusystem.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Integer userId; // 用户ID
    private String username; // 用户名
    private String email; // 电子邮件
    private String phone; // 手机号码
    private String passwordHash; // 密码哈希
    private String role; // 角色
    private String image;// 头像URL
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
    private Boolean isDeleted; // 是否删除
    private LocalDateTime lastLogin; // 最后登录时间
    private Integer status; // 状态
}