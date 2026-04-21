package com.ocp.backend.service;

import com.ocp.backend.dto.request.ReviewRequest;
import com.ocp.backend.dto.response.ReviewDto;
import com.ocp.backend.entity.Course;
import com.ocp.backend.entity.Review;
import com.ocp.backend.entity.User;
import com.ocp.backend.repository.CourseRepository;
import com.ocp.backend.repository.EnrollmentRepository;
import com.ocp.backend.repository.ReviewRepository;
import com.ocp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public ReviewDto addReview(Long userId, Long courseId, ReviewRequest request) {
        // 1. Validate parameters
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 2. Check if user is enrolled
        boolean isEnrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
        if (!isEnrolled) {
            throw new RuntimeException("User must be enrolled in the course to write a review");
        }

        // 3. Check for duplicate review
        boolean hasReviewedAlready = reviewRepository.existsByUserIdAndCourseId(userId, courseId);
        if (hasReviewedAlready) {
            throw new RuntimeException("User has already reviewed this course");
        }

        Review review = new Review();
        review.setUser(user);
        review.setCourse(course);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);
        return mapToDto(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getAllReviews(Long courseId) {
        List<Review> reviews = reviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        if (reviews == null || reviews.isEmpty()) {
            return Collections.emptyList();
        }
        return reviews.stream()
                .map((r) -> mapToDto(r))
                .collect(Collectors.toList());
    }

    private ReviewDto mapToDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setUserName(review.getUser().getUsername());
        reviewDto.setRating(review.getRating());
        reviewDto.setComment(review.getComment());
        reviewDto.setCreatedAt(review.getCreatedAt());
        return reviewDto;
    }
}
