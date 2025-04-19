package com.edusystem.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentSubmissionDTO {
    private Integer assignmentId;
    private Long studentId;
    private List<StudentAnswerDTO> answers;
}
