package com.edusystem.service.impl;

import com.edusystem.config.AIConfig;
import com.edusystem.dto.AIGeneratedResult;
import com.edusystem.mapper.QuestionBankMapper;
import com.edusystem.service.AIService;
import com.edusystem.service.StudentKnowledgeMasteryService;
import com.edusystem.util.AIResponseParser;
import com.edusystem.util.OpenAIClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("DeepSeekAIService")
public class DeepSeekAIGradingServiceImpl implements AIService {

    private final AIConfig aiConfig;

    @Autowired
    public DeepSeekAIGradingServiceImpl(AIConfig aiConfig) {
        this.aiConfig = aiConfig;
        log.info("api:{},baseurl:{},molde:{}", aiConfig.getApiKey(), aiConfig.getBaseUrl(), aiConfig.getModel());
    }

    @Autowired
    private OpenAIClient openAIClient;
    @Autowired
    private QuestionBankMapper questionBankMapper;
    @Autowired
    private StudentKnowledgeMasteryService studentKnowledgeMasteryService;

    @Override
    public AIGeneratedResult grade(Long questionId, String studentAnswer, String questionText, String correctAnswer)
            throws IOException {
        // 获取题目关联的知识点信息
        List<Map<String, Object>> knowledgePoints = questionBankMapper.getKnowledgePointsByQuestionId(questionId);

        // 构建知识点信息字符串
        StringBuilder knowledgePointsInfo = new StringBuilder();
        if (knowledgePoints != null && !knowledgePoints.isEmpty()) {
            knowledgePointsInfo.append("【相关知识点】\n");
            for (Map<String, Object> kp : knowledgePoints) {
                knowledgePointsInfo.append("- ").append(kp.get("name")).append("\n");
            }
        }

        // 1. 构造 AI 评分提示词（Prompt）
        String prompt = "你是一个智能评分 AI，请根据题目内容、正确答案和学生答案进行评分。\n"
                + "评分规则：\n"
                + "- 0-10 分，允许小数。\n"
                + "- 9-10 分：答案完全正确，逻辑清晰，表述准确。\n"
                + "- 7-8 分：答案基本正确，但存在少量错误或表述不清。\n"
                + "- 5-6 分：答案部分正确，但存在明显错误或逻辑不清晰。\n"
                + "- 3-4 分：答案偏离主题，但有部分合理内容。\n"
                + "- 0-2 分：答案完全错误或与主题无关。\n"
                + "【题目】\n" + questionText + "\n"
                + "【正确答案】\n" + correctAnswer + "\n"
                + "【考察知识点】" + knowledgePointsInfo.toString() // 添加知识点信息
                + "【学生答案】\n" + studentAnswer + "\n"
                + "请按照以下格式返回结果：\n"
                + "{\n"
                + "  \"score\": 0-10,  \n"
                + "  \"isCorrect\": true/false (如果答案完全正确为 true，否则为 false),  \n"
                + "  \"feedback\": \"你的答案 XXX\", \n"
                + "  \"explanation\": \"本题解析...\", \n"
                + "  \"knowledgePointsFeedback\": \"知识点掌握情况...\"  \n" // 新增知识点反馈
                + "}\n"
                + "例如：\n"
                + "{\n"
                + "  \"score\": 8.5,  \n"
                + "  \"isCorrect\": false,  \n"
                + "  \"feedback\": \"你的答案基本正确，但部分表述不够清晰。\", \n"
                + "  \"explanation\": \"本题考查 XXX，正确答案是 XXX。\", \n"
                + "  \"knowledgePointsFeedback\": \"你对'XXX'知识点的理解基本正确，但'YYY'知识点需要加强。\"  \n"
                + "}";

        // 2. 发送请求到 AI
        log.info("Apikey:{} BaseUrl:{} Model:{}", aiConfig.getApiKey(), aiConfig.getBaseUrl(), aiConfig.getModel());
        String response = openAIClient.callOpenAI(
                aiConfig.getApiKey(),
                aiConfig.getBaseUrl(),
                aiConfig.getModel(),
                prompt);

        // 3. 解析 AI 返回的 JSON
        AIGeneratedResult result = AIResponseParser.parse(response);

        // 4. 更新学生知识点掌握情况
        if (result != null && knowledgePoints != null && !knowledgePoints.isEmpty()) {
            updateStudentKnowledgeMastery(questionId, result);
        }

        return result;
    }

    /**
     * 更新学生知识点掌握情况
     */
    //TODO:实现更新学生知识点掌握情况的功能
    private void updateStudentKnowledgeMastery(Long questionId, AIGeneratedResult result) {
        try {
            // 获取题目相关的知识点
            List<Map<String, Object>> knowledgePoints = questionBankMapper.getKnowledgePointsByQuestionId(questionId);

            // 更新每个知识点的掌握情况
            for (Map<String, Object> kp : knowledgePoints) {
                Long knowledgePointId = (Long) kp.get("id");
                Double weight = (Double) kp.get("weight");

                // 根据评分结果更新掌握程度
                double masteryChange = calculateMasteryChange(result.getAiScore(), weight);

                // 调用服务更新学生知识点掌握情况
                studentKnowledgeMasteryService.updateMastery(
                        result.getStudentId(),
                        knowledgePointId,
                        masteryChange,
                        result.getIsCorrect());
            }
        } catch (Exception e) {
            log.error("更新学生知识点掌握情况失败", e);
        }
    }

    /**
     * 计算知识点掌握度变化
     */
    private double calculateMasteryChange(double score, double weight) {
        // 根据得分和权重计算掌握度变化
        // 满分10分情况下的计算公式
        return (score / 10.0) * weight * 100;
    }

    @Override
    public Map<String, Object> analyzeHabit(Map<String, Object> behaviorData) throws IOException {
        log.info("AI分析学习习惯: {}", behaviorData);

        // 构建提示词
        String prompt = "你是一个教育数据分析专家，请分析以下学生的学习习惯数据并给出专业分析。\n"
                + "【学习行为数据】\n" + behaviorData.toString() + "\n"
                + "请按照以下格式返回分析结果：\n"
                + "{\n"
                + "  \"timeDistributionAnalysis\": \"时间分布分析...\",\n"
                + "  \"frequencyAnalysis\": \"学习频率分析...\",\n"
                + "  \"durationAnalysis\": \"学习时长分析...\",\n"
                + "  \"habitSuggestion\": \"习惯改进建议...\"\n"
                + "}\n";

        // 调用AI服务
        String aiResponse = openAIClient.callOpenAI(
                aiConfig.getApiKey(),
                aiConfig.getBaseUrl(),
                aiConfig.getModel(),
                prompt);

        // 解析AI响应
        try {
            return AIResponseParser.parseJsonMap(aiResponse);
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            Map<String, Object> fallbackResult = new HashMap<>();
            fallbackResult.put("error", "AI分析处理失败");
            fallbackResult.put("rawResponse", aiResponse);
            return fallbackResult;
        }
    }

    @Override
    public Map<String, Object> analyzeStrengthWeakness(Map<String, Object> performanceData) throws IOException {
        log.info("AI分析优势劣势: {}", performanceData);

        // 构建提示词
        String prompt = "你是一个教育数据分析专家，请分析以下学生的学习表现数据并给出优势劣势分析。\n"
                + "【学习表现数据】\n" + performanceData.toString() + "\n"
                + "请严格按照以下JSON格式返回分析结果：\n"
                + "{\n"
                + "  \"strengths\": [\n"
                + "    \"优势描述1\",\n"
                + "    \"优势描述2\"\n"
                + "  ],\n"
                + "  \"weaknesses\": [\n"
                + "    \"劣势描述1\",\n"
                + "    \"劣势描述2\"\n"
                + "  ],\n"
                + "  \"analysisExplanation\": \"详细的分析说明\",\n"
                + "  \"recommendations\": [\n"
                + "    \"改进建议1\",\n"
                + "    \"改进建议2\"\n"
                + "  ]\n"
                + "}\n"
                + "注意：\n"
                + "1. 所有数组元素必须是字符串\n"
                + "2. 分析要基于提供的数据\n"
                + "3. 保持JSON格式的严格性";

        // 调用AI服务
        String aiResponse = openAIClient.callOpenAI(
                aiConfig.getApiKey(),
                aiConfig.getBaseUrl(),
                aiConfig.getModel(),
                prompt);

        // 解析AI响应
        try {
            return AIResponseParser.parseJsonMap(aiResponse);
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            Map<String, Object> fallbackResult = new HashMap<>();
            fallbackResult.put("error", "AI分析处理失败");
            fallbackResult.put("rawResponse", aiResponse);
            return fallbackResult;
        }
    }

    @Override
    public List<String> generateRecommendations(Map<String, Object> analysisData) throws IOException {
        log.info("AI生成学习建议: {}", analysisData);

        // 构建提示词
        String prompt = "你是一个教育顾问，请根据以下学生的学习分析数据，生成个性化的学习建议。\n"
                + "【学习分析数据】\n" + analysisData.toString() + "\n"
                + "请生成5-8条具体、可操作的学习建议，每条建议应该针对学生的具体情况，并且易于执行。\n"
                + "请严格按照以下JSON数组格式返回建议列表，不要添加任何其他内容或解释：\n"
                + "[\n"
                + "  \"建议1具体内容\",\n"
                + "  \"建议2具体内容\",\n"
                + "  \"建议3具体内容\"\n"
                + "]\n"
                + "注意：\n"
                + "1. 返回格式必须是严格的JSON数组\n"
                + "2. 数组中每个元素都是字符串\n"
                + "3. 不要添加任何额外的文字说明或注释";

        // 调用AI服务
        String aiResponse = openAIClient.callOpenAI(
                aiConfig.getApiKey(),
                aiConfig.getBaseUrl(),
                aiConfig.getModel(),
                prompt);

        // 解析AI响应
        try {
            return AIResponseParser.parseStringList(aiResponse);
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            List<String> fallbackResult = new ArrayList<>();
            fallbackResult.add("AI生成建议失败，请稍后再试");
            return fallbackResult;
        }
    }
    //TODO:实现AI分析的学习进度分析
    @Override
    public Map<String, Object> analyzeLearningProgress(Map<String, Object> progressData) throws IOException {
        return Map.of();
    }


    

}
