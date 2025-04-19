package com.edusystem.model;

import lombok.Data;

@Data
public class ExamQuestion {
    private Long id;
    private Long examId;          // 考试ID
    private Long questionId;      // 题目ID
    private Integer score;        // 题目分值
    private Integer orderNum;     // 题目顺序
    private String questionType;  // 题目类型（单选、多选、填空、主观题等）
    private String section;       // 所属试卷部分（如：选择题部分、主观题部分等）
    private QuestionBank questionBank; // 关联的题库题目

}