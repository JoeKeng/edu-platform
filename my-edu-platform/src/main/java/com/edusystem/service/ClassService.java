package com.edusystem.service;

import com.edusystem.model.Class;
import com.edusystem.model.PageResult;

import java.util.List;

public interface ClassService {
    PageResult<Class> page(Integer page, Integer pageSize, String className, Integer departmentId, Integer majorId, String grade);

    void delete(Integer id);

    void save(Class clazz);

    void update(Class clazz);

    List<Class> list();

    Class findById(Integer id);

    // 新增方法：添加学生到班级
    void addStudentToClass(Integer classId, Integer studentId);

    // 新增方法：从班级移除学生
    void removeStudentFromClass(Integer classId, Integer studentId);
    
    /**
     * 更新班级人数
     * @param classId 班级ID
     */
    void updateClassSize(Integer classId);
    
    /**
     * 批量更新所有班级人数
     */
    void updateAllClassSizes();
    
    /**
     * 获取班级统计信息
     * @param departmentId 学院ID
     * @param majorId 专业ID
     * @param grade 年级
     * @return 班级统计信息
     */
    List<Class> getClassStatistics(Integer departmentId, Integer majorId, String grade);
}