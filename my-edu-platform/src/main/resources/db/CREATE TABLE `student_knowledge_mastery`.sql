CREATE TABLE `student_knowledge_mastery` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `knowledge_point_id` bigint NOT NULL COMMENT '知识点ID',
  `mastery_level` double NOT NULL DEFAULT '0' COMMENT '掌握程度（0-100）',
  `question_count` int NOT NULL DEFAULT '0' COMMENT '练习题目数量',
  `correct_count` int NOT NULL DEFAULT '0' COMMENT '正确题目数量',
  `last_practice_time` datetime DEFAULT NULL COMMENT '最后练习时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_knowledge` (`student_id`,`knowledge_point_id`),
  KEY `idx_knowledge_point` (`knowledge_point_id`),
  KEY `idx_student` (`student_id`),
  CONSTRAINT `fk_skm_knowledge_point` FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_point` (`id`),
  CONSTRAINT `fk_skm_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生知识点掌握情况表';