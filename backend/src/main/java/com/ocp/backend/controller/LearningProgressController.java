package com.ocp.backend.controller;

import com.ocp.backend.dto.response.LearningProgressDto;
import com.ocp.backend.entity.User;
import com.ocp.backend.service.LearningProgressService;
import com.ocp.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class LearningProgressController {
    private final LearningProgressService learningProgressService;
    private final UserService userService;

    /**
     * 標記章節為已完成
     */
    @PostMapping("/{chapterId}")
    public ResponseEntity<LearningProgressDto> markComplete(@PathVariable Long chapterId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        LearningProgressDto learningProgressDto = learningProgressService.markChapterAsCompleted(user.getId(),
                chapterId);
        return ResponseEntity.ok(learningProgressDto);
    }

    /**
     * 取得使用者在某課程已完成的所有章節 ID
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Long>> getCompletedChapters(@PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        List<Long> completedChapterIds = learningProgressService.getCompletedChapterIds(user.getId(), courseId);
        return ResponseEntity.ok(completedChapterIds);
    }
}
