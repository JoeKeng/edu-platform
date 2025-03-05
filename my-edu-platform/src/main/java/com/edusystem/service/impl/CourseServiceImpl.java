package com.edusystem.service.impl;

import com.edusystem.mapper.CourseMapper;
import com.edusystem.mapper.TeacherMapper;
import com.edusystem.model.Course;
import com.edusystem.model.PageResult;
import com.edusystem.model.Result;
import com.edusystem.service.CourseService;
import com.edusystem.util.CurrentHolder;
import com.edusystem.util.JwtUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private TeacherMapper teacherMapper;

    // 分页查询课程
    public Result getCourses(String courseName, Long teacherId, Integer departmentId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Course> courses = courseMapper.queryCourses(courseName, teacherId, departmentId);
        Page<Course> pageInfo = (Page<Course>) courses;
        return Result.success(new PageResult<Course>(pageInfo.getTotal(), pageInfo.getResult()));
    }

    // 根据 ID 查询课程（包括关联班级和教师）
    public Result getCourseById(Integer courseId) {
        Course course = courseMapper.getCourseById(courseId);
        if (course == null) return Result.error("课程不存在");

        List<Integer> classIds = courseMapper.getCourseClasses(courseId);
        course.setClassIds(classIds);

        List<Long> teacherIds = courseMapper.getCourseTeachers(courseId);
        course.setTeacherIds(teacherIds);
        return Result.success(course);
    }

    // 添加课程
    //添加事务管理
    @Transactional
    @Override
    public Result addCourse(Course course) {
        int result = courseMapper.addCourse(course);
        Integer generatedCourseId = course.getCourseId(); // 假设 MyBatis 自动填充了这个字段
        course.setCourseId(generatedCourseId);
        if (result <= 0) {
            return Result.error("课程添加失败");
        }
        List<Long> teacherIds = course.getTeacherIds();
        List<Integer> classIds = course.getClassIds();

        // 绑定教师
        if (teacherIds != null && !teacherIds.isEmpty()) {
            for (Long teacherId : teacherIds) {
                courseMapper.addCourseTeacher(course.getCourseId(), teacherId);
            }
        } else {
            // 获取当前登录用户的教师ID
            Integer userId = CurrentHolder.getCurrentId();
            Integer teacherIdInt = teacherMapper.getTeacherIdByUserId(userId);

            if (teacherIdInt == null) {
                // 处理 teacherIdInt 为 null 的情况
                // 例如：抛出自定义异常或记录日志
                try {
                    throw new Exception("无法找到用户ID为 " + userId + " 的教师记录");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            Long teacherId = Long.valueOf(teacherIdInt);
            // 将 Integer 类型转换为 Long 类型
            if (course.getCourseId() == null) {
                throw new IllegalArgumentException("课程ID不能为空");
            }
            courseMapper.addCourseTeacher(course.getCourseId(), teacherId);
        }

        // 绑定班级
        if (classIds != null && !classIds.isEmpty()) {
            for (Integer classId : classIds) {
                courseMapper.addCourseClass(course.getCourseId(), classId);
            }
        } else {
            // 处理 classIds 为空的情况
            throw new IllegalArgumentException("班级ID列表不能为空");
        }


        return Result.success("课程添加成功");
    }

    // 更新课程
    public Result updateCourse(Course course) {
        if (course.getSyllabus() == null) {
            course.setSyllabus("[]"); // 默认空 JSON 数组
        }
        int result = courseMapper.updateCourse(course);
        if (result <= 0) {
            return Result.error("课程更新失败");
        }

        // 更新教师关联
        List<Long> teacherIds = course.getTeacherIds();
        System.out.println(teacherIds);
        if (teacherIds != null) {
            // 先删除原有的教师关联
            courseMapper.deleteCourseTeachers(course.getCourseId());
            // 再添加新的教师关联
            for (Long teacherId : teacherIds) {
                courseMapper.addCourseTeacher(course.getCourseId(), teacherId);
            }
        }

        // 更新班级关联
        List<Integer> classIds = course.getClassIds();
        System.out.println(classIds);
        if (classIds != null) {
            // 先删除原有的班级关联
            courseMapper.deleteCourseClasses(course.getCourseId());
            // 再添加新的班级关联
            for (Integer classId : classIds) {
                courseMapper.addCourseClass(course.getCourseId(), classId);
            }
        }

        return Result.success("课程更新成功");
    }

    // 删除课程
    public Result deleteCourse(Integer courseId) {
        int result = courseMapper.deleteCourse(courseId);
        return result > 0 ? Result.success("课程删除成功") : Result.error("课程删除失败");
    }

    // 获取所有课程
    @Override
    public Result getAllCourses() {
        List<Course> courses = courseMapper.getAllCourses();
        return Result.success(courses);

    }

    // 获取热门课程
    @Override
    public Result getPopularCourses() {
        List<Course> popularCourses = courseMapper.getPopularCourses();
        return Result.success(popularCourses);
    }

    // 绑定课程到班级
    public Result addCourseToClass(Integer courseId, Integer classId) {
        int result = courseMapper.addCourseClass(courseId, classId);
        return result > 0 ? Result.success("绑定成功") : Result.error("绑定失败");
    }

    // 解绑课程和班级
    public Result removeCourseFromClass(Integer courseId, Integer classId) {
        int result = courseMapper.removeCourseClass(courseId, classId);
        return result > 0 ? Result.success("解绑成功") : Result.error("解绑失败");
    }

    // 绑定课程到教师
    public Result addCourseToTeacher(Integer courseId, Long teacherId) {
        int result = courseMapper.addCourseTeacher(courseId, teacherId);
        return result > 0 ? Result.success("绑定成功") : Result.error("绑定失败");
    }

    // 解绑课程和教师
    public Result removeCourseFromTeacher(Integer courseId, Long teacherId) {
        int result = courseMapper.removeCourseTeacher(courseId, teacherId);
        return result > 0 ? Result.success("解绑成功") : Result.error("解绑失败");
    }

}