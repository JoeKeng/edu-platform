package com.edusystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {
    /**
     * 收藏ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 收藏类型（课程、题目、资源）
     */
    private String type;
    
    /**
     * 收藏分类（如视频、文档、习题等，适用于资源）
     */
    private String category;
    
    /**
     * 收藏对象ID（如课程ID、题目ID等）
     */
    private Long targetId;
    
    /**
     * 收藏时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否逻辑删除
     */
    private Integer isDeleted;

}