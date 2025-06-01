package com.edusystem.service;

import com.edusystem.model.PageResult;
import com.edusystem.model.QuestionBank;
import com.edusystem.model.Result;

import java.util.List;

public interface QuestionBankService {
    int addQuestion(QuestionBank question);

    QuestionBank getQuestionById(Long id);

    List<QuestionBank> getQuestionsByCourseAndChapter(Integer courseId, Integer chapterId);

    int updateQuestion(QuestionBank question);

    int deleteQuestion(Long id);

    PageResult<QuestionBank> pageQuestions(Integer page, Integer pageSize, String type, String difficulty, Integer courseId, Integer chapterId);

    Result getQuestionByKnowledgePointId(List<Long> knowledgePointIds);

    PageResult<QuestionBank> pageQuestionByKnowledgePointId(Integer page, Integer pageSize, List<Long> knowledgePointIds, String type, String difficulty);
}

