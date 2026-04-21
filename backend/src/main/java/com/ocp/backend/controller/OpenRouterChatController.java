package com.ocp.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocp.backend.service.OpenRouterChatService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class OpenRouterChatController {

    private final OpenRouterChatService chatService;

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody Map<String, String> req) {

        // 取得 request body 中的 prompt
        String prompt = req.get("prompt");

        if (prompt == null || prompt.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Prompt is required"));
        }

        String content = chatService.chat(prompt);

        if (content == null || content.isBlank()) {
            return ResponseEntity.ok(Map.of(
                    "content", "無法取得 AI 回應",
                    "success", false));
        }

        return ResponseEntity.ok(Map.of(
                "content", content,
                "success", true));
    }
}
