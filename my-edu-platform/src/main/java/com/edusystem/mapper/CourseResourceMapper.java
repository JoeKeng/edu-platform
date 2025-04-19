package com.edusystem.mapper;

import com.edusystem.model.CourseResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Arrays;
import java.util.List;

@Mapper
public interface CourseResourceMapper {
    int insert(CourseResource resource);

    int update(CourseResource resource);

    int delete(@Param("resourceId") Integer resourceId);

    CourseResource findById(@Param("resourceId") Integer resourceId);

    List<CourseResource> findByCourseId(@Param("courseId") Integer courseId);

    List<CourseResource> findByChapterId(@Param("chapterId") Integer chapterId);


    List<CourseResource> batchFindByIds(List<Long> resource);
}

