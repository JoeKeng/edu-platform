package com.edusystem.service;

import com.edusystem.model.CourseResource;
import java.util.List;

public interface CourseResourceService {
    void addResource(CourseResource resource);

    void updateResource(CourseResource resource);

    void deleteResource(Integer resourceId);

    CourseResource getResourceById(Integer resourceId);

    List<CourseResource> getResourcesByCourseId(Integer courseId);

    List<CourseResource> getResourcesByChapterId(Integer chapterId);
}
