package com.edusystem.mapper;

import com.edusystem.dto.StudentAnswerDTO;
import com.edusystem.dto.TeacherGradeDTO;
import com.edusystem.model.StudentAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentAnswerMapper {
    // 插入学生作答记录
    int insert(StudentAnswer studentAnswer);

    // 根据作答ID查询
    StudentAnswer selectById(@Param("id") Long id);

    // 查询某个作业的所有作答
    List<StudentAnswer> selectByAssignmentId(@Param("assignmentId") Long assignmentId);

    // 查询某个学生的所有作答
    List<StudentAnswer> selectByStudentId(@Param("studentId") Long studentId);

    // 更新评分和反馈
    int updateScoreAndFeedback(@Param("id") Long id,
                               @Param("isCorrect") Boolean isCorrect,
                               @Param("aiScore") Double aiScore,
                               @Param("teacherSorce") Double teacherSorce,
                               @Param("aiFeedback") String aiFeedback,
                               @Param("teacherFeedback") String teacherFeedback,
                                @Param("explanation") String explanation);

    void batchUpdateTeacherGrade(List<TeacherGradeDTO> grades);

    StudentAnswer getLatestAnswer(Long studentId, Long questionId);


    List<StudentAnswer> selectByCourseAndStudent(Integer courseId, Long studentId);

    int countAssignmentsByStudentId(Long studentId);

    int countCompletedAssignmentsByStudentId(Long studentId);
    // 根据学生ID和知识点ID查询知识点的准确率
    double getKnowledgePointAccuracy(Long studentId, Long pointId);

    //
    List<Map<String, Object>> getExamScoresByStudentId(Long studentId);

}
