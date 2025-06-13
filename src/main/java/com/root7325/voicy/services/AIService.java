package com.root7325.voicy.services;

import com.root7325.voicy.utils.Config;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author root7325 on 13.06.2025
 * todo: rename
 */
@Slf4j
public class AIService {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String AI_PROMPT = """

                        это голосовое сообщение. твоя задача:
                        1. сделать его краткое саммари
                        2. по возможности продолжить и развить высказанную идею
                        не задавай вопросов - лишь четко отвечай на них в таком формате
                        первый абзац - саммари
                        дальше пустая строка
                        дальше предлагай развитие идеи
                        на мою личность ты не переходишь - всё от третьего лица
            """;

    private final OkHttpClient httpClient;
    private final String apiKey;

    public AIService() {
        this.httpClient = new OkHttpClient();

        Config.MiscConfig miscConfig = Config.getInstance().getMiscConfig();
        this.apiKey = miscConfig.getOpenRouterKey();
    }

    public CompletableFuture<String> generateResponse(String prompt) {
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
                throw new RuntimeException("Failed to generate response", e);
            }
        });
    }
}
