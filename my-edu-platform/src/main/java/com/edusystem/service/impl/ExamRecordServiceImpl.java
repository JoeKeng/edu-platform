package com.edusystem.service.impl;

import com.edusystem.mapper.ExamMapper;
import com.edusystem.mapper.ExamRecordMapper;
import com.edusystem.mapper.StudentMapper;
import com.edusystem.model.Exam;
import com.edusystem.model.ExamRecord;
import com.edusystem.model.Result;
import com.edusystem.service.ExamRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试记录服务实现类
 */
@Slf4j
@Service
public class ExamRecordServiceImpl implements ExamRecordService {

    @Autowired
    private ExamRecordMapper examRecordMapper;
    
    @Autowired
    private ExamMapper examMapper;
    
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 开始考试，创建考试记录
     */
    @Override
    @Transactional
    public Result startExam(Long examId, Long studentId) {
        try {
            // 检查考试是否存在
            Exam exam = examMapper.getById(examId);
            if (exam == null) {
                return Result.error("考试不存在");
            }
            
            // 检查考试时间
            LocalDateTime now = LocalDateTime.now();
            if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
                return Result.error("考试还未开始");
            }
            if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
                return Result.error("考试已结束");
            }
            
            // 检查是否已有考试记录
            ExamRecord existRecord = examRecordMapper.getByExamAndStudent(examId, studentId);
            if (existRecord != null) {
                // 如果已提交，不允许重新开始
                if ("SUBMITTED".equals(existRecord.getStatus()) || "GRADED".equals(existRecord.getStatus())) {
                    return Result.error("考试已提交，不能重新开始");
                }
                
                // 如果已开始但未提交，更新状态为进行中
                if ("NOT_STARTED".equals(existRecord.getStatus())) {
                    existRecord.setStatus("IN_PROGRESS");
                    existRecord.setStartTime(now);
                    existRecord.setUpdatedAt(now);
                    examRecordMapper.update(existRecord);
                    return Result.success("继续考试");
                }
                
                // 如果正在进行中，直接返回成功
                return Result.success("继续考试");
            }
            
            // 创建新的考试记录
            ExamRecord record = new ExamRecord();
            record.setExamId(examId);
            record.setStudentId(studentId);
            record.setStartTime(now);
            record.setStatus("IN_PROGRESS");
            record.setCreatedAt(now);
            record.setUpdatedAt(now);
            
            examRecordMapper.insert(record);
            return Result.success("开始考试成功");
        } catch (Exception e) {
            log.error("开始考试失败", e);
            return Result.error("开始考试失败：" + e.getMessage());
        }
    }

    /**
     * 提交考试，更新考试记录状态
     */
    @Override
    @Transactional
    public Result submitExam(Long examId, Long studentId) {
        try {
            // 检查考试记录是否存在
            ExamRecord record = examRecordMapper.getByExamAndStudent(examId, studentId);
            if (record == null) {
                // 如果不存在记录，创建一个新记录
                record = new ExamRecord();
                record.setExamId(examId);
                record.setStudentId(studentId);
                record.setStatus("SUBMITTED");
                record.setSubmitTime(LocalDateTime.now());
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());
                examRecordMapper.insert(record);
            } else {
                // 更新状态为已提交
                examRecordMapper.updateStatus(examId, studentId, "SUBMITTED", LocalDateTime.now());
            }
            return Result.success("提交考试成功");
        } catch (Exception e) {
            log.error("提交考试失败", e);
            return Result.error("提交考试失败：" + e.getMessage());
        }
    }

    /**
     * 更新考试总分
     */
    @Override
    @Transactional
    public Result updateTotalScore(Long examId, Long studentId, BigDecimal totalScore) {
        try {
            // 检查考试记录是否存在
            ExamRecord record = examRecordMapper.getByExamAndStudent(examId, studentId);
            if (record == null) {
                return Result.error("考试记录不存在");
            }
            
            // 更新总分
            examRecordMapper.updateTotalScore(examId, studentId, totalScore);
            return Result.success("更新总分成功");
        } catch (Exception e) {
            log.error("更新总分失败", e);
            return Result.error("更新总分失败：" + e.getMessage());
        }
    }

    /**
     * 获取考试的所有考试记录
     */
    @Override
    public Result getExamRecords(Long examId) {
        try {
            List<ExamRecord> records = examRecordMapper.getByExamId(examId);
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取考试记录失败", e);
            return Result.error("获取考试记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取学生的所有考试记录
     */
    @Override
    public Result getStudentExamRecords(Long studentId) {
        try {
            List<ExamRecord> records = examRecordMapper.getByStudentId(studentId);
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取学生考试记录失败", e);
            return Result.error("获取学生考试记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取考试记录详情
     */
    @Override
    public Result getExamRecordDetail(Long examId, Long studentId) {
        try {
            ExamRecord record = examRecordMapper.getByExamAndStudent(examId, studentId);
            if (record == null) {
                return Result.error("考试记录不存在");
            }
            return Result.success(record);
        } catch (Exception e) {
            log.error("获取考试记录详情失败", e);
            return Result.error("获取考试记录详情失败：" + e.getMessage());
        }
    }
}