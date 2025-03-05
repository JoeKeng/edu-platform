package com.edusystem.mapper;

import com.edusystem.model.Class;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClassMapper {

    List<Class> page(String className, Integer departmentId, Integer majorId, String grade);

    void delete(Integer id);

    void insert(Class clazz);

    void update(Class clazz);

    List<Class> lists();

    Class findById(Integer id);

    // 新增方法：添加学生到班级
    void addStudentToClass(Integer classId, Integer studentId);

    // 新增方法：从班级移除学生
    void removeStudentFromClass(Integer classId, Integer studentId);
}