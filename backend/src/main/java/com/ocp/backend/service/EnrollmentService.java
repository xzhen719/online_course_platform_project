package com.ocp.backend.service;

import com.ocp.backend.dto.response.CourseDto;
import com.ocp.backend.entity.*;
import com.ocp.backend.repository.ChapterRepository;
import com.ocp.backend.repository.EnrollmentRepository;
import com.ocp.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final OrderRepository orderRepository;
    private final ChapterRepository chapterRepository;

    @Transactional
    public void enrollUserInCourse(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        // 檢查訂單付款狀態
        if (order.getStatus() != OrderStatus.PAID) {
            log.warn("Order has not been paid!");
            return;
        }

        User user = order.getUser();
        List<OrderItem> items = order.getItems();

        for (OrderItem orderItem : items) {
            enroll(user, orderItem.getCourse());
        }
    }

    private void enroll(User user, Course course) {
        Enrollment enrollment = new Enrollment();

        // 檢查使用者是否已註冊課程
        boolean enrolled = enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId());
        if (enrolled) {
            log.info("User{} is alreadly enrolled in Course{}", user.getEmail(), course.getName());
            return;
        }

        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);
        log.info("User{} successfully enrolled in Course{}", user.getEmail(), course.getName());
    }

    public List<CourseDto> getEnrolledCourses(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return enrollments.stream().map(enrollment -> {
            Course course = enrollment.getCourse();
            int totalChapters = chapterRepository.countByCourseId(course.getId());
            return new CourseDto(
                    course.getId(),
                    course.getName(),
                    course.getDescription(),
                    course.getImageUrl(),
                    course.getInstructorName(),
                    course.getPrice(),
                    course.getRatingAverage(),
                    course.getStudentCount(),
                    totalChapters,
                    course.getTotalTime());
        })
                .collect(Collectors.toList());
    }

    public boolean isEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }
}
