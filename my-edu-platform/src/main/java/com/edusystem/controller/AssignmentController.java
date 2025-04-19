package com.edusystem.controller;

import com.edusystem.dto.AssignmentSubmissionDTO;
import com.edusystem.dto.NewAssignmentDTO;
import com.edusystem.model.Assignment;
import com.edusystem.model.Result;
import com.edusystem.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "作业管理")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    // 创建作业
    @Operation(summary = "创建作业")
    @PostMapping("/teacher/assignment/create")
    public Result createAssignment(@RequestBody NewAssignmentDTO assignmentDTO) {
        int result = assignmentService.createAssignment(assignmentDTO);
        return result > 0 ? Result.success() : Result.error("作业创建失败");
    }

    // 根据作业ID获取作业信息
    @Operation(summary = "根据作业ID获取作业信息")
    @GetMapping("/assignment/get/{id}")
    public Result getAssignment(@PathVariable Integer id) {
        Assignment assignment = assignmentService.getAssignmentById(id);
        return assignment != null ? Result.success(assignment) : Result.error("未找到作业");
    }

    // 根据课程ID获取作业列表
    @Operation(summary = "根据课程ID获取作业列表")
    @GetMapping("/assignment/list")
    public Result listAssignments(@RequestParam Integer courseId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByCourse(courseId);
        return Result.success(assignments);
    }

    // 根据章节ID获取作业列表
    @Operation(summary = "根据章节ID获取作业列表")
    @GetMapping("/assignment/listByChapter")
    public Result listAssignmentsByChapter(@RequestParam Integer chapterId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByChapter(chapterId);
        return Result.success(assignments);
    }

    // 根据教师ID获取作业列表
    @Operation(summary = "根据教师ID获取作业列表")
    @GetMapping("/teacher/list")
    public Result getTeacherAssignments(@RequestParam Long teacherId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByTeacher(teacherId);
        return Result.success(assignments);
    }

    // 根据作业ID和角色获取作业题目详情
    @Operation(summary = "根据作业ID和角色获取作业题目详情")
    @GetMapping("/assignment/detail")
    public Result getAssignmentDetail(@RequestParam Integer assignmentId) {
        Assignment assignment = assignmentService.getAssignmentDetail(assignmentId);
        return Result.success(assignment);
    }


    // 更新作业信息
    @Operation(summary = "更新作业信息")
    @PutMapping("/teacher/assignment/update")
    public Result updateAssignment(@RequestBody Assignment assignment) {
        int result = assignmentService.updateAssignment(assignment);
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    // 删除作业
    @Operation(summary = "删除作业")
    @DeleteMapping("/teacher/assignment/delete/{id}")
    public Result deleteAssignment(@PathVariable Integer id) {
        int result = assignmentService.deleteAssignment(id);
        return result > 0 ? Result.success() : Result.error("删除失败");
    }

//    //提交作业
//    @PostMapping("/submit")
//    public Result submitAssignment(@RequestBody AssignmentSubmissionDTO submissionDTO) {
//        boolean success = assignmentService.submitAssignment(submissionDTO);
//        return success ? Result.success() : Result.error("提交失败");
//    }

}
