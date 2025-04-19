package com.edusystem.controller;

import com.edusystem.model.ExamAnswer;
import com.edusystem.model.Result;
import com.edusystem.service.ExamRecordService;
import com.edusystem.service.impl.ExamServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/exam/answer")
@Tag(name = "考试答题管理")
public class ExamAnswerController {

    @Autowired
    private ExamServiceImpl examService;

    @Operation(summary = "开始考试")
    @PostMapping("/start")
    public Result startExam(@RequestParam Long examId, @RequestParam Long studentId) {
        log.info("开始考试 examId:{}, studentId:{}", examId, studentId);
        return examService.startExamSession(examId, studentId);
    }

    @Operation(summary = "保存答案")
    @PostMapping("/save")
    public Result saveAnswer(@RequestParam Long examId,
                           @RequestParam Long studentId,
                           @RequestParam Long questionId,
                           @RequestParam String answer) {
        log.info("保存答案 examId:{}, studentId:{}, questionId:{}", examId, studentId, questionId);
        return examService.saveAnswer(examId, studentId, questionId, answer);
    }

    @Operation(summary = "获取答题进度")
    @GetMapping("/progress")
    public Result getProgress(@RequestParam Long examId, @RequestParam Long studentId) {
        log.info("获取答题进度 examId:{}, studentId:{}", examId, studentId);
        try {
            int answeredCount = examService.getAnsweredCount(examId, studentId);
            return Result.success(answeredCount);
        } catch (Exception e) {
            log.error("获取答题进度失败", e);
            return Result.error("获取答题进度失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取最后保存时间")
    @GetMapping("/last-saved")
    public Result getLastSavedTime(@RequestParam Long examId, @RequestParam Long studentId) {
        log.info("获取最后保存时间 examId:{}, studentId:{}", examId, studentId);
        try {
            ExamAnswer lastSaved = examService.getLastSaved(examId, studentId);
            return Result.success(lastSaved != null ? lastSaved.getLastSaveTime() : null);
        } catch (Exception e) {
            log.error("获取最后保存时间失败", e);
            return Result.error("获取最后保存时间失败：" + e.getMessage());
        }
    }

    @Operation(summary = "手动提交考试")
    @PostMapping("/submit")
    public Result submitExam(@RequestParam Long examId, @RequestParam Long studentId) {
        log.info("手动提交考试 examId:{}, studentId:{}", examId, studentId);
        try {
            // 更新答题记录状态
            examService.autoSubmitExam(examId, studentId);
            
            // 更新考试记录
            ExamRecordService examRecordService = com.edusystem.util.SpringContextUtil.getBean(ExamRecordService.class);
            if (examRecordService != null) {
                examRecordService.submitExam(examId, studentId);
            }
            
            return Result.success("提交成功");
        } catch (Exception e) {
            log.error("提交考试失败", e);
            return Result.error("提交考试失败：" + e.getMessage());
        }
    }

    @Operation(summary = "检查考试是否已提交")
    @GetMapping("/check-submitted")
    public Result checkSubmitted(@RequestParam Long examId, @RequestParam Long studentId) {
        log.info("检查考试是否已提交 examId:{}, studentId:{}", examId, studentId);
        try {
            boolean isSubmitted = examService.isSubmitted(examId, studentId);
            return Result.success(isSubmitted);
        } catch (Exception e) {
            log.error("检查考试提交状态失败", e);
            return Result.error("检查考试提交状态失败：" + e.getMessage());
        }
    }
}