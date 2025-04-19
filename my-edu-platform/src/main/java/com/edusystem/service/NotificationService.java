package com.edusystem.service;

import com.edusystem.model.Notification;
import com.edusystem.model.PageResult;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 发送通知
     * @param notification 通知对象
     * @return 是否成功
     */
    boolean sendNotification(Notification notification);
    
    /**
     * 发送个人通知
     * @param title 标题
     * @param content 内容
     * @param type 类型
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param relatedId 相关ID
     * @return 是否成功
     */
    boolean sendPersonalNotification(String title, String content, Integer type, Long senderId, Long receiverId, Long relatedId);
    
    /**
     * 发送班级通知
     * @param title 标题
     * @param content 内容
     * @param type 类型
     * @param senderId 发送者ID
     * @param classId 班级ID
     * @param relatedId 相关ID
     * @return 是否成功
     */
    boolean sendClassNotification(String title, String content, Integer type, Long senderId, Long classId, Long relatedId);
    
    /**
     * 发送系统通知
     * @param title 标题
     * @param content 内容
     * @param type 类型
     * @param relatedId 相关ID
     * @return 是否成功
     */
    boolean sendSystemNotification(String title, String content, Integer type, Long relatedId);
    
    /**
     * 分页查询用户的通知
     * @param receiverId 接收者ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<Notification> getNotificationsByReceiverId(Long receiverId, Integer page, Integer pageSize);
    
    /**
     * 获取用户的未读通知数量
     * @param receiverId 接收者ID
     * @return 未读通知数量
     */
    int getUnreadCount(Long receiverId);
    
    /**
     * 标记通知为已读
     * @param id 通知ID
     * @param receiverId 接收者ID
     * @return 是否成功
     */
    boolean markAsRead(Long id, Long receiverId);
    
    /**
     * 批量标记通知为已读
     * @param ids 通知ID列表
     * @param receiverId 接收者ID
     * @return 是否成功
     */
    boolean batchMarkAsRead(List<Long> ids, Long receiverId);
    
    /**
     * 删除通知
     * @param id 通知ID
     * @param receiverId 接收者ID
     * @return 是否成功
     */
    boolean deleteNotification(Long id, Long receiverId);
    
    /**
     * 批量删除通知
     * @param ids 通知ID列表
     * @param receiverId 接收者ID
     * @return 是否成功
     */
    boolean batchDeleteNotifications(List<Long> ids, Long receiverId);
}