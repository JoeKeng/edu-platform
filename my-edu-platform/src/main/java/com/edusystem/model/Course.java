package com.edusystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Integer courseId; // 课程ID，唯一标识课程
    private String courseName; // 课程名称
    private String courseCode; // 课程代码，用于标识课程的唯一编码
    private Integer categoryId; // 课程类别的ID
    private Integer departmentId; // 课程所属部门的ID
    private String description; // 课程描述，简要介绍课程内容
    private Double credits; // 课程学分
    private String syllabus; // 课程大纲，详细描述课程内容和安排
    private Date createdAt; // 课程创建时间
    private Date updatedAt; // 课程最后更新时间
    private Boolean isDeleted; // 标记课程是否被删除

    private String cover; // 课程封面URL
    private Date startDate; // 开课日期
    private Date endDate; // 结课日期
    private Integer enrollmentLimit; // 选课人数上限
    private List<Long> teacherIds; // 多个教师ID列表
    private List<Integer> classIds; // 关联班级 ID 列表



}