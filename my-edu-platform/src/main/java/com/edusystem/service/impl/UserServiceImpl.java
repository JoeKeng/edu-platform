package com.edusystem.service.impl;

import com.aliyuncs.ram.model.v20150501.CreateUserResponse;
import com.edusystem.mapper.StudentMapper;
import com.edusystem.mapper.TeacherMapper;
import com.edusystem.mapper.UserMapper;
import com.edusystem.model.LoginInfo;
import com.edusystem.model.Student;
import com.edusystem.model.Teacher;
import com.edusystem.model.User;
import com.edusystem.service.UserService;
import com.edusystem.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private UserMapper userMapper;

    // 添加BCryptPasswordEncoder的实例
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册逻辑
     */
    @Transactional // 添加事务管理
    @Override
    public String registerUser(String username, String email, String phone, String password, String role, Map<String, Object> roleInfo) {
        // 先检查邮箱是否已注册
        if (userMapper.findByEmail(email) != null) {
            return "邮箱已存在!";
        }
        // 检查其他条件，如手机号是否已注册等
        if (phone != null && !phone.isEmpty()) {
            if (userMapper.findByPhone(phone) != null) {
                return "手机号已存在!";
            }
        }
        // 检查其他条件，如用户名是否已存在等
        if (username != null && !username.isEmpty()) {
            if (userMapper.findByUsername(username) != null) {
                return "用户名已存在!";
            }
        }
        if (password == null || password.isEmpty()) {
            return "密码不能为空!";
        }
        //角色默认为学生用户并且只能为studnet或teacher
        if (role == null || role.isEmpty() || (!role.equals("student") && !role.equals("teacher"))) {
            return "角色不能为空或错误!";
        }
        // 创建新用户对象
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        // 对密码进行加密
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setRole(role);

        // 插入用户数据
        userMapper.insertUser(newUser);

        // 根据角色插入到student或teacher表
        if ("student".equals(role) && roleInfo != null) {
            Student student = new Student();
            student.setUserId(newUser.getUserId());
            // 设置其他学生特定的字段
            student.setStudentNo((String) roleInfo.get("studentNo"));
            student.setStudentName((String) roleInfo.get("studentName"));
            student.setGender((Integer) roleInfo.get("gender"));
            student.setAge((Integer) roleInfo.get("age"));
            student.setDepartmentId((Integer) roleInfo.get("departmentId"));
            student.setClassId((Integer) roleInfo.get("classId"));
            student.setMajorId((Integer) roleInfo.get("majorId"));

            student.setStudentPhoto((String) roleInfo.get("studentPhoto"));
            userMapper.insertStudent(student);
            return "学生用户注册成功!";
        } else if ("teacher".equals(role) && roleInfo != null) {
            Teacher teacher = new Teacher();
            teacher.setUserId(newUser.getUserId());
            // 设置其他教师特定的字段
            teacher.setTeacherName((String) roleInfo.get("teacherName"));
            teacher.setTitle((String) roleInfo.get("title"));
            teacher.setOffice((String) roleInfo.get("office"));
            userMapper.insertTeacher(teacher);
            return "教师用户注册成功!";
        }

        return "用户注册成功!";
    }

    @Override
    public LoginInfo login(String identifier, String password) {

        User user = userMapper.findByUsername(identifier);
        if(user!=null){
            log.info("用户 user:{}" , user);
        }
        if (user == null) {
            user = userMapper.findByEmail(identifier);
        }
        if (user == null) {
            user = userMapper.findByPhone(identifier);
        }
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            // 生成JWT令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getUserId());
            claims.put("username", user.getUsername());
            claims.put("role", user.getRole());
            String jwt = JwtUtil.generateToken(claims);
            return new LoginInfo(user.getUserId(), user.getUsername(), user.getRole(), jwt);
        }
        return null;
    }

    @Override
    public User getUserInfoByUserId(Integer userId) {
        return userMapper.getUserInfoByUserId(userId);
    }

    @Override
    public void updateUserById(User user) {
        userMapper.updateUserById(user);
    }


    public Integer getStudentId(Integer userId) {
        return studentMapper.getStudentIdByUserId(userId);
    }

    public Integer getTeacherId(Integer userId) {
        return teacherMapper.getTeacherIdByUserId(userId);
    }

}
