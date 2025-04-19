 package com.edusystem.service;

import com.edusystem.model.KnowledgePoint;

import java.util.List;
import java.util.Map;

/**
 * 知识点服务接口
 */
public interface KnowledgePointService {

    /**
     * 添加知识点
     * @param knowledgePoint 知识点对象
     * @return 添加结果
     */
    boolean addKnowledgePoint(KnowledgePoint knowledgePoint);
    
    /**
     * 更新知识点
     * @param knowledgePoint 知识点对象
     * @return 更新结果
     */
    boolean updateKnowledgePoint(KnowledgePoint knowledgePoint);
    
    /**
     * 删除知识点
     * @param id 知识点ID
     * @return 删除结果
     */
    boolean deleteKnowledgePoint(Long id);
    
    /**
     * 根据ID获取知识点
     * @param id 知识点ID
     * @return 知识点对象
     */
    KnowledgePoint getKnowledgePointById(Long id);
    
    /**
     * 根据课程ID获取知识点列表
     * @param courseId 课程ID
     * @return 知识点列表
     */
    List<KnowledgePoint> getKnowledgePointsByCourse(Integer courseId);
    
    /**
     * 根据章节ID获取知识点列表
     * @param chapterId 章节ID
     * @return 知识点列表
     */
    List<KnowledgePoint> getKnowledgePointsByChapter(Integer chapterId);
    
    /**
     * 获取知识点树形结构
     * @param courseId 课程ID
     * @return 知识点树形结构
     */
    List<KnowledgePoint> getKnowledgePointTree(Integer courseId);
    
    /**
     * 添加题目知识点关联
     * @param questionId 题目ID
     * @param knowledgePointId 知识点ID
     * @param weight 权重
     * @return 添加结果
     */
    boolean addQuestionKnowledgePoint(Long questionId, Long knowledgePointId, Double weight);
    
    /**
     * 删除题目知识点关联
     * @param questionId 题目ID
     * @param knowledgePointId 知识点ID
     * @return 删除结果
     */
    boolean deleteQuestionKnowledgePoint(Long questionId, Long knowledgePointId);
    
    /**
     * 更新题目知识点关联权重
     * @param questionId 题目ID
     * @param knowledgePointId 知识点ID
     * @param weight 权重
     * @return 更新结果
     */
    boolean updateQuestionKnowledgePointWeight(Long questionId, Long knowledgePointId, Double weight);
    
    /**
     * 获取学生知识点掌握情况
     * @param studentId 学生ID
     * @param knowledgePointId 知识点ID
     * @return 掌握情况
     */
    Map<String, Object> getStudentKnowledgeMastery(Long studentId, Long knowledgePointId);
    
    /**
     * 获取学生课程知识点掌握情况
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 课程知识点掌握情况
     */
    List<Map<String, Object>> getStudentCourseKnowledgeMastery(Long studentId, Integer courseId);
    
    /**
     * 获取学生所有知识点掌握情况
     * @param studentId 学生ID
     * @return 所有知识点掌握情况
     */
    List<Map<String, Object>> getStudentAllKnowledgeMastery(Long studentId);
    
    /**
     * 获取班级知识点掌握情况统计
     * @param classId 班级ID
     * @param knowledgePointId 知识点ID
     * @return 班级知识点掌握情况统计
     */
    Map<String, Object> getClassKnowledgePointStats(Integer classId, Long knowledgePointId);
}