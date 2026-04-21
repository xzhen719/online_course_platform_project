package com.ocp.backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenRouterChatService {

    private final RestClient restClient;

    public OpenRouterChatService(@Value("${openrouter.api.key}") String apiKey) {

        // RestClient is a synchronous HTTP client that perform HTTP request and return
        // response.
        this.restClient = RestClient.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    public String chat(String userMessage) {

        Map<String, Object> requestBody = Map.of(
                "model", "mistralai/devstral-2512:free",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", userMessage)));

        Map<?, ?> response = restClient.post()
                .uri("/chat/completions")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        // 等同 curl 回來的 JSON：
        // choices[0].message.content
        return extractContent(response);
    }

    private String extractContent(Map<?, ?> response) {

        if (response == null) {
            return null;
        }

        List<?> choices = (List<?>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            return null;
        }

        Map<?, ?> choice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) choice.get("message");

        return message != null ? (String) message.get("content") : null;
    }
}
