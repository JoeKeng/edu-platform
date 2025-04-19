package com.edusystem.service;

import com.edusystem.model.PageResult;
import com.edusystem.model.Student;

import java.util.List;

public interface StudentService {
    
    List<Student> getAllStudents();

    Student getStudentById(Long studentId);

    int insertStudent(Student student);

    int updateStudent(Student student);

    int deleteStudent(Long studentId);

    PageResult<Student> page(Integer page, Integer pageSize, String studentNo, String studentName, Integer gender, Integer departmentId, Integer majorId, Integer classId);

    int deleteRealStudent(Long studentId);
    
    /**
     * 根据班级ID获取学生列表
     * @param classId 班级ID
     * @return 学生列表
     */
    List<Student> getStudentsByClassId(Integer classId);
}
