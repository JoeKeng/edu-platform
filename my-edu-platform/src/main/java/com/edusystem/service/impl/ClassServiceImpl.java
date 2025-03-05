package com.edusystem.service.impl;

import com.edusystem.mapper.ClassMapper;
import com.edusystem.model.Class;
import com.edusystem.model.PageResult;
import com.edusystem.service.ClassService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Override
    public PageResult<Class> page(Integer page, Integer pageSize, String className, Integer departmentId, Integer majorId, String grade) {
        //1.设置分页查询(PageHelper)
        PageHelper.startPage(page, pageSize);
        //2.执行查询
        List<Class> rows = classMapper.page(className, departmentId, majorId, grade);
        //3.解析查询结果,并封装
        Page<Class> pageInfo = (Page<Class>) rows;

        return new PageResult<Class>(pageInfo.getTotal(), pageInfo.getResult());
    }

    @Override
    public void delete(Integer id) {
        classMapper.delete(id);
    }

    @Override
    public void save(Class clazz) {
        classMapper.insert(clazz);
    }

    @Override
    public void update(Class clazz) {

        classMapper.update(clazz);
    }

    @Override
    public List<Class> list() {

        return classMapper.lists();
    }

    @Override
    public Class findById(Integer id) {

        return classMapper.findById(id);
    }

    // 实现方法：添加学生到班级
    @Override
    public void addStudentToClass(Integer classId, Integer studentId) {
        classMapper.addStudentToClass(classId, studentId);
    }

    // 实现方法：从班级移除学生
    @Override
    public void removeStudentFromClass(Integer classId, Integer studentId) {
        classMapper.removeStudentFromClass(classId, studentId);
    }
}
