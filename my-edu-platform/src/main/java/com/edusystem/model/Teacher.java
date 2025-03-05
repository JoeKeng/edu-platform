package com.edusystem.model;

import lombok.Data;

@Data
public class Teacher {
    private Integer teacherId;//教师id
    private Integer userId;//教师用户id
    private String teacherName;//教师姓名
    private String title;//职称
    private String office;//办公室地址
}
