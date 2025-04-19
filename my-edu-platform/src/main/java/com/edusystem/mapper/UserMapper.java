package com.edusystem.mapper;
import com.edusystem.model.Student;
import com.edusystem.model.Teacher;
import com.edusystem.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 通过邮箱查找用户
     * @param email 用户邮箱
     * @return User 对象
     */
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);

    /**
     * 通过手机号查找用户
     * @param phone 用户手机号
     * @return User 对象
     */
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(String phone);

    /**
     * 通过用户名查找用户
     * @param username 用户名
     * @return User 对象
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 插入新用户
     * @param user 用户对象
     */
    @Insert("INSERT INTO user (username, email, phone, password_hash, role, status) " +
            "VALUES (#{username}, #{email}, #{phone}, #{passwordHash}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insertUser(User user);

    /**
     * 插入学生信息
     * @param student
     */
    @Insert("INSERT INTO student (user_id, student_no, student_name, gender, age, department_id, class_id, major_id, student_photo) " +
            "VALUES (#{userId}, #{studentNo}, #{studentName}, #{gender}, #{age}, #{departmentId}, #{classId}, #{majorId}, #{studentPhoto})")
    void insertStudent(Student student);

    @Insert("INSERT INTO teacher (user_id, teacher_name, title, office) " +
            "VALUES (#{userId}, #{teacherName}, #{title}, #{office})")
    void insertTeacher(Teacher teacher);

    /**
     * 更新用户信息
     * @param user 用户对象
     */
    void updateUserById(User user);

    /**
     * 删除用户
     * @param userId 用户ID
     */
    @Delete("DELETE FROM user WHERE user_id = #{userId}")
    void deleteUser(Long userId);

    /**
     * 获取所有用户
     * @return 用户列表
     */
    @Select("SELECT * FROM user")
    List<User> getAllUsers();

    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User getUserInfoByUserId(Integer userId);



}