package com.edusystem.service;

import com.edusystem.model.Result;
import com.edusystem.model.WrongQuestion;
import com.edusystem.model.PageResult;

import java.util.List;

/**
 * 错题本服务接口
 */
public interface WrongQuestionService {
    
    /**
     * 添加错题记录
     * @param wrongQuestion 错题记录对象
     * @return 是否成功
     */
    boolean addWrongQuestion(WrongQuestion wrongQuestion);


    /**
     * 一键添加错题记录
     * @param userId
     * @param questionId
     */
    void quickAddWrongQuestion(Long userId, Long questionId);

    
    /**
     * 根据ID获取错题记录
     * @param id 错题记录ID
     * @return 错题记录对象
     */
    WrongQuestion getWrongQuestionById(Long id);
    
    /**
     * 获取用户的错题记录列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Result getWrongQuestionsByUserId(Long userId, Integer page, Integer pageSize);
    
    /**
     * 检查用户是否已有特定题目的错题记录
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 是否存在错题记录
     */
    boolean hasWrongQuestion(Long userId, Long questionId);
    
    /**
     * 获取用户特定题目的错题记录
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 错题记录对象
     */
    WrongQuestion getWrongQuestionByUserIdAndQuestionId(Long userId, Long questionId);
    
    /**
     * 移除错题记录
     * @param id 错题记录ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean removeWrongQuestion(Long id, Long userId);
    
    /**
     * 移除用户特定题目的错题记录
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 是否成功
     */
    boolean removeWrongQuestionByQuestionId(Long userId, Long questionId);
    
    /**
     * 获取用户错题数量
     * @param userId 用户ID
     * @return 错题数量
     */
    int getWrongQuestionCount(Long userId);

    /**
     * 获取用户错题列表(全部错题包括为保存的错题)
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */

    Result getOutWrongQuestionsByUserId(Long userId, Integer page, Integer pageSize);
}