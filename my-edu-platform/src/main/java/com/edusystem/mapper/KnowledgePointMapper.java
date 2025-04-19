package com.edusystem.mapper;

import com.edusystem.model.KnowledgePoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 知识点Mapper接口
 */
@Mapper
public interface KnowledgePointMapper {

    /**
     * 添加知识点
     */
    int insert(KnowledgePoint knowledgePoint);

    /**
     * 更新知识点
     */
    int update(KnowledgePoint knowledgePoint);

    /**
     * 删除知识点
     */
    int delete(Long id);

    /**
     * 根据ID查询知识点
     */
    KnowledgePoint selectById(Long id);

    /**
     * 根据课程ID查询知识点列表
     */
    List<KnowledgePoint> selectByCourseId(Integer courseId);

    /**
     * 根据章节ID查询知识点列表
     */
    List<KnowledgePoint> selectByChapterId(Integer chapterId);
    
    /**
     * 根据父ID查询知识点列表（用于构建树形结构）
     */
    List<KnowledgePoint> selectByParentId(@Param("parentId") Long parentId, @Param("courseId") Integer courseId);
}