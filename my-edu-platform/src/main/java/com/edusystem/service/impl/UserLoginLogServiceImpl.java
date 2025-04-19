package com.edusystem.service.impl;

import com.edusystem.mapper.UserLoginLogMapper;
import com.edusystem.service.UserLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoginLogServiceImpl implements UserLoginLogService {

    @Autowired
    private UserLoginLogMapper userLoginLogMapper;

    @Override
    public void recordLogin(Long userId, String ipAddress, String deviceInfo) {
        userLoginLogMapper.insertLoginLog(userId, ipAddress, deviceInfo);
    }
}
