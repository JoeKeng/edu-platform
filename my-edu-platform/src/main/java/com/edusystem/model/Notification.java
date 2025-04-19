package com.edusystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long id;
    
    /**
     * 通知类型：
     * 1 - 课程通知（开课、结课）
     * 2 - 作业通知（发布作业、提交作业、评分反馈）
     * 3 - 互动通知（讨论区评论、回复）
     * 4 - 系统公告（考试安排、课程调整等）
     */
    private Integer type;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 发送者ID（系统通知为null）
     */
    private Long senderId;
    
    /**
     * 接收者ID（群发通知为null）
     */
    private Long receiverId;
    
    /**
     * 接收者类型：
     * 1 - 个人
     * 2 - 班级
     * 3 - 全体用户
     */
    private Integer receiverType;
    
    /**
     * 相关ID（如课程ID、作业ID等）
     */
    private Long relatedId;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer readStatus;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否删除：0-未删除，1-已删除
     */
    private Integer isDeleted;
}