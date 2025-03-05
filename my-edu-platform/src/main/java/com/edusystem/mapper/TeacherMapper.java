package com.edusystem.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeacherMapper {


    Integer getTeacherIdByUserId(Integer userId);
}
