package com.ocp.backend.service;

import com.ocp.backend.entity.Cart;
import com.ocp.backend.entity.CartItem;
import com.ocp.backend.entity.Course;
import com.ocp.backend.entity.User;
import com.ocp.backend.repository.CartRepository;
import com.ocp.backend.repository.CourseRepository;
import com.ocp.backend.repository.EnrollmentRepository;
import com.ocp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    // 注入所有需要的 Repository (User, Cart, Course, CartItem)
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    // --- 功能 1: 取得目前使用者的購物車 ---
    // 如果沒有，就幫他創建一個新的
    public Cart getUserCart(String email) {
        // 1. 找 User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 找 Cart，如果沒有就 create new
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    // --- 功能 2: 加入購物車 ---
    @Transactional
    public void addToCart(String email, Long courseId) {
        // 1. 取得購物車 (呼叫上面的 getUserCart)
        Cart cart = getUserCart(email);

        // 2. 檢查: 這門課是否已經在購物車裡了？
        List<CartItem> cartItemList = cart.getItems();
        for (CartItem cartItem : cartItemList) {
            if (cartItem.getCourse().getId().equals(courseId)) {
                throw new RuntimeException("Already in cart!");
            }
        }

        // 2.1檢查是否已經購買過這項課程
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new RuntimeException("Already enrolled!");
        }

        // 3. 找course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 4. 建立 CartItem
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setCourse(course);

        // 5. 加進購物車並存檔
        // 因為我們設了 orphanRemoval/Cascade，所以只要加進 list 並存 cart 就可以了
        cart.getItems().add(item);
        cartRepository.save(cart);
    }

    // --- 功能 3: 移除購物車項目 ---
    @Transactional
    public void removeFromCart(String email, Long cartItemId) {
        // 需確認這個 cartItem 真的屬於這個 User 的購物車

        // 1. 取得 User Cart
        Cart cart = getUserCart(email);

        // 2. 在 cart.getItems() 裡面找到這個 itemId，然後移除它
        cart.getItems().removeIf(cartItem -> cartItem.getId().equals(cartItemId));

        // 3. 存檔 cartRepository.save(cart);
        cartRepository.save(cart);
        // JPA will delete the cart items itself due to orphanRemoval.
    }

    @Transactional
    public void clearCart(String email) {
        Cart cart = getUserCart(email);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
