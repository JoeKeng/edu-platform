package com.edusystem.service.impl;

import com.edusystem.mapper.CourseChapterMapper;
import com.edusystem.model.CourseChapter;
import com.edusystem.service.CourseChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseChapterServiceImpl implements CourseChapterService {
    @Autowired
    private CourseChapterMapper chapterMapper;

    @Override
    public void addChapter(CourseChapter chapter) {
        chapterMapper.insert(chapter);
    }

    @Override
    public void updateChapter(CourseChapter chapter) {
        chapterMapper.update(chapter);
    }

    @Override
    public void deleteChapter(Integer chapterId) {
        chapterMapper.delete(chapterId);
    }

    @Override
    public CourseChapter getChapterById(Integer chapterId) {
        return chapterMapper.findById(chapterId);
    }

    @Override
    public List<CourseChapter> getChaptersByCourseId(Integer courseId) {
        return chapterMapper.findByCourseId(courseId);
    }
}

