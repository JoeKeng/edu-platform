package com.edusystem.mapper;

import com.edusystem.model.QuestionBank;
import org.apache.ibatis.annotations.*;

import java.util.List;

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
}
