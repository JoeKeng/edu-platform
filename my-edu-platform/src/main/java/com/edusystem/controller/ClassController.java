package com.edusystem.controller;

import com.edusystem.model.Class;
import com.edusystem.model.PageResult;
import com.edusystem.model.Result;
import com.edusystem.model.Student;
import com.edusystem.service.ClassService;
import com.edusystem.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/classes")
@Tag(name = "班级管理")
public class ClassController {

    @Autowired
    private ClassService classService;
    @Autowired
    private StudentService studentService;

    /**
     * 分页查询班级
     */
    @Operation(summary = "分页查询班级信息")
    @GetMapping
    public Result page( @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer pageSize,
                        @RequestParam(required = false) String className,
                        @RequestParam(required = false) Integer departmentId,
                        @RequestParam(required = false) Integer majorId,
                        @RequestParam(required = false) String grade
                        )
    {
        log.info("分页查询，参数：page={}，pageSize={}，className={}，departmentId={}，majorId={}，grade={}",page,pageSize,className,departmentId,majorId,grade);
        PageResult<Class> pageResult = classService.page(page,pageSize,className,departmentId,majorId,grade);
        return Result.success(pageResult);
    }

    /**
     * 删除班级信息
     */
    @Operation(summary = "删除班级信息")
    @DeleteMapping("/{classid}")
    public Result delete(@PathVariable Integer id)
    {
        log.info("根据id删除班级，id={}",id);
        classService.delete(id);
        return Result.success();
    }
    /**
     * 添加班级信息
     */
    @Operation(summary = "添加班级信息")
    @PostMapping
    public Result save(@RequestBody Class clazz)
    {
        log.info("添加班级信息，clazz参数:{}",clazz);
        classService.save(clazz);
        return Result.success();
    }

    /**
     * 修改班级信息
     */
    @Operation(summary = "修改班级信息")
    @PutMapping
    public Result update(@RequestBody Class clazz)
    {
        log.info("修改班级信息，clazz参数:{}",clazz);
        classService.update(clazz);
        return Result.success();
    }
    /**
     * 获取班级列表
     */
    @Operation(summary = "获取班级列表")
    @GetMapping("/list")
    public Result list()
    {
        log.info("获取班级列表");
        List<Class> classList = classService.list();
        return Result.success(classList);
    }
    /**
     * 根据id查询班级信息
     */
    @Operation(summary = "根据id查询班级信息")
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("根据id查询班级信息：{}", id);
        Class clazz = classService.findById(id);
        return Result.success(clazz);
    }
    
       
    /**
     * 获取班级所有学生列表
     */
    @GetMapping("/class/{classId}/students")
    @Operation(summary = "获取班级所有学生列表")
    public Result getClassStudents(@PathVariable Integer classId) {
        log.info("获取班级所有学生列表: classId={}", classId);
        try {
            List<Student> students = studentService.getStudentsByClassId(classId);
            return Result.success(students);
        } catch (Exception e) {
            log.error("获取班级学生列表失败", e);
            return Result.error("获取班级学生列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加学生到班级
     */
    @Operation(summary = "添加学生到班级")
    @PostMapping("/{classId}/students/{studentId}")
    public Result addStudentToClass(@PathVariable Integer classId, @PathVariable Integer studentId)
    {
        log.info("添加学生到班级，classId={}，studentId={}",classId,studentId);
        classService.addStudentToClass(classId,studentId);
        return Result.success();
    }
    /**
     * 从班级中移除学生
     */
    @Operation(summary = "从班级中移除学生")
    @DeleteMapping("/{classId}/students/{studentId}")
    public Result removeStudentFromClass(@PathVariable Integer classId, @PathVariable Integer studentId)
    {
        log.info("从班级中移除学生，classId={}，studentId={}", classId, studentId);
        classService.removeStudentFromClass(classId, studentId);
        return Result.success();
    }
    

        
    /**
     * 获取班级统计信息
     */
    @GetMapping("/class/{classId}/statistics")
    @Operation(summary = "获取班级统计信息")
    public Result getClassStatistics(@PathVariable Integer classId) {
        log.info("获取班级统计信息: classId={}", classId);
        try {
            Class classInfo = classService.findById(classId);
            if (classInfo == null) {
                return Result.error("班级信息不存在");
            }

            // 获取班级学生列表
            List<com.edusystem.model.Student> students = studentService.getStudentsByClassId(classId);

            // 统计信息
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("className", classInfo.getClassName());
            statistics.put("totalStudents", students.size());

            // 统计性别比例
            long maleCount = students.stream().filter(s -> s.getGender() != null && s.getGender() == 1).count();
            long femaleCount = students.stream().filter(s -> s.getGender() != null && s.getGender() == 2).count();

            statistics.put("maleCount", maleCount);
            statistics.put("femaleCount", femaleCount);
            statistics.put("malePercentage", students.isEmpty() ? 0 : (double) maleCount / students.size() * 100);
            statistics.put("femalePercentage", students.isEmpty() ? 0 : (double) femaleCount / students.size() * 100);

            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取班级统计信息失败", e);
            return Result.error("获取班级统计信息失败: " + e.getMessage());
        }
    }
    

}
