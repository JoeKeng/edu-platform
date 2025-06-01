package com.edusystem.controller;

import com.edusystem.model.PageResult;
import com.edusystem.model.QuestionBank;
import com.edusystem.model.Result;
import com.edusystem.service.QuestionBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "题库管理")
public class QuestionBankController {

    @Autowired
    private QuestionBankService questionBankService;

    // 添加题目
    @Operation(summary = "添加题目")
    @PostMapping("/teacher/question/add")
    public Result addQuestion(@RequestBody QuestionBank question) {
        log.info("添加题目 question:{}", question);
        int result = questionBankService.addQuestion(question);
        return result > 0 ? Result.success() : Result.error("添加失败");
    }

    // 根据id获取题目
    @Operation(summary = "根据id获取题目")
    @GetMapping("/question/get/{id}")
    public Result getQuestion(@PathVariable Long id) {
        log.info("根据id获取题目 id:{}", id);
        QuestionBank question = questionBankService.getQuestionById(id);
        return question != null ? Result.success(question) : Result.error("未找到题目");
    }
    // 根据课程id和章节id获取题目
    @Operation(summary = "根据课程id和章节id获取题目")
    @GetMapping("/question/list")
    public Result listQuestions(@RequestParam Integer courseId, @RequestParam Integer chapterId) {
        log.info("根据课程id和章节id获取题目 courseId:{}, chapterId:{}", courseId, chapterId);
        List<QuestionBank> questions = questionBankService.getQuestionsByCourseAndChapter(courseId, chapterId);
        return Result.success(questions);
    }

    //分页 查询题库
     @Operation(summary = "分页查询题库")
     @GetMapping("/question/page")
     public PageResult<QuestionBank> pageQuestions(
              @RequestParam(defaultValue = "1") Integer page,
              @RequestParam(defaultValue = "10") Integer pageSize,
              @RequestParam(required = false)String type,
              @RequestParam(required = false) String difficulty,
              @RequestParam(required = false) Integer courseId,
              @RequestParam(required = false) Integer chapterId
     ){
        log.info("分页查询题库，参数：page={}，pageSize={}，type={}，difficulty={}，courseId={}，chapterId={}", page, pageSize, type, difficulty, courseId, chapterId);
        return questionBankService.pageQuestions(page, pageSize, type, difficulty, courseId, chapterId);
     }

     //根据知识点ID获取题目(支持多个知识点)
     @Operation(summary = "根据知识点ID获取题目")
     @GetMapping("/question/getByKnowledgePointId")
     public Result getQuestionByKnowledgePointId(@RequestParam List<Long> knowledgePointIds) {
        log.info("根据知识点ID获取题目 knowledgePointIds:{}", knowledgePointIds);
        return questionBankService.getQuestionByKnowledgePointId(knowledgePointIds);
     }

     // 根据知识点ID获取题目(分页查询)
     @Operation(summary = "根据知识点ID获取题目(分页查询)")
     @GetMapping("/question/getByKnowledgePointId/page")
      public PageResult<QuestionBank> pageQuestionByKnowledgePointId(
             @RequestParam(defaultValue = "1") Integer page,
             @RequestParam(defaultValue = "10") Integer pageSize,
             @RequestParam List<Long> knowledgePointIds,
             @RequestParam(required = false) String type,
             @RequestParam(required = false) String difficulty
     ){
        log.info("根据知识点ID获取题目(分页查询) page={}，pageSize={}，knowledgePointIds={}，type={}，difficulty={}", 
            page, pageSize, knowledgePointIds, type, difficulty);
        return questionBankService.pageQuestionByKnowledgePointId(page, pageSize, knowledgePointIds, type, difficulty);
     }

    // 更新题目
    @Operation(summary = "更新题目")
    @PutMapping("/teacher/question/update")
    public Result updateQuestion(@RequestBody QuestionBank question) {
        log.info("更新题目 question:{}", question);
        int result = questionBankService.updateQuestion(question);
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    // 删除题目
    @DeleteMapping("/teacher/question/delete/{id}")
    @Operation(summary = "删除题目")
    public Result deleteQuestion(@PathVariable Long id) {
        log.info("删除题目 id:{}", id);
        int result = questionBankService.deleteQuestion(id);
        return result > 0 ? Result.success() : Result.error("删除失败");
    }
}
