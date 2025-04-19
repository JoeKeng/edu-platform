package com.edusystem.service;

import com.edusystem.model.StudentLearningBehavior;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生数据采集服务
 */
public interface StudentDataCollectionService {
    
    /**
     * 记录学生登录行为
     */
    void recordLoginBehavior(Long studentId, String deviceInfo, String ipAddress);
    
    /**
     * 记录学生学习行为
     */
    void recordStudyBehavior(Long studentId, String resourceType, Long resourceId, 
                            Integer duration, String deviceInfo, String ipAddress);
    
    /**
     * 记录学生作业提交行为
     */
    void recordAssignmentSubmission(Long studentId, Long assignmentId, Long questionId);
    
    /**
     * 记录学生考试行为
     */
    void recordExamBehavior(Long studentId, Long examId);
    
    /**
     * 记录学生资源访问行为
     */
    void recordResourceAccess(Long studentId, String resourceType, Long resourceId, 
                             Integer duration, String deviceInfo, String ipAddress);
    
    /**
     * 获取学生学习行为数据
     */
    StudentLearningBehavior getStudentBehavior(Long behaviorId);
    
    /**
     * 获取学生所有学习行为数据
     */
    List<StudentLearningBehavior> getStudentBehaviors(Long studentId, String behaviorType,
                                                      LocalDateTime startTime, LocalDateTime endTime);
}