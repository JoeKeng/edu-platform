package com.edusystem.controller;

import com.edusystem.model.PageResult;
import com.edusystem.model.Result;
import com.edusystem.model.Student;
import com.edusystem.service.ClassService;
import com.edusystem.service.StudentService;
import com.edusystem.service.UserService;
import com.edusystem.model.Class;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
@Tag(name = "学生管理")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassService classService;

    /**
     * 分页查询学生列表
     */
    @Operation(summary = "分页查询学生列表")
    @GetMapping
    public Result page( @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer pageSize,
                        @RequestParam(required = false) String studentNo,
                        @RequestParam(required = false) String studentName,
                        @RequestParam(required = false)Integer classId,
                        @RequestParam(required = false) Integer gender,
                        @RequestParam(required = false) Integer departmentId,
                        @RequestParam(required = false) Integer majorId

    )
            {
                log.info("分页查询，参数：page={}，pageSize={}，studentNo={}，studentName={}，gender={}，departmentId={}，majorId={}，classId={}",page,pageSize,studentNo,studentName,gender,departmentId,majorId,classId);
                PageResult<Student> pageResult = studentService.page(page,pageSize,studentNo,studentName,gender,departmentId,majorId,classId);
                return Result.success(pageResult);

    }

    /**
     * 获取所有学生列表
     */
    @Operation(summary = "获取所有学生列表")
    @GetMapping("/all")
    public Result getAllStudents() {
        log.info("获取所有学生列表");
        List<Student> students = studentService.getAllStudents();
        return Result.success(students);
    }

    /**
     * 根据 ID 获取学生信息
     */
    @Operation(summary = "根据 ID 获取学生信息")
    @GetMapping("/{id}")
    public Result getStudentById(@PathVariable("id") Long studentId) {
        log.info("获取学生信息, 学生ID: {}", studentId);
        Student student = studentService.getStudentById(studentId);
        return student != null ? Result.success(student) : Result.error("学生不存在");
    }

    /**
     * 添加学生
     */
    @Operation(summary = "添加学生")
    @PostMapping("/create")
    public Result createStudent(@RequestBody Student student) {
        log.info("添加学生信息: {}", student);
        try {
            int result = studentService.insertStudent(student);
            return result > 0 ? Result.success("添加成功") : Result.error("添加失败");
        } catch (Exception e) {
            log.error("添加学生失败", e);
            return Result.error("添加学生失败: " + e.getMessage());
        }
    }

    /**
     * 更新学生信息
     */
    @Operation(summary = "更新学生信息")
    @PutMapping("/update")
    public Result updateStudent(@RequestBody Student student) {
        log.info("更新学生信息: {}", student);
        try {
            int result = studentService.updateStudent(student);
            return result > 0 ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新学生失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 逻辑删除学生
     */
    @Operation(summary = "逻辑删除学生")
    @DeleteMapping("/{id}")
    public Result deleteStudent(@PathVariable("id") Long studentId) {
        log.info("逻辑删除学生, 学生ID: {}", studentId);
        try {
            int result = studentService.deleteStudent(studentId);
            return result > 0 ? Result.success("逻辑删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            log.error("逻辑删除学生失败", e);
            return Result.error("逻辑删除失败: " + e.getMessage());
        }
    }

    /**
     * 物理删除学生
     */
    @Operation(summary = "物理删除学生")
    @DeleteMapping("/delete/{id}")
    public Result deleteStudentById(@PathVariable("id") Long studentId) {
        log.info("删除学生, 学生ID: {}", studentId);
        try {
            int result = studentService.deleteRealStudent(studentId);
            return result > 0 ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除学生失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }
        
    /**
     * 获取当前用户所在班级信息
     */
    @GetMapping("/user/{userId}/class")
    @Operation(summary = "获取当前用户所在班级信息")
    public Result getUserClassInfo(@PathVariable Integer userId) {
        log.info("获取用户所在班级信息: userId={}", userId);
        try {
            // 获取学生ID
            Integer studentId = userService.getStudentId(userId);
            if (studentId == null) {
                return Result.error("该用户不是学生或不存在");
            }
            
            // 获取学生信息
            Student student = studentService.getStudentById(Long.valueOf(studentId));
            if (student == null) {
                return Result.error("学生信息不存在");
            }
            
            // 获取班级信息
            Integer classId = student.getClassId();
            if (classId == null) {
                return Result.error("该学生未分配班级");
            }
            
            Class classInfo = classService.findById(classId);
            if (classInfo == null) {
                return Result.error("班级信息不存在");
            }
            
            return Result.success(classInfo);
        } catch (Exception e) {
            log.error("获取用户所在班级信息失败", e);
            return Result.error("获取班级信息失败: " + e.getMessage());
        }
    }

}

