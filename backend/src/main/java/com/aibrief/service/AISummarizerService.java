package com.aibrief.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class AISummarizerService {

    private static final Logger log = LoggerFactory.getLogger(AISummarizerService.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.api-key:}") private String apiKey;
    @Value("${ai.api-url}") private String apiUrl;
    @Value("${ai.model}") private String model;
    @Value("${ai.request-delay-ms}") private long requestDelayMs;

    public record SummaryResult(String summary, int importanceScore) {}

    public AISummarizerService(
            @Value("${ai.connect-timeout-ms}") long connectTimeoutMs,
            @Value("${ai.read-timeout-ms}") long readTimeoutMs) {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeoutMs, TimeUnit.MILLISECONDS)
                .build();
    }

    public SummaryResult summarize(String title, String content) {
        try {
            Thread.sleep(requestDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI API key not configured, using fallback for: {}", title);
            return fallback(title);
        }

        String prompt = buildPrompt(title, content);
        String requestBody = buildRequestBody(prompt);

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(requestBody, JSON))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 429) {
                log.warn("LLM rate limited (429) for: {}", title);
                return fallback(title);
            }
            if (!response.isSuccessful() || response.body() == null) {
                log.warn("LLM error {} for: {}", response.code(), title);
                return fallback(title);
            }
            return parseResponse(response.body().string(), title);
        } catch (IOException e) {
            log.warn("LLM request failed for '{}': {}", title, e.getMessage());
            return fallback(title);
        }
    }

    private SummaryResult parseResponse(String responseBody, String title) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            return parseContent(content, title);
        } catch (Exception e) {
            log.warn("Failed to parse LLM response for '{}': {}", title, e.getMessage());
            return fallback(title);
        }
    }

    private SummaryResult parseContent(String content, String title) {
        try {
            // Strip markdown code block if present
            String json = content.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("```[a-z]*\\n?", "").trim();
            }
            JsonNode node = objectMapper.readTree(json);
            String summary = node.path("summary").asText(null);
            int importance = node.path("importance").asInt(3);
            if (summary == null || summary.isBlank()) return fallback(title);
            return new SummaryResult(summary, clamp(importance));
        } catch (Exception e) {
            log.warn("LLM returned non-JSON for '{}', using fallback", title);
            return fallback(title);
        }
    }

    private SummaryResult fallback(String title) {
        return new SummaryResult(title, 3);
    }

    private int clamp(int value) {
        return Math.max(1, Math.min(5, value));
    }

    private String buildPrompt(String title, String content) {
        return String.format("""
                你是一个AI新闻编辑。请对以下新闻进行摘要：
                标题：%s
                内容：%s

                要求：
                1. 用3句话概括核心内容
                2. 给出重要性评分（1-5，5最重要），评分标准：5=重大突破/发布，3=行业动态，1=一般资讯
                3. 输出格式：{"summary": "...", "importance": 3}
                只输出JSON，不要其他内容。""", title, content);
    }

    private String buildRequestBody(String prompt) {
        return String.format("""
                {"model":"%s","messages":[{"role":"user","content":%s}],"max_tokens":200}""",
                model, objectMapper.valueToTree(prompt).toString());
    }
}
