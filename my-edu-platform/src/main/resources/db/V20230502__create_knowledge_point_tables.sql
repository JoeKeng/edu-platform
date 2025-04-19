-- 知识点表
CREATE TABLE `knowledge_point` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '知识点ID',
    `name` varchar(100) NOT NULL COMMENT '知识点名称',
    `course_id` int NOT NULL COMMENT '所属课程ID',
    `chapter_id` int NULL COMMENT '所属章节ID（可选）',
    `parent_id` bigint NULL COMMENT '父知识点ID（用于构建知识点体系）',
    `description` text NULL COMMENT '知识点描述',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_chapter` (`course_id`, `chapter_id`),
    KEY `idx_parent` (`parent_id`),
    CONSTRAINT `knowledge_point_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`),
    CONSTRAINT `knowledge_point_ibfk_2` FOREIGN KEY (`chapter_id`) REFERENCES `course_chapter` (`chapter_id`),
    CONSTRAINT `knowledge_point_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `knowledge_point` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

-- 题目-知识点关联表（多对多关系）
CREATE TABLE `question_knowledge_point` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `question_id` bigint NOT NULL COMMENT '题目ID',
    `knowledge_point_id` bigint NOT NULL COMMENT '知识点ID',
    `weight` decimal(3,2) DEFAULT 1.00 COMMENT '权重（用于表示该题目对知识点的考察程度）',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_question_point` (`question_id`, `knowledge_point_id`),
    KEY `idx_knowledge_point` (`knowledge_point_id`),
    CONSTRAINT `question_knowledge_point_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `question_bank` (`id`),
    CONSTRAINT `question_knowledge_point_ibfk_2` FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_point` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目-知识点关联表';