package com.edusystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 错题本实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrongQuestion {
    /**
     * 错题记录id
     */
    private Long id;
    
    /**
     * 用户id
     */
    private Long userId;
    
    /**
     * 题目id
     */
    private Long questionId;
    
    /**
     * 所属作业ID（可选）
     */
    private Long assignmentId;
    
    /**
     * 学生错误答案
     */
    private String wrongAnswer;
    
    /**
     * 正确答案
     */
    private String correctAnswer;
    
    /**
     * 解析
     */
    private String analysis;
    
    /**
     * 错误类型（concept-概念错误、calculation-计算错误、misread-审题错误、other-其他）
     */
    private String wrongType;
    
    /**
     * 答题尝试次数
     */
    private Integer attemptCount;
    
    /**
     * 首次得分
     */
    private Double firstAttemptScore;
    
    /**
     * 最近一次得分
     */
    private Double lastAttemptScore;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 修改时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否逻辑删除
     */
    private Boolean isDeleted;

}