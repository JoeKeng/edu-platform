package com.edusystem.service;

import com.edusystem.dto.ExamCreateDTO;
import com.edusystem.model.Exam;
import com.edusystem.model.ExamQuestion;
import com.edusystem.model.Result;

import java.util.List;

public interface ExamService {
    // 创建考试（旧方法）
    Result createExam(Exam exam, List<ExamQuestion> questions);
    
    // 创建考试（新方法：支持从题库选择题目和创建新题目）
    Result createExam(ExamCreateDTO examCreateDTO);

    // 更新考试信息
    Result updateExam(Exam exam);

    // 删除考试
    Result deleteExam(Long id);

    // 根据ID获取考试信息
    Result getExamById(Long id);

    // 获取课程的所有考试
    Result getExamsByCourse(Integer courseId);

    // 获取章节的所有考试
    Result getExamsByChapter(Integer chapterId);

    // 获取教师创建的所有考试
    Result getExamsByTeacher(Long teacherId);

    // 更新考试状态
    Result updateExamStatus(Long id, String status);

    // 添加考试题目
    Result addExamQuestions(Long examId, List<ExamQuestion> questions);

    // 更新考试题目
    Result updateExamQuestions(List<ExamQuestion> questions);

    // 删除考试题目
    Result deleteExamQuestion(Long questionId);

    // 获取考试的所有题目
    Result getExamQuestions(Long examId);

    // 更新题目顺序
    Result updateQuestionOrder(Long id, Integer orderNum);

    // 批量更新题目分值
    Result batchUpdateQuestionScores(List<ExamQuestion> updates);
}