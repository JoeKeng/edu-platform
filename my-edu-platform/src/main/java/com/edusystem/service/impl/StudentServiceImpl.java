package com.edusystem.service.impl;

import com.edusystem.mapper.StudentMapper;
import com.edusystem.mapper.UserMapper;
import com.edusystem.model.PageResult;
import com.edusystem.model.Student;
import com.edusystem.model.User;
import com.edusystem.service.StudentService;
import com.edusystem.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // 获取所有学生
    public List<Student> getAllStudents() {
        return studentMapper.getAllStudents();
    }

    // 根据 ID 获取学生
    public Student getStudentById(Long studentId) {
        return studentMapper.getStudentById(studentId);
    }

    // 添加学生
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
        return 1;
    }

    // 更新学生
    public int updateStudent(Student student) {
        return studentMapper.updateStudent(student);
    }

    // 逻辑删除学生
    public int deleteStudent(Long studentId) {
        return studentMapper.deleteStudent(studentId);
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
    public int deleteRealStudent(Long studentId) {
        return studentMapper.deleteRealStudent(studentId);
    }

}
