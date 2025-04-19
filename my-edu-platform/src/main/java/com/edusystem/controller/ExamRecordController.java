package com.edusystem.controller;

import com.edusystem.model.Result;
import com.edusystem.service.ExamRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 考试记录控制器
 */
@Slf4j
@RestController
@RequestMapping("/exam/record")
@Tag(name = "考试记录管理")
public class ExamRecordController {

    @Autowired
    private ExamRecordService examRecordService;

    /**
     * 开始考试
     */
    @Operation(summary = "开始考试，创建考试记录")
    @PostMapping("/start")
    public Result startExam(@RequestParam Long examId, @RequestParam Long studentId) {
        log.info("开始考试 examId:{}, studentId:{}", examId, studentId);
        return examRecordService.startExam(examId, studentId);
    }

    /**
     * 提交考试
     */
    @Operation(summary = "提交考试，更新考试记录状态")
    @PostMapping("/submit")
    public Result submitExam(@RequestParam Long examId, @RequestParam Long studentId) {
        log.info("提交考试 examId:{}, studentId:{}", examId, studentId);
        return examRecordService.submitExam(examId, studentId);
    }

    /**
     * 更新考试总分
     */
    @Operation(summary = "更新考试总分")
    @PostMapping("/update-score")
    public Result updateTotalScore(@RequestParam Long examId, 
                                  @RequestParam Long studentId, 
                                  @RequestParam BigDecimal totalScore) {
        log.info("更新考试总分 examId:{}, studentId:{}, totalScore:{}", examId, studentId, totalScore);
        return examRecordService.updateTotalScore(examId, studentId, totalScore);
    }

    /**
     * 获取考试的所有考试记录
     */
    @Operation(summary = "获取考试的所有考试记录")
    @GetMapping("/exam/{examId}")
    public Result getExamRecords(@PathVariable Long examId) {
        log.info("获取考试的所有考试记录 examId:{}", examId);
        return examRecordService.getExamRecords(examId);
    }

    /**
     * 获取学生的所有考试记录
     */
    @Operation(summary = "获取学生的所有考试记录")
    @GetMapping("/student/{studentId}")
    public Result getStudentExamRecords(@PathVariable Long studentId) {
        log.info("获取学生的所有考试记录 studentId:{}", studentId);
        return examRecordService.getStudentExamRecords(studentId);
    }

    /**
     * 获取考试记录详情
     */
    @Operation(summary = "获取考试记录详情")
    @GetMapping("/detail")
    public Result getExamRecordDetail(@RequestParam Long examId, @RequestParam Long studentId) {
        log.info("获取考试记录详情 examId:{}, studentId:{}", examId, studentId);
        return examRecordService.getExamRecordDetail(examId, studentId);
    }
}