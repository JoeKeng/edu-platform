package com.edusystem.controller;

import com.edusystem.model.Result;
import com.edusystem.model.StudentLearningAnalysis;
import com.edusystem.service.StudentDataAnalysisService;
import com.edusystem.service.StudentDataCollectionService;
import com.edusystem.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 学生学习分析控制器
 */
@RestController
@RequestMapping("/student-analysis")
@Slf4j
@Tag(name = "学生学习分析")
public class StudentAnalysisController {

    @Autowired
    private StudentDataAnalysisService analysisService;
    
//    @Autowired
//    private StudentDataCollectionService collectionService;



    /**
     * 分析学生学习习惯
     */
    @Operation(summary = "分析学生学习习惯")
    @GetMapping("/{studentId}/learning-habit")
    public Result analyzeLearningHabit(
            @PathVariable Long studentId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        log.info("分析学生学习习惯: studentId={}, startDate={}, endDate={}", 
                studentId, startDate, endDate);
        
        try {
            LocalDateTime startTime = startDate != null ? 
                    LocalDateTime.parse(startDate + "T00:00:00") : 
                    LocalDateTime.now().minusDays(30);
            
            LocalDateTime endTime = endDate != null ? 
                    LocalDateTime.parse(endDate + "T23:59:59") : 
                    LocalDateTime.now();
            
            Map<String, Object> result = analysisService.analyzeLearningHabit(studentId, startTime, endTime , true);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分析学生学习习惯失败", e);
            return Result.error("分析失败: " + e.getMessage());
        }
    }

    /**
     * 分析学生优势劣势
     */
    @Operation(summary = "分析学生优势劣势")
    @GetMapping("/{studentId}/strength-weakness")
    public Result analyzeStrengthWeakness(@PathVariable Long studentId) {
        log.info("分析学生优势劣势: studentId={}", studentId);
        
        try {
            Map<String, Object> result = analysisService.analyzeStrengthWeakness(studentId, true);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分析学生优势劣势失败", e);
            return Result.error("分析失败: " + e.getMessage());
        }
    }

    /**
     * 生成学习建议
     */
    @Operation(summary = "生成学习建议")
    @GetMapping("/{studentId}/recommendations")
    public Result generateRecommendations(@PathVariable Long studentId) {
        log.info("生成学习建议: studentId={}", studentId);
        
        try {
            List<String> recommendations = analysisService.generateRecommendations(studentId , true);
            return Result.success(recommendations);
        } catch (Exception e) {
            log.error("生成学习建议失败", e);
            return Result.error("生成失败: " + e.getMessage());
        }
    }

    /**
     * 综合分析学生学习情况
     */
    @Operation(summary = "综合分析学生学习情况")
    @GetMapping("/{studentId}/comprehensive")
    public Result comprehensiveAnalysis(@PathVariable Long studentId) {
        log.info("综合分析学生学习情况: studentId={}", studentId);
        return analysisService.comprehensiveAnalysis(studentId);
    }

    /**
     * 分析班级学习情况
     */
    @Operation(summary = "分析班级学习情况")
    @GetMapping("/class/{classId}")
    public Result classAnalysis(@PathVariable Integer classId) {
        log.info("分析班级学习情况: classId={}", classId);
        return analysisService.classAnalysis(classId);
    }

    /**
     * 获取学生历史分析结果
     */
    @Operation(summary = "获取学生历史分析结果")
    @GetMapping("/{studentId}/history")
    public Result getStudentAnalysisHistory(
            @PathVariable Long studentId,
            @RequestParam(required = false) String analysisType) {
        
        log.info("获取学生历史分析结果: studentId={}, analysisType={}", studentId, analysisType);
        
        try {
            StudentLearningAnalysis analysis = analysisService.getStudentAnalysis(studentId, analysisType);
            return Result.success(analysis);
        } catch (Exception e) {
            log.error("获取学生历史分析结果失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 分析学生学习进度
     */
    @Operation(summary = "分析学生学习进度")
    @GetMapping("/{studentId}/learning-progress")
    public Result analyzeLearningProgress(
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer courseId) {
        
        log.info("分析学生学习进度: studentId={}, courseId={}", studentId, courseId);
        
        try {
            Map<String, Object> result = analysisService.analyzeLearningProgress(studentId, courseId,true);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分析学生学习进度失败", e);
            return Result.error("分析失败: " + e.getMessage());
        }
    }
}