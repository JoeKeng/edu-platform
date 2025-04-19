package com.edusystem.controller;

import com.edusystem.model.Result;
import com.edusystem.model.MessageBoard;
import com.edusystem.service.MessageBoardService;
import com.edusystem.util.CurrentHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 留言板控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/message-board")
@Tag(name = "留言板管理")
public class MessageBoardController {

    @Autowired
    private MessageBoardService messageBoardService;

    /**
     * 添加留言
     * @param messageBoard 留言对象
     * @return 添加结果
     */
    @PostMapping("/add")
    @Operation(summary = "添加留言")
    public Result addMessage(@RequestBody MessageBoard messageBoard) {
        try {
            MessageBoard result = messageBoardService.addMessage(messageBoard);
            return Result.success(result);
        } catch (Exception e) {
            log.error("添加留言失败", e);
            return Result.error("添加留言失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询留言
     * @param id 留言ID
     * @return 留言对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询留言")
    public Result getMessageById(
            @Parameter(description = "留言ID") @PathVariable Long id) {
        try {
            MessageBoard message = messageBoardService.getMessageById(id);
            if (message == null) {
                return Result.error("留言不存在");
            }
            return Result.success(message);
        } catch (Exception e) {
            log.error("查询留言失败", e);
            return Result.error("查询留言失败: " + e.getMessage());
        }
    }

    /**
     * 根据模块类型和模块ID查询留言列表
     * @param moduleType 模块类型
     * @param moduleId 模块ID
     * @return 留言列表
     */
    @GetMapping("/module")
    @Operation(summary = "根据模块查询留言列表")
    public Result getMessagesByModule(
            @Parameter(description = "模块类型") @RequestParam String moduleType,
            @Parameter(description = "模块ID") @RequestParam Long moduleId) {
        try {
            List<MessageBoard> messages = messageBoardService.getMessagesByModule(moduleType, moduleId);
            return Result.success(messages);
        } catch (Exception e) {
            log.error("查询模块留言失败", e);
            return Result.error("查询模块留言失败: " + e.getMessage());
        }
    }

    /**
     * 更新留言状态
     * @param id 留言ID
     * @param status 状态
     * @return 更新结果
     */
    @PutMapping("/status/{id}")
    @Operation(summary = "更新留言状态")
    public Result updateMessageStatus(
            @Parameter(description = "留言ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        try {
            boolean result = messageBoardService.updateMessageStatus(id, status);
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("更新留言状态失败，可能没有权限");
            }
        } catch (Exception e) {
            log.error("更新留言状态失败", e);
            return Result.error("更新留言状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除留言
     * @param id 留言ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除留言")
    public Result deleteMessage(
            @Parameter(description = "留言ID") @PathVariable Long id) {
        try {
            Integer currentUserId = CurrentHolder.getCurrentId();
            boolean result = messageBoardService.deleteMessage(id, currentUserId);
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("删除留言失败，可能没有权限或留言不存在");
            }
        } catch (Exception e) {
            log.error("删除留言失败", e);
            return Result.error("删除留言失败: " + e.getMessage());
        }
    }

    /**
     * 获取待审核的留言列表
     * @return 待审核留言列表
     */
    @GetMapping("/pending")
    @Operation(summary = "获取待审核的留言列表")
    public Result getPendingMessages() {
        try {
            List<MessageBoard> messages = messageBoardService.getPendingMessages();
            return Result.success(messages);
        } catch (Exception e) {
            log.error("获取待审核留言失败", e);
            return Result.error("获取待审核留言失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取该用户的所有留言
     * @param userId 用户ID
     * @return 留言列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的所有留言")
    public Result getMessagesByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            List<MessageBoard> messages = messageBoardService.getMessagesByUserId(userId);
            return Result.success(messages);
        } catch (Exception e) {
            log.error("获取用户留言失败", e);
            return Result.error("获取用户留言失败: " + e.getMessage());
        }
    }
}