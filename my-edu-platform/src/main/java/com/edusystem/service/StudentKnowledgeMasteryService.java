package com.edusystem.service;

import java.util.List;
import java.util.Map;

/**
 * 学生知识点掌握情况服务
 */
public interface StudentKnowledgeMasteryService {

    /**
     * 更新学生知识点掌握情况
     * @param studentId 学生ID
     * @param knowledgePointId 知识点ID
     * @param masteryChange 掌握度变化
     * @param isCorrect 是否正确
     * @return 更新结果
     */
    boolean updateMastery(Long studentId, Long knowledgePointId, double masteryChange, boolean isCorrect);
    
    /**
     * 获取学生知识点掌握情况
     * @param studentId 学生ID
     * @param knowledgePointId 知识点ID
     * @return 掌握情况
     */
    Map<String, Object> getMastery(Long studentId, Long knowledgePointId);
    
    /**
     * 获取学生所有知识点掌握情况
     * @param studentId 学生ID
     * @return 所有知识点掌握情况
     */
    List<Map<String, Object>> getAllMastery(Long studentId);
    
    /**
     * 获取学生课程知识点掌握情况
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 课程知识点掌握情况
     */
    List<Map<String, Object>> getCourseMastery(Long studentId, Integer courseId);
}