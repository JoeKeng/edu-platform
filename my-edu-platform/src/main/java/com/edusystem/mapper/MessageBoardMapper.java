package com.edusystem.mapper;

import com.edusystem.model.MessageBoard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 留言板Mapper接口
 */
@Mapper
public interface MessageBoardMapper {

    /**
     * 添加留言
     * @param messageBoard 留言对象
     * @return 影响行数
     */
    int insert(MessageBoard messageBoard);

    /**
     * 根据ID查询留言
     * @param id 留言ID
     * @return 留言对象
     */
    MessageBoard selectById(Long id);

    /**
     * 根据模块类型和模块ID查询留言列表（顶级留言）
     * @param moduleType 模块类型
     * @param moduleId 模块ID
     * @return 留言列表
     */
    List<MessageBoard> selectByModule(@Param("moduleType") String moduleType, @Param("moduleId") Long moduleId);

    /**
     * 根据父ID查询回复列表
     * @param parentId 父留言ID
     * @return 回复列表
     */
    List<MessageBoard> selectRepliesByParentId(Long parentId);

    /**
     * 更新留言状态
     * @param id 留言ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 逻辑删除留言
     * @param id 留言ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 查询待审核的留言
     * @return 待审核留言列表
     */
    List<MessageBoard> selectPendingMessages();
    
    /**
     * 根据用户ID查询该用户的所有留言
     * @param userId 用户ID
     * @return 留言列表
     */
    List<MessageBoard> selectByUserId(Long userId);
}