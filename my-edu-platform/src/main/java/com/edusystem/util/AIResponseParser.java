package com.edusystem.util;

import com.edusystem.dto.AIGeneratedResult;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AIResponseParser {

    public static AIGeneratedResult parse(String aiResponse) {
        AIGeneratedResult result = new AIGeneratedResult();

        try {
            // 打印 AI 返回的完整数据
            log.info("AI Response: {}", aiResponse);

            // 检查 AI 响应是否为空
            if (aiResponse == null || aiResponse.isEmpty()) {
                throw new RuntimeException("AI 响应为空！");
            }

            // 解析 AI 响应
            JSONObject jsonResponse = new JSONObject(aiResponse);

            // 检查 API 是否返回错误
            if (jsonResponse.has("error")) {
                System.err.println("AI API Error: " + jsonResponse.getString("error"));
                throw new RuntimeException("AI API 返回错误：" + jsonResponse.getString("error"));
            }

            // 确保 choices 存在
            if (!jsonResponse.has("choices")) {
                throw new JSONException("Response JSON does not contain 'choices' key!");
            }

            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.isEmpty()) {
                throw new JSONException("Response JSON 'choices' array is empty!");
            }

            // 确保 message.content 存在
            JSONObject choice = choices.getJSONObject(0);
            if (!choice.has("message")) {
                throw new JSONException("Response JSON does not contain 'message' key!");
            }

            JSONObject message = choice.getJSONObject("message");
            if (!message.has("content")) {
                throw new JSONException("Response JSON does not contain 'content' key!");
            }

            String content = message.getString("content");

            // 使用正则匹配评分信息
//            Pattern pattern = Pattern.compile("\\{\\s*\"score\":\\s*(\\d+(\\.\\d+)?),\\s*\"isCorrect\":\\s*(true|false),\\s*\"feedback\":\\s*\"(.*?)\",\\s*\"explanation\":\\s*\"(.*?)\"\\s*}");
            Pattern pattern = Pattern.compile(
                    "\\{\\s*" +                                   // 匹配 "{"
                            "\"score\"\\s*:\\s*(\\d+(\\.\\d+)?)\\s*,?" +  // 匹配 "score" 字段
                            "\\s*\"isCorrect\"\\s*:\\s*(true|false)\\s*,?" +  // 匹配 "isCorrect" 字段
                            "\\s*\"feedback\"\\s*:\\s*\"([^\"]*)\"\\s*,?" +  // 匹配 "feedback" 字段
                            "\\s*\"explanation\"\\s*:\\s*\"([^\"]*)\"\\s*" +  // 匹配 "explanation" 字段
                            "\\}",  // 匹配 "}"
                    Pattern.DOTALL
            );
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                result.setAiScore(Double.parseDouble(matcher.group(1)));
                result.setIsCorrect(Boolean.parseBoolean(matcher.group(3)));
                result.setFeedback(matcher.group(4));
                result.setExplanation(matcher.group(5));
                log.info("AI 评分结果：{}", result);
            } else {
                throw new RuntimeException("AI 返回的数据格式不正确，无法解析！");
            }

        } catch (Exception e) {
            // 记录错误日志
            e.printStackTrace();
            // 设置默认值
            result.setAiScore(0.0);
            result.setIsCorrect(false);
            result.setFeedback("AI 评分失败，请人工检查！");
            result.setExplanation("AI 解析失败，可能是格式错误。");
        }

        return result;
    }

    /**
     * 解析AI响应为Map<String, Object>
     */
    public static Map<String, Object> parseJsonMap(String aiResponse) {
        try {
            log.info("解析JSON Map，AI响应: {}", aiResponse);
            
            // 首先解析为 JSONObject
            JSONObject jsonResponse = new JSONObject(aiResponse);
            
            // 获取 choices 数组
            if (!jsonResponse.has("choices")) {
                throw new JSONException("Response JSON does not contain 'choices' key!");
            }
            
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.isEmpty()) {
                throw new JSONException("Response JSON 'choices' array is empty!");
            }
            
            // 获取第一个 choice 的 message content
            JSONObject choice = choices.getJSONObject(0);
            if (!choice.has("message")) {
                throw new JSONException("Response JSON does not contain 'message' key!");
            }
            
            JSONObject message = choice.getJSONObject("message");
            if (!message.has("content")) {
                throw new JSONException("Response JSON does not contain 'content' key!");
            }
            
            String content = message.getString("content");
            
            // 从 content 中提取 JSON 对象
            String jsonStr = extractJsonString(content);
            JSONObject jsonObject = new JSONObject(jsonStr);
            
            // 递归转换为 Map
            return convertJsonToMap(jsonObject);
            
        } catch (Exception e) {
            log.error("解析JSON Map失败", e);
            throw new RuntimeException("解析AI响应失败: " + e.getMessage());
        }
    }
    
    /**
     * 递归将 JSONObject 转换为 Map
     */
    private static Map<String, Object> convertJsonToMap(JSONObject json) {
        Map<String, Object> map = new HashMap<>();
        
        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (value instanceof JSONObject) {
                map.put(key, convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.put(key, convertJsonArrayToList((JSONArray) value));
            } else {
                map.put(key, value);
            }
        }
        
        return map;
    }
    
    /**
     * 递归将 JSONArray 转换为 List
     */
    private static List<Object> convertJsonArrayToList(JSONArray array) {
        List<Object> list = new ArrayList<>();
        
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONObject) {
                list.add(convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                list.add(convertJsonArrayToList((JSONArray) value));
            } else {
                list.add(value);
            }
        }
        
        return list;
    }

    /**
     * 解析AI响应为字符串列表
     */
    public static List<String> parseStringList(String aiResponse) {
        try {
            log.info("解析字符串列表，AI响应: {}", aiResponse);
            
            // 首先解析为 JSONObject
            JSONObject jsonResponse = new JSONObject(aiResponse);
            
            // 获取 choices 数组
            if (!jsonResponse.has("choices")) {
                throw new JSONException("Response JSON does not contain 'choices' key!");
            }
            
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.isEmpty()) {
                throw new JSONException("Response JSON 'choices' array is empty!");
            }
            
            // 获取第一个 choice 的 message content
            JSONObject choice = choices.getJSONObject(0);
            if (!choice.has("message")) {
                throw new JSONException("Response JSON does not contain 'message' key!");
            }
            
            JSONObject message = choice.getJSONObject("message");
            if (!message.has("content")) {
                throw new JSONException("Response JSON does not contain 'content' key!");
            }
            
            String content = message.getString("content");
            
            // 从 content 中提取 JSON 数组
            String jsonStr = extractJsonString(content);
            
            // 解析 JSON 数组
            JSONArray jsonArray = new JSONArray(jsonStr);
            
            // 转换为 List<String>
            List<String> result = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                result.add(jsonArray.getString(i));
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("解析字符串列表失败", e);
            throw new RuntimeException("解析AI响应失败: " + e.getMessage());
        }
    }

    /**
     * 从AI响应中提取JSON字符串
     */
    private static String extractJsonString(String aiResponse) {
        // 尝试查找JSON开始和结束的位置
        int startIndex = aiResponse.indexOf('{');
        int startArrayIndex = aiResponse.indexOf('[');
        
        // 确定是对象还是数组
        boolean isArray = false;
        if (startArrayIndex != -1 && (startIndex == -1 || startArrayIndex < startIndex)) {
            startIndex = startArrayIndex;
            isArray = true;
        }
        
        if (startIndex == -1) {
            throw new RuntimeException("无法在AI响应中找到JSON数据");
        }
        
        // 查找对应的结束括号
        int endIndex;
        if (isArray) {
            endIndex = findMatchingBracket(aiResponse, startIndex, '[', ']');
        } else {
            endIndex = findMatchingBracket(aiResponse, startIndex, '{', '}');
        }
        
        if (endIndex == -1) {
            throw new RuntimeException("无法在AI响应中找到完整的JSON数据");
        }
        
        return aiResponse.substring(startIndex, endIndex + 1);
    }

    /**
     * 查找匹配的括号位置
     */
    private static int findMatchingBracket(String text, int startIndex, char openBracket, char closeBracket) {
        int count = 1;
        for (int i = startIndex + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == openBracket) {
                count++;
            } else if (c == closeBracket) {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
}