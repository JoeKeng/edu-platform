package com.edusystem.mapper;

import com.edusystem.model.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamMapper {
    // 创建考试
    int insert(Exam exam);

    // 更新考试信息
    int update(Exam exam);

    // 删除考试
    int deleteById(Long id);

    // 根据ID获取考试信息
    Exam getById(Long id);

    // 获取课程的所有考试
    List<Exam> getByCourseId(Integer courseId);

    // 获取章节的所有考试
    List<Exam> getByChapterId(Integer chapterId);

    // 获取教师创建的所有考试
    List<Exam> getByTeacherId(Long teacherId);

    // 更新考试状态
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    // 批量删除考试
    int batchDelete(@Param("ids") List<Long> ids);
}