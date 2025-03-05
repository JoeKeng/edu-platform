package com.edusystem.controller;

import com.edusystem.model.QuestionBank;
import com.edusystem.model.Result;
import com.edusystem.service.QuestionBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class QuestionBankController {

    @Autowired
    private QuestionBankService questionBankService;

    // 添加题目
    @PostMapping("/teacher/question/add")
    public Result addQuestion(@RequestBody QuestionBank question) {
        log.info("添加题目 question:{}", question);
        int result = questionBankService.addQuestion(question);
        return result > 0 ? Result.success() : Result.error("添加失败");
    }

    // 根据id获取题目
    @GetMapping("/question/get/{id}")
    public Result getQuestion(@PathVariable Long id) {
        log.info("根据id获取题目 id:{}", id);
        QuestionBank question = questionBankService.getQuestionById(id);
        return question != null ? Result.success(question) : Result.error("未找到题目");
    }
    // 根据课程id和章节id获取题目
    @GetMapping("/question/list")
    public Result listQuestions(@RequestParam Integer courseId, @RequestParam Integer chapterId) {
        log.info("根据课程id和章节id获取题目 courseId:{}, chapterId:{}", courseId, chapterId);
        List<QuestionBank> questions = questionBankService.getQuestionsByCourseAndChapter(courseId, chapterId);
        return Result.success(questions);
    }

    // 更新题目
    @PutMapping("/teacher/question/update")
    public Result updateQuestion(@RequestBody QuestionBank question) {
        log.info("更新题目 question:{}", question);
        int result = questionBankService.updateQuestion(question);
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    // 删除题目
    @DeleteMapping("/teacher/question/delete/{id}")
    public Result deleteQuestion(@PathVariable Long id) {
        log.info("删除题目 id:{}", id);
        int result = questionBankService.deleteQuestion(id);
        return result > 0 ? Result.success() : Result.error("删除失败");
    }
}
