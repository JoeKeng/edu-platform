package com.edusystem.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExamAnswer {
    private Long id;
    private Long examId;          // 考试ID
    private Long studentId;       // 学生ID
    private Long questionId;      // 题目ID
    private String answer;        // 学生答案
    private Integer score;        // 得分
    private String status;        // 答题状态（未提交、已提交、已批改）
    private Boolean isCorrect ;    // 是否正确
    private String aiFeedback;      // AI反馈
    private String teacherFeedback; // 教师反馈
    private String explanation;     // 解析
    private LocalDateTime submitTime;    // 提交时间
    private LocalDateTime lastSaveTime;  // 最后保存时间
    private Integer attemptCount;        // 修改次数
    private Boolean isSubmitted;         // 是否已提交
    private String browserInfo;          // 浏览器信息
    private String ipAddress;           // IP地址
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}