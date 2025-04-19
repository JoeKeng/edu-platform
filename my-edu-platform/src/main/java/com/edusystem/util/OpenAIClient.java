package com.edusystem.util;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

@Component
public class OpenAIClient {

    private static final int MAX_RETRIES = 3;  // 最大重试次数

    // 配置 OkHttpClient，支持超时和自动重试
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)  // 自动重试网络错误
            .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时
            .readTimeout(60, TimeUnit.SECONDS)  // 读取超时
            .writeTimeout(60, TimeUnit.SECONDS)  // 写入超时
            .build();

    public static String callOpenAI(String apiKey, String baseUrl, String model, String prompt) throws IOException {
        // 校验 baseUrl 不为 null
        if (baseUrl == null) {
            throw new IllegalArgumentException("baseUrl 不能为空");
        }
        // 校验 baseUrl 格式（可选但推荐）
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            throw new IllegalArgumentException("baseUrl 必须以 'http://' 或 'https://' 开头");
        }

        // 构造请求 JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("messages", new JSONArray().put(new JSONObject().put("role", "user").put("content", prompt)));
        requestBody.put("temperature", 0.7);

        // 发送 POST 请求
        Request request = new Request.Builder()
                .url(baseUrl + "/v1/chat/completions")  // OpenAI 兼容 API 地址
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
                .build();

        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();  // 返回 AI 响应
                } else {
                    System.err.println("请求失败，状态码: " + response.code());
                }
            } catch (SocketTimeoutException e) {
                System.err.println("请求超时，重试中... (" + (attempt + 1) + "/" + MAX_RETRIES + ")");
            } catch (IOException e) {
                System.err.println("请求异常: " + e.getMessage());
            }

            attempt++;
        }

        throw new IOException("AI API 请求失败，已重试 " + MAX_RETRIES + " 次");
    }
}