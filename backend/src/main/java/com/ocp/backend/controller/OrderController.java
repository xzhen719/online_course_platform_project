package com.ocp.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ocp.backend.dto.response.OrderDto;
import com.ocp.backend.service.OrderService;
import com.ocp.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String couponCode) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(orderService.createOrder(email, couponCode));
    }

    @GetMapping("/my-orders")
    public List<OrderDto> getAllOrders(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return orderService.getOrdersByUserId(userService.getUserByEmail(email).getId());
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancelOrder(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        String email = userDetails.getUsername();
        orderService.cancelOrder(email, orderId);
        return ResponseEntity.noContent().build(); // 204
    }
}
