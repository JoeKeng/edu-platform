package com.edusystem.controller;

import com.edusystem.dto.AIGradeRequestDTO;
import com.edusystem.dto.AssignmentSubmissionDTO;
import com.edusystem.dto.BatchTeacherGradeRequest;
import com.edusystem.dto.StudentAnswerDTO;
import com.edusystem.mapper.QuestionBankMapper;
import com.edusystem.model.QuestionBank;
import com.edusystem.model.Result;
import com.edusystem.model.StudentAnswer;
import com.edusystem.service.StudentAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@Tag(name = "学生作答管理")
public class StudentAnswerController {

    @Autowired
    private StudentAnswerService studentAnswerService;
    @Autowired
    private QuestionBankMapper questionBankMapper;

    // 学生提交单题作答
    @Operation(summary = "学生提交单题作答")
    @PostMapping("/student/submit")
    public Result submitAnswer(@RequestBody StudentAnswerDTO studentAnswerDTO) throws IOException {
        int result = studentAnswerService.submitAnswer(studentAnswerDTO);
        return result > 0 ? Result.success("提交成功") : Result.error("提交失败");
    }

    // 学生提交整个作业
    @Operation(summary = "学生提交整个作业")
    @PostMapping("/student/submit-assignment")
    public Result submitAssignment(@RequestBody AssignmentSubmissionDTO submission) throws IOException {
        int result = studentAnswerService.submitAssignment(submission);
        return result > 0 ? Result.success("作业提交成功") : Result.error("作业提交失败");
    }


    // 获取作答详情
    @Operation(summary = "获取作答详情")
    @GetMapping("common/{id}")
    public Result getAnswerById(@PathVariable Long id) {
        StudentAnswer answer = studentAnswerService.getAnswerById(id);
        return answer != null ? Result.success(answer) : Result.error("作答记录不存在");
    }

    // 获取作业的所有作答
    @Operation(summary = "获取作业的所有作答")
    @GetMapping("/teacher/assignment/{assignmentId}")
    public Result getAnswersByAssignment(@PathVariable Long assignmentId) {
        List<StudentAnswer> answers = studentAnswerService.getAnswersByAssignmentId(assignmentId);
        return Result.success(answers);
    }

    // AI 评分
    @Operation(summary = "AI 评分")
    @PostMapping("/common/ai-grade")
        public Result aiGrade(@RequestBody AIGradeRequestDTO requestDTO) throws IOException {
        Integer assignmentId = requestDTO.getAssignmentId();
        int updatedRows = 0 ;
        for (Long answerId : requestDTO.getAnswerIds()) {
            StudentAnswer answer = studentAnswerService.getAnswerById(answerId);
            if (answer != null) {
                Long questionId = answer.getQuestionId();
                QuestionBank question = questionBankMapper.getById(questionId);
                String questionText = question.getQuestionText();
                String studentAnswer = answer.getAnswer();
                String correctAnswer = question.getCorrectAnswer();
                updatedRows += studentAnswerService.hybridGrade(answerId, questionId, studentAnswer, "DeepSeekAIService");
            }
        }

            return updatedRows > 0 ? Result.success() : Result.error("AI 评分失败");
        }

    // 教师评分
    @Operation(summary = "教师评分")
    @PostMapping("/teacher/teacher-grade")
    public Result teacherGrade(@RequestParam Long questionId, @RequestParam Boolean isCorrect ,@RequestParam Double teacherScore, @RequestParam String teacherFeedback,@RequestParam String explanation) {
        int result = studentAnswerService.teacherGrade(questionId, isCorrect, teacherScore, teacherFeedback , explanation);
        return result > 0 ? Result.success("教师评分完成") : Result.error("评分失败");
    }

    // 批量教师评分
    @Operation(summary = "作业中批量题目教师评分")
    @PostMapping("/teacher/batch")
    public Result batchTeacherGrade(@RequestBody BatchTeacherGradeRequest request) {
        studentAnswerService.batchTeacherGrade(request.getAssignmentId(), request.getGrades());
        return Result.success("教师批量评分成功");
    }

}
