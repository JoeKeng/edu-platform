package com.edusystem.service;

import com.edusystem.dto.AssignmentSubmissionDTO;
import com.edusystem.dto.NewAssignmentDTO;
import com.edusystem.model.Assignment;
import java.util.List;

public interface AssignmentService {
    int createAssignment(NewAssignmentDTO assignmentDTO);
    Assignment getAssignmentById(Integer id);
    List<Assignment> getAssignmentsByCourse(Integer courseId);
    int updateAssignment(Assignment assignment);
    int deleteAssignment(Integer id);

    List<Assignment> getAssignmentsByChapter(Integer chapterId);

    List<Assignment> getAssignmentsByTeacher(Long teacherId);

    Assignment getAssignmentDetail(Integer assignmentId);

//    boolean submitAssignment(AssignmentSubmissionDTO submissionDTO);
}
