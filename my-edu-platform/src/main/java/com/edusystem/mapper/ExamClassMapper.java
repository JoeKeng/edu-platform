package com.edusystem.mapper;

import com.edusystem.model.ExamClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamClassMapper {
    /**
     * 批量插入考试-班级关联记录
     * @param examClasses 考试-班级关联记录列表
     * @return 插入的记录数
     */
    int batchInsert(@Param("examClasses") List<ExamClass> examClasses);

    /**
     * 删除指定考试ID的所有班级关联记录
     * @param examId 考试ID
     * @return 删除的记录数
     */
    int deleteByExamId(@Param("examId") Long examId);

    /**
     * 获取指定考试关联的所有班级ID
     * @param examId 考试ID
     * @return 班级ID列表
     */
    List<Integer> getClassIdsByExamId(@Param("examId") Long examId);

    /**
     * 获取指定班级关联的所有考试ID
     * @param classId 班级ID
     * @return 考试ID列表
     */
    List<Long> getExamIdsByClassId(@Param("classId") Integer classId);
}