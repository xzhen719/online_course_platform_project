package com.ocp.backend.service;

import com.ocp.backend.entity.Coupon;
import com.ocp.backend.entity.Course;
import com.ocp.backend.entity.User;
import com.ocp.backend.repository.CouponRepository;
import com.ocp.backend.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CourseRepository courseRepository;
    private final Random random = new Random();

    /**
     * Generate a course completion coupon for the user.
     * Returns existing coupon if user already earned one for this course.
     */
    @Transactional
    public Coupon generateCourseCompletionCoupon(User user, Long courseId) {
        // Check if user already has a coupon for this course
        Optional<Coupon> existing = couponRepository.findByUserIdAndCourseId(user.getId(), courseId);
        if (existing.isPresent()) {
            // Return existing coupon instead of creating duplicate
            return existing.get();
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

        Coupon coupon = new Coupon();

        // 1. Generate Unique Code (shortened UUID for readability)
        String uniqueCode = "OCP-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        coupon.setCode(uniqueCode);

        // 2. Lottery Logic: 5, 10, or 15
        int[] possibleDiscounts = { 5, 10, 15 };
        int selectedDiscount = possibleDiscounts[random.nextInt(possibleDiscounts.length)];
        coupon.setDiscountPercent(selectedDiscount);

        // 3. Set Dates
        LocalDateTime now = LocalDateTime.now();
        coupon.setCreatedAt(now);
        coupon.setExpiryDate(now.plusDays(15)); // 到期日 = 建立日 + 15 天
        coupon.setUsed(false);
        coupon.setUser(user);
        coupon.setCourse(course); // Link to course

        return couponRepository.save(coupon);
    }

    public Integer getCouponDiscount(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        if (coupon.isUsed()) {
            throw new RuntimeException("Coupon has already been used");
        }

        return coupon.getDiscountPercent();
    }

    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));
    }

    public List<Coupon> getCouponsByUser(User user) {
        return couponRepository.findByUserId(user.getId());
    }

    @Transactional
    public void markCouponAsUsed(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        // Double check to prevent race conditions
        if (coupon.isUsed()) {
            throw new RuntimeException("Coupon already used");
        }

        coupon.setUsed(true);
        couponRepository.save(coupon);
    }
}
