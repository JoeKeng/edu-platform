package com.edusystem.service.impl;

import com.edusystem.mapper.ClassMapper;
import com.edusystem.mapper.StudentMapper;
import com.edusystem.model.Class;
import com.edusystem.model.PageResult;
import com.edusystem.service.ClassService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;
    
    @Autowired
    private StudentMapper studentMapper;

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
    @Transactional(rollbackFor = Exception.class)
    public void addStudentToClass(Integer classId, Integer studentId) {
        classMapper.addStudentToClass(classId, studentId);
        // 更新班级人数
        updateClassSize(classId);
    }

    // 实现方法：从班级移除学生
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeStudentFromClass(Integer classId, Integer studentId) {
        classMapper.removeStudentFromClass(classId, studentId);
        // 更新班级人数
        updateClassSize(classId);
    }
    
    /**
     * 更新班级人数
     * @param classId 班级ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateClassSize(Integer classId) {
        log.info("更新班级人数: classId={}", classId);
        // 获取班级信息
        Class clazz = classMapper.findById(classId);
        if (clazz == null) {
            log.error("班级不存在: classId={}", classId);
            return;
        }
        
        // 查询班级实际学生数量
        List<com.edusystem.model.Student> students = studentMapper.getStudentsByClassId(classId);
        int actualSize = students.size();
        
        // 更新班级人数
        if (clazz.getClassSize() == null || clazz.getClassSize() != actualSize) {
            log.info("班级[{}]人数从{}更新为{}", clazz.getClassName(), clazz.getClassSize(), actualSize);
            clazz.setClassSize(actualSize);
            classMapper.update(clazz);
        }
    }
    
    /**
     * 批量更新所有班级人数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAllClassSizes() {
        log.info("开始批量更新所有班级人数");
        List<Class> classes = classMapper.lists();
        for (Class clazz : classes) {
            updateClassSize(clazz.getClassId());
        }
        log.info("完成批量更新所有班级人数，共更新{}个班级", classes.size());
    }
    
    /**
     * 获取班级统计信息
     * @param departmentId 学院ID
     * @param majorId 专业ID
     * @param grade 年级
     * @return 班级统计信息
     */
    @Override
    public List<Class> getClassStatistics(Integer departmentId, Integer majorId, String grade) {
        // 先确保所有班级人数是最新的
        updateAllClassSizes();
        // 查询班级信息
        return classMapper.page(null, departmentId, majorId, grade);
    }
}
