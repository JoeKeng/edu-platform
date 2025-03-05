package com.edusystem.service.impl;

import com.edusystem.mapper.AssignmentMapper;
import com.edusystem.mapper.AssignmentQuestionMapper;
import com.edusystem.mapper.QuestionBankMapper;
import com.edusystem.model.Assignment;
import com.edusystem.model.QuestionBank;
import com.edusystem.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Autowired
    private AssignmentQuestionMapper assignmentQuestionMapper;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    // 创建作业（支持选择已有题目和新建题目）
    @Override
    public int createAssignment(Assignment assignment) {
        // 1. 插入作业
        int result = assignmentMapper.insert(assignment);

        // 2. 关联已有题目
        if (result > 0 && assignment.getQuestionIds() != null) {
            int order = 1;
            for (Long questionId : assignment.getQuestionIds()) {
                assignmentQuestionMapper.insert(assignment.getId(), questionId, order++);
            }
        }

        // 3. 处理新建题目
        if (result > 0 && assignment.getNewQuestions() != null) {
            int order = assignment.getQuestionIds() != null ? assignment.getQuestionIds().size() + 1 : 1;
            for (QuestionBank question : assignment.getNewQuestions()) {
                question.setCourseId(assignment.getCourseId());
                question.setTeacherId(assignment.getTeacherId());

                // 插入题库
                int questionResult = questionBankMapper.insert(question);
                if (questionResult > 0) {
                    // 关联新建的题目到作业
                    assignmentQuestionMapper.insert(assignment.getId(), question.getId(), order++);
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


}

