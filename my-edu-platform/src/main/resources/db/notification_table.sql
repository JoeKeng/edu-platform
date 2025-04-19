-- 创建通知表
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    type INT NOT NULL COMMENT '通知类型：1-课程通知，2-作业通知，3-互动通知，4-系统公告',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    sender_id BIGINT COMMENT '发送者ID（系统通知为null）',
    receiver_id BIGINT COMMENT '接收者ID（群发通知为null）',
    receiver_type INT NOT NULL COMMENT '接收者类型：1-个人，2-班级，3-全体用户',
    related_id BIGINT COMMENT '相关ID（如课程ID、作业ID等）',
    read_status TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_create_time (create_time),
    INDEX idx_read_status (read_status)
) COMMENT '通知表';