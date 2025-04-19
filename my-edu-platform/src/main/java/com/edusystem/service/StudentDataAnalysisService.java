package com.edusystem.service;

import com.edusystem.model.Result;
import com.edusystem.model.StudentLearningAnalysis;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 学生数据分析服务
 */
public interface StudentDataAnalysisService {
    
    /**
     * 分析学生学习习惯
     */
    Map<String, Object> analyzeLearningHabit(Long studentId, LocalDateTime startTime, LocalDateTime endTime,boolean saveResult);
    
    /**
     * 分析学生学习进度
     */
    Map<String, Object> analyzeLearningProgress(Long studentId, Integer courseId , boolean saveResult);

    /**
     * 分析学生学习效率
     */
    //TODO:分析学习效率
    /**
     * 分析学生优势劣势
     */
    Map<String, Object> analyzeStrengthWeakness(Long studentId , boolean saveResult);
    
    /**
     * 生成学习建议
     */
    List<String> generateRecommendations(Long studentId , boolean saveResult);
    
    /**
     * 综合分析学生学习情况
     */
    Result comprehensiveAnalysis(Long studentId);
    
    /**
     * 获取班级学习情况分析
     */
    Result classAnalysis(Integer classId);
    
    /**
     * 获取学生学习分析结果
     */
    StudentLearningAnalysis getStudentAnalysis(Long studentId, String analysisType);
    
    /**
     * 保存学生学习分析结果
     */
    void saveStudentAnalysis(StudentLearningAnalysis analysis);
}