package com.edusystem.service;

import com.aliyuncs.ram.model.v20150501.CreateUserResponse;
import com.edusystem.model.LoginInfo;
import com.edusystem.model.User;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 用户注册方法
     * @param username 用户名
     * @param email 邮箱
     * @param phone 手机号
     * @param password 明文密码
     * @param role 用户角色
     * @return 注册结果消息
     */
    String registerUser(String username, String email, String phone, String password, String role, Map<String, Object> roleInfo);


    /**
     * 用户登录
     */

    LoginInfo login(String identifier, String password);

    User getUserInfoByUserId(Integer userId);

    void updateUserById(User user);

    Integer getStudentId(Integer userId) ;

    Integer getTeacherId(Integer userId);
}
