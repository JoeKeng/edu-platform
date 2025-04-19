package com.edusystem.mapper;

import com.edusystem.model.QuestionBank;
import org.apache.ibatis.annotations.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mapper
public interface QuestionBankMapper {

//
    @Insert("INSERT INTO question_bank (course_id, chapter_id, teacher_id, type, question_text, correct_answer, analysis, difficulty, options) " +
            "VALUES (#{courseId}, #{chapterId}, #{teacherId}, #{type}, #{questionText}, #{correctAnswer}, #{analysis}, #{difficulty}, #{options})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(QuestionBank question);

    @Select("SELECT * FROM question_bank WHERE id = #{id}")
    QuestionBank getById(Long id);

    @Select("SELECT * FROM question_bank WHERE course_id = #{courseId} AND chapter_id = #{chapterId}")
    List<QuestionBank> getByCourseAndChapter(@Param("courseId") Integer courseId, @Param("chapterId") Integer chapterId);

    @Update("UPDATE question_bank SET type = #{type} , question_text = #{questionText}, options = #{options} , correct_answer = #{correctAnswer}, analysis = #{analysis}, difficulty = #{difficulty} WHERE id = #{id}")
    int update(QuestionBank question);

    @Delete("DELETE FROM question_bank WHERE id = #{id}")
    int delete(Long id);

    // 通过多对多关系查询作业的所有题目
    @Select("SELECT q.* FROM question_bank q " +
            "INNER JOIN assignment_question aq ON q.id = aq.question_id " +
            "WHERE aq.assignment_id = #{assignmentId} ORDER BY aq.question_order")
    List<QuestionBank> findByAssignmentId(Integer assignmentId);


    List<QuestionBank> batchGetByIds(List<Long> question);

    //TODO:题目知识点模块
    /**
     * 获取题目关联的知识点
     */
    List<Map<String, Object>> getKnowledgePointsByQuestionId(Long questionId);
    
    /**
     * 获取课程关联的知识点
     */
    List<Map<String, Object>> getKnowledgePointsByCourseId(Integer courseId);
    
    /**
     * 添加题目知识点关联
     */
    int addQuestionKnowledgePoint(Long questionId, Long knowledgePointId, Double weight);
    
    /**
     * 删除题目知识点关联
     */
    int deleteQuestionKnowledgePoint(Long questionId, Long knowledgePointId);
    
    /**
     * 更新题目知识点关联权重
     */
    int updateQuestionKnowledgePointWeight(Long questionId, Long knowledgePointId, Double weight);
}
