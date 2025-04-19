package com.edusystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIGeneratedResult {
    private Double aiScore;
    private Boolean isCorrect;
    private String feedback;
    private String explanation;
    private String knowledgePointsFeedback; // 新增知识点反馈
    private Long studentId; // 添加学生ID字段，用于更新知识点掌握情况
}

