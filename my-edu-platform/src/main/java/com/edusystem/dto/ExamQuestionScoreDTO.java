package com.edusystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "考试题目分值更新DTO")
public class ExamQuestionScoreDTO {
    
    @Schema(description = "考试ID")
    private Long examId;
    
    @Schema(description = "题目ID")
    private Long examQuestionId;
    
    @Schema(description = "分值")
    private Integer score;
}