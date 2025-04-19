package com.edusystem.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Exam {
    private Long id;
    private String title;        // 考试名称
    private String description;     // 考试描述
    private List<Integer> classIds;  // 班级ID列表
    private Integer courseId;       // 所属课程ID
    private Integer chapterId;      // 所属章节ID
    private Long teacherId;         // 出题教师ID
    private LocalDateTime startTime; // 考试开始时间
    private LocalDateTime endTime;   // 考试结束时间
    private Integer duration;       // 考试时长（分钟）
    private Integer totalScore;     // 总分
    private String examType;        // 考试类型（如：期中考试、期末考试、随堂测验等）
    private String status;      // 考试状态（未开始、进行中、已结束）
    // private Boolean isAutoSubmit;   // 是否自动提交
    private String examRules;       // 考试规则
    private LocalDateTime createAt; // 创建时间
    private LocalDateTime updateAt; // 更新时间
}