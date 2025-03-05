package com.edusystem.mapper;

import com.edusystem.model.CourseChapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseChapterMapper {
    int insert(CourseChapter chapter);

    int update(CourseChapter chapter);

    int delete(@Param("chapterId") Integer chapterId);

    CourseChapter findById(@Param("chapterId") Integer chapterId);

    List<CourseChapter> findByCourseId(@Param("courseId") Integer courseId);
}

