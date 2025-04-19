package com.edusystem.service;

import com.edusystem.dto.AIGeneratedResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AIService {
    /**
     * 调用 AI 评分
     * @param questionId 题目ID
     * @param studentAnswer 学生作答
     * @return 评分结果（包括正确性、分数、AI 反馈）
     */
    AIGeneratedResult grade(Long questionId, String studentAnswer, String questionText, String correctAnswer)
            throws IOException;

                /**
     * AI 分析学习习惯
     */
    Map<String, Object> analyzeHabit(Map<String, Object> behaviorData) throws IOException;

    /**
     * AI 分析优势劣势
     */
    Map<String, Object> analyzeStrengthWeakness(Map<String, Object> performanceData) throws IOException;

    /**
     * AI 生成学习建议
     */
    List<String> generateRecommendations(Map<String, Object> analysisData) throws IOException;

    //实现AI分析的学习进度分析
    Map<String, Object> analyzeLearningProgress(Map<String, Object> progressData) throws IOException;
}


