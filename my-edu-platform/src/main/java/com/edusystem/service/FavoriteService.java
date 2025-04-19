package com.edusystem.service;

import com.edusystem.model.Favorite;
import com.edusystem.model.PageResult;
import com.edusystem.model.Result;

import java.util.List;
import java.util.Map;

/**
 * 收藏服务接口
 */
public interface FavoriteService {
    
    /**
     * 添加收藏
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 目标ID
     * @return 是否成功
     */
    boolean addFavorite(Long userId, String type, Long targetId);
    
    /**
     * 取消收藏
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 目标ID
     * @return 是否成功
     */
    boolean cancelFavorite(Long userId, String type, Long targetId);
    
    /**
     * 检查是否已收藏
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 目标ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, String type, Long targetId);
    
    /**
     * 获取用户收藏列表
     * @param userId 用户ID
     * @param type 收藏类型（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Result getFavoritesByUserId(Long userId, String type, Integer page, Integer pageSize);
    
    /**
     * 获取用户收藏数量
     * @param userId 用户ID
     * @param type 收藏类型（可选）
     * @return 收藏数量
     */
    int getFavoriteCount(Long userId, String type);
    
    /**
     * 获取收藏详情列表（包含收藏对象的详细信息）
     * @param userId 用户ID
     * @param type 收藏类型（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Result getFavoriteDetailsById(Long userId, String type, Integer page, Integer pageSize);
    
    /**
     * 获取单个收藏详情
     * @param favoriteId 收藏ID
     * @return 收藏详情
     */
    Result getFavoriteDetailById(Long favoriteId);
}