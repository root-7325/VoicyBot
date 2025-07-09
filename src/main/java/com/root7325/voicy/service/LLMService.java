package com.root7325.voicy.service;

import com.google.inject.Inject;
import com.root7325.voicy.config.Config;
import com.root7325.voicy.config.MiscConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
public class LLMService {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String AI_PROMPT = """

        Проанализируй предоставленный текст:
        1. Сначала кратко изложи основную мысль (саммари).
        2. Затем предложи возможные направления для развития или улучшения идеи.
        Не задавай вопросов, отвечай лаконично и по существу. Используй третье лицо.
        Используй Markdown для форматирования ответа (*bold text* _italic text_).
        Формат:
        - Первый абзац: саммари
        - Пустая строка
        - Второй абзац: развитие или предложения
    """;

    private final ExecutorService executorService;
    private final OkHttpClient httpClient;
    private final String apiKey;

    @Inject
    public LLMService(Config config, ExecutorService executorService) {
        this.executorService = executorService;
        this.httpClient = new OkHttpClient();

        MiscConfig miscConfig = config.getMiscConfig();
        this.apiKey = miscConfig.getOpenRouterKey();
    }

    public CompletableFuture<String> generateResponse(String prompt) {
        String uuid = UUID.randomUUID().toString();

        log.debug("Starting processing LLM request. UUID-{}", uuid);
        return CompletableFuture.supplyAsync(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("model", "deepseek/deepseek-r1:free");

                JSONObject message = new JSONObject();
                message.put("role", "user");
                message.put("content", prompt + AI_PROMPT);

                requestBody.put("messages", new JSONObject[]{message});

                RequestBody body = RequestBody.create(requestBody.toString(), JSON);

                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    log.debug("Opened request. UUID-{}", uuid);

                    if (!response.isSuccessful()) {
                        log.error("OpenRouter API request failed: {}", response.body().string());
                        throw new IOException("Unexpected code " + response);
                    }

                    JSONObject responseJson = new JSONObject(response.body().string());
                    return responseJson.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                }
            } catch (Exception e) {
                log.error("Error in OpenRouter API call", e);
                throw new RuntimeException(e);
            }
        }, executorService);
    }
}
