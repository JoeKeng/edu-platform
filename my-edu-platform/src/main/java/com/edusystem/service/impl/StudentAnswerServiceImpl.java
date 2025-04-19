
package com.edusystem.service.impl;

import com.edusystem.dto.*;
import com.edusystem.exception.BusinessException;
import com.edusystem.factory.AIGradingFactory;
import com.edusystem.mapper.QuestionBankMapper;
import com.edusystem.mapper.StudentAnswerMapper;
import com.edusystem.mapper.StudentMapper;
import com.edusystem.model.QuestionBank;
import com.edusystem.model.StudentAnswer;
import com.edusystem.service.AIService;
import com.edusystem.service.StudentAnswerService;
import com.edusystem.util.CurrentHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * 学生答案服务实现类
 * 处理学生答案的提交、查询和评分相关业务逻辑
 */
@Service
public class StudentAnswerServiceImpl implements StudentAnswerService {

    @Autowired
    private StudentAnswerMapper studentAnswerMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private AIService aiGradingService;
    @Autowired
    private QuestionBankMapper questionBankMapper;
    @Autowired
    private AIGradingFactory aiGradingFactory;

    /**
     * 提交单个题目答案
     * @param studentAnswerDTO 包含题目答案的学生答案实体对象
     * @return 数据库受影响的行数（1表示成功，0表示失败）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitAnswer(StudentAnswerDTO studentAnswerDTO) throws IOException {
        Integer userId = CurrentHolder.getCurrentId();
        Long studentId = studentMapper.getStudentIdByUserId(userId);
        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setStudentId(studentId);
        studentAnswer.setQuestionId(studentAnswerDTO.getQuestionId());
        studentAnswer.setAnswer(studentAnswerDTO.getAnswer());
        studentAnswer.setFileUrls(studentAnswerDTO.getFileUrls());

        int count = studentAnswerMapper.insert(studentAnswer);

        // 获取题目类型
        QuestionBank question = questionBankMapper.getById(studentAnswerDTO.getQuestionId());
        if (question != null && isObjectiveQuestion(question.getType())) {
            // 选择题/填空题/判断题：提交时 AI 评分
            AIGeneratedResult aiResult = aiGradingService.grade(
                    question.getId(),
                    studentAnswerDTO.getAnswer(),
                    question.getQuestionText(),
                    question.getCorrectAnswer()
            );
            studentAnswerMapper.updateScoreAndFeedback(
                    studentAnswer.getId(),
                    aiResult.getIsCorrect(),
                    aiResult.getAiScore(),
                    null,
                    aiResult.getFeedback(),
                    null,
                    aiResult.getExplanation()
            );
        } else {
            // 主观题：异步 AI 评分
            asyncAIGrade(studentAnswer.getId());
        }

        return count;
    }

    /**
     * 根据答案ID获取答案详情
     * @param id 答案记录唯一标识符
     * @return 对应ID的学生答案实体对象
     */
    @Override
    public StudentAnswer getAnswerById(Long id) {
        return studentAnswerMapper.selectById(id);
    }

    /**
     * 获取指定作业的全部学生答案
     * @param assignmentId 作业唯一标识符
     * @return 该作业下所有学生的答案列表
     */
    @Override
    public List<StudentAnswer> getAnswersByAssignmentId(Long assignmentId) {
        return studentAnswerMapper.selectByAssignmentId(assignmentId);
    }

    /**
     * 获取指定学生的所有答案记录
     * @param studentId 学生唯一标识符
     * @return 该学生提交的所有答案列表
     */
    @Override
    public List<StudentAnswer> getAnswersByStudentId(Long studentId) {
        return studentAnswerMapper.selectByStudentId(studentId);
    }

    /**
     * 混合模式评分
     * 1. 先尝试直接匹配客观题答案
     * 2. 主观题调用 AI 评分
     */
    public int hybridGrade(Long id, Long questionId, String studentAnswer, String aiModel) throws IOException {
        // 查询正确答案
        QuestionBank question = questionBankMapper.getById(questionId);
        String correctAnswer = question.getCorrectAnswer();
        String questionText = question.getQuestionText();

        // 获取 AI 评分服务
        AIService aiService = aiGradingFactory.getAIService(aiModel);

        // 进行评分
        AIGeneratedResult result = aiService.grade(questionId, questionText ,studentAnswer ,  correctAnswer);

        // 更新数据库
        return studentAnswerMapper.updateScoreAndFeedback(id, result.getIsCorrect(), result.getAiScore(), null , result.getFeedback(), null, result.getExplanation());
    }
    /**
     * 教师手动评分
     * @param id 答案记录ID
     * @param teacherScore 教师评定的分数
     * @param teacherFeedback 教师评语
     * @return 数据库受影响的行数
     */
    @Override
    public int teacherGrade(Long id, Boolean isCorrect, Double teacherScore, String teacherFeedback , String explanation) {
        // 查询学生答案
        StudentAnswer studentAnswer = studentAnswerMapper.selectById(id);
        if (studentAnswer == null) {
            throw new RuntimeException("学生答案不存在");
        }

        // **更新评分和反馈**
        return studentAnswerMapper.updateScoreAndFeedback(id, isCorrect, null, teacherScore, null, teacherFeedback, explanation);
    }


    /**
     * 提交作业
     * 选择题/填空题：提交时 AI 评分
     * 主观题：提交后异步 AI 评分
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int submitAssignment(AssignmentSubmissionDTO submission) throws IOException {
        int count = 0;
        Integer userId = CurrentHolder.getCurrentId();
        Long studentId = studentMapper.getStudentIdByUserId(userId);
        submission.setStudentId(studentId);

        for (StudentAnswerDTO answerDTO : submission.getAnswers()) {
            StudentAnswer studentAnswer = new StudentAnswer();
            studentAnswer.setStudentId(studentId);
            studentAnswer.setAssignmentId(submission.getAssignmentId());
            studentAnswer.setQuestionId(answerDTO.getQuestionId());
            studentAnswer.setAnswer(answerDTO.getAnswer());
            studentAnswer.setFileUrls(answerDTO.getFileUrls());

            count += studentAnswerMapper.insert(studentAnswer);

            // 获取题目类型
            QuestionBank question = questionBankMapper.getById(answerDTO.getQuestionId());
            if (question != null && isObjectiveQuestion(question.getType())) {
                // 选择题/填空题：提交时 AI 评分
                AIGeneratedResult aiResult = aiGradingService.grade(question.getId(), answerDTO.getAnswer(), question.getQuestionText(), question.getCorrectAnswer());
                studentAnswerMapper.updateScoreAndFeedback(studentAnswer.getId(), aiResult.getIsCorrect(), aiResult.getAiScore(), null,aiResult.getFeedback(), null , aiResult.getExplanation());
            } else {
                // 主观题：异步 AI 评分
                asyncAIGrade(studentAnswer.getId());
            }
        }
        return count;
    }

    @Override
    @Transactional
    public void batchTeacherGrade(Long assignmentId, List<TeacherGradeDTO> grades) {
        if (grades == null || grades.isEmpty()) {
            throw new BusinessException("评分列表不能为空");
        }

        studentAnswerMapper.batchUpdateTeacherGrade(grades);
    }


    /**
     * 判断是否为客观题（选择题/填空题）
     */
    private boolean isObjectiveQuestion(String type) {
        return "单选".equals(type) || "多选".equals(type) || "填空".equals(type);
    }

    /**
     * 异步 AI 评分（适用于主观题）
     */
    @Async
    public void asyncAIGrade(Long answerId) throws IOException {
        StudentAnswer answer = studentAnswerMapper.selectById(answerId);
        QuestionBank question = questionBankMapper.getById(answer.getQuestionId());

        if (answer != null && question != null) {
            // 调用 AI 评分
            AIGeneratedResult aiResult = aiGradingService.grade(
                    question.getId(),
                    answer.getAnswer(),
                    question.getQuestionText(),
                    question.getCorrectAnswer()
            );

            // 更新数据库（评分、反馈、解析）
            studentAnswerMapper.updateScoreAndFeedback(
                    answerId, aiResult.getIsCorrect(), aiResult.getAiScore(), null ,aiResult.getFeedback(),null, aiResult.getExplanation()
            );
        }
    }

}
