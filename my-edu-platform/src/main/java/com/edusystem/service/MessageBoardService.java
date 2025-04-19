package com.edusystem.service;

import com.edusystem.model.MessageBoard;
import com.edusystem.model.PageResult;

import java.util.List;

/**
 * 留言板服务接口
 */
public interface MessageBoardService {

    /**
     * 添加留言
     * @param messageBoard 留言对象
     * @return 添加后的留言对象
     */
    MessageBoard addMessage(MessageBoard messageBoard);

    /**
     * 根据ID查询留言
     * @param id 留言ID
     * @return 留言对象
     */
    MessageBoard getMessageById(Long id);

    /**
     * 根据模块类型和模块ID查询留言列表（包含回复）
     * @param moduleType 模块类型
     * @param moduleId 模块ID
     * @return 留言列表
     */
    List<MessageBoard> getMessagesByModule(String moduleType, Long moduleId);

    /**
     * 更新留言状态
     * @param id 留言ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateMessageStatus(Long id, Integer status);

    /**
     * 删除留言（需要权限验证）
     * @param id 留言ID
     * @param currentUserId 当前用户ID
     * @return 是否成功
     */
    boolean deleteMessage(Long id, Integer currentUserId);

    /**
     * 获取待审核的留言列表
     * @return 待审核留言列表
     */
    List<MessageBoard> getPendingMessages();
    
    /**
     * AI审核留言内容
     * @param content 留言内容
     * @return 审核结果（1=通过, 0=待人工审核, -1=不通过）
     */
    Integer aiReviewContent(String content);
    
    /**
     * 根据用户ID获取该用户的所有留言
     * @param userId 用户ID
     * @return 留言列表
     */
    List<MessageBoard> getMessagesByUserId(Long userId);
}