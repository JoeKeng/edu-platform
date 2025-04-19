package com.edusystem.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExamClass {
    private Long id;
    private Long examId;           // 考试ID
    private Integer classId;        // 班级ID
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}