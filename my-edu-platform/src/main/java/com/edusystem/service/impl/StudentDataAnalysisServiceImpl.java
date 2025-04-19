package com.edusystem.service.impl;

import com.edusystem.factory.AIGradingFactory;
import com.edusystem.mapper.*;
import com.edusystem.model.*;
import com.edusystem.model.Class;
import com.edusystem.service.AIService;
import com.edusystem.service.StudentDataAnalysisService;
import com.edusystem.service.WrongQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生数据分析服务实现类
 */
@Service
@Slf4j
public class StudentDataAnalysisServiceImpl implements StudentDataAnalysisService {

    @Autowired
    private StudentLearningBehaviorMapper behaviorMapper;
    
    @Autowired
    private StudentAnswerMapper studentAnswerMapper;
    
    @Autowired
    private WrongQuestionService wrongQuestionService;
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private ClassMapper classMapper;
    
    @Autowired
    private StudentLearningAnalysisMapper analysisMapper;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Autowired
    private AIGradingFactory aiGradingFactory;


    @Override
    public Map<String, Object> analyzeLearningHabit(Long studentId, LocalDateTime startTime, LocalDateTime endTime, boolean saveResult) {
        log.info("分析学生学习习惯: studentId={}, startTime={}, endTime={}",
                studentId, startTime, endTime);

        // 获取学生学习行为数据
        List<StudentLearningBehavior> behaviors = behaviorMapper.selectByStudentIdAndType(
                studentId, null, startTime, endTime);

        // 分析学习时间分布
        Map<String, Integer> timeDistribution = new LinkedHashMap<>();//LinkedHashMap 会按照插入顺序保存键值对，因此可以确保 timeDistribution 的键值对按照时间顺序排列。
        for (int i = 0; i < 24; i++) {
            timeDistribution.put(String.format("%02d:00", i), 0);
        }

        // 统计每个小时的学习时长
        behaviors.stream()
                .filter(b -> "STUDY".equals(b.getBehaviorType()))
                .forEach(b -> {
                    int hour = b.getStartTime().getHour();
                    String timeKey = String.format("%02d:00", hour);
                    timeDistribution.put(timeKey, timeDistribution.get(timeKey) + b.getDuration());
                });

        // 分析学习频率
        long studyDays = behaviors.stream()
                .filter(b -> "STUDY".equals(b.getBehaviorType()))
                .map(b -> b.getStartTime().toLocalDate())
                .distinct()
                .count();

        // 计算总学习时长
        int totalStudyDuration = behaviors.stream()
                .filter(b -> "STUDY".equals(b.getBehaviorType()))
                .mapToInt(StudentLearningBehavior::getDuration)
                .sum();

        // 计算平均每天学习时长
        double avgDailyStudyHours = studyDays > 0 ? (double) totalStudyDuration / studyDays / 3600 : 0;

        // 构建基础分析结果
        Map<String, Object> basicResult = new HashMap<>();
        basicResult.put("timeDistribution", timeDistribution);
        basicResult.put("studyDays", studyDays);
        basicResult.put("totalStudyDuration", totalStudyDuration);
        basicResult.put("avgDailyStudyHours", avgDailyStudyHours);
        
        // 添加学习行为数据
        Map<String, Object> behaviorData = new HashMap<>();
        behaviorData.put("basicStats", basicResult);
        
        // 添加学习行为模式分析
        Map<String, Integer> behaviorCounts = new HashMap<>();
        behaviors.forEach(b -> {
            behaviorCounts.merge(b.getBehaviorType(), 1, Integer::sum);
        });
        behaviorData.put("behaviorCounts", behaviorCounts);
        
        // 最终结果
        Map<String, Object> result = basicResult;
        
        try {
            // 获取AI服务
            AIService aiService = aiGradingFactory.getAIService("DeepSeekAIService");
            
            // 调用AI服务进行深度分析
            Map<String, Object> aiAnalysis = aiService.analyzeHabit(behaviorData);
            
            // 合并AI分析结果
            if (aiAnalysis != null && !aiAnalysis.isEmpty()) {
                // 添加AI分析结果
                result.put("aiAnalysis", aiAnalysis);
            }
        } catch (IOException e) {
            log.error("AI分析学习习惯失败", e);
            // AI分析失败时，仍然使用基础分析结果
        }

        // 保存分析结果到数据库中
        StudentLearningAnalysis analysis = new StudentLearningAnalysis();
        analysis.setStudentId(studentId);
        analysis.setAnalysisType("HABIT");
        analysis.setLearningHabitData(result);
        analysis.setAnalysisTime(LocalDateTime.now());
        analysis.setCreatedAt(LocalDateTime.now());
        analysis.setUpdatedAt(LocalDateTime.now());
        analysis.setIsDeleted(0);

        // 根据参数决定是否保存分析结果
        if (saveResult) {
            analysisMapper.insert(analysis);
        }

        return result;
    }

    /*
     * 分析学生学习进度
     */
    @Override
    public Map<String, Object> analyzeLearningProgress(Long studentId, Integer courseId, boolean saveResult) {
        log.info("分析学生学习进度: studentId={}, courseId={}", studentId, courseId);

        Map<String, Object> progressData = new HashMap<>();

        // 1. 获取课程完成情况
        List<StudentLearningBehavior> courseBehaviors = behaviorMapper.selectByStudentIdAndType(
                studentId, "STUDY", null, null);

        // 统计课程访问次数和总时长
        Map<String, Integer> courseVisits = new HashMap<>();
        Map<String, Integer> courseDurations = new HashMap<>();
        // 处理每个课程的学习行为，统计访问次数和时长
        courseBehaviors.stream()
                .filter(b -> "COURSE".equals(b.getResourceType()))
                .forEach(b -> {
                    String courseKey = b.getResourceId().toString();
                    courseVisits.merge(courseKey, 1, Integer::sum);
                    courseDurations.merge(courseKey, b.getDuration(), Integer::sum);
                });
        // 将统计结果存入进度数据
        progressData.put("courseVisits", courseVisits);
        progressData.put("courseDurations", courseDurations);

        // 2. 获取作业完成情况
        int totalAssignments = studentAnswerMapper.countAssignmentsByStudentId(studentId);
        int completedAssignments = studentAnswerMapper.countCompletedAssignmentsByStudentId(studentId);
        double assignmentCompletionRate = totalAssignments > 0 ?
                (double) completedAssignments / totalAssignments * 100 : 0;

        Map<String, Object> assignmentStats = new HashMap<>();
        assignmentStats.put("total", totalAssignments);
        assignmentStats.put("completed", completedAssignments);
        assignmentStats.put("completionRate", assignmentCompletionRate);

        progressData.put("assignmentStats", assignmentStats);

        // 3. 获取考试成绩情况
        List<Map<String, Object>> examScores = studentAnswerMapper.getExamScoresByStudentId(studentId);
        progressData.put("examScores", examScores);

        // 4. 计算知识点掌握进度
        Map<String, Object> knowledgeProgress = new HashMap<>();
        if (courseId != null) {
            // 如果指定了课程ID,则只分析该课程的知识点掌握情况
            knowledgeProgress = analyzeKnowledgeProgress(studentId, courseId);
        }
        progressData.put("knowledgeProgress", knowledgeProgress);

        // 保存分析结果
        StudentLearningAnalysis analysis = new StudentLearningAnalysis();
        analysis.setStudentId(studentId);
        analysis.setAnalysisType("PROGRESS");
        analysis.setLearningProgressData(progressData);
        analysis.setAnalysisTime(LocalDateTime.now());
        analysis.setCreatedAt(LocalDateTime.now());
        analysis.setUpdatedAt(LocalDateTime.now());
        analysis.setIsDeleted(0);

        // 根据参数决定是否保存分析结果
        if (saveResult) {
            analysisMapper.insert(analysis);
        }
        return progressData;
    }

    /**
     * 分析知识点掌握进度
     */
    private Map<String, Object> analyzeKnowledgeProgress(Long studentId, Integer courseId) {
        Map<String, Object> progressMap = new HashMap<>();

        // 获取课程相关的知识点列表
        List<Map<String, Object>> knowledgePoints = questionBankMapper.getKnowledgePointsByCourseId(courseId);

        // 统计每个知识点的掌握情况
        for (Map<String, Object> point : knowledgePoints) {
            Long pointId = (Long) point.get("id");
            String pointName = (String) point.get("name");

            // 获取该知识点相关的题目正确率
            double accuracy = studentAnswerMapper.getKnowledgePointAccuracy(studentId, pointId);

            Map<String, Object> pointProgress = new HashMap<>();
            pointProgress.put("name", pointName);
            pointProgress.put("accuracy", accuracy);
            pointProgress.put("status", getProgressStatus(accuracy));

            progressMap.put(pointId.toString(), pointProgress);
        }

        return progressMap;
    }

    /**
     * 根据正确率判断掌握状态
     */
    private String getProgressStatus(double accuracy) {
        if (accuracy >= 90) {
            return "MASTERED";
        } else if (accuracy >= 70) {
            return "PROFICIENT";
        } else if (accuracy >= 50) {
            return "LEARNING";
        } else {
            return "NEEDS_WORK";
        }
    }

    @Override
    public Map<String, Object> analyzeStrengthWeakness(Long studentId, boolean saveResult) {
        log.info("分析学生优势劣势: studentId={}", studentId);

        // 获取学生的答题记录
        List<StudentAnswer> answers = studentAnswerMapper.selectByStudentId(studentId);

        // 获取学生的错题记录
        int wrongQuestionCount = wrongQuestionService.getWrongQuestionCount(studentId);

        // 按题型分类统计正确率:单选题、多选题、判断题、填空题、简答题
        Map<String, Map<String, Object>> questionTypeStats = new HashMap<>();

        // 按知识点分类统计正确率
        Map<String, Map<String, Object>> knowledgePointStats = new HashMap<>();

        // 处理每个答题记录
        for (StudentAnswer answer : answers) {
            // 获取题目信息
            QuestionBank question = questionBankMapper.getById(answer.getQuestionId());
            if (question == null) continue;

            // 获取题型
            String questionType = question.getType();

            // 获取知识点
            List<KnowledgePoint> knowledgePoints = question.getKnowledgePoints();

            // 更新题型统计
            updateTypeStats(questionTypeStats, questionType, answer.getIsCorrect());

            // 更新知识点统计
            if (knowledgePoints != null && !knowledgePoints.isEmpty()) {
                for (KnowledgePoint kp : knowledgePoints) {
                    updateTypeStats(knowledgePointStats, kp.getName(), answer.getIsCorrect());
                }
            }
        }

        // 找出优势题型（正确率最高的前3个）
        List<Map.Entry<String, Map<String, Object>>> strengthTypes = questionTypeStats.entrySet().stream()
                .sorted((e1, e2) -> {
                    double rate1 = (double) e1.getValue().get("correctRate");
                    double rate2 = (double) e2.getValue().get("correctRate");
                    return Double.compare(rate2, rate1);
                })
                .limit(3)
                .collect(Collectors.toList());

        // 找出劣势题型（正确率最低的前3个）
        List<Map.Entry<String, Map<String, Object>>> weaknessTypes = questionTypeStats.entrySet().stream()
                .sorted((e1, e2) -> {
                    double rate1 = (double) e1.getValue().get("correctRate");
                    double rate2 = (double) e2.getValue().get("correctRate");
                    return Double.compare(rate1, rate2);
                })
                .limit(3)
                .collect(Collectors.toList());

        // 找出优势知识点（正确率最高的前5个）
        List<Map.Entry<String, Map<String, Object>>> strengthPoints = knowledgePointStats.entrySet().stream()
                .sorted((e1, e2) -> {
                    double rate1 = (double) e1.getValue().get("correctRate");
                    double rate2 = (double) e2.getValue().get("correctRate");
                    return Double.compare(rate2, rate1);
                })
                .limit(5)
                .collect(Collectors.toList());

        // 找出劣势知识点（正确率最低的前5个）
        List<Map.Entry<String, Map<String, Object>>> weaknessPoints = knowledgePointStats.entrySet().stream()
                .sorted((e1, e2) -> {
                    double rate1 = (double) e1.getValue().get("correctRate");
                    double rate2 = (double) e2.getValue().get("correctRate");
                    return Double.compare(rate1, rate2);
                })
                .limit(5)
                .collect(Collectors.toList());

        // 构建基础分析结果
        Map<String, Object> basicResult = new HashMap<>();
        basicResult.put("strengthQuestionTypes", strengthTypes);
        basicResult.put("weaknessQuestionTypes", weaknessTypes);
        basicResult.put("strengthKnowledgePoints", strengthPoints);
        basicResult.put("weaknessKnowledgePoints", weaknessPoints);
        basicResult.put("wrongQuestionCount", wrongQuestionCount);
        
        // 构建性能数据
        Map<String, Object> performanceData = new HashMap<>();
        performanceData.put("basicStats", basicResult);
        performanceData.put("questionTypeStats", questionTypeStats);
        performanceData.put("knowledgePointStats", knowledgePointStats);
        
        // 最终结果
        Map<String, Object> result = basicResult;
        
        try {
            // 获取AI服务
            AIService aiService = aiGradingFactory.getAIService("DeepSeekAIService");
            
            // 调用AI服务进行深度分析
            Map<String, Object> aiAnalysis = aiService.analyzeStrengthWeakness(performanceData);
            
            // 合并AI分析结果
            if (aiAnalysis != null && !aiAnalysis.isEmpty()) {
                // 添加AI分析结果
                result.put("aiAnalysis", aiAnalysis);
            }
        } catch (IOException e) {
            log.error("AI分析优势劣势失败", e);
            // AI分析失败时，仍然使用基础分析结果
        }

        // 保存分析结果到数据库
        // 根据参数决定是否保存分析结果
        if (saveResult) {
        StudentLearningAnalysis analysis = new StudentLearningAnalysis();
        analysis.setStudentId(studentId);
        analysis.setAnalysisType("STRENGTH_WEAKNESS");
        analysis.setStrengthWeaknessData(result);
        analysis.setAnalysisTime(LocalDateTime.now());
        analysis.setCreatedAt(LocalDateTime.now());
        analysis.setUpdatedAt(LocalDateTime.now());
        analysis.setIsDeleted(0);

        analysisMapper.insert(analysis);
        }

        return result;
        }

    /**
     * 更新类型统计信息
     */
    private void updateTypeStats(Map<String, Map<String, Object>> typeStats, String type, Boolean isCorrect) {
        if (type == null || type.isEmpty()) {
            type = "未分类";
        }

        // 获取或创建该类型的统计信息
        Map<String, Object> stats = typeStats.computeIfAbsent(type, k -> {
            Map<String, Object> newStats = new HashMap<>();
            newStats.put("total", 0);
            newStats.put("correct", 0);
            newStats.put("correctRate", 0.0);
            return newStats;
        });

        // 更新统计信息
        int total = (int) stats.get("total") + 1;
        int correct = (int) stats.get("correct") + (Boolean.TRUE.equals(isCorrect) ? 1 : 0);
        double correctRate = (double) correct / total;

        stats.put("total", total);
        stats.put("correct", correct);
        stats.put("correctRate", correctRate);
    }

    /**
     * 使用AI生成学习建议
     */
    @Override
    public List<String> generateRecommendations(Long studentId, boolean saveResult) {
        log.info("生成学习建议: studentId={}", studentId);

        try {
            // 获取AI服务
            AIService aiService = aiGradingFactory.getAIService("DeepSeekAIService");
            
            // 1. 分析学生的优势劣势
            Map<String, Object> strengthWeakness = analyzeStrengthWeakness(studentId, false);
            
            // 2. 获取学习习惯数据
            Map<String, Object> learningHabit = analyzeLearningHabit(
                    studentId, LocalDateTime.now().minusDays(30), LocalDateTime.now(), false);
            
            // 3. 合并分析数据
            Map<String, Object> analysisData = new HashMap<>();
            analysisData.put("strengthWeakness", strengthWeakness);
            analysisData.put("learningHabit", learningHabit);
            
            // 4. 调用AI服务生成建议
            List<String> recommendations = aiService.generateRecommendations(analysisData);
            
            // 5. 保存分析结果到数据库
            StudentLearningAnalysis analysis = new StudentLearningAnalysis();
            analysis.setStudentId(studentId);
            analysis.setAnalysisType("RECOMMENDATION");
            analysis.setRecommendations(recommendations);
            analysis.setAnalysisTime(LocalDateTime.now());
            analysis.setCreatedAt(LocalDateTime.now());
            analysis.setUpdatedAt(LocalDateTime.now());
            analysis.setIsDeleted(0);
            
            // 根据参数决定是否保存分析结果
            if (saveResult) {
                analysisMapper.insert(analysis);
            }
            
            return recommendations;
            
        } catch (IOException e) {
            log.error("AI生成学习建议失败", e);
            
            // 如果AI服务失败，使用传统方法生成建议
            return generateFallbackRecommendations(studentId, saveResult);
        }
    }
    
    /**
     * 传统方法生成学习建议（作为AI服务失败的备选方案）
     */
    private List<String> generateFallbackRecommendations(Long studentId, boolean saveResult) {
        // 分析学生的优势劣势
        Map<String, Object> strengthWeakness = analyzeStrengthWeakness(studentId, false);

        // 生成建议列表
        List<String> recommendations = new ArrayList<>();

        // 基于劣势题型的建议
        @SuppressWarnings("unchecked")
        List<Map.Entry<String, Map<String, Object>>> weaknessTypes =
                (List<Map.Entry<String, Map<String, Object>>>) strengthWeakness.get("weaknessQuestionTypes");

        if (weaknessTypes != null && !weaknessTypes.isEmpty()) {
            for (Map.Entry<String, Map<String, Object>> entry : weaknessTypes) {
                String type = entry.getKey();
                Map<String, Object> stats = entry.getValue();
                double correctRate = (double) stats.get("correctRate");

                if (correctRate < 0.6) {
                    recommendations.add(String.format("建议加强「%s」类型题目的练习，当前正确率仅为%.1f%%",
                            type, correctRate * 100));
                }
            }
        }

        // 基于劣势知识点的建议
        @SuppressWarnings("unchecked")
        List<Map.Entry<String, Map<String, Object>>> weaknessPoints =
                (List<Map.Entry<String, Map<String, Object>>>) strengthWeakness.get("weaknessKnowledgePoints");

        if (weaknessPoints != null && !weaknessPoints.isEmpty()) {
            for (Map.Entry<String, Map<String, Object>> entry : weaknessPoints) {
                String point = entry.getKey();
                Map<String, Object> stats = entry.getValue();
                double correctRate = (double) stats.get("correctRate");

                if (correctRate < 0.7) {
                    recommendations.add(String.format("建议复习「%s」相关知识点，当前掌握程度较低，正确率为%.1f%%",
                            point, correctRate * 100));
                }
            }
        }

        // 基于错题数量的建议
        int wrongQuestionCount = (int) strengthWeakness.get("wrongQuestionCount");
        if (wrongQuestionCount > 20) {
            recommendations.add(String.format("您当前有%d道错题，建议定期复习错题本，巩固薄弱环节", wrongQuestionCount));
        }

        // 添加一些通用建议
        recommendations.add("建议制定合理的学习计划，保持规律的学习习惯");
        recommendations.add("建议多做题、多练习，通过实践加深理解");
        
        return recommendations;
    }

    /**
     * 综合分析学生学习情况，只保存综合分析，不保存每个部分的分析
     */
    @Override
    public Result comprehensiveAnalysis(Long studentId) {
        log.info("综合分析学生学习情况: studentId={}", studentId);

        try {
            // 获取学生信息
            Student student = studentMapper.getStudentByStudentId(studentId);
            if (student == null) {
                return Result.error("学生不存在");
            }

            // 分析学习习惯
            Map<String, Object> learningHabit = analyzeLearningHabit(
                    studentId, LocalDateTime.now().minusDays(30), LocalDateTime.now(), false);

            // 分析学习进度
            Map<String, Object> learningProgress = analyzeLearningProgress(studentId, null, false);

            // 分析优势劣势
            Map<String, Object> strengthWeakness = analyzeStrengthWeakness(studentId, false);

            // 合并分析数据用于AI生成建议
            Map<String, Object> analysisData = new HashMap<>();
            analysisData.put("learningHabit", learningHabit);
            analysisData.put("learningProgress", learningProgress);
            analysisData.put("strengthWeakness", strengthWeakness);
            
            // 生成学习建议
            List<String> recommendations;
            try {
                // 获取AI服务
                AIService aiService = aiGradingFactory.getAIService("DeepSeekAIService");
                // 调用AI服务生成建议
                recommendations = aiService.generateRecommendations(analysisData);
            } catch (IOException e) {
                log.error("AI生成学习建议失败，使用传统方法生成", e);
                // AI服务失败时使用传统方法生成建议
                recommendations = generateFallbackRecommendations(studentId, false);
            }

            // 构建综合分析结果
            Map<String, Object> result = new HashMap<>();
            result.put("studentInfo", student);
            result.put("learningHabit", learningHabit);
            result.put("learningProgress", learningProgress);
            result.put("strengthWeakness", strengthWeakness);
            result.put("recommendations", recommendations);

            // 保存分析结果
            StudentLearningAnalysis analysis = new StudentLearningAnalysis();
            analysis.setStudentId(studentId);
            analysis.setAnalysisType("COMPREHENSIVE");
            analysis.setLearningHabitData(learningHabit);
            analysis.setLearningProgressData(learningProgress);
            analysis.setStrengthWeaknessData(strengthWeakness);
            analysis.setRecommendations(recommendations);
            analysis.setAnalysisTime(LocalDateTime.now());
            analysis.setCreatedAt(LocalDateTime.now());
            analysis.setUpdatedAt(LocalDateTime.now());
            analysis.setIsDeleted(0);

            analysisMapper.insert(analysis);

            return Result.success(result);
        } catch (Exception e) {
            log.error("综合分析学生学习情况失败: studentId={}", studentId, e);
            return Result.error("分析失败: " + e.getMessage());
        }
    }

    //TODO: 班级分析
    @Override
    public Result classAnalysis(Integer classId) {
        log.info("分析班级学习情况: classId={}", classId);

        try {
            // 获取班级信息
            Class classInfo = classMapper.findById(classId);
            if (classInfo == null) {
                return Result.error("班级不存在");
            }

            // 获取班级学生列表
            List<Student> students = studentMapper.getStudentsByClassId(classId);
            if (students.isEmpty()) {
                return Result.error("班级暂无学生");
            }

            // 班级整体统计
            Map<String, Object> classStats = new HashMap<>();
            classStats.put("totalStudents", students.size());

            // 学习时间分布统计
            Map<String, Integer> timeDistribution = new HashMap<>();
            for (int i = 0; i < 24; i++) {
                timeDistribution.put(String.format("%02d:00", i), 0);
            }

            // 题型正确率统计
            Map<String, Map<String, Object>> questionTypeStats = new HashMap<>();

            // 知识点掌握情况统计
            Map<String, Map<String, Object>> knowledgePointStats = new HashMap<>();

            // 分析每个学生的情况并汇总
            for (Student student : students) {
                // 分析学生的优势劣势
                Map<String, Object> strengthWeakness = analyzeStrengthWeakness(student.getStudentId(), false);

                // 更新题型统计
                updateClassTypeStats(questionTypeStats, strengthWeakness.get("strengthQuestionTypes"));
                updateClassTypeStats(questionTypeStats, strengthWeakness.get("weaknessQuestionTypes"));

                // 更新知识点统计
                updateClassTypeStats(knowledgePointStats, strengthWeakness.get("strengthKnowledgePoints"));
                updateClassTypeStats(knowledgePointStats, strengthWeakness.get("weaknessKnowledgePoints"));
            }

            // 找出班级整体的优势和劣势
            List<Map.Entry<String, Map<String, Object>>> classStrengthTypes = questionTypeStats.entrySet().stream()
                    .sorted((e1, e2) -> {
                        double rate1 = (double) e1.getValue().get("avgCorrectRate");
                        double rate2 = (double) e2.getValue().get("avgCorrectRate");
                        return Double.compare(rate2, rate1);
                    })
                    .limit(3)
                    .collect(Collectors.toList());

            List<Map.Entry<String, Map<String, Object>>> classWeaknessTypes = questionTypeStats.entrySet().stream()
                    .sorted((e1, e2) -> {
                        double rate1 = (double) e1.getValue().get("avgCorrectRate");
                        double rate2 = (double) e2.getValue().get("avgCorrectRate");
                        return Double.compare(rate1, rate2);
                    })
                    .limit(3)
                    .collect(Collectors.toList());

            // 构建班级分析结果
            Map<String, Object> result = new HashMap<>();
            result.put("classInfo", classInfo);
            result.put("classStats", classStats);
            result.put("classStrengthTypes", classStrengthTypes);
            result.put("classWeaknessTypes", classWeaknessTypes);

            return Result.success(result);
        } catch (Exception e) {
            log.error("分析班级学习情况失败: classId={}", classId, e);
            return Result.error("分析失败: " + e.getMessage());
        }
    }

    /**
     * 更新班级类型统计信息
     */
    @SuppressWarnings("unchecked")
    private void updateClassTypeStats(Map<String, Map<String, Object>> classTypeStats, Object typeEntries) {
        if (typeEntries == null) return;

        List<Map.Entry<String, Map<String, Object>>> entries =
                (List<Map.Entry<String, Map<String, Object>>>) typeEntries;

        for (Map.Entry<String, Map<String, Object>> entry : entries) {
            String type = entry.getKey();
            Map<String, Object> stats = entry.getValue();
            double correctRate = (double) stats.get("correctRate");

            // 获取或创建该类型的班级统计信息
            Map<String, Object> classStats = classTypeStats.computeIfAbsent(type, k -> {
                Map<String, Object> newStats = new HashMap<>();
                newStats.put("studentCount", 0);
                newStats.put("totalCorrectRate", 0.0);
                newStats.put("avgCorrectRate", 0.0);
                return newStats;
            });

            // 更新统计信息
            int studentCount = (int) classStats.get("studentCount") + 1;
            double totalCorrectRate = (double) classStats.get("totalCorrectRate") + correctRate;
            double avgCorrectRate = totalCorrectRate / studentCount;

            classStats.put("studentCount", studentCount);
            classStats.put("totalCorrectRate", totalCorrectRate);
            classStats.put("avgCorrectRate", avgCorrectRate);
        }
    }

    /**
     * 根据班级ID获取班级学习分析
     * @param studentId
     * @param analysisType
     * @return
     */
    @Override
    public StudentLearningAnalysis getStudentAnalysis(Long studentId, String analysisType) {
        return analysisMapper.selectByStudentIdAndType(studentId, analysisType);
    }

    /**
     * 保存学生学习分析
     * @param analysis
     */
    @Override
    public void saveStudentAnalysis(StudentLearningAnalysis analysis) {
        analysisMapper.insert(analysis);
    }
}