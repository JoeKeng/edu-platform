package com.edusystem.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserLoginLogMapper {


    void insertLoginLog(Long userId, String ipAddress, String deviceInfo);
}
