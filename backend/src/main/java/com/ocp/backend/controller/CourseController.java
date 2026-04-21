package com.ocp.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ocp.backend.dto.request.CourseRequest;
import com.ocp.backend.dto.response.CourseDto;
import com.ocp.backend.entity.CourseStatus;
import com.ocp.backend.service.CourseService;
import com.ocp.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    // 1. Get all courses (Public)
    // 可選擇透過categoryId篩選
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDto>> getAllCourses(@RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(courseService.getAllCourses(categoryId));
    }

    @GetMapping("/instructor/courses")
    public ResponseEntity<List<CourseDto>> getInstructorCourses(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Long instructorId = userService.getUserByEmail(email).getId();
        return ResponseEntity.ok(courseService.getCoursesByInstructorId(instructorId));
    }

    // 1.5 Get Single Course (Public)
    // GET /api/courses/{id}
    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // 2. Create Course
    // POST /api/courses
    @PostMapping("/courses")
    public ResponseEntity<CourseDto> createCourse(
            @RequestBody CourseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        CourseDto newCourse = courseService.createCourse(request, email);
        return ResponseEntity.ok(newCourse);
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long id, @RequestBody CourseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        CourseDto dto = courseService.updateCourse(id, request, email);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/courses/{id}/status")
    public ResponseEntity<CourseDto> updateCourseStatus(@PathVariable Long id,
            @RequestParam CourseStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        CourseDto dto = courseService.updateCourseStatus(id, status, email);
        return ResponseEntity.ok(dto);
    }
}
