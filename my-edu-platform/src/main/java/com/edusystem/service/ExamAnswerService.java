package com.edusystem.service;

import com.edusystem.dto.AIGeneratedResult;
import com.edusystem.model.ExamAnswer;

import java.io.IOException;
import java.util.List;

public interface ExamAnswerService {
    /**
     * AI自动批改考试答案
     * @param examAnswerId 考试答案ID
     * @return 批改结果
     */
    AIGeneratedResult aiGradeExamAnswer(Long examAnswerId) throws IOException;
    
    /**
     * 批量AI自动批改考试答案
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 批改成功的数量
     */
    int batchAIGradeExamAnswers(Long examId, Long studentId) throws IOException;
    
    /**
     * 教师手动批改考试答案
     * @param examAnswerId 考试答案ID
     * @param score 分数
     * @param feedback 反馈
     * @return 批改结果
     */
    int teacherGradeExamAnswer(Long examAnswerId,Boolean isCorrect, Integer score, String feedback);
    
    /**
     * 批量教师手动批改考试答案
     * @param examId 考试ID
     * @param studentId 学生ID
     * @param scores 分数列表
     * @param feedbacks 反馈列表
     * @return 批改成功的数量
     */
    int batchTeacherGradeExamAnswers(Long examId, Long studentId,List<Boolean> isCorrects, List<Integer> scores, List<String> feedbacks);
    
    /**
     * 获取考试答案详情
     * @param examAnswerId 考试答案ID
     * @return 考试答案详情
     */
    ExamAnswer getExamAnswerById(Long examAnswerId);
    
    /**
     * 获取学生在某次考试中的所有答案
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 考试答案列表
     */
    List<ExamAnswer> getExamAnswersByExamAndStudent(Long examId, Long studentId);
    
    /**
     * 获取某次考试的所有答案
     * @param examId 考试ID
     * @return 考试答案列表
     */
    List<ExamAnswer> getExamAnswersByExam(Long examId);
    
    /**
     * 更新考试答案状态
     * @param examAnswerId 考试答案ID
     * @param status 状态
     * @return 更新结果
     */
    int updateExamAnswerStatus(Long examAnswerId, String status);
    
    /**
     * 混合模式评分（支持选择不同的AI模型）
     * @param examAnswerId 考试答案ID
     * @param aiModel AI模型名称
     * @return 批改结果
     */
    AIGeneratedResult hybridGradeExamAnswer(Long examAnswerId, String aiModel) throws IOException;
}