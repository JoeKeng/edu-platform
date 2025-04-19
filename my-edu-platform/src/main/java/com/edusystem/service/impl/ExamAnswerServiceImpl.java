package com.edusystem.service.impl;

import com.edusystem.dto.AIGeneratedResult;
import com.edusystem.factory.AIGradingFactory;
import com.edusystem.mapper.ExamAnswerMapper;
import com.edusystem.mapper.ExamQuestionMapper;
import com.edusystem.mapper.QuestionBankMapper;
import com.edusystem.model.ExamAnswer;
import com.edusystem.model.ExamQuestion;
import com.edusystem.model.QuestionBank;
import com.edusystem.service.AIService;
import com.edusystem.service.ExamAnswerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ExamAnswerServiceImpl implements ExamAnswerService {

    @Autowired
    private ExamAnswerMapper examAnswerMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    private AIService aiGradingService;

    @Autowired
    private AIGradingFactory aiGradingFactory;
    @Autowired
    private QuestionBankMapper questionBankMapper;

    /**
     * AI自动批改考试答案
     * @param examAnswerId 考试答案ID
     * @return 批改结果
     */
    @Override
    public AIGeneratedResult aiGradeExamAnswer(Long examAnswerId) throws IOException {
        // 获取考试答案信息
        ExamAnswer examAnswer = examAnswerMapper.selectByAnswerId(examAnswerId);
        if (examAnswer == null) {
            throw new RuntimeException("考试答案不存在");
        }

        // 获取题目信息
        ExamQuestion examQuestion = examQuestionMapper.getByExamIdAndQuestionId(examAnswer.getExamId(),examAnswer.getQuestionId());
        if (examQuestion == null) {
            throw new RuntimeException("考试题目不存在");
        }

        QuestionBank question = questionBankMapper.getById(examAnswer.getQuestionId());
        // 调用AI评分服务
        AIGeneratedResult result = aiGradingService.grade(
                examQuestion.getQuestionId(),
                examAnswer.getAnswer(),
                question.getQuestionText(),
                question.getCorrectAnswer()
        );

        // 更新考试答案的分数和状态
        examAnswer.setScore(result.getAiScore().intValue());
        examAnswer.setStatus("已批改");
        examAnswer.setAiFeedback(result.getFeedback());
        examAnswer.setIsCorrect(result.getIsCorrect());
        examAnswer.setExplanation(result.getExplanation());
        examAnswer.setUpdatedAt(LocalDateTime.now());
        examAnswerMapper.update(examAnswer);

        return result;
    }

    /**
     * 批量AI自动批改考试答案
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 批改成功的数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAIGradeExamAnswers(Long examId, Long studentId) throws IOException {
        // 获取学生在该考试中的所有答案
        List<ExamAnswer> answers = examAnswerMapper.selectByExamAndStudent(examId, studentId);
        if (answers == null || answers.isEmpty()) {
            return 0;
        }

        int successCount = 0;
        for (ExamAnswer answer : answers) {
            try {
                aiGradeExamAnswer(answer.getId());
                successCount++;
            } catch (Exception e) {
                log.error("批改考试答案失败: {}", e.getMessage());
            }
        }

        return successCount;
    }

    /**
     * 教师手动批改考试答案
     * @param examAnswerId 考试答案ID
     * @param score 分数
     * isCorrect 是否正确
     * teacherFeedback 教师反馈
     * @return 批改结果
     */
    @Override
    public int teacherGradeExamAnswer(Long examAnswerId, Boolean isCorrect, Integer score, String teacerFeedback) {
        ExamAnswer examAnswer = examAnswerMapper.selectByAnswerId(examAnswerId);
        if (examAnswer == null) {
            throw new RuntimeException("考试答案不存在");
        }

        // 更新分数和反馈
        examAnswer.setScore(score);
        // 这里可以添加一个反馈字段，目前ExamAnswer实体类中没有
        examAnswer.setIsCorrect(isCorrect);
        examAnswer.setTeacherFeedback(teacerFeedback);
        examAnswer.setStatus("已批改");
        examAnswer.setUpdatedAt(LocalDateTime.now());

        return examAnswerMapper.update(examAnswer);
    }

    /**
     * 批量教师手动批改考试答案
     * @param examId 考试ID
     * @param studentId 学生ID
     * @param scores 分数列表
     * @param feedbacks 反馈列表
     * @return 批改成功的数量
     */
    @Override
    @Transactional
    public int batchTeacherGradeExamAnswers(Long examId, Long studentId,List<Boolean> isCorrects, List<Integer> scores, List<String> feedbacks) {
        // 获取学生在该考试中的所有答案
        List<ExamAnswer> answers = examAnswerMapper.selectByExamAndStudent(examId, studentId);
        if (answers == null || answers.isEmpty() || scores == null || scores.size() != answers.size()) {
            return 0;
        }

        int successCount = 0;
        for (int i = 0; i < answers.size(); i++) {
            ExamAnswer answer = answers.get(i);
            Integer score = i < scores.size() ? scores.get(i) : null;
            String feedback = i < feedbacks.size() ? feedbacks.get(i) : null;
            Boolean isCorrect = i < isCorrects.size() ? isCorrects.get(i) : null;

            if (score != null) {
                try {
                    teacherGradeExamAnswer(answer.getId(),isCorrect, score, feedback);
                    successCount++;
                } catch (Exception e) {
                    log.error("教师批改考试答案失败: {}", e.getMessage());
                }
            }
        }

        return successCount;
    }

    /**
     * 获取考试答案详情
     * @param examAnswerId 考试答案ID
     * @return 考试答案详情
     */
    @Override
    public ExamAnswer getExamAnswerById(Long examAnswerId) {
        return examAnswerMapper.selectByExamAndQuestion(examAnswerId, null);
    }

    /**
     * 获取学生在某次考试中的所有答案
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 考试答案列表
     */
    @Override
    public List<ExamAnswer> getExamAnswersByExamAndStudent(Long examId, Long studentId) {
        return examAnswerMapper.selectByExamAndStudent(examId, studentId);
    }

    /**
     * 获取某次考试的所有答案
     * @param examId 考试ID
     * @return 考试答案列表
     */
    @Override
    public List<ExamAnswer> getExamAnswersByExam(Long examId) {
        // 这个方法需要在ExamAnswerMapper中添加
        // 暂时返回null
        return null;
    }

    /**
     * 更新考试答案状态
     * @param examAnswerId 考试答案ID
     * @param status 状态
     * @return 更新结果
     */
    @Override
    public int updateExamAnswerStatus(Long examAnswerId, String status) {
        ExamAnswer examAnswer = examAnswerMapper.selectByExamAndQuestion(examAnswerId, null);
        if (examAnswer == null) {
            throw new RuntimeException("考试答案不存在");
        }

        examAnswer.setStatus(status);
        examAnswer.setUpdatedAt(LocalDateTime.now());

        return examAnswerMapper.update(examAnswer);
    }

    /**
     * 混合模式评分（支持选择不同的AI模型）
     * @param examAnswerId 考试答案ID
     * @param aiModel AI模型名称
     * @return 批改结果
     */
    @Override
    public AIGeneratedResult hybridGradeExamAnswer(Long examAnswerId, String aiModel) throws IOException {
        // 获取考试答案信息
        ExamAnswer examAnswer = examAnswerMapper.selectByExamAndQuestion(examAnswerId, null);
        if (examAnswer == null) {
            throw new RuntimeException("考试答案不存在");
        }

        // 获取题目信息
        ExamQuestion examQuestion = examQuestionMapper.getById(examAnswer.getQuestionId());
        if (examQuestion == null) {
            throw new RuntimeException("考试题目不存在");
        }

        // 获取指定的AI服务
        AIService aiService = aiGradingFactory.getAIService(aiModel);

        QuestionBank question = questionBankMapper.getById(examQuestion.getQuestionId());

        // 调用AI评分服务
        AIGeneratedResult result = aiService.grade(
                examQuestion.getQuestionId(),
                examAnswer.getAnswer(),
                question.getQuestionText(),
                question.getCorrectAnswer()
        );

        // 更新考试答案的分数和状态
        examAnswer.setScore(result.getAiScore().intValue());
        examAnswer.setStatus("已批改");
        examAnswer.setUpdatedAt(LocalDateTime.now());
        examAnswerMapper.update(examAnswer);

        return result;
    }

    /**
     * 异步AI批改（适用于主观题）
     */
    @Async
    public void asyncAIGradeExamAnswer(Long examAnswerId) throws IOException {
        try {
            aiGradeExamAnswer(examAnswerId);
        } catch (Exception e) {
            log.error("异步批改考试答案失败: {}", e.getMessage());
        }
    }
}