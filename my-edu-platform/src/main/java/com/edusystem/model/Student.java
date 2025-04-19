package com.edusystem.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Student {
    private Long studentId; //学生id
    private Integer userId; //用户id
    private String studentNo; //学生编号
    private String studentName; //学生姓名
    private Integer gender; //性别 1:男 2:女 3:未知
    private Integer age;//年龄
    private Integer departmentId;//所属学院id
    private Integer majorId;//所属专业id
    private Integer classId;//所属班级id
    private String studentPhoto;//学生照片

}
