package com.edusystem.controller;

import com.edusystem.model.Result;
import com.edusystem.service.StudentDataCollectionService;
import com.edusystem.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student-data")
@Slf4j
@Tag(name = "学生数据采集")
public class StudentDataCollectionController {

    @Autowired
    private StudentDataCollectionService collectionService;
    /**
     * 记录学生学习行为
     */
    @Operation(summary = "记录学生学习行为")
    @PostMapping("/record-behavior")
    public Result recordStudyBehavior(
            @RequestParam Long studentId,
            @RequestParam String resourceType,
            @RequestParam Long resourceId,
            @RequestParam Integer duration,
            HttpServletRequest request) {
        
        // 设备信息和IP地址，则从请求中获取
            String deviceInfo = RequestUtil.getDeviceInfo(request);
        
            String ipAddress = RequestUtil.getClientIP(request);
        
        
        log.info("记录学生学习行为: studentId={}, resourceType={}, resourceId={}, duration={}秒",
                studentId, resourceType, resourceId, duration);
        
        try {
            collectionService.recordStudyBehavior(studentId, resourceType, resourceId, 
                    duration, deviceInfo, ipAddress);
            return Result.success("记录成功");
        } catch (Exception e) {
            log.error("记录学生学习行为失败", e);
            return Result.error("记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 记录学生登录行为
     */
    @Operation(summary = "记录学生登录行为")
    @PostMapping("/record-login")
    public Result recordLoginBehavior(
            @RequestParam Long studentId,
            HttpServletRequest request) {
        
        // 设备信息和IP地址，则从请求中获取
        String deviceInfo = RequestUtil.getDeviceInfo(request);
        
        String ipAddress = RequestUtil.getClientIP(request);
        
        log.info("记录学生登录行为: studentId={}", studentId);
        
        try {
            collectionService.recordLoginBehavior(studentId, deviceInfo, ipAddress);
            return Result.success("记录成功");
        } catch (Exception e) {
            log.error("记录学生登录行为失败", e);
            return Result.error("记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 记录学生作业提交行为
     */
    @Operation(summary = "记录学生作业提交行为")
    @PostMapping("/record-assignment")
    public Result recordAssignmentSubmission(
            @RequestParam Long studentId,
            @RequestParam Long assignmentId,
            @RequestParam Long questionId) {
        
        log.info("记录学生作业提交行为: studentId={}, assignmentId={}, questionId={}", 
                studentId, assignmentId, questionId);
        
        try {
            collectionService.recordAssignmentSubmission(studentId, assignmentId, questionId);
            return Result.success("记录成功");
        } catch (Exception e) {
            log.error("记录学生作业提交行为失败", e);
            return Result.error("记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 记录学生考试行为
     */
    @Operation(summary = "记录学生考试行为")
    @PostMapping("/record-exam")
    public Result recordExamBehavior(
            @RequestParam Long studentId,
            @RequestParam Long examId) {
        
        log.info("记录学生考试行为: studentId={}, examId={}", studentId, examId);
        
        try {
            collectionService.recordExamBehavior(studentId, examId);
            return Result.success("记录成功");
        } catch (Exception e) {
            log.error("记录学生考试行为失败", e);
            return Result.error("记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 记录学生资源访问行为
     */
    @Operation(summary = "记录学生资源访问行为")
    @PostMapping("/record-resource")
    public Result recordResourceAccess(
            @RequestParam Long studentId,
            @RequestParam String resourceType,
            @RequestParam Long resourceId,
            @RequestParam Integer duration,
            HttpServletRequest request) {
        
        // 设备信息和IP地址，则从请求中获取
        String deviceInfo = RequestUtil.getDeviceInfo(request);
        
        String ipAddress = RequestUtil.getClientIP(request);
        
        
        log.info("记录学生资源访问行为: studentId={}, resourceType={}, resourceId={}, duration={}秒",
                studentId, resourceType, resourceId, duration);
        
        try {
            collectionService.recordResourceAccess(studentId, resourceType, resourceId, 
                    duration, deviceInfo, ipAddress);
            return Result.success("记录成功");
        } catch (Exception e) {
            log.error("记录学生资源访问行为失败", e);
            return Result.error("记录失败: " + e.getMessage());
        }
    }
}