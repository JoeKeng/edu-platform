package com.edusystem.service;

import com.edusystem.model.Result;

import java.math.BigDecimal;

/**
 * 考试记录服务接口
 */
public interface ExamRecordService {
    
    /**
     * 开始考试，创建考试记录
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 操作结果
     */
    Result startExam(Long examId, Long studentId);
    
    /**
     * 提交考试，更新考试记录状态
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 操作结果
     */
    Result submitExam(Long examId, Long studentId);
    
    /**
     * 更新考试总分
     * @param examId 考试ID
     * @param studentId 学生ID
     * @param totalScore 总分
     * @return 操作结果
     */
    Result updateTotalScore(Long examId, Long studentId, BigDecimal totalScore);
    
    /**
     * 获取考试的所有考试记录
     * @param examId 考试ID
     * @return 考试记录列表
     */
    Result getExamRecords(Long examId);
    
    /**
     * 获取学生的所有考试记录
     * @param studentId 学生ID
     * @return 考试记录列表
     */
    Result getStudentExamRecords(Long studentId);
    
    /**
     * 获取考试记录详情
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 考试记录详情
     */
    Result getExamRecordDetail(Long examId, Long studentId);
}