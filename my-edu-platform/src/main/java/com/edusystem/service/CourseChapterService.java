package com.edusystem.service;

import com.edusystem.model.CourseChapter;
import java.util.List;

public interface CourseChapterService {
    void addChapter(CourseChapter chapter);

    void updateChapter(CourseChapter chapter);

    void deleteChapter(Integer chapterId);

    CourseChapter getChapterById(Integer chapterId);

    List<CourseChapter> getChaptersByCourseId(Integer courseId);
}
