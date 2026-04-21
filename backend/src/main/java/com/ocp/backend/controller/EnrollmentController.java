package com.ocp.backend.controller;

import com.ocp.backend.dto.response.CourseDto;
import com.ocp.backend.entity.User;
import com.ocp.backend.repository.UserRepository;
import com.ocp.backend.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<List<CourseDto>> getMyCourses(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<CourseDto> courses = enrollmentService.getEnrolledCourses(user.getId());

        return ResponseEntity.ok(courses);
    }
}
