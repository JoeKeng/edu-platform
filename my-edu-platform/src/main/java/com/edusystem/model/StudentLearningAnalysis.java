package com.edusystem.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 学生学习分析结果
 */
@Data
public class StudentLearningAnalysis {
    private Long id;
    private Long studentId;
    private String analysisType; // HABIT, PROGRESS, STRENGTH_WEAKNESS, RECOMMENDATION
    
    // 学习习惯分析
    private Map<String, Object> learningHabitData; // 学习时间分布、频率等
    
    // 学习进度分析
    private Map<String, Object> learningProgressData; // 课程完成情况、作业完成率等
    
    // 优势劣势分析
    private Map<String, Object> strengthWeaknessData; // 各知识点、题型的掌握情况
    
    // 学习建议
    private List<String> recommendations; // 学习建议列表
    
    private LocalDateTime analysisTime; // 分析时间
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}