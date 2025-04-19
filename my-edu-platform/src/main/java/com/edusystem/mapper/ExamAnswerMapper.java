package com.edusystem.mapper;

import com.edusystem.model.ExamAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamAnswerMapper {
    // 插入答题记录
    int insert(ExamAnswer examAnswer);

    // 批量插入答题记录
    int batchInsert(List<ExamAnswer> examAnswers);

    // 更新答题记录
    int update(ExamAnswer examAnswer);

    // 插入或更新答题记录
    int insertOrUpdate(ExamAnswer examAnswer);

    // 根据考试ID和学生ID查询所有答题记录
    List<ExamAnswer> selectByExamAndStudent(@Param("examId") Long examId, @Param("studentId") Long studentId);

    // 根据考试ID和题目ID查询答题记录
    ExamAnswer selectByExamAndQuestion(@Param("examId") Long examId, @Param("questionId") Long questionId);

    // 更新提交状态
    int updateSubmitStatus(@Param("examId") Long examId, @Param("studentId") Long studentId);

    // 获取学生在某场考试中的答题进度
    int getAnsweredCount(@Param("examId") Long examId, @Param("studentId") Long studentId);

    // 获取最后保存时间
    ExamAnswer getLastSaved(@Param("examId") Long examId, @Param("studentId") Long studentId);

    // 删除答题记录
    int deleteByExamAndStudent(@Param("examId") Long examId, @Param("studentId") Long studentId);

    // 批量更新答题状态
    int batchUpdateStatus(@Param("examId") Long examId, @Param("studentId") Long studentId, @Param("status") String status);

    // 检查是否已提交
    boolean isSubmitted(@Param("examId") Long examId, @Param("studentId") Long studentId);

    ExamAnswer selectByAnswerId(Long examAnswerId);
}