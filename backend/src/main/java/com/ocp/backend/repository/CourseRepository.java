package com.ocp.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ocp.backend.entity.Course;
import com.ocp.backend.entity.CourseStatus;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // JPA Syntax: findBy + 欄位名稱 + (條件) + And/Or + 欄位名稱 + (條件)
    List<Course> findByInstructorId(Long instructorId);

    List<Course> findByStatus(CourseStatus status);

    List<Course> findByCategoryId(Long categoryId);
}
