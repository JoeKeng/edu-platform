package com.edusystem.service.impl;

import com.edusystem.dto.ExamCreateDTO;
import com.edusystem.mapper.ExamMapper;
import com.edusystem.mapper.ExamQuestionMapper;
import com.edusystem.mapper.QuestionBankMapper;
import com.edusystem.mapper.TeacherMapper;
import com.edusystem.mapper.ExamClassMapper;
import com.edusystem.model.ExamClass;
import com.edusystem.model.QuestionBank;
import java.util.stream.Collectors;
import com.edusystem.model.Exam;
import com.edusystem.model.ExamQuestion;
import com.edusystem.model.Result;
import com.edusystem.service.ExamService;
import com.edusystem.service.impl.QuestionBankServiceImpl;
import com.edusystem.util.CurrentHolder;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edusystem.mapper.ExamAnswerMapper;
import com.edusystem.model.ExamAnswer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private ExamClassMapper examClassMapper;

    @Autowired
    private ExamAnswerMapper examAnswerMapper;
    
    @Autowired
    private QuestionBankMapper questionBankMapper;
    
    @Autowired
    private QuestionBankServiceImpl questionBankService;

    // 创建考试（旧方法）
    @Override
    @Transactional
    public Result createExam(Exam exam, List<ExamQuestion> questions) {
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        exam.setCreateAt(now);
        exam.setUpdateAt(now);
        
        // 如果未设置教师ID，则使用当前登录用户的ID
        if (exam.getTeacherId() == null) {
            // 获取当前登录用户的教师ID
            Integer userId = CurrentHolder.getCurrentId();
            Long teacherId = Long.valueOf(teacherMapper.getTeacherIdByUserId(userId));
            exam.setTeacherId(teacherId);
        }

        // 插入考试基本信息
        int result = examMapper.insert(exam);
        if (result <= 0) {
            return Result.error("创建考试失败");
        }

        // 处理班级关联
        if (exam.getClassIds() != null && !exam.getClassIds().isEmpty()) {
            List<ExamClass> examClasses = exam.getClassIds().stream()
                .map(classId -> {
                    ExamClass examClass = new ExamClass();
                    examClass.setExamId(exam.getId());
                    examClass.setClassId(classId);
                    examClass.setCreatedAt(now);
                    examClass.setUpdatedAt(now);
                    return examClass;
                })
                .collect(Collectors.toList());
            
            result = examClassMapper.batchInsert(examClasses);
            if (result <= 0) {
                throw new RuntimeException("关联班级失败");
            }
        }

        // 批量插入考试题目
        if (questions != null && !questions.isEmpty()) {
            for (ExamQuestion question : questions) {
                question.setExamId(exam.getId());
            }
            result = examQuestionMapper.batchInsert(questions);
            if (result <= 0) {
                throw new RuntimeException("添加考试题目失败");
            }
        }

        return Result.success("创建考试成功");
    }
    
    // 创建考试（新方法：支持从题库选择题目和创建新题目）
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createExam(ExamCreateDTO examCreateDTO) {
        // 获取考试基本信息
        Exam exam = examCreateDTO.getExam();
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        exam.setCreateAt(now);
        exam.setCreateAt(now);
        
        // 如果未设置教师ID，则使用当前登录用户的ID
        if (exam.getTeacherId() == null) {
            // 获取当前登录用户的教师ID
            Integer userId = CurrentHolder.getCurrentId();
            Long teacherId = Long.valueOf(teacherMapper.getTeacherIdByUserId(userId));
            exam.setTeacherId(teacherId);
        }

        // 插入考试基本信息
        int result = examMapper.insert(exam);
        if (result <= 0) {
            return Result.error("创建考试失败");
        }

        // 处理班级关联
        if (exam.getClassIds() != null && !exam.getClassIds().isEmpty()) {
            List<ExamClass> examClasses = exam.getClassIds().stream()
                .map(classId -> {
                    ExamClass examClass = new ExamClass();
                    examClass.setExamId(exam.getId());
                    examClass.setClassId(classId);
                    examClass.setCreatedAt(now);
                    examClass.setUpdatedAt(now);
                    return examClass;
                })
                .collect(Collectors.toList());
            
            result = examClassMapper.batchInsert(examClasses);
            if (result <= 0) {
                throw new RuntimeException("关联班级失败");
            }
        }
        
        // 处理题目关联
        List<ExamQuestion> examQuestions = new ArrayList<>();
        int orderNum = 1;
        
        // 1. 关联已有题目
        if (examCreateDTO.getQuestionIds() != null && !examCreateDTO.getQuestionIds().isEmpty()) {
            for (Long questionId : examCreateDTO.getQuestionIds()) {
                // 获取题目信息，设置分值和类型等
                QuestionBank questionBank = questionBankMapper.getById(questionId);
                if (questionBank != null) {
                    ExamQuestion examQuestion = new ExamQuestion();
                    examQuestion.setExamId(exam.getId());
                    examQuestion.setQuestionId(questionId);
                    examQuestion.setScore(questionBank.getScore() != null ? questionBank.getScore().intValue() : 10); // 默认分值10
                    examQuestion.setOrderNum(orderNum++);
                    examQuestion.setQuestionType(questionBank.getType());
                    // 根据题目类型设置所属部分
                    if ("单选题".equals(questionBank.getType()) || "多选题".equals(questionBank.getType()) || "判断题".equals(questionBank.getType())) {
                        examQuestion.setSection("选择题部分");
                    } else {
                        examQuestion.setSection("主观题部分");
                    }
                    examQuestions.add(examQuestion);
                }
            }
        }
        
        // 2. 处理新建题目
        if (CollectionUtils.isNotEmpty(examCreateDTO.getNewQuestions())) {
            for (QuestionBank question : examCreateDTO.getNewQuestions()) {
                if (question == null || StringUtils.isBlank(question.getQuestionText())) {
                    throw new IllegalArgumentException("题目内容不能为空");
                }
                
                // 设置题目关联信息
                question.setCourseId(exam.getCourseId());
                question.setTeacherId(exam.getTeacherId());
                question.setChapterId(exam.getChapterId());
                
                // 插入题库
                int questionResult = questionBankService.addQuestion(question);
                if (questionResult > 0) {
                    // 创建考试题目关联
                    ExamQuestion examQuestion = new ExamQuestion();
                    examQuestion.setExamId(exam.getId());
                    examQuestion.setQuestionId(question.getId());
                    examQuestion.setScore(question.getScore() != null ? question.getScore().intValue() : 10); // 默认分值10
                    examQuestion.setOrderNum(orderNum++);
                    examQuestion.setQuestionType(question.getType());
                    // 根据题目类型设置所属部分
                    if ("单选题".equals(question.getType()) || "多选题".equals(question.getType()) || "判断题".equals(question.getType())) {
                        examQuestion.setSection("选择题部分");
                    } else {
                        examQuestion.setSection("主观题部分");
                    }
                    examQuestions.add(examQuestion);
                }
            }
        }
        
        // 批量插入考试题目
        if (!examQuestions.isEmpty()) {
            result = examQuestionMapper.batchInsert(examQuestions);
            if (result <= 0) {
                throw new RuntimeException("添加考试题目失败");
            }
        }
        
        return Result.success("创建考试成功");
    }

    // 更新考试信息
    @Override
    @Transactional
    public Result updateExam(Exam exam) {
        exam.setCreateAt(LocalDateTime.now());
        int result = examMapper.update(exam);
        if (result <= 0) {
            return Result.error("更新失败");
        }

        // 更新班级关联
        if (exam.getClassIds() != null) {
            // 先删除原有关联
            examClassMapper.deleteByExamId(exam.getId());
            
            // 添加新的关联
            if (!exam.getClassIds().isEmpty()) {
                List<ExamClass> examClasses = exam.getClassIds().stream()
                    .map(classId -> {
                        ExamClass examClass = new ExamClass();
                        examClass.setExamId(exam.getId());
                        examClass.setClassId(classId);
                        examClass.setCreatedAt(LocalDateTime.now());
                        examClass.setUpdatedAt(LocalDateTime.now());
                        return examClass;
                    })
                    .collect(Collectors.toList());
                
                result = examClassMapper.batchInsert(examClasses);
                if (result <= 0) {
                    throw new RuntimeException("更新班级关联失败");
                }
            }
        }

        return Result.success("更新成功");
    }

    // 删除考试
    @Override
    @Transactional
    public Result deleteExam(Long id) {
        // 删除考试相关的题目
        examQuestionMapper.deleteByExamId(id);
        // 删除班级关联
        examClassMapper.deleteByExamId(id);
        // 删除考试本身
        int result = examMapper.deleteById(id);
        return result > 0 ? Result.success("删除成功") : Result.error("删除失败");
    }

    // 根据ID获取考试信息
    @Override
    public Result getExamById(Long id) {
        // 获取考试基本信息
        Exam exam = examMapper.getById(id);
        if (exam == null) {
            return Result.error("考试不存在");
        }

        // 获取关联的班级信息
        List<Integer> classIds = examClassMapper.getClassIdsByExamId(id);
        exam.setClassIds(classIds);

        // 获取考试题目及其详细信息
        List<ExamQuestion> examQuestions = examQuestionMapper.getByExamId(id);
        if (examQuestions != null && !examQuestions.isEmpty()) {
            // 获取每个题目的详细信息
            for (ExamQuestion question : examQuestions) {
                QuestionBank questionBank = questionBankMapper.getById(question.getQuestionId());
                if (questionBank != null) {
                    // 设置题目详细信息
                    question.setQuestionType(questionBank.getType());
                    // question.setRequirement(questionBank.getQuestionText());
                }
            }
        }

        return Result.success(exam);
    }

    // 获取课程的所有考试
    @Override
    public Result getExamsByCourse(Integer courseId) {
        List<Exam> exams = examMapper.getByCourseId(courseId);
        return Result.success(exams);
    }

    // 获取章节的所有考试
    @Override
    public Result getExamsByChapter(Integer chapterId) {
        List<Exam> exams = examMapper.getByChapterId(chapterId);
        return Result.success(exams);
    }

    // 获取教师创建的所有考试
    @Override
    public Result getExamsByTeacher(Long teacherId) {
        List<Exam> exams = examMapper.getByTeacherId(teacherId);
        return Result.success(exams);
    }

    // 更新考试状态
    @Override
    public Result updateExamStatus(Long id, String status) {
        int result = examMapper.updateStatus(id, status);
        return result > 0 ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    // 添加考试题目
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addExamQuestions(Long examId, List<ExamQuestion> questions) {
        try {
            if (questions == null || questions.isEmpty()) {
                return Result.error("题目列表不能为空");
            }

            // 获取考试信息（用于设置新建题目的关联信息）
            Exam exam = examMapper.getById(examId);
            if (exam == null) {
                return Result.error("考试不存在");
            }

            // 获取当前考试最大的题目顺序号
            Integer maxOrderNum = examQuestionMapper.getMaxOrderNum(examId);
            int nextOrderNum = (maxOrderNum == null ? 0 : maxOrderNum) + 1;

            // 设置考试ID和顺序号
            for (ExamQuestion question : questions) {
                question.setExamId(examId);
                question.setOrderNum(nextOrderNum++);

                // 如果是新建题目（questionId为空），先添加到题库
                if (question.getQuestionId() == null && question.getQuestionBank() != null) {
                    QuestionBank newQuestion = question.getQuestionBank();
                    // 设置题目关联信息
                    newQuestion.setCourseId(exam.getCourseId());
                    newQuestion.setChapterId(exam.getChapterId());
                    newQuestion.setTeacherId(exam.getTeacherId());
                    
                    // 添加到题库
                    int result = questionBankService.addQuestion(newQuestion);
                    if (result <= 0) {
                        throw new RuntimeException("添加题目到题库失败");
                    }
                    // 设置新建题目的ID
                    question.setQuestionId(newQuestion.getId());
                    question.setQuestionType(newQuestion.getType());
                } else if (question.getQuestionId() != null) {
                    // 如果是添加已有题目，获取题目信息
                    QuestionBank existingQuestion = questionBankMapper.getById(question.getQuestionId());
                    if (existingQuestion == null) {
                        throw new RuntimeException("题库中不存在ID为" + question.getQuestionId() + "的题目");
                    }
                    question.setQuestionType(existingQuestion.getType());
                } else {
                    throw new RuntimeException("题目信息不完整");
                }

                // 根据题目类型设置所属部分
                if (question.getQuestionType() != null) {
                    if ("单选题".equals(question.getQuestionType()) 
                        || "多选题".equals(question.getQuestionType()) 
                        || "判断题".equals(question.getQuestionType())) {
                        question.setSection("选择题部分");
                    } else {
                        question.setSection("主观题部分");
                    }
                }
            }

            // 批量插入考试题目
            int result = examQuestionMapper.batchInsert(questions);
            if (result <= 0) {
                throw new RuntimeException("添加题目失败");
            }

            return Result.success("添加题目成功");
        } catch (Exception e) {
            log.error("添加考试题目异常, examId: {}", examId, e);
            throw new RuntimeException("添加题目失败: " + e.getMessage());
        }
    }

    // 更新考试题目
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateExamQuestions(List<ExamQuestion> questions) {
        try {
            if (questions == null || questions.isEmpty()) {
                return Result.error("题目列表不能为空");
            }

            // 按照examId分组处理，确保同一考试的题目顺序正确
            Map<Long, List<ExamQuestion>> examQuestionMap = questions.stream()
                    .collect(Collectors.groupingBy(ExamQuestion::getExamId));

            for (Map.Entry<Long, List<ExamQuestion>> entry : examQuestionMap.entrySet()) {
                Long examId = entry.getKey();
                List<ExamQuestion> examQuestions = entry.getValue();

                // 获取当前考试的所有题目
                List<ExamQuestion> existingQuestions = examQuestionMapper.getByExamId(examId);
                Map<Long, Integer> originalOrders = existingQuestions.stream()
                        .collect(Collectors.toMap(ExamQuestion::getId, ExamQuestion::getOrderNum));

                // 处理每个题目的更新
                for (ExamQuestion question : examQuestions) {
                    // 更新题库中的题目
                    if (question.getQuestionBank() != null) {
                        QuestionBank updatedQuestion = question.getQuestionBank();
                        // 确保题目ID一致
                        updatedQuestion.setId(question.getQuestionId());
                        int questionResult = questionBankService.updateQuestion(updatedQuestion);
                        if (questionResult <= 0) {
                            throw new RuntimeException("更新题库题目失败：" + question.getQuestionId());
                        }
                    }

                    // 如果顺序发生变化
                    if (originalOrders.containsKey(question.getId()) && 
                        !originalOrders.get(question.getId()).equals(question.getOrderNum())) {
                        
                        // 更新受影响的题目顺序
                        if (question.getOrderNum() > originalOrders.get(question.getId())) {
                            // 如果新位置在后面，中间的题目顺序号减1
                            examQuestionMapper.decreaseOrderNum(
                                examId,
                                originalOrders.get(question.getId()) + 1,
                                question.getOrderNum()
                            );
                        } else {
                            // 如果新位置在前面，中间的题目顺序号加1
                            examQuestionMapper.increaseOrderNum(
                                examId,
                                question.getOrderNum(),
                                originalOrders.get(question.getId()) - 1
                            );
                        }
                    }

                    // 更新考试题目信息
                    int result = examQuestionMapper.update(question);
                    if (result <= 0) {
                        throw new RuntimeException("更新题目失败：" + question.getId());
                    }
                }
            }

            return Result.success("更新成功");
        } catch (Exception e) {
            log.error("更新考试题目异常", e);
            throw new RuntimeException("更新题目失败：" + e.getMessage());
        }
    }

    // 删除考试题目
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteExamQuestion(Long questionId) {
        try {
            // 获取要删除的题目信息
            ExamQuestion question = examQuestionMapper.selectById(questionId);
            if (question == null) {
                return Result.error("题目不存在");
            }

            // 删除题目
            int result = examQuestionMapper.deleteById(questionId);
            if (result <= 0) {
                return Result.error("删除失败");
            }

            // 更新后续题目的顺序号
            int updateResult = examQuestionMapper.updateOrderNumsAfterDelete(
                question.getExamId(), 
                question.getOrderNum()
            );
            
            if (updateResult <= 0) {
                log.warn("没有需要更新顺序的题目");
            }

            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除考试题目异常, questionId: {}", questionId, e);
            throw new RuntimeException("删除题目失败", e);
        }
    }

    // 获取考试题目
    @Override
    public Result getExamQuestions(Long examId) {
        List<ExamQuestion> questions = examQuestionMapper.getByExamId(examId);
        for (ExamQuestion question : questions) {
            // 获取题目详情
            QuestionBank questionBank = questionBankMapper.getById(question.getQuestionId());
            question.setQuestionBank(questionBank);
            if (questionBank != null) {
                question.setQuestionType(questionBank.getType());
            }
        }
        return Result.success(questions);
    }
    // 更新题目顺序
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateQuestionOrder(Long id, Integer orderNum) {
        try {
            // 参数校验
            if (id == null || id <= 0) {
                log.warn("无效题目ID: {}", id);
                return Result.error("题目ID不合法");
            }
            if (orderNum == null || orderNum < 0) {
                log.warn("非法顺序号: {}", orderNum);
                return Result.error("顺序号必须为非负数");
            }

            // 查询当前位置的题目
            ExamQuestion current = examQuestionMapper.selectById(id);
            if (current == null) {
                log.error("未找到对应题目, ID: {}", id);
                return Result.error("题目不存在");
            }
            Long examId = current.getExamId();

            // 查找目标位置现有题目
            ExamQuestion existing = examQuestionMapper.selectByOrderNum(examId,orderNum);

            if (existing != null) {
                // 交换顺序：先更新现有题目到临时位置
                examQuestionMapper.updateOrderNum(existing.getId(), -1);
                // 更新当前题目到目标位置
                examQuestionMapper.updateOrderNum(id, orderNum);
                // 将原题目更新回当前题目原位置
                examQuestionMapper.updateOrderNum(existing.getId(), current.getOrderNum());
            } else {
                // 直接更新位置
                examQuestionMapper.updateOrderNum(id, orderNum);
            }

            return Result.success("更新顺序成功");
        } catch (Exception e) {
            log.error("更新题目顺序异常, ID: {}, Order: {}", id, orderNum, e);
            return Result.error("系统错误，更新失败");
        }
    }


    // 批量更新题目分值
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchUpdateQuestionScores(List<ExamQuestion> updates) {
        try {
            if (updates == null || updates.isEmpty()) {
                return Result.error("更新列表不能为空");
            }

            // 按照examId分组，确保同一考试的题目一起处理
            Map<Long, List<ExamQuestion>> examQuestionMap = updates.stream()
                    .collect(Collectors.groupingBy(ExamQuestion::getExamId));

            for (Map.Entry<Long, List<ExamQuestion>> entry : examQuestionMap.entrySet()) {
                Long examId = entry.getKey();
                List<ExamQuestion> questions = entry.getValue();

                // 验证考试是否存在
                Exam exam = examMapper.getById(examId);
                if (exam == null) {
                    throw new RuntimeException("考试不存在：" + examId);
                }

                // 验证每个题目的分值
                for (ExamQuestion question : questions) {
                    if (question.getScore() == null || question.getScore() < 0) {
                        throw new RuntimeException("题目分值不能为空或负数：" + question.getId());
                    }
                }

                // 验证总分是否超过考试设置的总分
                if (exam.getTotalScore() != null) {
                    int totalScore = questions.stream()
                            .mapToInt(ExamQuestion::getScore)
                            .sum();
                    if (totalScore > exam.getTotalScore()) {
                        throw new RuntimeException("题目总分(" + totalScore + 
                            ")超过考试设置的总分(" + exam.getTotalScore() + ")");
                    }
                }
            }

            // 批量更新分值
            int result = examQuestionMapper.batchUpdateScore(updates);
            if (result <= 0) {
                throw new RuntimeException("更新分值失败");
            }
            return Result.success("更新分值成功");
        } catch (Exception e) {
            log.error("批量更新题目分值异常", e);
            throw new RuntimeException("更新分值失败：" + e.getMessage());
        }
    }

    
    // 开始考试会话
    public Result startExamSession(Long examId, Long studentId) {
        // 获取考试信息
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
        
        // 创建考试记录
        try {
            com.edusystem.service.ExamRecordService examRecordService = 
                com.edusystem.util.SpringContextUtil.getBean(com.edusystem.service.ExamRecordService.class);
            if (examRecordService != null) {
                examRecordService.startExam(examId, studentId);
            }
        } catch (Exception e) {
            log.warn("创建考试记录失败：{}", e.getMessage());
        }

        // 检查是否已提交
        if (examAnswerMapper.isSubmitted(examId, studentId)) {
            return Result.error("考试已提交，不能重复参加");
        }

        return Result.success("开始考试成功");
    }

    // 保存答案
    // 修改 saveAnswer 方法
    public Result saveAnswer(Long examId, Long studentId, Long questionId, String answer) {
        try {
            // 检查考试状态
            Exam exam = examMapper.getById(examId);
            if (exam == null) {
                return Result.error("考试不存在");
            }
    
            // 检查考试时间
            LocalDateTime now = LocalDateTime.now();
            if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
                // 如果超时，自动提交
                autoSubmitExam(examId, studentId);
                return Result.error("考试时间已结束，已自动提交");
            }
    
            // 保存或更新答案
            ExamAnswer examAnswer = new ExamAnswer();
            examAnswer.setExamId(examId);
            examAnswer.setStudentId(studentId);
            examAnswer.setQuestionId(questionId);
            examAnswer.setAnswer(answer);
            examAnswer.setStatus("未提交");  // 修改状态为 未提交
            examAnswer.setIsCorrect(null);      // 设置为 null，等待批改
            examAnswer.setScore(null);          // 设置为 null，等待评分
            examAnswer.setAiFeedback(null);     // 设置为 null
            examAnswer.setTeacherFeedback(null);// 设置为 null
            examAnswer.setSubmitTime(null);     // 设置为 null，等待提交
            examAnswer.setCreatedAt(LocalDateTime.now());
            examAnswer.setUpdatedAt(LocalDateTime.now());
    
            examAnswerMapper.insertOrUpdate(examAnswer);
            return Result.success("保存成功");
        } catch (Exception e) {
            log.error("保存答案失败", e);
            return Result.error("保存失败：" + e.getMessage());
        }
    }

    // 获取答题数量
    public int getAnsweredCount(Long examId, Long studentId) {
        return examAnswerMapper.getAnsweredCount(examId, studentId);
    }

    // 获取最后保存的答案
    public ExamAnswer getLastSaved(Long examId, Long studentId) {
        return examAnswerMapper.getLastSaved(examId, studentId);
    }

    // 检查是否已提交
    public boolean isSubmitted(Long examId, Long studentId) {
        return examAnswerMapper.isSubmitted(examId, studentId);
    }

    // 自动提交考试
    public void autoSubmitExam(Long examId, Long studentId) {
        try {
            // 更新所有答题记录的状态为已提交
            examAnswerMapper.batchUpdateStatus(examId, studentId, "已提交");
//            examAnswerMapper.updateSubmitStatus(examId, studentId);
        } catch (Exception e) {
            throw new RuntimeException("自动提交失败：" + e.getMessage());
        }
    }
}