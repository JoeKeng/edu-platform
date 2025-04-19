package com.edusystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIAnalysisResultDTO {
    private Map<String, Object> analysisData; // 分析数据
    private List<String> recommendations; // 学习建议
    private String summary; // 分析总结
    private Long studentId; // 学生ID
}