package com.edusystem.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AssignmentQuestionMapper {

    /**
     * 在assignment_question表中插入一条新记录
     *
     * @param assignmentId 作业的ID
     * @param questionId 题目的ID
     * @param questionOrder 题目在作业中的顺序
     * @return 插入操作影响的行数
     */
    @Insert("INSERT INTO assignment_question (assignment_id, question_id, question_order) VALUES (#{assignmentId}, #{questionId}, #{questionOrder})")
    int insert(@Param("assignmentId") Integer assignmentId, @Param("questionId") Long questionId, @Param("questionOrder") Integer questionOrder);

    // 根据 assignment_id 查询 question_id
    @Select("SELECT question_id FROM assignment_question WHERE assignment_id = #{assignmentId} ORDER BY question_order")
    List<Long> getQuestionsByAssignmentId(@Param("assignmentId") Integer assignmentId);

    @Delete("DELETE FROM assignment_question WHERE assignment_id = #{assignmentId}")
    int deleteByAssignmentId(@Param("assignmentId") Integer assignmentId);
}
