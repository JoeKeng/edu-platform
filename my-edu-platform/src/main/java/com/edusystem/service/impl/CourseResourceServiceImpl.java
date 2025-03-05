package com.edusystem.service.impl;

import com.edusystem.mapper.CourseResourceMapper;
import com.edusystem.model.CourseResource;
import com.edusystem.service.CourseResourceService;
import com.edusystem.util.CurrentHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseResourceServiceImpl implements CourseResourceService {
    @Autowired
    private CourseResourceMapper resourceMapper;

    @Override
    public void addResource(CourseResource resource) {
        //获取当前用户ID
        Long userId = Long.valueOf(CurrentHolder.getCurrentId());

        //设置上传用户ID
        resource.setUploadUserId(userId);

        resourceMapper.insert(resource);
    }

    @Override
    public void updateResource(CourseResource resource) {
        resourceMapper.update(resource);
    }

    @Override
    public void deleteResource(Integer resourceId) {
        resourceMapper.delete(resourceId);
    }

    @Override
    public CourseResource getResourceById(Integer resourceId) {
        return resourceMapper.findById(resourceId);
    }

    @Override
    public List<CourseResource> getResourcesByCourseId(Integer courseId) {
        return resourceMapper.findByCourseId(courseId);
    }

    @Override
    public List<CourseResource> getResourcesByChapterId(Integer chapterId) {
        return resourceMapper.findByChapterId(chapterId);
    }
}
