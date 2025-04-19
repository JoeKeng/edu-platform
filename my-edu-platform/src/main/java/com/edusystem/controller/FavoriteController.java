package com.edusystem.controller;

import com.edusystem.model.Favorite;
import com.edusystem.model.PageResult;
import com.edusystem.model.Result;
import com.edusystem.service.FavoriteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 收藏功能控制器
 */
@RestController
@RequestMapping("/favorite")
@Slf4j
@Tag(name = "收藏管理")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;
    
    /**
     * 添加收藏
     * @param userId 用户ID
     * @param type 收藏类型：1-课程，2-题目，3-资源
     * @param targetId 目标ID
     * @return 操作结果
     */
    @Operation(summary = "添加收藏")
    @PostMapping("/add")
    public Result addFavorite(@RequestParam Long userId, 
                             @RequestParam String type,
                             @RequestParam Long targetId) {
        boolean success = favoriteService.addFavorite(userId, type, targetId);
        if (success) {
            return Result.success("收藏成功");
        } else {
            return Result.error("收藏失败");
        }
    }
    
    /**
     * 取消收藏
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 目标ID
     * @return 操作结果
     */
    @Operation(summary = "取消收藏")
    @PostMapping("/remove")
    public Result cancelFavorite(@RequestParam Long userId, 
                                @RequestParam String type,
                                @RequestParam Long targetId) {
        boolean success = favoriteService.cancelFavorite(userId, type, targetId);
        if (success) {
            return Result.success("取消收藏成功");
        } else {
            return Result.error("取消收藏失败");
        }
    }
    
    /**
     * 检查是否已收藏
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 目标ID
     * @return 操作结果
     */
    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check")
    public Result checkFavorite(@RequestParam Long userId, 
                               @RequestParam String type,
                               @RequestParam Long targetId) {
        boolean isFavorite = favoriteService.isFavorite(userId, type, targetId);
        Map<String, Boolean> data = new HashMap<>();
        data.put("isFavorite", isFavorite);
        return Result.success(data);
    }
    
    /**
     * 获取用户收藏列表
     * @param userId 用户ID
     * @param type 收藏类型（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 收藏列表
     */
    @Operation(summary = "获取用户收藏列表")
    @GetMapping("/list")
    public Result getFavoriteList(@RequestParam Long userId, 
                                 @RequestParam(required = false) String type,
                                 @RequestParam(defaultValue = "1") Integer page, 
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取用户收藏列表: userId={}, type={}, page={}, pageSize={}", userId, type, page, pageSize);
        return        favoriteService.getFavoritesByUserId(userId, type, page, pageSize);
    }
    
    /**
     * 获取用户收藏数量
     * @param userId 用户ID
     * @param type 收藏类型（可选）
     * @return 收藏数量
     */
    @Operation(summary = "获取用户收藏数量")
    @GetMapping("/count")
    public Result getFavoriteCount(@RequestParam Long userId, 
                                  @RequestParam(required = false) String type) {
        int count = favoriteService.getFavoriteCount(userId, type);
        Map<String, Integer> data = new HashMap<>();
        data.put("count", count);
        return Result.success(data);
    }
    
    /**
     * 获取用户收藏详情列表（包含收藏对象的详细信息）
     * @param userId 用户ID
     * @param type 收藏类型（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 收藏详情列表
     */
    @Operation(summary = "获取用户收藏详情列表")
    @GetMapping("/details")
    public Result getFavoriteDetailsList(@RequestParam Long userId, 
                                       @RequestParam(required = false) String type,
                                       @RequestParam(defaultValue = "1") Integer page, 
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取用户收藏详情列表: userId={}, type={}, page={}, pageSize={}", userId, type, page, pageSize);
        return favoriteService.getFavoriteDetailsById(userId, type, page, pageSize);
    }
    
    /**
     * 获取单个收藏详情
     * @param favoriteId 收藏ID
     * @return 收藏详情
     */
    @Operation(summary = "获取单个收藏详情")
    @GetMapping("/detail/{favoriteId}")
    public Result getFavoriteDetail(@PathVariable Long favoriteId) {
        log.info("获取单个收藏详情: favoriteId={}", favoriteId);
        return favoriteService.getFavoriteDetailById(favoriteId);
    }
}