package com.edusystem.service.impl;

import com.edusystem.mapper.NotificationMapper;
import com.edusystem.model.Notification;
import com.edusystem.model.PageResult;
import com.edusystem.service.NotificationService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务实现类
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Override
    @Transactional
    public boolean sendNotification(Notification notification) {
        // 设置默认值
        if (notification.getReadStatus() == null) {
            notification.setReadStatus(0); // 默认未读
        }
        if (notification.getCreateTime() == null) {
            notification.setCreateTime(LocalDateTime.now());
        }
        if (notification.getUpdateTime() == null) {
            notification.setUpdateTime(LocalDateTime.now());
        }
        if (notification.getIsDeleted() == null) {
            notification.setIsDeleted(0); // 默认未删除
        }
        
        // 保存通知到数据库
        int result = notificationMapper.insert(notification);
        
        // 如果保存成功，通过WebSocket推送通知
        if (result > 0) {
            try {
                // 根据接收者类型推送通知
                switch (notification.getReceiverType()) {
                    case 1: // 个人通知
                        if (notification.getReceiverId() != null) {
                            // 发送到个人频道
                            messagingTemplate.convertAndSendToUser(
                                    notification.getReceiverId().toString(),
                                    "/queue/notifications",
                                    notification
                            );
                        }
                        break;
                    case 2: // 班级通知
                        if (notification.getReceiverId() != null) {
                            // 发送到班级频道
                            messagingTemplate.convertAndSend(
                                    "/topic/class/" + notification.getReceiverId(),
                                    notification
                            );
                        }
                        break;
                    case 3: // 全体通知
                        // 发送到全体频道
                        messagingTemplate.convertAndSend(
                                "/topic/all",
                                notification
                        );
                        break;
                    default:
                        log.warn("未知的接收者类型: {}", notification.getReceiverType());
                }
                return true;
            } catch (Exception e) {
                log.error("WebSocket推送通知失败: {}", e.getMessage());
                // 即使WebSocket推送失败，只要数据库保存成功，我们仍然认为发送成功
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean sendPersonalNotification(String title, String content, Integer type, Long senderId, Long receiverId, Long relatedId) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setSenderId(senderId);
        notification.setReceiverId(receiverId);
        notification.setReceiverType(1); // 个人
        notification.setRelatedId(relatedId);
        notification.setReadStatus(0); // 未读
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        notification.setIsDeleted(0); // 未删除
        
        return sendNotification(notification);
    }
    
    @Override
    public boolean sendClassNotification(String title, String content, Integer type, Long senderId, Long classId, Long relatedId) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setSenderId(senderId);
        notification.setReceiverId(classId);
        notification.setReceiverType(2); // 班级
        notification.setRelatedId(relatedId);
        notification.setReadStatus(0); // 未读
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        notification.setIsDeleted(0); // 未删除
        
        return sendNotification(notification);
    }
    
    @Override
    public boolean sendSystemNotification(String title, String content, Integer type, Long relatedId) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setSenderId(null); // 系统通知没有发送者
        notification.setReceiverId(null); // 系统通知发送给所有人
        notification.setReceiverType(3); // 全体用户
        notification.setRelatedId(relatedId);
        notification.setReadStatus(0); // 未读
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        notification.setIsDeleted(0); // 未删除
        
        return sendNotification(notification);
    }
    
    @Override
    public PageResult<Notification> getNotificationsByReceiverId(Long receiverId, Integer page, Integer pageSize) {
        // 使用 PageHelper 设置分页
        PageHelper.startPage(page, pageSize);
        
        // 查询通知列表
        List<Notification> notifications = notificationMapper.selectByReceiverId(receiverId);
        
        // 将结果转换为 Page 对象
        Page<Notification> pageInfo = (Page<Notification>) notifications;
        
        // 构建分页结果
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getResult());
    }
    
    @Override
    public int getUnreadCount(Long receiverId) {
        return notificationMapper.countUnreadByReceiverId(receiverId);
    }
    
    @Override
    @Transactional
    public boolean markAsRead(Long id, Long receiverId) {
        int result = notificationMapper.markAsRead(id, receiverId);
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean batchMarkAsRead(List<Long> ids, Long receiverId) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        int result = notificationMapper.batchMarkAsRead(ids, receiverId);
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteNotification(Long id, Long receiverId) {
        int result = notificationMapper.deleteById(id, receiverId);
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean batchDeleteNotifications(List<Long> ids, Long receiverId) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        int result = notificationMapper.batchDelete(ids, receiverId);
        return result > 0;
    }
}