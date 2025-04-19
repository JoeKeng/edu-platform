-- 学生学习行为记录表
CREATE TABLE `student_learning_behavior` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `behavior_type` varchar(20) NOT NULL COMMENT '行为类型：LOGIN, STUDY, ASSIGNMENT, EXAM, RESOURCE',
  `resource_type` varchar(20) DEFAULT NULL COMMENT '资源类型：COURSE, VIDEO, DOCUMENT, QUESTION',
  `resource_id` bigint(20) DEFAULT NULL COMMENT '资源ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `duration` int(11) DEFAULT NULL COMMENT '持续时间（秒）',
  `device_info` varchar(255) DEFAULT NULL COMMENT '设备信息',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
  KEY `idx_student_id` (`student_id`),
  KEY `idx_behavior_type` (`behavior_type`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生学习行为记录表';

-- 学生学习分析结果表
CREATE TABLE `student_learning_analysis` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `analysis_type` varchar(30) NOT NULL COMMENT '分析类型：HABIT, PROGRESS, STRENGTH_WEAKNESS, RECOMMENDATION, COMPREHENSIVE',
  `learning_habit_data` json DEFAULT NULL COMMENT '学习习惯分析数据',
  `learning_progress_data` json DEFAULT NULL COMMENT '学习进度分析数据',
  `strength_weakness_data` json DEFAULT NULL COMMENT '优势劣势分析数据',
  `recommendations` json DEFAULT NULL COMMENT '学习建议',
  `analysis_time` datetime NOT NULL COMMENT '分析时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
  KEY `idx_student_id` (`student_id`),
  KEY `idx_analysis_type` (`analysis_type`),
  KEY `idx_analysis_time` (`analysis_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生学习分析结果表';

-- 学生知识点掌握情况表
CREATE TABLE `student_knowledge_mastery` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `knowledge_point_id` bigint(20) NOT NULL COMMENT '知识点ID',
  `mastery_level` decimal(5,2) NOT NULL COMMENT '掌握程度：0-100',
  `question_count` int(11) NOT NULL DEFAULT '0' COMMENT '题目数量',
  `correct_count` int(11) NOT NULL DEFAULT '0' COMMENT '正确数量',
  `last_practice_time` datetime DEFAULT NULL COMMENT '最后练习时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
  UNIQUE KEY `uk_student_knowledge` (`student_id`,`knowledge_point_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_knowledge_point_id` (`knowledge_point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生知识点掌握情况表';

-- 学生学习目标表
CREATE TABLE `student_learning_goal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `goal_type` varchar(20) NOT NULL COMMENT '目标类型：DAILY, WEEKLY, MONTHLY, COURSE',
  `goal_content` varchar(255) NOT NULL COMMENT '目标内容',
  `target_value` int(11) NOT NULL COMMENT '目标值',
  `current_value` int(11) NOT NULL DEFAULT '0' COMMENT '当前值',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `status` varchar(20) NOT NULL COMMENT '状态：NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
  KEY `idx_student_id` (`student_id`),
  KEY `idx_goal_type` (`goal_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生学习目标表';