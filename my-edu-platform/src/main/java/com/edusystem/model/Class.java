package com.edusystem.model;

import lombok.Data;

@Data
public class Class {
    private Integer classId; //班级id
    private String className; ///班级名称
    private Integer departmentId;//学院id
    private Integer majorId;//专业id
    private String grade;//年级
    private Integer classSize;//班级人数

}
