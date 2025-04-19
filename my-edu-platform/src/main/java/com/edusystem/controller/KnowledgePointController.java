  package com.edusystem.controller;

import com.edusystem.model.KnowledgePoint;
import com.edusystem.model.Result;
import com.edusystem.service.KnowledgePointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知识点管理控制器
 */
@Slf4j
@RestController
@Tag(name = "知识点管理")
public class KnowledgePointController {

    @Autowired
    private KnowledgePointService knowledgePointService;

    /**
     * 添加知识点
     */
    @Operation(summary = "添加知识点")
    @PostMapping("/teacher/knowledge/add")
    public Result addKnowledgePoint(@RequestBody KnowledgePoint knowledgePoint) {
        log.info("添加知识点：{}", knowledgePoint);
        boolean success = knowledgePointService.addKnowledgePoint(knowledgePoint);
        return success ? Result.success() : Result.error("添加知识点失败");
    }

    /**
     * 更新知识点
     */
    @Operation(summary = "更新知识点")
    @PutMapping("/teacher/knowledge/update")
    public Result updateKnowledgePoint(@RequestBody KnowledgePoint knowledgePoint) {
        log.info("更新知识点：{}", knowledgePoint);
        boolean success = knowledgePointService.updateKnowledgePoint(knowledgePoint);
        return success ? Result.success() : Result.error("更新知识点失败");
    }

    /**
     * 删除知识点
     */
    @Operation(summary = "删除知识点")
    @DeleteMapping("/teacher/knowledge/delete/{id}")
    public Result deleteKnowledgePoint(@PathVariable Long id) {
        log.info("删除知识点，id：{}", id);
        boolean success = knowledgePointService.deleteKnowledgePoint(id);
        return success ? Result.success() : Result.error("删除知识点失败");
    }

    /**
     * 根据ID获取知识点
     */
    @Operation(summary = "根据ID获取知识点")
    @GetMapping("/knowledge/get/{id}")
    public Result getKnowledgePoint(@PathVariable Long id) {
        log.info("获取知识点，id：{}", id);
        KnowledgePoint knowledgePoint = knowledgePointService.getKnowledgePointById(id);
        return knowledgePoint != null ? Result.success(knowledgePoint) : Result.error("知识点不存在");
    }

    /**
     * 根据课程ID获取知识点列表
     */
    @Operation(summary = "根据课程ID获取知识点列表")
    @GetMapping("/knowledge/list/course/{courseId}")
    public Result getKnowledgePointsByCourse(@PathVariable Integer courseId) {
        log.info("获取课程知识点，courseId：{}", courseId);
        List<KnowledgePoint> knowledgePoints = knowledgePointService.getKnowledgePointsByCourse(courseId);
        return Result.success(knowledgePoints);
    }

    /**
     * 根据章节ID获取知识点列表
     */
    @Operation(summary = "根据章节ID获取知识点列表")
    @GetMapping("/knowledge/list/chapter/{chapterId}")
    public Result getKnowledgePointsByChapter(@PathVariable Integer chapterId) {
        log.info("获取章节知识点，chapterId：{}", chapterId);
        List<KnowledgePoint> knowledgePoints = knowledgePointService.getKnowledgePointsByChapter(chapterId);
        return Result.success(knowledgePoints);
    }

    /**
     * 获取知识点树形结构
     */
    @Operation(summary = "获取知识点树形结构")
    @GetMapping("/knowledge/tree/{courseId}")
    public Result getKnowledgePointTree(@PathVariable Integer courseId) {
        log.info("获取知识点树形结构，courseId：{}", courseId);
        List<KnowledgePoint> knowledgePointTree = knowledgePointService.getKnowledgePointTree(courseId);
        return Result.success(knowledgePointTree);
    }

    /**
     * 添加题目知识点关联
     */
    @Operation(summary = "添加题目知识点关联")
    @PostMapping("/teacher/knowledge/question/add")
    public Result addQuestionKnowledgePoint(
            @RequestParam Long questionId,
            @RequestParam Long knowledgePointId,
            @RequestParam(required = false, defaultValue = "1.0") Double weight) {
        log.info("添加题目知识点关联，questionId：{}，knowledgePointId：{}，weight：{}", questionId, knowledgePointId, weight);
        boolean success = knowledgePointService.addQuestionKnowledgePoint(questionId, knowledgePointId, weight);
        return success ? Result.success() : Result.error("添加题目知识点关联失败");
    }

    /**
     * 删除题目知识点关联
     */
    @Operation(summary = "删除题目知识点关联")
    @DeleteMapping("/teacher/knowledge/question/delete")
    public Result deleteQuestionKnowledgePoint(
            @RequestParam Long questionId,
            @RequestParam Long knowledgePointId) {
        log.info("删除题目知识点关联，questionId：{}，knowledgePointId：{}", questionId, knowledgePointId);
        boolean success = knowledgePointService.deleteQuestionKnowledgePoint(questionId, knowledgePointId);
        return success ? Result.success() : Result.error("删除题目知识点关联失败");
    }

    /**
     * 更新题目知识点关联权重
     */
    @Operation(summary = "更新题目知识点关联权重")
    @PutMapping("/teacher/knowledge/question/weight")
    public Result updateQuestionKnowledgePointWeight(
            @RequestParam Long questionId,
            @RequestParam Long knowledgePointId,
            @RequestParam Double weight) {
        log.info("更新题目知识点关联权重，questionId：{}，knowledgePointId：{}，weight：{}", questionId, knowledgePointId, weight);
        boolean success = knowledgePointService.updateQuestionKnowledgePointWeight(questionId, knowledgePointId, weight);
        return success ? Result.success() : Result.error("更新题目知识点关联权重失败");
    }
    
    /**
     * 获取学生知识点掌握情况
     */
    @Operation(summary = "获取学生知识点掌握情况")
    @GetMapping("/student/knowledge/mastery")
    public Result getStudentKnowledgeMastery(
            @RequestParam Long studentId,
            @RequestParam Long knowledgePointId) {
        log.info("获取学生知识点掌握情况，studentId：{}，knowledgePointId：{}", studentId, knowledgePointId);
        Map<String, Object> mastery = knowledgePointService.getStudentKnowledgeMastery(studentId, knowledgePointId);
        return Result.success(mastery);
    }
    
    /**
     * 获取学生课程知识点掌握情况
     */
    @Operation(summary = "获取学生课程知识点掌握情况")
    @GetMapping("/student/knowledge/mastery/course")
    public Result getStudentCourseKnowledgeMastery(
            @RequestParam Long studentId,
            @RequestParam Integer courseId) {
        log.info("获取学生课程知识点掌握情况，studentId：{}，courseId：{}", studentId, courseId);
        List<Map<String, Object>> masteryList = knowledgePointService.getStudentCourseKnowledgeMastery(studentId, courseId);
        return Result.success(masteryList);
    }
    
    /**
     * 获取学生所有知识点掌握情况
     */
    @Operation(summary = "获取学生所有知识点掌握情况")
    @GetMapping("/student/knowledge/mastery/all")
    public Result getStudentAllKnowledgeMastery(@RequestParam Long studentId) {
        log.info("获取学生所有知识点掌握情况，studentId：{}", studentId);
        List<Map<String, Object>> masteryList = knowledgePointService.getStudentAllKnowledgeMastery(studentId);
        return Result.success(masteryList);
    }
    
    /**
     * 获取班级知识点掌握情况统计
     */
    @Operation(summary = "获取班级知识点掌握情况统计")
    @GetMapping("/teacher/class/knowledge/stats")
    public Result getClassKnowledgePointStats(
            @RequestParam Integer classId,
            @RequestParam Long knowledgePointId) {
        log.info("获取班级知识点掌握情况统计，classId：{}，knowledgePointId：{}", classId, knowledgePointId);
        Map<String, Object> stats = knowledgePointService.getClassKnowledgePointStats(classId, knowledgePointId);
        return Result.success(stats);
    }
}