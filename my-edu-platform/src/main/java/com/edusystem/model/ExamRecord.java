 package com.edusystem.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生考试记录实体类
 */
@Data
public class ExamRecord {
    private Long id;              // 主键ID
    private Long examId;          // 考试ID
    private Long studentId;       // 学生ID
    private LocalDateTime startTime;    // 开始答题时间
    private LocalDateTime submitTime;   // 提交时间
    private BigDecimal totalScore;      // 总得分
    private String status;        // 状态(NOT_STARTED-未开始/IN_PROGRESS-进行中/SUBMITTED-已提交/GRADED-已批改)
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
    
    // 非数据库字段，用于前端展示
    private String studentName;   // 学生姓名
    private String examName;      // 考试名称
}