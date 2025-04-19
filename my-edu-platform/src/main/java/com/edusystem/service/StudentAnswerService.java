package com.edusystem.service;

import com.edusystem.dto.AIGradeRequestDTO;
import com.edusystem.dto.AssignmentSubmissionDTO;
import com.edusystem.dto.StudentAnswerDTO;
import com.edusystem.dto.TeacherGradeDTO;
import com.edusystem.model.StudentAnswer;

import java.io.IOException;
import java.util.List;

public interface StudentAnswerService {
    // 提交学生答案
    int submitAnswer(StudentAnswerDTO studentAnswerDTO) throws IOException;

    // 获取作答详情
    StudentAnswer getAnswerById(Long id);

    // 获取某作业的所有作答
    List<StudentAnswer> getAnswersByAssignmentId(Long assignmentId);

    // 获取某个学生的所有作答
    List<StudentAnswer> getAnswersByStudentId(Long studentId);

    // AI 评分
//    int aiGradeBatch(AIGradeRequestDTO requestDTO);

    int hybridGrade(Long id, Long questionId, String studentAnswer, String aiModel) throws IOException;
    // 教师评分
    int teacherGrade(Long id, Boolean isCorrect ,Double score, String teacherFeedback , String explanation);

    int submitAssignment(AssignmentSubmissionDTO submission) throws IOException;

    void batchTeacherGrade(Long assignmentId, List<TeacherGradeDTO> grades);
}

