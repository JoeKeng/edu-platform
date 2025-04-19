package com.edusystem.controller;

import com.edusystem.model.Class;
import com.edusystem.model.Result;
import com.edusystem.service.ClassService;
import com.edusystem.service.CourseService;
import com.edusystem.service.StudentService;
import com.edusystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统信息控制器
 */
@RestController
@RequestMapping("/system")
@Slf4j
@Tag(name = "系统信息", description = "系统信息相关接口")
public class SystemController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private ClassService classService;
    
    @Autowired
    private CourseService courseService;
    
    @Value("${spring.application.name:教育管理系统}")
    private String applicationName;
    
    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;
    
    /**
     * 获取系统基本信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取系统基本信息")
    public Result getSystemInfo() {
        log.info("获取系统基本信息");
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("name", applicationName);
        systemInfo.put("version", applicationVersion);
        systemInfo.put("description", "教育管理平台，提供学生、教师、课程等管理功能");
        return Result.success(systemInfo);
    }
    
    /**
     * 获取学生所有课程信息
     */
    @GetMapping("/student/{studentId}/courses")
    @Operation(summary = "获取学生所有课程信息")
    public Result getStudentCourses(@PathVariable Long studentId) {
        log.info("获取学生所有课程信息: studentId={}", studentId);
        try {
            return courseService.getCoursesByStudentId(studentId);
        } catch (Exception e) {
            log.error("获取学生课程信息失败", e);
            return Result.error("获取学生课程信息失败: " + e.getMessage());
        }
    }
}