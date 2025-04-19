package com.edusystem.mapper;

import com.edusystem.model.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamQuestionMapper {
    // 添加考试题目
    int insert(ExamQuestion examQuestion);

    // 批量添加考试题目
    int batchInsert(@Param("examQuestions") List<ExamQuestion> examQuestions);

    // 更新考试题目信息
    int update(ExamQuestion examQuestion);

    // 删除考试题目
    int deleteById(Long id);

    // 根据考试ID删除所有题目
    int deleteByExamId(Long examId);

    // 根据ID获取考试题目信息
    ExamQuestion getById(Long id);

    // 获取考试的所有题目
    List<ExamQuestion> getByExamId(Long examId);

    // 根据考试ID和题目类型获取题目
    List<ExamQuestion> getByExamIdAndType(@Param("examId") Long examId, @Param("questionType") String questionType);

    // 更新题目顺序
    int updateOrderNum(@Param("id") Long id, @Param("orderNum") Integer orderNum);

    // 批量更新题目分值
    int batchUpdateScore(@Param("updates") List<ExamQuestion> updates);

    // 根据ID查询题目
    ExamQuestion selectById(Long id);

    // 根据考试ID和题目顺序获取题目
    ExamQuestion selectByOrderNum(Long examId, Integer orderNum);

    // 更新删除题目后的顺序号
    int updateOrderNumsAfterDelete(@Param("examId") Long examId, @Param("orderNum") Integer orderNum);

    // 减少指定范围内题目的顺序号
    int decreaseOrderNum(@Param("examId") Long examId, 
                        @Param("startOrder") Integer startOrder, 
                        @Param("endOrder") Integer endOrder);

    // 增加指定范围内题目的顺序号
    int increaseOrderNum(@Param("examId") Long examId, 
                        @Param("startOrder") Integer startOrder, 
                        @Param("endOrder") Integer endOrder);

    // 获取考试中最大的题目顺序号
    Integer getMaxOrderNum(@Param("examId") Long examId);

    ExamQuestion getByExamIdAndQuestionId(Long examId, Long questionId);
}