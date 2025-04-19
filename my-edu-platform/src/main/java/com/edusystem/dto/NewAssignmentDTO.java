package com.edusystem.dto;

import com.edusystem.model.QuestionBank;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class NewAssignmentDTO {
    private Integer id;//课程作业唯一标识符
    private Long teacherId;//关联教师ID
    private Integer courseId;//关联课程ID
    private Integer chapterId;//关联章节ID
    private String title;//作业标题
    private String description;//作业描述信息
    private Integer maxAttempts;//最大允许提交次数
    private Date startTime;//作业开始时间
    private Date endTime;//作业截止时间
    private Date createdAt;//记录创建时间
    private Date updatedAt;//记录最后更新时间
    private List<Long> questionIds; // 题目ID列表（用于前端传参）
    private List<QuestionBank> newQuestions;//问题列表（用于后端处理）

}

