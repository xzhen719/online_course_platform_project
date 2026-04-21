package com.ocp.backend.controller;

import com.ocp.backend.dto.request.ReviewRequest;
import com.ocp.backend.dto.response.ReviewDto;
import com.ocp.backend.entity.User;
import com.ocp.backend.service.ReviewService;
import com.ocp.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ReviewDto> addReview(
            @PathVariable Long courseId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);
        
        ReviewDto reviewDto = reviewService.addReview(user.getId(), courseId, request);
        return ResponseEntity.ok(reviewDto);
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getCourseReviews(@PathVariable Long courseId) {
        List<ReviewDto> reviews = reviewService.getAllReviews(courseId);
        return ResponseEntity.ok(reviews);
    }
}
