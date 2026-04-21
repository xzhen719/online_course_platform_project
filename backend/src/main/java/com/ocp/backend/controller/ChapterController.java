package com.ocp.backend.controller;

import com.ocp.backend.dto.request.ChapterRequest;
import com.ocp.backend.dto.response.ChapterDto;
import com.ocp.backend.dto.response.CourseDto;
import com.ocp.backend.entity.User;
import com.ocp.backend.service.ChapterService;
import com.ocp.backend.service.CourseService;
import com.ocp.backend.service.EnrollmentService;
import com.ocp.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChapterController {
    private final ChapterService chapterService;
    private final UserService userService;
    private final EnrollmentService enrollmentService;
    private final CourseService courseService;

    @GetMapping("/courses/{courseId}/syllabus")
    public ResponseEntity<List<ChapterDto>> getCourseSyllabus(@PathVariable Long courseId) {
        return ResponseEntity.ok(chapterService.getPublicChapters(courseId));
    }

    @GetMapping("/courses/{courseId}/chapters")
    public ResponseEntity<List<ChapterDto>> getCourseChapters(@PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);

        boolean isEnrolled = enrollmentService.isEnrolled(user.getId(), courseId);
        if (isEnrolled) {
            return ResponseEntity.ok(chapterService.getAuthChapters(courseId));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/courses/{courseId}/chapters")
    public ResponseEntity<ChapterDto> addChapter(@PathVariable Long courseId, @RequestBody ChapterRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);
        CourseDto courseDto = courseService.getCourseById(courseId);

        // 確認Instructor確實有開課
        if (!courseDto.getInstructorId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        ChapterDto newChapter = chapterService.addChapter(courseId, request);
        return ResponseEntity.ok(newChapter);
    }

    // Get chapters for instructor (course owner)
    @GetMapping("/instructor/courses/{courseId}/chapters")
    public ResponseEntity<List<ChapterDto>> getInstructorChapters(@PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);
        CourseDto courseDto = courseService.getCourseById(courseId);

        // Verify instructor owns this course
        if (!courseDto.getInstructorId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(chapterService.getAuthChapters(courseId));
    }

    @PutMapping("/chapters/{chapterId}")
    public ResponseEntity<ChapterDto> updateChapter(@PathVariable Long chapterId,
            @RequestBody ChapterRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);

        // Get chapter to verify ownership via course
        ChapterDto existingChapter = chapterService.getChapterById(chapterId);
        CourseDto courseDto = courseService.getCourseById(existingChapter.getCourseId());

        if (!courseDto.getInstructorId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        ChapterDto updatedChapter = chapterService.updateChapter(chapterId, request);
        return ResponseEntity.ok(updatedChapter);
    }
}