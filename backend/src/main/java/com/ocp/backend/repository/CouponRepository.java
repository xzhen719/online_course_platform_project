package com.ocp.backend.repository;

import com.ocp.backend.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);

    List<Coupon> findByUserId(Long userId);

    // Check if user already has a coupon for this course
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<Coupon> findByUserIdAndCourseId(Long userId, Long courseId);
}
