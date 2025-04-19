package com.edusystem.mapper;

import com.edusystem.model.StudentLearningBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生学习行为Mapper
 */
@Mapper
public interface StudentLearningBehaviorMapper {
    
    /**
     * 插入学习行为记录
     */
    int insert(StudentLearningBehavior behavior);
    
    /**
     * 根据ID查询学习行为
     */
    StudentLearningBehavior selectById(Long id);
    
    /**
     * 根据学生ID和行为类型查询学习行为
     */
    List<StudentLearningBehavior> selectByStudentIdAndType(
            @Param("studentId") Long studentId, 
            @Param("behaviorType") String behaviorType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计学生某类行为的次数
     */
    int countByStudentIdAndType(
            @Param("studentId") Long studentId, 
            @Param("behaviorType") String behaviorType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计学生某类行为的总时长
     */
    int sumDurationByStudentIdAndType(
            @Param("studentId") Long studentId, 
            @Param("behaviorType") String behaviorType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询班级学生的学习行为
     */
    List<StudentLearningBehavior> selectByClassId(
            @Param("classId") Integer classId,
            @Param("behaviorType") String behaviorType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}