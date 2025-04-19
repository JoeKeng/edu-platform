package com.edusystem.service.impl;

import com.edusystem.dto.AssignmentSubmissionDTO;
import com.edusystem.dto.NewAssignmentDTO;
import com.edusystem.dto.StudentAnswerDTO;
import com.edusystem.mapper.AssignmentMapper;
import com.edusystem.mapper.AssignmentQuestionMapper;
import com.edusystem.mapper.QuestionBankMapper;
import com.edusystem.model.Assignment;
import com.edusystem.model.QuestionBank;
import com.edusystem.model.StudentAnswer;
import com.edusystem.service.AssignmentService;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Autowired
    private AssignmentQuestionMapper assignmentQuestionMapper;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Autowired
    private QuestionBankServiceImpl questionBankService;

    // 创建作业（支持选择已有题目和新建题目）
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int createAssignment(NewAssignmentDTO NewassignmentDTO) {

        // 1. 插入作业
        int result = assignmentMapper.insert(NewassignmentDTO);

        // 2. 关联已有题目
        if (result > 0 && NewassignmentDTO.getQuestionIds() != null) {
            int order = 1;
            for (Long questionId : NewassignmentDTO.getQuestionIds()) {
                assignmentQuestionMapper.insert(NewassignmentDTO.getId(), questionId, order++);
            }
        }

        // 3. 处理新建题目
        if (result > 0 && CollectionUtils.isNotEmpty(NewassignmentDTO.getNewQuestions())) {
            int order = NewassignmentDTO.getQuestionIds() != null ? NewassignmentDTO.getQuestionIds().size() + 1 : 1;


            for (QuestionBank question : NewassignmentDTO.getNewQuestions()) {
                if (question == null || StringUtils.isBlank(question.getQuestionText())) {
                    throw new IllegalArgumentException("题目内容不能为空");
                }
                question.setCourseId(NewassignmentDTO.getCourseId());
                question.setTeacherId(NewassignmentDTO.getTeacherId());
                question.setChapterId(NewassignmentDTO.getChapterId());

                // 插入题库
                int questionResult = questionBankService.addQuestion(question);
                if (questionResult > 0) {
                    // 关联新建的题目到作业
                    assignmentQuestionMapper.insert(NewassignmentDTO.getId(), question.getId(), order++);
                }
            }
        }


        return result;
    }

    // 获取作业详情
    @Override
    public Assignment getAssignmentById(Integer id) {
        Assignment assignment = assignmentMapper.getById(id);
        if (assignment != null) {
            assignment.setQuestionIds(assignmentQuestionMapper.getQuestionsByAssignmentId(id));
        }
        return assignment;
    }

    // 获取课程下的所有作业
    @Override
    public List<Assignment> getAssignmentsByCourse(Integer courseId) {
        return assignmentMapper.getByCourseId(courseId);
    }
    // 获取章节下的所有作业
    @Override
    public List<Assignment> getAssignmentsByChapter(Integer chapterId) {
        return assignmentMapper.getByChapterId(chapterId);
    }
    // 获取教师下的所有作业
    public List<Assignment> getAssignmentsByTeacher(Long teacherId) {
        List<Assignment> assignments = assignmentMapper.findByTeacherId(teacherId);
        if (assignments != null) {
            for (Assignment assignment : assignments) {
                int id = assignment.getId();
                assignment.setQuestionIds(assignmentQuestionMapper.getQuestionsByAssignmentId(id));
            }
        }
        return assignments;
    }

    // 根据作业ID获取作业所有题目详情
    @Override
    public Assignment getAssignmentDetail(Integer assignmentId) {
        Assignment assignment = assignmentMapper.getById(assignmentId);
        if (assignment != null) {
            assignment.setQuestionIds(assignmentQuestionMapper.getQuestionsByAssignmentId(assignmentId));
        }
        List<QuestionBank> questions = questionBankMapper.findByAssignmentId(assignmentId);

        // 获取当前用户的角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AtomicReference<String> role = new AtomicReference<>("ROLE_STUDENT"); // 默认角色

        if (authentication != null && authentication.getAuthorities() != null) {
            authentication.getAuthorities().forEach(authority -> {
                if (authority.getAuthority().equals("ROLE_STUDENT")) {
                    role.set("ROLE_STUDENT");
                } else if (authority.getAuthority().equals("ROLE_TEACHER")) {
                    role.set("ROLE_TEACHER");
                }
            });
        }


        System.out.println("当前用户的角色：" + role);
        // 根据角色处理题目
        if (role.get().equals("ROLE_STUDENT")) {
            //将questions中的题目答案和解析设置为null
            questions.forEach(question -> {
                question.setCorrectAnswer(null);
                question.setAnalysis(null);
            });
        }

        assignment.setQuestions(questions);
        return assignment;
    }


    // 更新作业
    @Override
    public int updateAssignment(Assignment assignment) {
        int result = assignmentMapper.update(assignment);
        if (result > 0) {
            assignmentQuestionMapper.deleteByAssignmentId(assignment.getId());
            int order = 1;
            for (Long questionId : assignment.getQuestionIds()) {
                assignmentQuestionMapper.insert(assignment.getId(), questionId, order++);
            }
        }
        return result;
    }

    // 删除作业
    @Override
    public int deleteAssignment(Integer id) {
        assignmentQuestionMapper.deleteByAssignmentId(id);
        return assignmentMapper.delete(id);
    }

//    // 提交作业
//    @Override
//    @Transactional
//    public boolean submitAssignment(AssignmentSubmissionDTO submissionDTO) {
//        for (StudentAnswerDTO answer : submissionDTO.getAnswers()) {
//            // 获取题目信息
//            QuestionBank question = questionBankMapper.getById(answer.getQuestionId());
//
//            // 自动评分
//            boolean isCorrect = autoGrade(question, answer.getStudentAnswer());
//            double score = isCorrect ? 10.0 : 0.0;  // 每题满分 10 分
//
//            // 记录作答
//            StudentAnswer studentAnswer = new StudentAnswer();
//            studentAnswer.setStudentId(submissionDTO.getStudentId());
//            studentAnswer.setQuestionId(answer.getQuestionId());
//            studentAnswer.setStudentAnswer(answer.getStudentAnswer());
//            studentAnswer.setScore(score);
//            studentAnswer.setIsCorrect(isCorrect);
//            studentAnswerMapper.insert(studentAnswer);
//        }
//        return true;
//    }
//
//    // 自动评分
//    private boolean autoGrade(QuestionBank question, String studentAnswer) {
//        if (question.getType().equals("single_choice") || question.getType().equals("multiple_choice")) {
//            return question.getCorrectAnswer().equals(studentAnswer);
//        } else {
//            // 其他题型调用 AI 评分
//            return false;
//        }
//    }



}

