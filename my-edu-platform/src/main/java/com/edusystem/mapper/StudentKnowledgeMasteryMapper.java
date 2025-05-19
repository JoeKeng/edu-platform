package com.edusystem.mapper;

import com.edusystem.model.StudentKnowledgeMastery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 学生知识点掌握情况Mapper
 */
@Mapper
public interface StudentKnowledgeMasteryMapper {

    //TODO知识点掌握的操作
    /**
     * 插入知识点掌握情况
     */
    int insert(StudentKnowledgeMastery mastery);

    /**
     * 更新知识点掌握情况
     */
    int update(StudentKnowledgeMastery mastery);

    /**
     * 根据学生ID和知识点ID查询掌握情况
     */
    StudentKnowledgeMastery selectByStudentAndKnowledge(
            @Param("studentId") Long studentId,
            @Param("knowledgePointId") Long knowledgePointId);

    /**
     * 根据学生ID查询所有知识点掌握情况
     */
    List<Map<String, Object>> selectAllMasteryByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据学生ID和课程ID查询知识点掌握情况
     */
    List<Map<String, Object>> selectCourseMasteryByStudentId(
            @Param("studentId") Long studentId,
            @Param("courseId") Integer courseId);

    /**
     * 根据知识点ID查询所有学生掌握情况
     */
    List<StudentKnowledgeMastery> selectByKnowledgePointId(Long knowledgePointId);

    /**
     * 根据班级ID查询知识点掌握情况
     */
    List<StudentKnowledgeMastery> selectByClassId(
            @Param("classId") Integer classId,
            @Param("knowledgePointId") Long knowledgePointId);
            
    /**
     * 获取班级知识点掌握情况统计
     */
    Map<String, Object> getClassKnowledgePointStats(
            @Param("classId") Integer classId,
            @Param("knowledgePointId") Long knowledgePointId);
}