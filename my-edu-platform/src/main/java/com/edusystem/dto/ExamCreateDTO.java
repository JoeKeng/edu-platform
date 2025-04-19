package com.edusystem.dto;

import com.edusystem.model.Exam;
import com.edusystem.model.QuestionBank;

import lombok.Data;

import java.util.List;

@Data
public class ExamCreateDTO {
    private Exam exam;
    private List<Long> questionIds; // 题目ID列表（用于前端传参）
    private List<QuestionBank> newQuestions;//问题列表（用于后端处理）
}