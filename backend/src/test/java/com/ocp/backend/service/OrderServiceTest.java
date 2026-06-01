package com.ocp.backend.service;

import com.ocp.backend.dto.response.OrderDto;
import com.ocp.backend.entity.Cart;
import com.ocp.backend.entity.CartItem;
import com.ocp.backend.entity.Coupon;
import com.ocp.backend.entity.Course;
import com.ocp.backend.entity.Order;
import com.ocp.backend.entity.OrderItem;
import com.ocp.backend.entity.OrderStatus;
import com.ocp.backend.entity.User;
import com.ocp.backend.repository.OrderRepository;
import com.ocp.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {

    }

    // ──────────────────────────────────────────
    // getOrdersByUserId
    // ──────────────────────────────────────────

    @Test
    @DisplayName("Should return list of OrderDto when user has multiple orders")
    void getOrdersByUserId_WithValidUserId_ReturnsOrderDtoList() {
        // Arrange
        Long userId = 1L;
        Course course = new Course();
        course.setName("Java Spring Boot Masterclass");
        course.setImageUrl("test-url.jpg");
        course.setPrice(1000);

        Order order = new Order();

        OrderItem orderItem = new OrderItem();
        orderItem.setId(10L);
        orderItem.setCourse(course);
        orderItem.setPriceAtPurchase(1000);
        orderItem.setOrder(order);

        order.setId(100L);
        order.setMerchantTradeNo("MERCH123");
        order.setTotalAmount(1000);
        order.setDiscountAmount(100);
        order.setFinalAmount(900);
        order.setStatus(OrderStatus.PAID);
        order.setCreatedAt(LocalDateTime.now());
        order.setPaymentDate(LocalDateTime.now());
        order.setItems(List.of(orderItem));

        when(orderRepository.findAllByUserId(userId)).thenReturn(List.of(order));

        // Act
        List<OrderDto> result = orderService.getOrdersByUserId(userId);

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);
        assertThat(result.get(0).getMerchantTradeNo()).isEqualTo("MERCH123");
        assertThat(result.get(0).getItems().get(0).getCourseName()).isEqualTo("Java Spring Boot Masterclass");
        assertThat(result.get(0).getItems().get(0).getPriceAtPurchase()).isEqualTo(1000);
        assertThat(result.get(0).getFinalAmount()).isEqualTo(900);
        verify(orderRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("Should return an empty list when user has no orders")
    void getOrdersByUserId_WhenNoOrdersFound_ReturnsEmptyList() {
        // Arrange
        Long userId = 2L;
        when(orderRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<OrderDto> result = orderService.getOrdersByUserId(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(orderRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("Should throw NullPointerException when repository unexpectedly returns null")
    void getOrdersByUserId_WhenRepositoryReturnsNull_ThrowsNullPointerException() {
        // Arrange
        Long userId = 3L;
        when(orderRepository.findAllByUserId(userId)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> orderService.getOrdersByUserId(userId))
                .isInstanceOf(NullPointerException.class);
    }

    // ──────────────────────────────────────────
    // cancelOrder
    // ──────────────────────────────────────────
    @Test
    @DisplayName("Should cancel order successfully")
    void cancelOrder_WithValidOrderId_ChangesStatusToCancelled() {
        // Arrange
        String email = "test@example.com";
        Long orderId = 100L;

        User user = new User();
        user.setEmail(email);

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PAID);
        order.setUser(user);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        orderService.cancelOrder(email, orderId);

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw RuntimeException when order not found")
    void cancelOrder_WhenOrderNotFound_ThrowsRuntimeException() {
        // Arrange
        String email = "test@example.com";
        Long orderId = 100L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        assertThatThrownBy(() -> orderService.cancelOrder(email, orderId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");

        // Assert
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when user is not the owner")
    void cancelOrder_WhenUserIsNotOwner_ThrowsRuntimeException() {
        // Arrange
        String email = "wrongUser@example.com";
        Long orderId = 100L;

        User user = new User();
        user.setEmail("other@example.com");
        User wrongUser = new User();
        wrongUser.setEmail(email);

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PAID);
        order.setUser(user);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        assertThatThrownBy(() -> orderService.cancelOrder(email, orderId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User is not authorized to cancel this order");

        // Assert
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    // ──────────────────────────────────────────
    // createOrder
    // ──────────────────────────────────────────

    @Test
    @DisplayName("Should create order and clear cart when no coupon is provided")
    void createOrder_WithValidCartAndNoCoupon_CreatesOrderAndClearsCart() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        Course course = new Course();
        course.setPrice(1000);
        course.setName("Java Course");

        CartItem cartItem = new CartItem();
        cartItem.setCourse(course);

        Cart cart = new Cart();
        cart.setItems(List.of(cartItem));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartService.getUserCart(email)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderDto result = orderService.createOrder(email, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalAmount()).isEqualTo(1000);
        assertThat(result.getDiscountAmount()).isEqualTo(0);
        assertThat(result.getFinalAmount()).isEqualTo(1000);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING.toString());

        verify(cartService, times(1)).clearCart(email);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should apply discount and create order when valid coupon is provided")
    void createOrder_WithValidCartAndValidCoupon_AppliesDiscountAndCreatesOrder() {
        // Arrange
        String email = "test@example.com";
        String couponCode = "SAVE20";
        User user = new User();
        user.setEmail(email);

        Course course = new Course();
        course.setPrice(1000);

        CartItem cartItem = new CartItem();
        cartItem.setCourse(course);

        Cart cart = new Cart();
        cart.setItems(List.of(cartItem));

        Coupon coupon = new Coupon();
        coupon.setCode(couponCode);
        coupon.setDiscountPercent(20);
        coupon.setUsed(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartService.getUserCart(email)).thenReturn(cart);
        when(couponService.getCouponByCode(couponCode)).thenReturn(coupon);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderDto result = orderService.createOrder(email, couponCode);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalAmount()).isEqualTo(1000);
        assertThat(result.getDiscountAmount()).isEqualTo(200); // 1000 * 0.20
        assertThat(result.getFinalAmount()).isEqualTo(800);

        verify(cartService, times(1)).clearCart(email);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when user is not found")
    void createOrder_WhenUserNotFound_ThrowsRuntimeException() {
        // Arrange
        String email = "unknown@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(email, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when cart is empty")
    void createOrder_WhenCartIsEmpty_ThrowsRuntimeException() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        Cart cart = new Cart();
        cart.setItems(List.of()); // empty list

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartService.getUserCart(email)).thenReturn(cart);

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(email, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("===No items found in your cart====");

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when coupon is already used")
    void createOrder_WhenCouponIsAlreadyUsed_ThrowsRuntimeException() {
        // Arrange
        String email = "test@example.com";
        String couponCode = "USED10";
        User user = new User();
        user.setEmail(email);

        Course course = new Course();
        course.setPrice(1000);
        CartItem cartItem = new CartItem();
        cartItem.setCourse(course);

        Cart cart = new Cart();
        cart.setItems(List.of(cartItem));

        Coupon coupon = new Coupon();
        coupon.setUsed(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartService.getUserCart(email)).thenReturn(cart);
        when(couponService.getCouponByCode(couponCode)).thenReturn(coupon);

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(email, couponCode))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Coupon already used");

        verify(orderRepository, never()).save(any());
    }
}
