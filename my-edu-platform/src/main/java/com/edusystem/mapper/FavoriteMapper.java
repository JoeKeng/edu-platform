package com.edusystem.mapper;

import com.edusystem.model.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏数据访问接口
 */
@Mapper
public interface FavoriteMapper {
    
    /**
     * 添加收藏
     * @param favorite 收藏对象
     * @return 影响的行数
     */
    int insert(Favorite favorite);
    
    /**
     * 根据ID查询收藏
     * @param id 收藏ID
     * @return 收藏对象
     */
    Favorite selectById(Long id);
    
    /**
     * 查询用户的收藏列表
     * @param userId 用户ID
     * @param type 收藏类型（可选）
     * @return 收藏列表
     */
    List<Favorite> selectByUserId(@Param("userId") Long userId, @Param("type") String type);
    
    /**
     * 查询用户的收藏列表（带分类）
     * @param userId 用户ID
     * @param type 收藏类型
     * @param category 收藏分类
     * @return 收藏列表
     */
    List<Favorite> selectByUserIdAndCategory(@Param("userId") Long userId, @Param("type") String type, @Param("category") String category);
    
    /**
     * 检查是否已收藏
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 目标ID
     * @return 收藏对象，如果不存在则返回null
     */
    Favorite checkFavorite(@Param("userId") Long userId, @Param("type") String type, @Param("targetId") Long targetId);
    
    /**
     * 删除收藏
     * @param id 收藏ID
     * @return 影响的行数
     */
    int deleteById(Long id);
    
    /**
     * 取消收藏
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 目标ID
     * @return 影响的行数
     */
    int cancelFavorite(@Param("userId") Long userId, @Param("type") String type, @Param("targetId") Long targetId);
    
    /**
     * 统计用户收藏数量
     * @param userId 用户ID
     * @param type 收藏类型（可选）
     * @return 收藏数量
     */
    int countByUserId(@Param("userId") Long userId, @Param("type") String type);

}