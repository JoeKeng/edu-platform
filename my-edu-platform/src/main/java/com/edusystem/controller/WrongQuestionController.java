package com.edusystem.controller;

import com.edusystem.model.WrongQuestion;
import com.edusystem.model.PageResult;
import com.edusystem.model.Result;
import com.edusystem.service.WrongQuestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 错题本功能控制器
 */
@RestController
@RequestMapping("/wrong-question")
@Slf4j
@Tag(name = "错题本管理")
public class WrongQuestionController {

    @Autowired
    private WrongQuestionService wrongQuestionService;
    
    /**
     * 添加错题记录
     * @param wrongQuestion 错题记录对象
     * @return 操作结果
     */
    @Operation(summary = "添加错题记录")
    @PostMapping("/add")
    public Result addWrongQuestion(@RequestBody WrongQuestion wrongQuestion) {
        boolean success = wrongQuestionService.addWrongQuestion(wrongQuestion);
        if (success) {
            return Result.success("添加错题成功");
        } else {
            return Result.error("添加错题失败");
        }
    }
    @Operation(summary = "快速添加错题记录")
    @PostMapping("/quickadd")
    public Result addWrongQuestion(@RequestParam Long userId, @RequestParam Long questionId) {
        wrongQuestionService.quickAddWrongQuestion(userId, questionId);
        return Result.success();
    }


    /**
     * 移除错题记录
     * @param id 错题记录ID
     * @param userId 用户ID
     * @return 操作结果
     */
    @Operation(summary = "移除错题记录")
    @PostMapping("/remove")
    public Result removeWrongQuestion(@RequestParam Long id, @RequestParam Long userId) {
        boolean success = wrongQuestionService.removeWrongQuestion(id, userId);
        if (success) {
            return Result.success("移除错题成功");
        } else {
            return Result.error("移除错题失败");
        }
    }
    
    /**
     * 根据题目ID移除错题记录
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 操作结果
     */
    @Operation(summary = "根据题目ID移除错题记录")
    @PostMapping("/remove-by-question")
    public Result removeWrongQuestionByQuestionId(@RequestParam Long userId, @RequestParam Long questionId) {
        boolean success = wrongQuestionService.removeWrongQuestionByQuestionId(userId, questionId);
        if (success) {
            return Result.success("移除错题成功");
        } else {
            return Result.error("移除错题失败");
        }
    }
    
    /**
     * 获取用户错题集列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 错题列表
     */
    @Operation(summary = "获取用户错题集列表(加入错题集的错题)")
    @GetMapping("/list")
    public Result getWrongQuestionList(@RequestParam Long userId,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取用户错题集列表，参数：userId={}, page={}, pageSize={}", userId, page, pageSize);
        return wrongQuestionService.getWrongQuestionsByUserId(userId, page, pageSize);
    }
    /**
     * 获取用户错题列表(所有的错题包括为保存的错题)
     */
    @Operation(summary = "获取用户错题列表(所有的错题包括为保存的错题)")
    @GetMapping("/list2")
    public Result getOutWrongQuestionList(@RequestParam Long userId,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取用户错题列表，参数：userId={}, page={}, pageSize={}", userId, page, pageSize);
        return Result.success(wrongQuestionService.getOutWrongQuestionsByUserId(userId, page, pageSize));
    }

    
    /**
     * 获取错题详情
     * @param id 错题记录ID
     * @return 错题详情
     */
    @Operation(summary = "获取错题详情")
    @GetMapping("/detail")
    public Result getWrongQuestionDetail(@RequestParam Long id) {
        WrongQuestion wrongQuestion = wrongQuestionService.getWrongQuestionById(id);
        log.info("获取错题详情，参数：id={}", id);
        if (wrongQuestion != null) {
            return Result.success(wrongQuestion);
        } else {
            return Result.error("错题不存在");
        }
    }
    
    /**
     * 检查用户是否已有特定题目的错题记录
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 检查结果
     */
    @Operation(summary = "检查用户是否已有特定题目的错题记录")
    @GetMapping("/check")
    public Result checkWrongQuestion(@RequestParam Long userId, @RequestParam Long questionId) {
        boolean hasWrongQuestion = wrongQuestionService.hasWrongQuestion(userId, questionId);
        Map<String, Boolean> data = new HashMap<>();
        data.put("hasWrongQuestion", hasWrongQuestion);
        return Result.success(data);
    }
    
    /**
     * 获取用户错题数量
     * @param userId 用户ID
     * @return 错题数量
     */
    @Operation(summary = "获取用户错题数量")
    @GetMapping("/count")
    public Result getWrongQuestionCount(@RequestParam Long userId) {
        int count = wrongQuestionService.getWrongQuestionCount(userId);
        Map<String, Integer> data = new HashMap<>();
        data.put("count", count);
        return Result.success(data);
    }
}