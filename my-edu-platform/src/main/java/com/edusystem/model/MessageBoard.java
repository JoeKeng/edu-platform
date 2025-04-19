package com.edusystem.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 留言板实体类
 */
@Data
public class MessageBoard {
    private Long id; // 留言ID
    private Long userId; // 用户ID，关联用户表
    private String content; // 留言内容
    private LocalDateTime createdAt; // 创建时间
    private Long parentId; // 父留言ID，支持留言回复
    private Integer status; // 状态（1=正常, 0=待审核, -1=审核未通过）
    private String moduleType; // 模块类型（如 course, assignment, discussion）
    private Long moduleId; // 对应模块的ID（如课程ID、作业ID等）
    private Boolean isDeleted; // 是否删除（0=未删除, 1=已删除）
    
    // 非数据库字段，用于前端展示
    private String username; // 用户名
    private String userImage; // 用户头像
    private String userRole; // 用户角色
}