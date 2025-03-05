package com.edusystem.controller;

import com.edusystem.model.Class;
import com.edusystem.model.PageResult;
import com.edusystem.model.Result;
import com.edusystem.service.ClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/classes")
public class ClassController {

    @Autowired
    private  ClassService classService;

    /**
     * 分页查询班级
     */
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
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("根据id查询班级信息：{}",id);
        Class clazz = classService.findById(id);
        return Result.success(clazz);
    }

    /**
     * 添加学生到班级
     */
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
    @DeleteMapping("/{classId}/students/{studentId}")
    public Result removeStudentFromClass(@PathVariable Integer classId, @PathVariable Integer studentId)
    {
        log.info("从班级中移除学生，classId={}，studentId={}",classId,studentId);
        classService.removeStudentFromClass(classId,studentId);
        return Result.success();
    }

}
