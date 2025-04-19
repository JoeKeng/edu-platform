package com.edusystem.mapper;

import com.edusystem.model.ExamRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试记录Mapper接口
 */
@Mapper
public interface ExamRecordMapper {
    
    /**
     * 插入考试记录
     * @param record 考试记录
     * @return 影响行数
     */
    int insert(ExamRecord record);
    
    /**
     * 更新考试记录
     * @param record 考试记录
     * @return 影响行数
     */
    int update(ExamRecord record);
    
    /**
     * 根据考试ID和学生ID查询考试记录
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 考试记录
     */
    ExamRecord getByExamAndStudent(@Param("examId") Long examId, @Param("studentId") Long studentId);
    
    /**
     * 根据考试ID和学生ID更新考试记录状态
     * @param examId 考试ID
     * @param studentId 学生ID
     * @param status 状态
     * @param submitTime 提交时间
     * @return 影响行数
     */
    int updateStatus(@Param("examId") Long examId, 
                     @Param("studentId") Long studentId, 
                     @Param("status") String status,
                     @Param("submitTime") LocalDateTime submitTime);
    
    /**
     * 根据考试ID查询所有考试记录
     * @param examId 考试ID
     * @return 考试记录列表
     */
    List<ExamRecord> getByExamId(@Param("examId") Long examId);
    
    /**
     * 根据学生ID查询所有考试记录
     * @param studentId 学生ID
     * @return 考试记录列表
     */
    List<ExamRecord> getByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 更新考试总分
     * @param examId 考试ID
     * @param studentId 学生ID
     * @param totalScore 总分
     * @return 影响行数
     */
    int updateTotalScore(@Param("examId") Long examId, 
                         @Param("studentId") Long studentId, 
                         @Param("totalScore") java.math.BigDecimal totalScore);
}