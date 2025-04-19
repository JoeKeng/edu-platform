package com.edusystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class AIGradeRequestDTO {
    private Integer assignmentId; // 作业ID
    private Long studentId; // 学生ID
    private List<Long> answerIds; // 学生答案ID列表
}
