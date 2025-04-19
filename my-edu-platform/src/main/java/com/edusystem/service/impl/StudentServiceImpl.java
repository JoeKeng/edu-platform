package com.edusystem.service.impl;

import com.edusystem.mapper.StudentMapper;
import com.edusystem.mapper.UserMapper;
import com.edusystem.model.PageResult;
import com.edusystem.model.Student;
import com.edusystem.model.User;
import com.edusystem.service.ClassService;
import com.edusystem.service.StudentService;
import com.edusystem.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserService userService; // 添加UserService的注入
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ClassService classService; // 添加ClassService的注入

    // 获取所有学生
    public List<Student> getAllStudents() {
        return studentMapper.getAllStudents();
    }

    // 根据 用户ID 获取学生
    public Student getStudentByUserId(Integer userId) {
        Long studentId = studentMapper.getStudentIdByUserId(userId);
        return studentMapper.getStudentByStudentId(studentId);
    }

    // 根据 学生ID 获取学生
    public Student getStudentById(Long studentId) {
        return studentMapper.getStudentByStudentId(studentId);
    }

    // 添加学生
    @Transactional(rollbackFor = Exception.class)
    public int insertStudent(Student student) {
        // 创建用户账户
        String username = "A_" + student.getStudentNo();
        String password = "X_" + student.getStudentNo();
        Map<String, Object> roleInfo = new HashMap<>();
        roleInfo.put("studentNo", student.getStudentNo());
        roleInfo.put("studentName", student.getStudentName());
        roleInfo.put("gender", student.getGender());
        roleInfo.put("age", student.getAge());
        roleInfo.put("departmentId", student.getDepartmentId());
        roleInfo.put("classId", student.getClassId());
        roleInfo.put("majorId", student.getMajorId());
        roleInfo.put("studentPhoto", student.getStudentPhoto());


        String registerResult = userService.registerUser(username, null, null, password, "student", roleInfo);
        if (!registerResult.equals("学生用户注册成功!")) {
            throw new RuntimeException("学生账户创建失败: " + registerResult);
        }
        
        // 如果学生有班级，更新班级人数
        if (student.getClassId() != null) {
            classService.updateClassSize(student.getClassId());
        }
        
        return 1;
    }

    // 更新学生
    @Transactional(rollbackFor = Exception.class)
    public int updateStudent(Student student) {
        // 获取学生原信息，用于判断班级是否变更
        Student oldStudent = studentMapper.getStudentByStudentId(Long.valueOf(student.getStudentId()));
        int result = studentMapper.updateStudent(student);
        
        // 如果班级发生变更，需要更新新旧两个班级的人数
        if (oldStudent != null) {
            // 更新旧班级人数
            if (oldStudent.getClassId() != null) {
                classService.updateClassSize(oldStudent.getClassId());
            }
            
            // 如果新班级与旧班级不同，更新新班级人数
            if (student.getClassId() != null && 
                (oldStudent.getClassId() == null || !oldStudent.getClassId().equals(student.getClassId()))) {
                classService.updateClassSize(student.getClassId());
            }
        }
        
        return result;
    }

    // 逻辑删除学生
    @Transactional(rollbackFor = Exception.class)
    public int deleteStudent(Long studentId) {
        // 获取学生信息，用于更新班级人数
        Student student = studentMapper.getStudentByStudentId(studentId);
        int result = studentMapper.deleteStudent(studentId);
        
        // 如果学生有班级，更新班级人数
        if (student != null && student.getClassId() != null) {
            classService.updateClassSize(student.getClassId());
        }
        
        return result;
    }

    @Override
    public PageResult<Student> page(Integer page, Integer pageSize, String studentNo, String studentName, Integer gender, Integer departmentId, Integer majorId, Integer classId) {
        //1.设置分页查询(PageHelper)
        PageHelper.startPage(page, pageSize);
        //2.执行查询
        List<Student> rows = studentMapper.page(studentNo, studentName, gender, departmentId, majorId, classId);
        //3.解析查询结果,并封装
        Page<Student> pageInfo = (Page<Student>) rows;
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getResult());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRealStudent(Long studentId) {
        // 获取学生信息，用于更新班级人数
        Student student = studentMapper.getStudentByStudentId(studentId);
        int result = studentMapper.deleteRealStudent(studentId);
        
        // 如果学生有班级，更新班级人数
        if (student != null && student.getClassId() != null) {
            classService.updateClassSize(student.getClassId());
        }
        
        return result;
    }
    
    @Override
    public List<Student> getStudentsByClassId(Integer classId) {
        log.info("根据班级ID获取学生列表: classId={}", classId);
        return studentMapper.getStudentsByClassId(classId);
    }
}
