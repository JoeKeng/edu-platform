package com.edusystem.mapper;

import com.edusystem.model.WrongQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 错题本数据访问接口
 */
@Mapper
public interface WrongQuestionMapper {
    
    /**
     * 添加错题记录
     * @param wrongQuestion 错题记录对象
     * @return 影响的行数
     */
    int insert(WrongQuestion wrongQuestion);
    
    /**
     * 根据ID查询错题记录
     * @param wrongId 错题记录ID
     * @return 错题记录对象
     */
    WrongQuestion selectById(Long wrongId);
    
    /**
     * 查询学生的错题记录列表
     * @param userId 用户ID
     * @return 错题记录列表
     */
    List<WrongQuestion> selectByUserId(Long userId);
    
    /**
     * 查询学生特定考试题目的错题记录
     * @param studentId 学生ID
     * @param examQuestionId 考试题目ID
     * @return 错题记录对象，如果不存在则返回null
     */
    WrongQuestion selectByStudentIdAndQuestionId(@Param("studentId") Long studentId, @Param("questionId") Integer questionId);
    
    /**
     * 查询学生特定考试的错题记录
     * @param studentId 学生ID
     * @param examId 考试ID
     * @return 错题记录列表
     */
    List<WrongQuestion> selectByStudentIdAndExamId(@Param("studentId") Long studentId, @Param("examId") Integer examId);
    
    /**
     * 更新错题记录
     * @param wrongQuestion 错题记录对象
     * @return 影响的行数
     */
    int update(WrongQuestion wrongQuestion);
    
    /**
     * 更新错题备注
     * @param wrongId 错题记录ID
     * @param note 备注内容
     * @return 影响的行数
     */
    int updateNote(@Param("wrongId") Integer wrongId, @Param("note") String note);
    
    /**
     * 增加错题出现次数
     * @param studentId 学生ID
     * @param examQuestionId 考试题目ID
     * @return 影响的行数
     */
    int increaseErrorCount(@Param("studentId") Long studentId, @Param("examQuestionId") Integer examQuestionId);
    
    /**
     * 删除错题记录（逻辑删除）
     * @param wrongId 错题记录ID
     * @return 影响的行数
     */
    int deleteById(Integer wrongId);
    
    /**
     * 统计学生错题数量
     * @param studentId 学生ID
     * @return 错题数量
     */
    int countByStudentId(Long studentId);

//    void addWrongQuestion(WrongQuestion wrongQuestion);
}