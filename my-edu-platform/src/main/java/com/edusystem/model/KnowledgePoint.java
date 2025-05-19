package com.edusystem.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;

@Data
public class KnowledgePoint {
    private Long id;                      // 知识点ID
    private String name;                  // 知识点名称
    private Integer courseId;             // 所属课程ID
    private Integer chapterId;            // 所属章节ID
    private Long parentId;                // 父知识点ID
    private String description;           // 知识点描述
    private LocalDateTime createdAt;      // 创建时间
    private LocalDateTime updatedAt;      // 更新时间
    
    // 非数据库字段，用于构建知识点树形结构
    @TableField(exist = false)
    private List<KnowledgePoint> children;
    
    // 非数据库字段，用于记录权重（与题目关联时使用）
    @TableField(exist = false)
    private Double weight;
}