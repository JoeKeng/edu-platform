package com.edusystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AIAnalysisRequestDTO {
    private Long studentId; // 学生ID
    private String analysisType; // 分析类型：HABIT(学习习惯), STRENGTH_WEAKNESS(优势劣势), RECOMMENDATION(学习建议)
    private LocalDateTime startTime; // 分析开始时间
    private LocalDateTime endTime; // 分析结束时间
    private Integer courseId; // 课程ID（可选）
}