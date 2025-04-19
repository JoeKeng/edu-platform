package com.edusystem.controller;

import com.edusystem.model.Course;
import com.edusystem.model.Result;
import com.edusystem.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "课程管理")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 分页查询课程列表
     */
    @Operation(summary = "分页查询课程列表", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/list")
    public Result getCourseList(@RequestParam(required = false) String courseName,
                                @RequestParam(required = false) Long teacherId,
                                @RequestParam(required = false) Integer departmentId,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页查询课程列表，参数：courseName={}，teacherId={}，departmentId={}，page={}，pageSize={}", courseName, teacherId, departmentId, page, pageSize);
        return courseService.getCourses(courseName, teacherId, departmentId, page, pageSize);
    }

    /**
     * 根据课程ID获取课程信息
     */
    @Operation(summary = "根据课程ID获取课程信息")
    @GetMapping("/common/courses/{courseId}")
    public Result getCourse(@PathVariable Integer courseId) {
        log.info("根据课程ID获取课程信息，参数：courseId={}", courseId);
        return courseService.getCourseById(courseId);
    }

    /**
     * 添加课程
     */
    @Operation(summary = "添加课程")
    @PostMapping("/teacher/courses")
    public Result addCourse(@RequestBody Course course
                            ) {
        log.info("添加课程，参数：course={}, teacherIds={}, classIds={}, currentTeacherId={}", course);
        return courseService.addCourse(course);
    }

    /**
     * 更新课程信息
     */
    @Operation(summary = "更新课程信息")
    @PutMapping("/teacher/courses")
    public Result updateCourse(@RequestBody Course course) {
        log.info("更新课程信息，参数：{}", course);
        return courseService.updateCourse(course);
    }

    /**
     * 删除课程
     */
    @Operation(summary = "删除课程")
    @DeleteMapping("/teacher/courses/{courseId}")
    public Result deleteCourse(@PathVariable Integer courseId) {
        log.info("删除课程，参数：courseId={}", courseId);
        return courseService.deleteCourse(courseId);
    }
    /**
     * 查询所有课程
     */
    @Operation(summary = "查询所有课程", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/common/courses/list")
    public Result getAllCourses() {
        log.info("查询所有课程");
        return courseService.getAllCourses();
    }

    /**
     * 绑定课程到教师
     */
    @Operation(summary = "绑定课程到教师")
    @PostMapping("/teacher/courses/{courseId}/teachers/{teacherId}")
    public Result addCourseToTeacher(@PathVariable Integer courseId, @PathVariable Long teacherId) {
        log.info("绑定课程到教师，参数：courseId={}, teacherId={}", courseId, teacherId);
        return courseService.addCourseToTeacher(courseId, teacherId);
    }

    /**
     * 解绑课程和教师
     */
    @Operation(summary = "解绑课程和教师")
    @DeleteMapping("/teacher/courses/{courseId}/teachers/{teacherId}")
    public Result removeCourseFromTeacher(@PathVariable Integer courseId, @PathVariable Long teacherId) {
        log.info("解绑课程和教师，参数：courseId={}, teacherId={}", courseId, teacherId);
        return courseService.removeCourseFromTeacher(courseId, teacherId);
    }


}

