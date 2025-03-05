package com.edusystem.service;

import com.edusystem.model.QuestionBank;
import java.util.List;

public interface QuestionBankService {
    int addQuestion(QuestionBank question);
    QuestionBank getQuestionById(Long id);
    List<QuestionBank> getQuestionsByCourseAndChapter(Integer courseId, Integer chapterId);
    int updateQuestion(QuestionBank question);
    int deleteQuestion(Long id);
}

