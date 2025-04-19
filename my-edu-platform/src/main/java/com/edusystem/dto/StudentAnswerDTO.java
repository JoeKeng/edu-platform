package com.edusystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerDTO {
    private Long questionId;
    private String answer;
    private List<String> fileUrls; // 附件URL
}