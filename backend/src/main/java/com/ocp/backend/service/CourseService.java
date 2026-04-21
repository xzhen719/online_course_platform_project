package com.ocp.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ocp.backend.dto.request.CourseRequest;
import com.ocp.backend.dto.response.CourseDto;
import com.ocp.backend.entity.Category;
import com.ocp.backend.entity.Course;
import com.ocp.backend.entity.CourseStatus;
import com.ocp.backend.entity.User;
import com.ocp.backend.repository.CategoryRepository;
import com.ocp.backend.repository.ChapterRepository;
import com.ocp.backend.repository.CourseRepository;
import com.ocp.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ChapterRepository chapterRepository;

    // --- 功能 A: 查詢所有課程 (公開首頁用) ---
    // 只回傳已上架 (ON_SHELF) 的課程
    public List<CourseDto> getAllCourses(Long categoryId) {
        List<Course> courses;
        if (categoryId != null) {
            courses = courseRepository.findByCategoryId(categoryId);
        } else {
            courses = courseRepository.findAll();
        }
        return courses.stream()
                .filter(course -> course.getStatus() == CourseStatus.ON_SHELF)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- 功能 A: 查詢單一課程 (詳情頁用) ---
    public CourseDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found: " + id));
        return convertToDto(course);
    }

    @Transactional(readOnly = true)
    public List<CourseDto> getCoursesByInstructorId(Long instructorId) {
        List<Course> courses = courseRepository.findByInstructorId(instructorId);
        return courses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- 功能 B: 新增課程 (講師後台用) ---
    @Transactional
    public CourseDto createCourse(CourseRequest request, String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Course course = new Course();
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            course.setCategory(category);
        }

        course.setVideoUrl(request.getVideoUrl());
        course.setInstructor(instructor);
        course.setInstructorName(request.getInstructorName());
        course.setStatus(CourseStatus.ON_SHELF);
        course.setOnShelfDate(LocalDateTime.now());
        course.setSubmittedDate(LocalDateTime.now());

        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            course.setImageUrl(request.getImageUrl());
        } else {
            course.setImageUrl("https://placehold.co/600x400?text=No+Image");
        }

        Course saved = courseRepository.save(course);
        return convertToDto(saved);
    }

    @Transactional
    public CourseDto updateCourse(Long courseId, CourseRequest request, String instructorEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this course");
        }

        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setInstructorName(request.getInstructorName());
        course.setPrice(request.getPrice());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            course.setCategory(category);
        } else {
            course.setCategory(null); // Allow category removal
        }

        course.setImageUrl(request.getImageUrl());
        course.setVideoUrl(request.getVideoUrl());

        Course saved = courseRepository.save(course);
        return convertToDto(saved);
    }

    @Transactional
    public CourseDto updateCourseStatus(Long courseId, CourseStatus status, String instructorEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this course");
        }
        course.setStatus(status);
        Course saved = courseRepository.save(course);
        return convertToDto(saved);
    }

    /**
     * Entity -> DTO 轉換
     */
    private CourseDto convertToDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setImageUrl(course.getImageUrl());
        dto.setVideoUrl(course.getVideoUrl());
        dto.setInstructorName(course.getInstructorName());
        dto.setInstructorDesc(course.getInstructorDesc());

        // 處理lazy-loaded instructor
        if (course.getInstructor() != null) {
            dto.setInstructorId(course.getInstructor().getId());
        }

        dto.setPrice(course.getPrice());
        dto.setRatingAverage(course.getRatingAverage());
        dto.setRatingCount(course.getRatingCount());
        dto.setStudentCount(course.getStudentCount());

        dto.setTotalChapters(chapterRepository.countByCourseId(course.getId()));
        dto.setTotalTime(course.getTotalTime());

        // 處理lazy-loaded category
        if (course.getCategory() != null) {
            dto.setCategoryName(course.getCategory().getName());
            dto.setCategoryId(course.getCategory().getId());
        }

        dto.setStatus(course.getStatus() != null ? course.getStatus().toString() : null);

        return dto;
    }
}
