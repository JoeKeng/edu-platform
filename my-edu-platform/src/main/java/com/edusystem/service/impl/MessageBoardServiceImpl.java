package com.edusystem.service.impl;

import com.edusystem.config.AIConfig;
import com.edusystem.mapper.MessageBoardMapper;
import com.edusystem.mapper.UserMapper;
import com.edusystem.model.MessageBoard;
import com.edusystem.model.User;
import com.edusystem.service.MessageBoardService;
import com.edusystem.util.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 留言板服务实现类
 */
@Slf4j
@Service
public class MessageBoardServiceImpl implements MessageBoardService {

    @Autowired
    private MessageBoardMapper messageBoardMapper;

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private AIConfig aiConfig;
    
    /**
     * 添加留言
     * @param messageBoard 留言对象
     * @return 添加后的留言对象
     */
    @Override
    @Transactional
    public MessageBoard addMessage(MessageBoard messageBoard) {
        // 设置当前用户ID
        Integer currentUserId = CurrentHolder.getCurrentId();
        messageBoard.setUserId(currentUserId.longValue());
        
        // 默认设置为待审核状态
        messageBoard.setStatus(0);
        
        // 如果配置了AI审核，则进行AI审核
        if (aiConfig != null && aiConfig.getApiKey() != null && !aiConfig.getApiKey().isEmpty()) {
            Integer reviewResult = aiReviewContent(messageBoard.getContent());
            messageBoard.setStatus(reviewResult);
        }
        
        // 保存留言
        messageBoardMapper.insert(messageBoard);
        
        // 查询完整信息返回
        return messageBoardMapper.selectById(messageBoard.getId());
    }

    /**
     * 根据ID查询留言
     * @param id 留言ID
     * @return 留言对象
     */
    @Override
    public MessageBoard getMessageById(Long id) {
        return messageBoardMapper.selectById(id);
    }

    /**
     * 根据模块类型和模块ID查询留言列表（包含回复）
     * @param moduleType 模块类型
     * @param moduleId 模块ID
     * @return 留言列表
     */
    @Override
    public List<MessageBoard> getMessagesByModule(String moduleType, Long moduleId) {
        // 获取顶级留言
        List<MessageBoard> topMessages = messageBoardMapper.selectByModule(moduleType, moduleId);
        
        // 获取每个顶级留言的回复
        for (MessageBoard message : topMessages) {
            List<MessageBoard> replies = messageBoardMapper.selectRepliesByParentId(message.getId());
            // 这里可以将回复添加到留言对象的一个临时字段中，或者使用Map结构返回
            // 为简化实现，这里直接在日志中记录回复数量
            log.info("留言ID: {} 有 {} 条回复", message.getId(), replies.size());
        }
        
        return topMessages;
    }

    /**
     * 更新留言状态
     * @param id 留言ID
     * @param status 状态
     * @return 是否成功
     */
    @Override
    public boolean updateMessageStatus(Long id, Integer status) {
        // 检查当前用户是否有权限（管理员或教师）
        Integer currentUserId = CurrentHolder.getCurrentId();
        User currentUser = userMapper.getUserInfoByUserId(currentUserId);
        
        if (currentUser == null || (!"admin".equals(currentUser.getRole()) && !"teacher".equals(currentUser.getRole()))) {
            log.warn("用户 {} 尝试更新留言状态但没有权限", currentUserId);
            return false;
        }
        
        return messageBoardMapper.updateStatus(id, status) > 0;
    }

    /**
     * 删除留言（需要权限验证）
     * @param id 留言ID
     * @param currentUserId 当前用户ID
     * @return 是否成功
     */
    @Override
    public boolean deleteMessage(Long id, Integer currentUserId) {
        // 获取留言信息
        MessageBoard message = messageBoardMapper.selectById(id);
        if (message == null) {
            return false;
        }
        
        // 检查权限：只有留言作者、管理员或教师可以删除留言
        User currentUser = userMapper.getUserInfoByUserId(currentUserId);
        boolean isAuthor = message.getUserId().equals(currentUserId.longValue());
        boolean isAdminOrTeacher = currentUser != null && 
                ("admin".equals(currentUser.getRole()) || "teacher".equals(currentUser.getRole()));
        
        if (!isAuthor && !isAdminOrTeacher) {
            log.warn("用户 {} 尝试删除留言 {} 但没有权限", currentUserId, id);
            return false;
        }
        
        return messageBoardMapper.deleteById(id) > 0;
    }

    /**
     * 获取待审核的留言列表
     * @return 待审核留言列表
     */
    @Override
    public List<MessageBoard> getPendingMessages() {
        // 检查当前用户是否有权限（管理员或教师）
        Integer currentUserId = CurrentHolder.getCurrentId();
        User currentUser = userMapper.getUserInfoByUserId(currentUserId);
        
        if (currentUser == null || (!"admin".equals(currentUser.getRole()) && !"teacher".equals(currentUser.getRole()))) {
            log.warn("用户 {} 尝试获取待审核留言但没有权限", currentUserId);
            return new ArrayList<>();
        }
        
        return messageBoardMapper.selectPendingMessages();
    }
    
    /**
     * AI审核留言内容
     * @param content 留言内容
     * @return 审核结果（1=通过, 0=待人工审核, -1=不通过）
     */
    @Override
    public Integer aiReviewContent(String content) {
        try {
            // 这里实现AI审核逻辑
            // 可以调用OpenAI API或其他AI服务进行内容审核
            // 简单实现：检查内容长度和敏感词
            if (content == null || content.trim().isEmpty()) {
                return -1; // 内容为空，不通过
            }
            
            // 简单的敏感词检查（实际项目中应该使用更复杂的算法或调用专门的API）
            String[] sensitiveWords = {"政治", "色情", "赌博", "诈骗", "违法"};
            for (String word : sensitiveWords) {
                if (content.contains(word)) {
                    return 0; // 包含敏感词，需要人工审核
                }
            }
            
            // TODO: 集成实际的AI审核API
            // 如果内容较长或复杂，建议进行人工审核
            if (content.length() > 200) {
                return 0; // 内容较长，建议人工审核
            }
            
            return 1; // 默认通过
        } catch (Exception e) {
            log.error("AI审核内容失败", e);
            return 0; // 出错时设为待人工审核
        }
    }
    
    /**
     * 根据用户ID获取该用户的所有留言
     * @param userId 用户ID
     * @return 留言列表
     */
    @Override
    public List<MessageBoard> getMessagesByUserId(Long userId) {
        return messageBoardMapper.selectByUserId(userId);
    }
}