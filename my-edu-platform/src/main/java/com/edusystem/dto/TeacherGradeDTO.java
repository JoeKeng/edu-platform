package com.edusystem.dto;

import lombok.Data;

@Data
public class TeacherGradeDTO {
    private Long studentAnswerId;
    private Double teacherScore;
    private String teacherFeedback;
}
