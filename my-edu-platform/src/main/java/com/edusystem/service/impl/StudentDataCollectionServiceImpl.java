package com.edusystem.service.impl;

import com.edusystem.mapper.StudentLearningBehaviorMapper;
import com.edusystem.model.StudentLearningBehavior;
import com.edusystem.service.StudentDataCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生数据采集服务实现类
 */
@Service
@Slf4j
public class StudentDataCollectionServiceImpl implements StudentDataCollectionService {

    @Autowired
    private StudentLearningBehaviorMapper behaviorMapper;

    @Override
    public void recordLoginBehavior(Long studentId, String deviceInfo, String ipAddress) {
        StudentLearningBehavior behavior = new StudentLearningBehavior();
        behavior.setStudentId(studentId);
        behavior.setBehaviorType("LOGIN");
        behavior.setStartTime(LocalDateTime.now());
        behavior.setEndTime(LocalDateTime.now());
        behavior.setDuration(0);
        behavior.setDeviceInfo(deviceInfo);
        behavior.setIpAddress(ipAddress);
        behavior.setCreatedAt(LocalDateTime.now());
        behavior.setIsDeleted(0);
        
        behaviorMapper.insert(behavior);
        log.info("记录学生登录行为: studentId={}", studentId);
    }

    @Override
    public void recordStudyBehavior(Long studentId, String resourceType, Long resourceId, 
                                  Integer duration, String deviceInfo, String ipAddress) {
        StudentLearningBehavior behavior = new StudentLearningBehavior();
        behavior.setStudentId(studentId);
        behavior.setBehaviorType("STUDY");
        behavior.setResourceType(resourceType);
        behavior.setResourceId(resourceId);
        behavior.setStartTime(LocalDateTime.now().minusSeconds(duration));
        behavior.setEndTime(LocalDateTime.now());
        behavior.setDuration(duration);
        behavior.setDeviceInfo(deviceInfo);
        behavior.setIpAddress(ipAddress);
        behavior.setCreatedAt(LocalDateTime.now());
        behavior.setIsDeleted(0);
        
        behaviorMapper.insert(behavior);
        log.info("记录学生学习行为: studentId={}, resourceType={}, resourceId={}, duration={}秒", 
                studentId, resourceType, resourceId, duration);
    }

    @Override
    public void recordAssignmentSubmission(Long studentId, Long assignmentId, Long questionId) {
        StudentLearningBehavior behavior = new StudentLearningBehavior();
        behavior.setStudentId(studentId);
        behavior.setBehaviorType("ASSIGNMENT");
        behavior.setResourceType("QUESTION");
        behavior.setResourceId(questionId);
        behavior.setStartTime(LocalDateTime.now());
        behavior.setEndTime(LocalDateTime.now());
        behavior.setCreatedAt(LocalDateTime.now());
        behavior.setIsDeleted(0);
        
        behaviorMapper.insert(behavior);
        log.info("记录学生作业提交行为: studentId={}, assignmentId={}, questionId={}", 
                studentId, assignmentId, questionId);
    }

    @Override
    public void recordExamBehavior(Long studentId, Long examId) {

    }

    @Override
    public void recordResourceAccess(Long studentId, String resourceType, Long resourceId, Integer duration, String deviceInfo, String ipAddress) {

    }

    //TODO: 其他方法实现...

    @Override
    public StudentLearningBehavior getStudentBehavior(Long behaviorId) {
        return behaviorMapper.selectById(behaviorId);
    }

    @Override
    public List<StudentLearningBehavior> getStudentBehaviors(Long studentId, String behaviorType, 
                                                           LocalDateTime startTime, LocalDateTime endTime) {
        return behaviorMapper.selectByStudentIdAndType(studentId, behaviorType, startTime, endTime);
    }
}