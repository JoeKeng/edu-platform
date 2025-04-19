package com.edusystem.service.impl;

import com.edusystem.mapper.*;
import com.edusystem.model.*;
import com.edusystem.service.WrongQuestionService;
import com.edusystem.util.CurrentHolder;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

/**
 * 错题本服务实现类
 */
@Service
@Slf4j
public class WrongQuestionServiceImpl implements WrongQuestionService {

    @Autowired
    private WrongQuestionMapper wrongQuestionMapper;
    @Autowired
    private QuestionBankMapper questionBankMapper;
    @Autowired
    private StudentAnswerMapper studentAnswerMapper;
    @Autowired
    private StudentMapper studentMapper;
    
    @Override
    @Transactional
    public boolean addWrongQuestion(WrongQuestion wrongQuestion) {
        // 检查是否已存在该题目的错题记录
        WrongQuestion existingRecord = wrongQuestionMapper.selectByStudentIdAndQuestionId(
                wrongQuestion.getUserId(), wrongQuestion.getQuestionId().intValue());
        
        if (existingRecord != null) {
            // 如果已存在但被标记为删除，则恢复并更新
            if (existingRecord.getIsDeleted()) {
                existingRecord.setIsDeleted(false);
                existingRecord.setAnalysis(wrongQuestion.getAnalysis());
                existingRecord.setUpdatedAt(LocalDateTime.now());
                existingRecord.setCreatedAt(LocalDateTime.now());
                return wrongQuestionMapper.insert(existingRecord) > 0;
            }
            // 已存在且未删除，无需操作
            return true;
        }
        
        // 设置默认值
        if (wrongQuestion.getCreatedAt() == null) {
            wrongQuestion.setCreatedAt(LocalDateTime.now());
        }
        if (wrongQuestion.getUpdatedAt() == null) {
            wrongQuestion.setUpdatedAt(LocalDateTime.now());
        }
        if (wrongQuestion.getIsDeleted() == null) {
            wrongQuestion.setIsDeleted(false);
        }
        
        return wrongQuestionMapper.insert(wrongQuestion) > 0;
    }

    @Override
    public void quickAddWrongQuestion(Long userId, Long questionId) {
        // 1. 获取该题目的正确答案 & 解析
        QuestionBank question = questionBankMapper.getById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 2. 获取该用户的最新作答记录
        //根据用户ID获取学生ID
        Long studentId = studentMapper.getStudentIdByUserId((userId).intValue());
        StudentAnswer studentAnswer = studentAnswerMapper.getLatestAnswer(studentId, questionId);
        log.info("studentAnswer:{}", studentAnswer);
        if (studentAnswer == null) {
            throw new RuntimeException("未找到该用户的作答记录");
        }

        // 3. 检查 studentAnswer 和 score 是否为 null
        if (studentAnswer == null || (studentAnswer.getAiScore() == null&& studentAnswer.getTeacherScore() == null)) {
            throw new RuntimeException("学生答题记录或分数未正确初始化");
        }
        double score=0.0;
        if(studentAnswer.getAiScore()!=null&&studentAnswer.getTeacherScore()==null){
            score = studentAnswer.getAiScore();}
        else if(studentAnswer.getAiScore()==null&&studentAnswer.getTeacherScore()!=null){
            score = studentAnswer.getTeacherScore();
        }else if(studentAnswer.getAiScore()!=null&&studentAnswer.getTeacherScore()!=null){
            score = (studentAnswer.getAiScore()+studentAnswer.getTeacherScore())/2;
        }
        if (score == 100.0|| score == 10.0) {
            throw new RuntimeException("该题已答对，无需加入错题本");
        }

        // 4. 组装错题信息
        WrongQuestion wrongQuestion = new WrongQuestion();
        wrongQuestion.setUserId(userId);
        wrongQuestion.setQuestionId(questionId);
        wrongQuestion.setAssignmentId(Long.valueOf(studentAnswer.getAssignmentId()));
        wrongQuestion.setWrongAnswer(studentAnswer.getAnswer());
        wrongQuestion.setCorrectAnswer(question.getCorrectAnswer());
        wrongQuestion.setAnalysis(question.getAnalysis());
        wrongQuestion.setWrongType("other"); // 默认类型，后续可改
        wrongQuestion.setAttemptCount(1);
        wrongQuestion.setIsDeleted(false);
        wrongQuestion.setFirstAttemptScore(score);
        wrongQuestion.setLastAttemptScore(score);


        // 5. 存入数据库（若已存在，则更新）
        wrongQuestionMapper.insert(wrongQuestion);
    }


    @Override
    public WrongQuestion getWrongQuestionById(Long id) {
        return wrongQuestionMapper.selectById(id);
    }
    
    @Override
    public Result getWrongQuestionsByUserId(Long userId, Integer page, Integer pageSize) {
        // 设置分页参数
        PageHelper.startPage(page, pageSize);
        // 执行查询
        List<WrongQuestion> wrongQuestions = wrongQuestionMapper.selectByUserId(userId);
        // 处理分页结果
        Page<WrongQuestion> pageInfo = (Page<WrongQuestion>) wrongQuestions;
        // 返回封装后的分页结果
        return Result.success(new PageResult<WrongQuestion>(pageInfo.getTotal(), pageInfo.getResult()));
    }
    @Override
    public boolean hasWrongQuestion(Long userId, Long questionId) {
        return wrongQuestionMapper.selectByStudentIdAndQuestionId(userId, questionId.intValue()) != null;
    }
    
    @Override
    public WrongQuestion getWrongQuestionByUserIdAndQuestionId(Long userId, Long questionId) {
        return wrongQuestionMapper.selectByStudentIdAndQuestionId(userId, questionId.intValue());
    }
    
    @Override
    @Transactional
    public boolean removeWrongQuestion(Long id, Long userId) {
        return wrongQuestionMapper.deleteById(id.intValue()) > 0;
    }
    
    @Override
    @Transactional
    public boolean removeWrongQuestionByQuestionId(Long userId, Long questionId) {
        // 先获取错题记录
        WrongQuestion wrongQuestion = wrongQuestionMapper.selectByStudentIdAndQuestionId(userId, questionId.intValue());
        if (wrongQuestion != null) {
            return wrongQuestionMapper.deleteById(wrongQuestion.getId().intValue()) > 0;
        }
        return false;
    }
    
    @Override
    public int getWrongQuestionCount(Long userId) {
        return wrongQuestionMapper.countByStudentId(userId);
    }
    //TODO:实现查询所有错题内容的功能
    @Override
    public Result getOutWrongQuestionsByUserId(Long userId, Integer page, Integer pageSize) {
        // 设置分页参数
        PageHelper.startPage(page, pageSize);
        // 执行查询
        List<WrongQuestion> wrongQuestions = wrongQuestionMapper.selectByUserId(userId);
        // 处理分页结果
        Page<WrongQuestion> pageInfo = (Page<WrongQuestion>) wrongQuestions;
        // 返回封装后的分页结果
        return Result.success(new PageResult<>(pageInfo.getTotal(), pageInfo.getResult()));
    }
}