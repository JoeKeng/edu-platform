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
    
    /**
     * 根据学生ID获取所有课程
     * @param studentId 学生ID
     * @return 课程列表
     */
    Result getCoursesByStudentId(Long studentId);
}
