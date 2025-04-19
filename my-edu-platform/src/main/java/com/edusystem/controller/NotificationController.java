package com.edusystem.controller;

import com.edusystem.model.Notification;
import com.edusystem.model.PageResult;
import com.edusystem.model.Result;
import com.edusystem.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/notifications")
@Slf4j
@Tag(name = "通知管理", description = "通知相关接口")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    
    /**
     * 发送个人通知
     */
    @PostMapping("/personal")
    @Operation(summary = "发送个人通知")
    public Result sendPersonalNotification(@RequestParam String title,
                                          @RequestParam String content,
                                          @RequestParam Integer type,
                                          @RequestParam Long senderId,
                                          @RequestParam Long receiverId,
                                          @RequestParam(required = false) Long relatedId) {
        log.info("发送个人通知: title={}, type={}, senderId={}, receiverId={}", title, type, senderId, receiverId);
        boolean success = notificationService.sendPersonalNotification(title, content, type, senderId, receiverId, relatedId);
        return success ? Result.success() : Result.error("发送通知失败");
    }
    
    /**
     * 发送班级通知
     */
    @PostMapping("/class")
    @Operation(summary = "发送班级通知")
    public Result sendClassNotification(@RequestParam String title,
                                       @RequestParam String content,
                                       @RequestParam Integer type,
                                       @RequestParam Long senderId,
                                       @RequestParam Long classId,
                                       @RequestParam(required = false) Long relatedId) {
        log.info("发送班级通知: title={}, type={}, senderId={}, classId={}", title, type, senderId, classId);
        boolean success = notificationService.sendClassNotification(title, content, type, senderId, classId, relatedId);
        return success ? Result.success() : Result.error("发送通知失败");
    }
    
    /**
     * 发送系统通知
     */
    @PostMapping("/system")
    @Operation(summary = "发送系统通知")
    public Result sendSystemNotification(@RequestParam String title,
                                        @RequestParam String content,
                                        @RequestParam Integer type,
                                        @RequestParam(required = false) Long relatedId) {
        log.info("发送系统通知: title={}, type={}", title, type);
        boolean success = notificationService.sendSystemNotification(title, content, type, relatedId);
        return success ? Result.success() : Result.error("发送通知失败");
    }
    
    /**
     * 获取用户的通知列表（分页）
     */
    @GetMapping("/user/{receiverId}")
    @Operation(summary = "获取用户的通知列表")
    public Result getNotifications(
            @PathVariable Long receiverId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取用户通知: receiverId={}, page={}, pageSize={}", receiverId, page, pageSize);
        PageResult<Notification> pageResult = notificationService.getNotificationsByReceiverId(receiverId, page, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 获取用户的未读通知数量
     */
    @GetMapping("/user/{receiverId}/unread/count")
    @Operation(summary = "获取用户的未读通知数量")
    public Result getUnreadCount(@PathVariable Long receiverId) {
        log.info("获取用户未读通知数量: receiverId={}", receiverId);
        int count = notificationService.getUnreadCount(receiverId);
        return Result.success(count);
    }
    
    /**
     * 标记通知为已读
     */
    @PutMapping("/read/{id}")
    @Operation(summary = "标记通知为已读")
    public Result markAsRead(@PathVariable Long id, @RequestParam Long receiverId) {
        log.info("标记通知为已读: id={}, receiverId={}", id, receiverId);
        boolean success = notificationService.markAsRead(id, receiverId);
        return success ? Result.success() : Result.error("标记已读失败");
    }
    
    /**
     * 批量标记通知为已读
     */
    @PutMapping("/read/batch")
    @Operation(summary = "批量标记通知为已读")
    public Result batchMarkAsRead(@RequestBody List<Long> ids, @RequestParam Long receiverId) {
        log.info("批量标记通知为已读: ids={}, receiverId={}", ids, receiverId);
        boolean success = notificationService.batchMarkAsRead(ids, receiverId);
        return success ? Result.success() : Result.error("批量标记已读失败");
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知")
    public Result deleteNotification(@PathVariable Long id, @RequestParam Long receiverId) {
        log.info("删除通知: id={}, receiverId={}", id, receiverId);
        boolean success = notificationService.deleteNotification(id, receiverId);
        return success ? Result.success() : Result.error("删除通知失败");
    }
    
    /**
     * 批量删除通知
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除通知")
    public Result batchDeleteNotifications(@RequestBody List<Long> ids, @RequestParam Long receiverId) {
        log.info("批量删除通知: ids={}, receiverId={}", ids, receiverId);
        boolean success = notificationService.batchDeleteNotifications(ids, receiverId);
        return success ? Result.success() : Result.error("批量删除通知失败");
    }
}