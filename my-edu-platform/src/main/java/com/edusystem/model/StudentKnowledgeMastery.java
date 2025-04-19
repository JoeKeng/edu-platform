package com.edusystem.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学生知识点掌握情况实体类
 */
@Data
public class StudentKnowledgeMastery {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 知识点ID
     */
    private Long knowledgePointId;
    
    /**
     * 掌握程度（0-100）
     */
    private Double masteryLevel;
    
    /**
     * 练习题目数量
     */
    private Integer questionCount;
    
    /**
     * 正确题目数量
     */
    private Integer correctCount;
    
    /**
     * 最后练习时间
     */
    private LocalDateTime lastPracticeTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Integer isDeleted;
}