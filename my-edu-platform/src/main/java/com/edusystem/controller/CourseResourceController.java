package com.edusystem.controller;

import com.edusystem.model.CourseResource;
import com.edusystem.model.Result;
import com.edusystem.service.CourseResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "课程资源管理" ,description = "课程资源管理相关的接口")
public class CourseResourceController {
    @Autowired
    private CourseResourceService resourceService;

    /**
     * 添加资源
     */
    @Operation(summary = "添加资源")
    @PostMapping("/teacher/course/resource/add")
    public Result addResource(@RequestBody CourseResource resource) {
        log.info("添加资源 resource:{}", resource);
        resourceService.addResource(resource);
        return Result.success();
    }

    /**
     * 更新资源
     */
    @Operation(summary = "更新资源")
    @PutMapping("/teacher/course/resource/update")
    public Result updateResource(@RequestBody CourseResource resource) {
        log.info("更新资源 resource:{}", resource);
        resourceService.updateResource(resource);
        return Result.success();
    }

    /**
     * 删除资源
     */
    @Operation(summary = "删除资源")
    @DeleteMapping("/teacher/course/resource/delete/{id}")
    public Result deleteResource(@PathVariable Integer id) {
        log.info("删除资源 id:{}", id);
        resourceService.deleteResource(id);
        return Result.success();
    }

    /**
     * 根据id获取资源
     */
    @Operation(summary = "根据id获取资源")
    @GetMapping("/common/resource/{id}")
    public Result getResourceById(@PathVariable Integer id) {
        log.info("获取资源 id:{}", id);
        return Result.success(resourceService.getResourceById(id));
    }

    /**
     * 根据课程id获取资源
     */
    @Operation(summary = "根据课程id获取资源")
    @GetMapping("/common/course/resource/{courseId}")
    public Result getResourcesByCourse(@PathVariable Integer courseId) {
        log.info("获取资源 courseId:{}", courseId);
        return Result.success(resourceService.getResourcesByCourseId(courseId));
    }
    /**
     * 根据章节id获取资源
     */
    @Operation(summary = "根据章节id获取资源")
    @GetMapping("/common/course/resource/chapter/{chapterId}")
    public Result getResourcesByChapter(@PathVariable Integer chapterId) {
        log.info("获取资源 chapterId:{}", chapterId);
        return Result.success(resourceService.getResourcesByChapterId(chapterId));
    }
}
