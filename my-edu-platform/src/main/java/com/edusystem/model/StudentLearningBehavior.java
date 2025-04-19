package com.edusystem.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学生学习行为记录
 */
@Data
public class StudentLearningBehavior {
    private Long id;
    private Long studentId;
    private String behaviorType; // LOGIN, STUDY, ASSIGNMENT, EXAM, RESOURCE
    private String resourceType; // 资源类型：COURSE, VIDEO, DOCUMENT, QUESTION
    private Long resourceId; // 资源ID
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime; // 结束时间
    private Integer duration; // 持续时间（秒）
    private String deviceInfo; // 设备信息
    private String ipAddress; // IP地址
    private LocalDateTime createdAt;
    private Integer isDeleted;
}