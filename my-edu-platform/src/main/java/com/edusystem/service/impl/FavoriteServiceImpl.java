package com.edusystem.service.impl;

import com.edusystem.mapper.CourseMapper;
import com.edusystem.mapper.FavoriteMapper;
import com.edusystem.mapper.QuestionBankMapper;
import com.edusystem.mapper.CourseResourceMapper;
import com.edusystem.model.Course;
import com.edusystem.model.CourseResource;
import com.edusystem.model.Favorite;
import com.edusystem.model.PageResult;
import com.edusystem.model.QuestionBank;
import com.edusystem.model.Result;
import com.edusystem.service.FavoriteService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Service
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private QuestionBankMapper questionMapper;
    
    @Autowired
    private CourseResourceMapper courseResourceMapper;
    
    @Override
    @Transactional
    public boolean addFavorite(Long userId, String type, Long targetId) {
        // 检查是否已收藏
        Favorite existingFavorite = favoriteMapper.checkFavorite(userId, String.valueOf(type), targetId);
        if (existingFavorite != null) {
            // 已收藏且未删除，无需操作
            return true;
        }

        // 创建新收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setType(type);
        favorite.setTargetId(targetId);
        favorite.setCreatedAt(LocalDateTime.now());
        favorite.setUpdatedAt(LocalDateTime.now());
        favorite.setIsDeleted(0);
        
        return favoriteMapper.insert(favorite) > 0;
    }
    
    @Override
    @Transactional
    public boolean cancelFavorite(Long userId, String type, Long targetId) {
        return favoriteMapper.cancelFavorite(userId, type, targetId) > 0;
    }
    
    @Override
    public boolean isFavorite(Long userId, String type, Long targetId) {
        return favoriteMapper.checkFavorite(userId, type, targetId) != null;
    }
    
    @Override
    public Result getFavoritesByUserId(Long userId, String type, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);

        List<Favorite> favorites = favoriteMapper.selectByUserId(userId, type != null ? type : null);
        
        Page<Favorite> pageInfo = (Page<Favorite>) favorites;

        return Result.success(new PageResult<Favorite>(pageInfo.getTotal(), pageInfo.getResult()));
    }
    
    @Override
    public int getFavoriteCount(Long userId, String type) {
        return favoriteMapper.countByUserId(userId, type != null ? type : null);
    }

    @Override
    public Result getFavoriteDetailsById(Long userId, String type, Integer page, Integer pageSize) {
        try {
            PageHelper.startPage(page, pageSize);
            List<Favorite> favorites = favoriteMapper.selectByUserId(userId, type);
            Page<Favorite> pageInfo = (Page<Favorite>) favorites;

            // 批量预加载关联数据
           Map<String, List<Long>> typeIds = pageInfo.getResult().stream()  // 确保Favorite列表非空
                   .collect(Collectors.groupingBy(
                           Favorite::getType,
                           Collectors.mapping(f -> f.getTargetId(), Collectors.toList())
                   ));

           // 处理Course数据
           Map<Long, Course> courses = !typeIds.containsKey("course")? Collections.emptyMap():
                   (courseMapper.batchGetCourses(typeIds.get("course"))
                           .stream()
                           .collect(Collectors.toMap(c-> (long) c.getCourseId(), c -> c)));


           // QuestionBank数据
           Map<Long, QuestionBank> questions = !typeIds.containsKey("question") ? Collections.emptyMap() :
                   questionMapper.batchGetByIds(typeIds.get("question"))
                           .stream()
                           .collect(Collectors.toMap(QuestionBank::getId, q -> q));

           // 处理CourseResource数据
           Map<Long, CourseResource> resources = !typeIds.containsKey("resource") ? Collections.emptyMap() :
                   courseResourceMapper.batchFindByIds(typeIds.get("resource"))
                           .stream()
                           .collect(Collectors.toMap(r-> (long) r.getResourceId(), r -> r));

           // 构建详情列表
           List<Map<String, Object>> detailsList = pageInfo.getResult().stream().map(favorite -> {
               Map<String, Object> detailMap = new HashMap<>();


               Object detail = switch (favorite.getType()) {
                   case "course" -> courses.getOrDefault(favorite.getTargetId(),// 获取课程信息，如果课程不存在则返回一个默认的课程对象
                           new Course());
                   case "question" -> questions.getOrDefault(favorite.getTargetId(),
                           new QuestionBank());
                   case "resource" -> resources.getOrDefault(favorite.getTargetId(),
                           new CourseResource());
                   default -> null;
               };
               detailMap.put("favorite", favorite);
               detailMap.put("detail", detail);
               return detailMap;
           }).collect(Collectors.toList());


            PageResult<Map<String, Object>> pageResult = new PageResult<>();
            pageResult.setTotal(pageInfo.getTotal()); // 总记录数
            pageResult.setRows(detailsList); // 数据列表
            //分页参数由PageHelper自动处理，无需手动设置page和pageSize


            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("获取收藏详情失败 userId:{}", userId, e);
            return Result.error("获取收藏信息失败，请稍后重试");
        }
    }

    
    @Override
    public Result getFavoriteDetailById(Long favoriteId) {
        // 1. 获取收藏信息
        Favorite favorite = favoriteMapper.selectById(favoriteId);
        if (favorite == null) {
            return Result.error("收藏不存在");
        }
        
        // 2. 获取收藏对象的详细信息
        Map<String, Object> detailMap = new HashMap<>();
        
        // 根据收藏类型获取不同的详细信息
        switch (favorite.getType()) {
            case "course":
                Course course = courseMapper.getCourseById(favorite.getTargetId().intValue());
                detailMap.put("detail", course);
                break;
            case "question":
                QuestionBank question = questionMapper.getById(favorite.getTargetId());
                detailMap.put("detail", question);
                break;
            case "resource":
                CourseResource resource = courseResourceMapper.findById(favorite.getTargetId().intValue());
                detailMap.put("detail", resource);
                break;
            default:
                detailMap.put("detail", null);
                break;
        }
        detailMap.put("favorite", favorite);
        return Result.success(detailMap);
    }
}