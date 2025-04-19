package com.edusystem.service.impl;

import com.edusystem.mapper.KnowledgePointMapper;
import com.edusystem.mapper.QuestionBankMapper;
import com.edusystem.mapper.StudentKnowledgeMasteryMapper;
import com.edusystem.model.KnowledgePoint;
import com.edusystem.model.StudentKnowledgeMastery;
import com.edusystem.service.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识点服务实现类
 */
@Service
public class KnowledgePointServiceImpl implements KnowledgePointService {

    @Autowired
    private KnowledgePointMapper knowledgePointMapper;
    
    @Autowired
    private QuestionBankMapper questionBankMapper;
    
    @Autowired
    private StudentKnowledgeMasteryMapper studentKnowledgeMasteryMapper;

    @Override
    @Transactional
    public boolean addKnowledgePoint(KnowledgePoint knowledgePoint) {
        knowledgePoint.setCreatedAt(LocalDateTime.now());
        knowledgePoint.setUpdatedAt(LocalDateTime.now());
        return knowledgePointMapper.insert(knowledgePoint) > 0;
    }

    @Override
    @Transactional
    public boolean updateKnowledgePoint(KnowledgePoint knowledgePoint) {
        knowledgePoint.setUpdatedAt(LocalDateTime.now());
        return knowledgePointMapper.update(knowledgePoint) > 0;
    }

    @Override
    @Transactional
    public boolean deleteKnowledgePoint(Long id) {
        return knowledgePointMapper.delete(id) > 0;
    }

    @Override
    public KnowledgePoint getKnowledgePointById(Long id) {
        return knowledgePointMapper.selectById(id);
    }

    @Override
    public List<KnowledgePoint> getKnowledgePointsByCourse(Integer courseId) {
        return knowledgePointMapper.selectByCourseId(courseId);
    }

    @Override
    public List<KnowledgePoint> getKnowledgePointsByChapter(Integer chapterId) {
        return knowledgePointMapper.selectByChapterId(chapterId);
    }

    @Override
    public List<KnowledgePoint> getKnowledgePointTree(Integer courseId) {
        // 获取所有根节点（parentId为null或0的节点）
        List<KnowledgePoint> rootNodes = knowledgePointMapper.selectByParentId(0L, courseId);
        
        // 递归构建树形结构
        for (KnowledgePoint rootNode : rootNodes) {
            buildKnowledgePointTree(rootNode, courseId);
        }
        
        return rootNodes;
    }
    
    /**
     * 递归构建知识点树
     */
    private void buildKnowledgePointTree(KnowledgePoint parent, Integer courseId) {
        List<KnowledgePoint> children = knowledgePointMapper.selectByParentId(parent.getId(), courseId);
        if (children != null && !children.isEmpty()) {
            parent.setChildren(children);
            for (KnowledgePoint child : children) {
                buildKnowledgePointTree(child, courseId);
            }
        }
    }

    @Override
    @Transactional
    public boolean addQuestionKnowledgePoint(Long questionId, Long knowledgePointId, Double weight) {
        return questionBankMapper.addQuestionKnowledgePoint(questionId, knowledgePointId, weight) > 0;
    }

    @Override
    @Transactional
    public boolean deleteQuestionKnowledgePoint(Long questionId, Long knowledgePointId) {
        return questionBankMapper.deleteQuestionKnowledgePoint(questionId, knowledgePointId) > 0;
    }

    @Override
    @Transactional
    public boolean updateQuestionKnowledgePointWeight(Long questionId, Long knowledgePointId, Double weight) {
        return questionBankMapper.updateQuestionKnowledgePointWeight(questionId, knowledgePointId, weight) > 0;
    }
//TODO:结构不确定是否是OK的
    @Override
    public Map<String, Object> getStudentKnowledgeMastery(Long studentId, Long knowledgePointId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取学生知识点掌握情况
        StudentKnowledgeMastery mastery = studentKnowledgeMasteryMapper.selectByStudentAndKnowledge(studentId, knowledgePointId);
        if (mastery != null) {
            result.put("knowledge_point_id", mastery.getKnowledgePointId());
            result.put("mastery_level", mastery.getMasteryLevel());
            result.put("question_count", mastery.getQuestionCount());
            result.put("correct_count", mastery.getCorrectCount());
            // 计算正确率
            double accuracy = mastery.getQuestionCount() > 0 
                ? (double) mastery.getCorrectCount() / mastery.getQuestionCount() 
                : 0.0;
            result.put("accuracy", accuracy);
        } else {
            // 如果没有记录，返回默认值
            result.put("knowledge_point_id", knowledgePointId);
            result.put("mastery_level", 0.0);
            result.put("question_count", 0);
            result.put("correct_count", 0);
            result.put("accuracy", 0.0);
        }
        
        // 获取知识点信息
        KnowledgePoint knowledgePoint = knowledgePointMapper.selectById(knowledgePointId);
        if (knowledgePoint != null) {
            result.put("knowledge_point_name", knowledgePoint.getName());
            result.put("description", knowledgePoint.getDescription());
            result.put("course_id", knowledgePoint.getCourseId());
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getStudentCourseKnowledgeMastery(Long studentId, Integer courseId) {
        return studentKnowledgeMasteryMapper.selectCourseMasteryByStudentId(studentId, courseId);
    }

    @Override
    public List<Map<String, Object>> getStudentAllKnowledgeMastery(Long studentId) {
        return studentKnowledgeMasteryMapper.selectAllMasteryByStudentId(studentId);
    }

    // 获取班级知识点掌握情况
    @Override
    public Map<String, Object> getClassKnowledgePointStats(Integer classId, Long knowledgePointId) {
        Map<String, Object> stats = studentKnowledgeMasteryMapper.getClassKnowledgePointStats(classId, knowledgePointId);
        
        // 如果没有数据，返回默认值
        if (stats == null) {
            stats = new HashMap<>();
            stats.put("avg_mastery", 0.0);
            stats.put("min_mastery", 0.0);
            stats.put("max_mastery", 0.0);
            stats.put("student_count", 0);
            stats.put("proficient_count", 0);
        }
        
        // 添加知识点信息
        KnowledgePoint knowledgePoint = knowledgePointMapper.selectById(knowledgePointId);
        if (knowledgePoint != null) {
            stats.put("knowledge_point_name", knowledgePoint.getName());
            stats.put("description", knowledgePoint.getDescription());
            stats.put("course_id", knowledgePoint.getCourseId());
        }
        
        return stats;
    }
}