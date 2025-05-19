package com.edusystem.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.edusystem.handler.ListTypeHandler;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 题库表实体类
 * 用于存储试题的基本信息及关联数据
 */
@Data
public class QuestionBank {
    private Long id;//题目唯一标识符（主键）
    private Integer courseId; //关联的课程ID
    private Integer chapterId;//关联的章节ID
    private Long teacherId; //出题教师ID
    private String type;//题目类型（如单选/多选/判断等）
    private String questionText;//题目正文内容
    private Double score;//题目分值
    private String options;//题目选项（JSON格式存储）
    private String correctAnswer;//正确答案（根据题目类型存储不同格式）
    private String analysis;// 题目解析说明
    private String difficulty;//难度等级（如简单/中等/困难）
    private Date createdAt;//题目创建时间
    private Date updatedAt;//题目最后更新时间
    // 添加知识点关联属性
    @TableField(exist = false)  // 标记该字段不是数据库表中的字段
    private List<Long> knowledgePointIds;
     // 非数据库字段，用于关联知识点权重
     @TableField(exist = false)
     private List<Double> knowledgePointWeights;  // 关联的知识点权重列表
}
