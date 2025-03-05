package com.edusystem.controller;

import com.edusystem.model.CourseChapter;
import com.edusystem.model.Result;
import com.edusystem.service.CourseChapterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class CourseChapterController {
    @Autowired
    private CourseChapterService chapterService;

    /**
     * 添加章节信息
     */
    @PostMapping("/teacher/course/chapter/add")
    public Result addChapter(@RequestBody CourseChapter chapter) {
        log.info("添加章节信息：{}", chapter);
        // 检查父章节是否存在，如果存在则检查父章节的课程ID是否与当前章节的课程ID一致
        if (chapter.getParentId() != null) {
            CourseChapter parentChapter = chapterService.getChapterById(chapter.getParentId());
            if (parentChapter == null) {
                return Result.error("父章节不存在");
            }
            if (!parentChapter.getCourseId().equals(chapter.getCourseId())) {
                return Result.error("父章节的课程ID与当前章节的课程ID不一致");
            }
        }
        chapterService.addChapter(chapter);
        return Result.success();
    }

    // 修改章节信息
    @PutMapping("/teacher/course/chapter/update")
    public Result updateChapter(@RequestBody CourseChapter chapter) {
        log.info("修改章节信息：{}", chapter);
        chapterService.updateChapter(chapter);
        return Result.success();
    }

    // 删除章节信息
    @DeleteMapping("/teacher/course/chapter/delete/{id}")
    public Result deleteChapter(@PathVariable Integer id) {
        log.info("删除章节信息，id：{}", id);
        chapterService.deleteChapter(id);
        return Result.success();
    }
    // 根据id查询章节信息
    @GetMapping("/common/chapter/{id}")
    public Result getChapterById(@PathVariable Integer id) {
        log.info("根据id查询章节信息，id：{}", id);
        return Result.success(chapterService.getChapterById(id));
    }

    // 根据课程id查询章节信息
    @GetMapping("/common/course/chapter/{courseId}")
    public Result getChaptersByCourse(@PathVariable Integer courseId) {
        log.info("根据课程id查询章节信息，courseId：{}", courseId);
        return Result.success(chapterService.getChaptersByCourseId(courseId));
    }
}