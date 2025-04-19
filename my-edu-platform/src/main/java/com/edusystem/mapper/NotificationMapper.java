package com.edusystem.mapper;

import com.edusystem.model.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 通知数据访问接口
 */
@Mapper
public interface NotificationMapper {
    
    /**
     * 保存通知
     * @param notification 通知对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO notification(type, title, content, sender_id, receiver_id, receiver_type, related_id, read_status, create_time, update_time, is_deleted) " +
            "VALUES(#{type}, #{title}, #{content}, #{senderId}, #{receiverId}, #{receiverType}, #{relatedId}, #{readStatus}, #{createTime}, #{updateTime}, #{isDeleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);
    
    /**
     * 根据ID查询通知
     * @param id 通知ID
     * @return 通知对象
     */
    @Select("SELECT * FROM notification WHERE id = #{id} AND is_deleted = 0")
    Notification selectById(Long id);
    
    // /**
    //  * 查询用户的通知列表（分页）
    //  * @param receiverId 接收者ID
    //  * @param offset 偏移量
    //  * @param limit 限制数量
    //  * @return 通知列表
    //  */
    // @Select("SELECT * FROM notification WHERE (receiver_id = #{receiverId} OR (receiver_type = 3) OR " +
    //         "(receiver_type = 2 AND receiver_id IN (SELECT class_id FROM student WHERE id = #{receiverId}))) " +
    //         "AND is_deleted = 0 ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    // List<Notification> selectByReceiverId(Long receiverId, int offset, int limit);
    
    /**
     * 统计用户的通知总数
     * @param receiverId 接收者ID
     * @return 通知总数
     */
    @Select("SELECT COUNT(*) FROM notification WHERE (receiver_id = #{receiverId} OR (receiver_type = 3) OR " +
            "(receiver_type = 2 AND receiver_id IN (SELECT class_id FROM student WHERE id = #{receiverId}))) " +
            "AND is_deleted = 0")
    int countByReceiverId(Long receiverId);
    
    /**
     * 统计用户的未读通知数量
     * @param receiverId 接收者ID
     * @return 未读通知数量
     */
    @Select("SELECT COUNT(*) FROM notification WHERE (receiver_id = #{receiverId} OR (receiver_type = 3) OR " +
            "(receiver_type = 2 AND receiver_id IN (SELECT class_id FROM student WHERE id = #{receiverId}))) " +
            "AND read_status = 0 AND is_deleted = 0")
    int countUnreadByReceiverId(Long receiverId);
    
    /**
     * 标记通知为已读
     * @param id 通知ID
     * @param receiverId 接收者ID
     * @return 影响的行数
     */
    @Update("UPDATE notification SET read_status = 1, update_time = NOW() " +
            "WHERE id = #{id} AND (receiver_id = #{receiverId} OR receiver_type > 1) AND is_deleted = 0")
    int markAsRead(Long id, Long receiverId);
    
    /**
     * 批量标记通知为已读
     * @param ids 通知ID列表
     * @param receiverId 接收者ID
     * @return 影响的行数
     */
    int batchMarkAsRead(@Param("ids") List<Long> ids, @Param("receiverId") Long receiverId);
    
    /**
     * 逻辑删除通知
     * @param id 通知ID
     * @param receiverId 接收者ID
     * @return 影响的行数
     */
    @Update("UPDATE notification SET is_deleted = 1, update_time = NOW() " +
            "WHERE id = #{id} AND (receiver_id = #{receiverId} OR receiver_type > 1) AND is_deleted = 0")
    int deleteById(Long id, Long receiverId);
    
    /**
     * 批量逻辑删除通知
     * @param ids 通知ID列表
     * @param receiverId 接收者ID
     * @return 影响的行数
     */
    int batchDelete(@Param("ids") List<Long> ids, @Param("receiverId") Long receiverId);
    

    /**
     * 查询用户的通知列表(分页)
     * @param receiverId 接收者ID
     * @return 通知列表
     */
    // Remove the @Select annotation and parameters for offset and limit
    List<Notification> selectByReceiverId(Long receiverId);
}