package com.edusystem.mapper;

import com.edusystem.model.StudentLearningAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生学习分析Mapper
 */
@Mapper
public interface StudentLearningAnalysisMapper {
    
    /**
     * 插入分析结果
     */
    int insert(StudentLearningAnalysis analysis);
    
    /**
     * 更新分析结果
     */
    int update(StudentLearningAnalysis analysis);
    
    /**
     * 根据ID查询分析结果
     */
    StudentLearningAnalysis selectById(Long id);
    
    /**
     * 根据学生ID和分析类型查询最新分析结果
     */
    StudentLearningAnalysis selectByStudentIdAndType(
            @Param("studentId") Long studentId, 
            @Param("analysisType") String analysisType);
    
    /**
     * 根据学生ID查询所有分析结果
     */
    List<StudentLearningAnalysis> selectByStudentId(
            @Param("studentId") Long studentId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据班级ID查询分析结果
     */
    List<StudentLearningAnalysis> selectByClassId(
            @Param("classId") Integer classId,
            @Param("analysisType") String analysisType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}