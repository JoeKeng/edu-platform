package com.edusystem.controller;

import com.edusystem.model.LoginInfo;
import com.edusystem.model.Result;
import com.edusystem.model.User;
import com.edusystem.service.UserService;
import dao.LoginDTO;
import dao.UserRegisterDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@Slf4j
@RestController
public class AuthController {

    @Autowired
    private UserService userService;


    @PostMapping("/user/register")
    public Result register(@RequestBody UserRegisterDTO userDTO) {
        log.info("接收到注册请求, 用户名: {}", userDTO.getUsername());
        try {
            String result = userService.registerUser(
                    userDTO.getUsername(),
                    userDTO.getEmail(),
                    userDTO.getPhone(),
                    userDTO.getPassword(),
                    userDTO.getRole(),
                    userDTO.getRoleInfo()
            );
            log.info("注册结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 登录
     */
    @PostMapping("/user/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        if (loginDTO.getIdentifier() == null || loginDTO.getIdentifier().isEmpty()) {
            return Result.error("identifier 参数不能为空");
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
            return Result.error("password 参数不能为空");
        }

        log.info("登录请求信息, identifier: {}, password: {}", loginDTO.getIdentifier(), loginDTO.getPassword());
        LoginInfo loginInfo = userService.login(loginDTO.getIdentifier(), loginDTO.getPassword());

        if (loginInfo != null) {
            String token = loginInfo.getToken();
            // 使用 token
             if (token != null) {
                        return Result.success(loginInfo);
                    } else {
                        return Result.error("登录失败，用户名、邮箱或手机号不正确，或密码错误");
                    }
        } else {
            // 处理 loginInfo 为 null 的情况，例如抛出异常或返回默认值
            return Result.error("登录失败，用户名、邮箱或手机号不正确，或密码错误");
        }


    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/common/user/{userId}")
    public Result getUserInfo(@PathVariable Integer userId) {
        User user = userService.getUserInfoByUserId(userId);
        log.info("获取用户信息，用户ID: {}", userId);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/common/user/update")
    public Result updateUser(@RequestBody User user) {
        log.info("更新用户信息，参数：{}", user);
        userService.updateUserById(user);
        return Result.success();
    }

}