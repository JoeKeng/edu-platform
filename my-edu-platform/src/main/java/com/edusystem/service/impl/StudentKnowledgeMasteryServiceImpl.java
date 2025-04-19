package com.edusystem.service.impl;

import com.edusystem.mapper.StudentKnowledgeMasteryMapper;
import com.edusystem.model.StudentKnowledgeMastery;
import com.edusystem.service.StudentKnowledgeMasteryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StudentKnowledgeMasteryServiceImpl implements StudentKnowledgeMasteryService {

    @Autowired
    private StudentKnowledgeMasteryMapper masteryMapper;

    //TODO:如何实现学生的知识点掌握情况的更新(如何计算掌握度)
    @Override
    public boolean updateMastery(Long studentId, Long knowledgePointId, double masteryChange, boolean isCorrect) {
        try {
            // 查询现有掌握情况
            StudentKnowledgeMastery mastery = masteryMapper.selectByStudentAndKnowledge(studentId, knowledgePointId);
            
            if (mastery == null) {
                // 创建新记录
                mastery = new StudentKnowledgeMastery();
                mastery.setStudentId(studentId);
                mastery.setKnowledgePointId(knowledgePointId);
                mastery.setMasteryLevel(Math.min(100, Math.max(0, masteryChange))); // 确保在0-100范围内
                mastery.setQuestionCount(1);
                mastery.setCorrectCount(isCorrect ? 1 : 0);
                mastery.setLastPracticeTime(LocalDateTime.now());
                mastery.setCreatedAt(LocalDateTime.now());
                mastery.setUpdatedAt(LocalDateTime.now());
                mastery.setIsDeleted(0);
                
                return masteryMapper.insert(mastery) > 0;
            } else {
                // 更新现有记录
                double newMastery = Math.min(100, Math.max(0, mastery.getMasteryLevel() + masteryChange));
                mastery.setMasteryLevel(newMastery);
                mastery.setQuestionCount(mastery.getQuestionCount() + 1);
                if (isCorrect) {
                    mastery.setCorrectCount(mastery.getCorrectCount() + 1);
                }
                mastery.setLastPracticeTime(LocalDateTime.now());
                mastery.setUpdatedAt(LocalDateTime.now());
                
                return masteryMapper.update(mastery) > 0;
            }
        } catch (Exception e) {
            log.error("更新学生知识点掌握情况失败: studentId={}, knowledgePointId={}", studentId, knowledgePointId, e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getMastery(Long studentId, Long knowledgePointId) {
        StudentKnowledgeMastery mastery = masteryMapper.selectByStudentAndKnowledge(studentId, knowledgePointId);
        
        Map<String, Object> result = new HashMap<>();
        if (mastery != null) {
            result.put("masteryLevel", mastery.getMasteryLevel());
            result.put("questionCount", mastery.getQuestionCount());
            result.put("correctCount", mastery.getCorrectCount());
            result.put("accuracy", mastery.getQuestionCount() > 0 ? 
                    (double) mastery.getCorrectCount() / mastery.getQuestionCount() * 100 : 0);
            result.put("lastPracticeTime", mastery.getLastPracticeTime());
            result.put("status", getMasteryStatus(mastery.getMasteryLevel()));
        } else {
            result.put("masteryLevel", 0);
            result.put("questionCount", 0);
            result.put("correctCount", 0);
            result.put("accuracy", 0);
            result.put("lastPracticeTime", null);
            result.put("status", "未学习");
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getAllMastery(Long studentId) {
        return masteryMapper.selectAllMasteryByStudentId(studentId);
    }

    @Override
    public List<Map<String, Object>> getCourseMastery(Long studentId, Integer courseId) {
        return masteryMapper.selectCourseMasteryByStudentId(studentId, courseId);
    }
    
    /**
     * 根据掌握度获取状态描述
     */
    private String getMasteryStatus(double masteryLevel) {
        if (masteryLevel >= 90) {
            return "精通";
        } else if (masteryLevel >= 70) {
            return "熟练";
        } else if (masteryLevel >= 50) {
            return "理解";
        } else if (masteryLevel >= 30) {
            return "了解";
        } else {
            return "待学习";
        }
    }
}