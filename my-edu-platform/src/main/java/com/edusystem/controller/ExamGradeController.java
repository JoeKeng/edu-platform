package com.edusystem.controller;

import com.edusystem.dto.AIGeneratedResult;
import com.edusystem.model.ExamAnswer;
import com.edusystem.model.Result;
import com.edusystem.service.ExamAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 考试答案批改控制器
 * 处理考试答案的AI批改和教师批改功能
 */
@Slf4j
@RestController
@RequestMapping("/exam/grade")
@Tag(name = "考试批改管理")
public class ExamGradeController {

    @Autowired
    private ExamAnswerService examAnswerService;

    /**
     * AI自动批改考试答案
     * @param examAnswerId 考试答案ID
     * @return 批改结果
     */
    @Operation(summary = "AI自动批改考试答案")
    @PostMapping("/ai/{examAnswerId}")
    public Result aiGradeExamAnswer(@PathVariable Long examAnswerId) {
        log.info("AI批改考试答案 examAnswerId:{}", examAnswerId);
        try {
            AIGeneratedResult result = examAnswerService.aiGradeExamAnswer(examAnswerId);
            return Result.success("AI批改成功", result);
        } catch (IOException e) {
            log.error("AI批改失败", e);
            return Result.error("AI批改失败: " + e.getMessage());
        }
    }

    /**
     * 批量AI自动批改考试答案
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 批改结果
     */
    @Operation(summary = "批量AI自动批改考试答案")
    @PostMapping("/ai/batch")
    public Result batchAIGradeExamAnswers(
            @RequestParam Long examId,
            @RequestParam Long studentId) {
        log.info("批量AI批改考试答案 examId:{}, studentId:{}", examId, studentId);
        try {
            int count = examAnswerService.batchAIGradeExamAnswers(examId, studentId);
            return Result.success("批量AI批改成功，共批改" + count + "道题");
        } catch (IOException e) {
            log.error("批量AI批改失败", e);
            return Result.error("批量AI批改失败: " + e.getMessage());
        }
    }

    /**
     * 教师手动批改考试答案
     * @param examAnswerId 考试答案ID
     * @param score 分数
     * @param teacherFeedback 反馈
     * @return 批改结果
     */
    @Operation(summary = "教师手动批改考试答案")
    @PostMapping("/teacher/{examAnswerId}")
    public Result teacherGradeExamAnswer(
            @PathVariable Long examAnswerId,
            @RequestParam Boolean isCorrect,
            @RequestParam Integer score,
            @RequestParam String teacherFeedback) {
        log.info("教师批改考试答案 examAnswerId:{}, score:{}", examAnswerId, score);
        int result = examAnswerService.teacherGradeExamAnswer(examAnswerId,isCorrect , score, teacherFeedback);
        return result > 0 ? Result.success("教师批改成功") : Result.error("教师批改失败");
    }

    /**
     * 批量教师手动批改考试答案
     * @param examId 考试ID
     * @param studentId 学生ID
     * @param scores 分数列表
     * @param feedbacks 反馈列表
     * @return 批改结果
     */
    @Operation(summary = "批量教师手动批改考试答案")
    @PostMapping("/teacher/batch")
    public Result batchTeacherGradeExamAnswers(
            @RequestParam Long examId,
            @RequestParam Long studentId,
            @RequestParam List<Boolean> isCorrects,
            @RequestParam List<Integer> scores,
            @RequestParam List<String> feedbacks) {
        log.info("批量教师批改考试答案 examId:{}, studentId:{}", examId, studentId);
        int count = examAnswerService.batchTeacherGradeExamAnswers(examId, studentId, isCorrects, scores, feedbacks);
        return Result.success("批量教师批改成功，共批改" + count + "道题");
    }

    /**
     * 混合模式评分（支持选择不同的AI模型）
     * @param examAnswerId 考试答案ID
     * @param aiModel AI模型名称
     * @return 批改结果
     */
//    @Operation(summary = "混合模式评分")
//    @PostMapping("/hybrid/{examAnswerId}")
//    public Result hybridGradeExamAnswer(
//            @PathVariable Long examAnswerId,
//            @RequestParam String aiModel) {
//        log.info("混合模式批改考试答案 examAnswerId:{}, aiModel:{}", examAnswerId, aiModel);
//        try {
//            AIGeneratedResult result = examAnswerService.hybridGradeExamAnswer(examAnswerId, aiModel);
//            return Result.success("混合模式批改成功", result);
//        } catch (IOException e) {
//            log.error("混合模式批改失败", e);
//            return Result.error("混合模式批改失败: " + e.getMessage());
//        }
//    }

    /**
     * 获取学生在某次考试中的所有答案及批改状态
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 考试答案列表
     */
    @Operation(summary = "获取学生考试答案及批改状态")
    @GetMapping("/status/{examId}/{studentId}")
    public Result getExamGradeStatus(
            @PathVariable Long examId,
            @PathVariable Long studentId) {
        log.info("获取学生考试答案及批改状态 examId:{}, studentId:{}", examId, studentId);
        List<ExamAnswer> answers = examAnswerService.getExamAnswersByExamAndStudent(examId, studentId);
        return Result.success(answers);
    }
}