package com.edusystem.mapper;


import com.edusystem.model.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StudentMapper {
    List<Student> getAllStudents();
    Student getStudentById(@Param("studentId") Long studentId);
    int insertStudent(Student student);
    int updateStudent(Student student);
    int deleteStudent(@Param("studentId") Long studentId);

    List<Student> page(String studentNo, String studentName, Integer gender, Integer departmentId, Integer majorId, Integer classId);

    int deleteRealStudent(Long studentId);

    Integer getStudentIdByUserId(Integer userId);
}
