package com.ocp.backend.controller;

import com.ocp.backend.entity.Coupon;
import com.ocp.backend.entity.User;
import com.ocp.backend.service.CouponService;
import com.ocp.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final UserService userService;

    @GetMapping("/my-coupons")
    public ResponseEntity<List<Coupon>> getMyCoupons(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        List<Coupon> coupons = couponService.getCouponsByUser(user);
        return ResponseEntity.ok(coupons);
    }

    @PostMapping("/gift-coupon")
    public ResponseEntity<Coupon> generateCourseCompletionCoupon(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Long> request) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Long courseId = request.get("courseId");

        if (courseId == null) {
            return ResponseEntity.badRequest().build();
        }

        Coupon coupon = couponService.generateCourseCompletionCoupon(user, courseId);
        return ResponseEntity.ok(coupon);
    }
}
