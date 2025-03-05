package com.edusystem.mapper;

import com.edusystem.model.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CourseMapper {

    List<Course> queryCourses(@Param("courseName") String courseName,
                              @Param("teacherId") Long teacherId,
                              @Param("departmentId") Integer departmentId);

    Course getCourseById(@Param("courseId") Integer courseId);

    int addCourse(Course course);

    int updateCourse(Course course);

    int deleteCourse(@Param("courseId") Integer courseId);

    List<Integer> getCourseClasses(@Param("courseId") Integer courseId);

    int addCourseClass(@Param("courseId") Integer courseId, @Param("classId") Integer classId);

    int removeCourseClass(@Param("courseId") Integer courseId, @Param("classId") Integer classId);

    List<Course> getAllCourses();

    List<Course> getPopularCourses();

    List<Long> getCourseTeachers(@Param("courseId") Integer courseId);

    int addCourseTeacher(@Param("courseId") Integer courseId, Long teacherId);

    int removeCourseTeacher(@Param("courseId") Integer courseId, @Param("teacherId") Long teacherId);


    void deleteCourseTeachers(Integer courseId);

    void deleteCourseClasses(Integer courseId);
}

