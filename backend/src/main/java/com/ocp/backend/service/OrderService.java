package com.ocp.backend.service;

import com.ocp.backend.dto.response.OrderDto;
import com.ocp.backend.dto.response.OrderItemDto;
import com.ocp.backend.entity.*;
import com.ocp.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final CouponService couponService;

    @Transactional
    public OrderDto createOrder(String email, String couponCode) {
        // 1. 找 User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // 2. 找使用者的 Cart
        Cart cart = cartService.getUserCart(email);

        // 3. 檢查購物車是不是空的 (如果是空的，拋出例外)
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("===No items found in your cart====");
        }

        // 4. 建立order物件
        Order order = new Order();
        order.setUser(user);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        order.setMerchantTradeNo(uuid.substring(0, 20)); // 產生流水號
        // 設定狀態為 PENDING
        order.setStatus(OrderStatus.PENDING);

        // 確認是否有套用coupon
        int discountAmount = 0;
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon coupon = couponService.getCouponByCode(couponCode);

            if (coupon.isUsed()) {
                throw new RuntimeException("Coupon already used");
            }

            order.setCoupon(coupon);
        }

        // 5. 把 CartItems 轉換成 OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        int totalAmount = 0;
        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setPriceAtPurchase(item.getCourse().getPrice());
            orderItem.setOrder(order);
            orderItem.setCourse(item.getCourse());
            totalAmount += item.getCourse().getPrice();
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        // Recalculate discount if coupon exists
        if (order.getCoupon() != null) {
            Integer percent = order.getCoupon().getDiscountPercent();
            // Calculation: total * (percent / 100)
            // Use double for precision then round back to int
            discountAmount = (int) Math.round(totalAmount * (percent / 100.0));
        }

        // 6. 設定訂單金額 (totalAmount, finalAmount)
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(totalAmount - discountAmount);

        // 7. 存檔 Order (Cascade 會自動存 OrderItems)
        Order savedOrder = orderRepository.save(order);

        // 8. 清空購物車
        cartService.clearCart(email);

        return convertToDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public void cancelOrder(String email, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getEmail().equals(email)) {
            throw new RuntimeException("User is not authorized to cancel this order");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // Entity -> DTO 轉換; 含 OrderItems 的 Course 資訊
    public OrderDto convertToDto(Order order) {
        List<OrderItemDto> itemDtoList = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(item.getId());
                if (item.getCourse() != null) {
                    itemDto.setCourseId(item.getCourse().getId());
                    itemDto.setCourseName(item.getCourse().getName());
                    itemDto.setCourseImage(item.getCourse().getImageUrl());
                }
                // OrderItem的Price是存在OrderItems Table裡的Course Price snapshot,
                // 即便Course不存在也應該有Item price.
                itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
                itemDtoList.add(itemDto);
            }
        }
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setMerchantTradeNo(order.getMerchantTradeNo());
        orderDto.setTotalAmount(order.getTotalAmount());
        orderDto.setDiscountAmount(order.getDiscountAmount());
        orderDto.setFinalAmount(order.getFinalAmount());
        orderDto.setStatus(order.getStatus().toString());
        orderDto.setItems(itemDtoList);
        orderDto.setCreatedAt(order.getCreatedAt());
        orderDto.setPaymentDate(order.getPaymentDate());
        return orderDto;
    }
}
