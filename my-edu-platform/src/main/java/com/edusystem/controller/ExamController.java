package com.edusystem.controller;

import com.edusystem.dto.ExamCreateDTO;
import com.edusystem.model.Exam;
import com.edusystem.model.ExamQuestion;
import com.edusystem.model.Result;
import com.edusystem.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.edusystem.dto.ExamQuestionScoreDTO;
import java.util.stream.Collectors;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "考试管理")
public class ExamController {

    @Autowired
    private ExamService examService;

    // 创建考试
    @Operation(summary = "创建考试")
    @PostMapping("/teacher/exam/create")
    public Result createExam(@RequestBody ExamCreateDTO examDTO) {
        log.info("创建考试 examDTO:{}", examDTO);
        return examService.createExam(examDTO);
    }

    // 更新考试信息
    @Operation(summary = "更新考试信息")
    @PutMapping("/teacher/exam/update")
    public Result updateExam(@RequestBody Exam exam) {
        log.info("更新考试信息 exam:{}", exam);
        return examService.updateExam(exam);
    }

    // 删除考试
    @Operation(summary = "删除考试")
    @DeleteMapping("/teacher/exam/delete/{id}")
    public Result deleteExam(@PathVariable Long id) {
        log.info("删除考试 id:{}", id);
        return examService.deleteExam(id);
    }

    // 根据ID获取考试信息
    @Operation(summary = "根据ID获取考试信息")
    @GetMapping("/exam/get/{id}")
    public Result getExam(@PathVariable Long id) {
        log.info("获取考试信息 id:{}", id);
        return examService.getExamById(id);
    }

    // 获取课程的所有考试
    @Operation(summary = "获取课程的所有考试")
    @GetMapping("/exam/list/course/{courseId}")
    public Result getExamsByCourse(@PathVariable Integer courseId) {
        log.info("获取课程的所有考试 courseId:{}", courseId);
        return examService.getExamsByCourse(courseId);
    }

    // 获取章节的所有考试
    @Operation(summary = "获取章节的所有考试")
    @GetMapping("/exam/list/chapter/{chapterId}")
    public Result getExamsByChapter(@PathVariable Integer chapterId) {
        log.info("获取章节的所有考试 chapterId:{}", chapterId);
        return examService.getExamsByChapter(chapterId);
    }

    // 获取教师创建的所有考试
    @Operation(summary = "获取教师创建的所有考试")
    @GetMapping("/teacher/exam/list/{teacherId}")
    public Result getTeacherExams(@PathVariable Long teacherId) {
        log.info("获取教师创建的所有考试 teacherId:{}", teacherId);
        return examService.getExamsByTeacher(teacherId);
    }

    // 更新考试状态
    @Operation(summary = "更新考试状态")
    @PutMapping("/teacher/exam/status")
    public Result updateExamStatus(@RequestParam Long id, @RequestParam String status) {
        log.info("更新考试状态 id:{}, status:{}", id, status);
        return examService.updateExamStatus(id, status);
    }

    // 添加考试题目
    @Operation(summary = "添加考试题目")
    @PostMapping("/teacher/exam/questions/add")
    public Result addExamQuestions(@RequestParam Long examId, @RequestBody List<ExamQuestion> questions) {
        log.info("添加考试题目 examId:{}, questions:{}", examId, questions);
        return examService.addExamQuestions(examId, questions);
    }

    // 更新考试题目
    @Operation(summary = "更新考试题目")
    @PutMapping("/teacher/exam/questions/update")
    public Result updateExamQuestions(@RequestBody List<ExamQuestion> questions) {
        log.info("更新考试题目 questions:{}", questions);
        return examService.updateExamQuestions(questions);
    }

    // 删除考试题目
    @Operation(summary = "删除考试题目")
    @DeleteMapping("/teacher/exam/question/{questionId}")
    public Result deleteExamQuestion(@PathVariable Long questionId) {
        log.info("删除考试题目 questionId:{}", questionId);
        return examService.deleteExamQuestion(questionId);
    }

    // 获取考试的所有题目
    @Operation(summary = "获取考试的所有题目")
    @GetMapping("/exam/questions/{examId}")
    public Result getExamQuestions(@PathVariable Long examId) {
        log.info("获取考试的所有题目 examId:{}", examId);
        return examService.getExamQuestions(examId);
    }

    // 更新题目顺序
    @Operation(summary = "更新题目顺序")
    @PutMapping("/teacher/exam/question/order")
    public Result updateQuestionOrder(@RequestParam Long id, @RequestParam Integer orderNum) {
        log.info("更新题目顺序 id:{}, orderNum:{}", id, orderNum);
        return examService.updateQuestionOrder(id, orderNum);
    }

    // 批量更新题目分值
    @Operation(summary = "批量更新题目分值")
    @PutMapping("/teacher/exam/questions/scores")
    public Result batchUpdateQuestionScores(@RequestBody List<ExamQuestionScoreDTO> updates) {
        log.info("批量更新题目分值 updates:{}", updates);
        
        // 转换DTO为实体对象
        List<ExamQuestion> questions = updates.stream()
                .map(dto -> {
                    ExamQuestion question = new ExamQuestion();
                    question.setExamId(dto.getExamId());
                    question.setId(dto.getExamQuestionId());
                    question.setScore(dto.getScore());
                    return question;
                })
                .collect(Collectors.toList());
                
        return examService.batchUpdateQuestionScores(questions);
    }
}