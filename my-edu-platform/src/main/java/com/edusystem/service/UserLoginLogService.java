package com.edusystem.service;

public interface UserLoginLogService {
    public void recordLogin(Long userId, String ipAddress, String deviceInfo) ;
}
