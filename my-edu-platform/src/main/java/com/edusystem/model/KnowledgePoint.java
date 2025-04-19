package com.edusystem.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;

@Data
public class KnowledgePoint {
    private Long id;
    private String name;
    private Integer courseId;
    private Integer chapterId;
    private Long parentId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 非数据库字段，用于构建知识点树形结构
    @TableField(exist = false)
    private List<KnowledgePoint> children;
    
    // 非数据库字段，用于记录权重（与题目关联时使用）
    @TableField(exist = false)
    private Double weight;
}