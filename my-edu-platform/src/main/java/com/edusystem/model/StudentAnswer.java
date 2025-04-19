package com.edusystem.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StudentAnswer {
    private Long id; // 作答ID
    private Long studentId; // 学生ID
    private Long questionId; // 题目ID
    private Integer assignmentId;// 作业ID
    private String answer; // 学生提交的答案
    private Double aiScore; // 得分（可能为空，表示未批改）
    private Double teacherScore;// 老师给的分数（可能为空，表示未批改）
    private String aiFeedback; // AI 的反馈
    private String teacherFeedback;// 教师反馈
    private Boolean isCorrect; // 是否正确（AI 评分用）
    private Date submittedAt; // 提交时间
    private List<String> fileUrls; // 附件URL
}

