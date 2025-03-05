package com.edusystem.controller;

import com.edusystem.model.Assignment;
import com.edusystem.model.Result;
import com.edusystem.service.AssignmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    // 创建作业
    @PostMapping("/teacher/assignment/create")
    public Result createAssignment(@RequestBody Assignment assignment) {
        int result = assignmentService.createAssignment(assignment);
        return result > 0 ? Result.success() : Result.error("作业创建失败");
    }

    // 根据作业ID获取作业信息
    @GetMapping("/assignment/get/{id}")
    public Result getAssignment(@PathVariable Integer id) {
        Assignment assignment = assignmentService.getAssignmentById(id);
        return assignment != null ? Result.success(assignment) : Result.error("未找到作业");
    }

    // 根据课程ID获取作业列表
    @GetMapping("/assignment/list")
    public Result listAssignments(@RequestParam Integer courseId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByCourse(courseId);
        return Result.success(assignments);
    }

    // 根据章节ID获取作业列表
    @GetMapping("/assignment/listByChapter")
    public Result listAssignmentsByChapter(@RequestParam Integer chapterId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByChapter(chapterId);
        return Result.success(assignments);
    }

    // 更新作业信息
    @PutMapping("/teacher/assignment/update")
    public Result updateAssignment(@RequestBody Assignment assignment) {
        int result = assignmentService.updateAssignment(assignment);
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    // 删除作业
    @DeleteMapping("/teacher/assignment/delete/{id}")
    public Result deleteAssignment(@PathVariable Integer id) {
        int result = assignmentService.deleteAssignment(id);
        return result > 0 ? Result.success() : Result.error("删除失败");
    }
}
