package com.edusystem.model;

import lombok.Data;
import java.util.Date;

/**
 * 课程章节实体类
 */
@Data
public class CourseChapter {
    private Integer chapterId;  // 章节ID
    private Integer courseId;   // 课程ID
    private Integer parentId;   // 父章节ID
    private Integer chapterNumber; // 章节序号
    private String chapterTitle; // 章节标题
    private String description; // 章节描述
    private Date createdAt;     // 创建时间
    private Date updatedAt;     // 更新时间
}

