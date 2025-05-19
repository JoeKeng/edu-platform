package com.edusystem.service.impl;

import com.edusystem.mapper.QuestionBankMapper;
import com.edusystem.model.QuestionBank;
import com.edusystem.service.QuestionBankService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionBankServiceImpl implements QuestionBankService {

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Override
    public int addQuestion(QuestionBank question) {
        // 如果 options 字段为空，设置为默认值（空 JSON 数组）
        if (question.getOptions() == null|| question.getOptions().equals("")|| question.getOptions().isEmpty()) {
            question.setOptions("[]");
        } else {
            // 如果 options 是逗号分隔的字符串，转换为 JSON 数组
            if (question.getOptions().contains(",")) {
                String[] optionsArray = question.getOptions().split(",");
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonOptions = objectMapper.writeValueAsString(optionsArray);
                    question.setOptions(jsonOptions);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("无法将选项转换为 JSON 格式", e);
                }
            } else {
                // 假设 options 已经是有效的 JSON 格式
                // 可以选择验证或直接设置
                // 在此假设前端会保证其有效性
                // 或者通过 Jackson 解析验证
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    // 尝试将 options 反序列化为 Object，然后重新序列化为 JSON 字符串
                    // 如果是无效的 JSON，会抛出异常
                    Object parsedOptions = objectMapper.readValue(question.getOptions(), Object.class);
                    String jsonOptions = objectMapper.writeValueAsString(parsedOptions);
                    question.setOptions(jsonOptions);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("无效的 JSON 格式：options", e);
                }
            }
        }
        // 插入问题
        int result = questionBankMapper.insert(question);
        //TODO将知识点和题目关联
        if(result > 0){

        }

        return result;
    }

    @Override
    public QuestionBank getQuestionById(Long id) {
        return questionBankMapper.getById(id);
    }

    @Override
    public List<QuestionBank> getQuestionsByCourseAndChapter(Integer courseId, Integer chapterId) {
        return questionBankMapper.getByCourseAndChapter(courseId, chapterId);
    }

    @Override
    public int updateQuestion(QuestionBank question) {
        // 如果 options 字段为空，设置为默认值（空 JSON 数组）
        if (question.getOptions() == null|| question.getOptions().equals("")) {
            question.setOptions("[]");
        } else {
            // 如果 options 是逗号分隔的字符串，转换为 JSON 数组
            if (question.getOptions().contains(",")) {
                String[] optionsArray = question.getOptions().split(",");
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonOptions = objectMapper.writeValueAsString(optionsArray);
                    question.setOptions(jsonOptions);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("无法将选项转换为 JSON 格式", e);
                }
            }
            //如果options不是逗号分隔的字符串是以空格分隔的字符串
            else if(question.getOptions().contains(" ")){
                String[] optionsArray = question.getOptions().split(" ");
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonOptions = objectMapper.writeValueAsString(optionsArray);
                    question.setOptions(jsonOptions);
                    return questionBankMapper.update(question);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("无法将选项转换为 JSON 格式", e);
                }
            } else {
                // 假设 options 已经是有效的 JSON 格式
                // 可以选择验证或直接设置
                // 在此假设前端会保证其有效性
                // 或者通过 Jackson 解析验证
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    // 尝试将 options 反序列化为 Object，然后重新序列化为 JSON 字符串
                    // 如果是无效的 JSON，会抛出异常
                    Object parsedOptions = objectMapper.readValue(question.getOptions(), Object.class);
                    String jsonOptions = objectMapper.writeValueAsString(parsedOptions);
                    question.setOptions(jsonOptions);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("无效的 JSON 格式：options", e);
                }
            }
        }
        return questionBankMapper.update(question);
    }

    @Override
    public int deleteQuestion(Long id) {
        return questionBankMapper.delete(id);
    }
}
