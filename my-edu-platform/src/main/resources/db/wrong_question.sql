CREATE TABLE `wrong_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '错题记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `assignment_id` bigint DEFAULT NULL COMMENT '所属作业ID（可选）',
  `wrong_answer` text NOT NULL COMMENT '学生错误答案',
  `correct_answer` text NOT NULL COMMENT '正确答案',
  `analysis` text COMMENT '解析',
  `wrong_type` enum('concept','calculation','misread','other') NOT NULL COMMENT '错误类型（概念错误、计算错误、审题错误等）',
  `attempt_count` int NOT NULL DEFAULT '1' COMMENT '答题尝试次数',
  `first_attempt_score` decimal(5,2) DEFAULT NULL COMMENT '首次得分',
  `last_attempt_score` decimal(5,2) DEFAULT NULL COMMENT '最近一次得分',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_wrong_question` (`user_id`,`question_id`),
  KEY `idx_user_wrong` (`user_id`),
  CONSTRAINT `wrong_question_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='错题本表'

