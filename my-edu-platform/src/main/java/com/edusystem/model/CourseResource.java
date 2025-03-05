package com.edusystem.model;

import lombok.Data;

import java.util.Date;

/**
 * 课程资源实体类
 */
@Data
public class CourseResource {
    private Integer resourceId;  // 资源ID
    private Integer courseId;    // 课程ID
    private Integer chapterId;   // 章节ID
    private String resourceName; // 资源名称
    private String resourceType; // 资源类型（视频、PPT、PDF、其他）
    private String resourceUrl;  // 资源链接
    private Long uploadUserId;   // 上传用户ID
    private Date uploadTime;     // 上传时间
    private Integer fileSize;    // 文件大小
    private String description;  // 资源描述
    private Boolean isPublic;    // 是否公开
    private Integer downloadCount; // 下载次数
    private Date createdAt;      // 创建时间
    private Date updatedAt;      // 更新时间
}

