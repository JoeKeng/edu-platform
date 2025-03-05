package com.edusystem.service;

import com.edusystem.model.Assignment;
import java.util.List;

public interface AssignmentService {
    int createAssignment(Assignment assignment);
    Assignment getAssignmentById(Integer id);
    List<Assignment> getAssignmentsByCourse(Integer courseId);
    int updateAssignment(Assignment assignment);
    int deleteAssignment(Integer id);

    List<Assignment> getAssignmentsByChapter(Integer chapterId);
}
