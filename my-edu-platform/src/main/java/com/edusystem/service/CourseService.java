package com.edusystem.service;

import com.edusystem.model.Course;
import com.edusystem.model.Result;

import java.util.List;

public interface CourseService {
    Result getCourses(String courseName, Long teacherId, Integer departmentId, Integer page, Integer pageSize);

    Result getCourseById(Integer courseId);

    Result addCourse(Course course);


    Result updateCourse(Course course);

    Result deleteCourse(Integer courseId);

    Result getAllCourses();

    Result getPopularCourses();

    Result addCourseToTeacher(Integer courseId, Long teacherId);

    Result removeCourseFromTeacher(Integer courseId, Long teacherId);
}
