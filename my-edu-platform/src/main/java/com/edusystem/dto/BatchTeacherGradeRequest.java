package com.edusystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchTeacherGradeRequest {
    private Long assignmentId;
    private List<TeacherGradeDTO> grades;
}

