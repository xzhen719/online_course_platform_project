package com.ocp.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ocp.backend.dto.request.UserLoginRequest;
import com.ocp.backend.dto.request.UserProfileUpdateRequest;
import com.ocp.backend.dto.request.UserRegistrationRequest;
import com.ocp.backend.dto.response.AuthResponse;
import com.ocp.backend.entity.User;
import com.ocp.backend.entity.UserRole;
import com.ocp.backend.repository.UserRepository;
import com.ocp.backend.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // auto-generate constructor for final fields
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    // 註冊邏輯
    @Transactional
    public AuthResponse registerNewUser(UserRegistrationRequest registrationRequest) {

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new RuntimeException("此信箱已被註冊!請重新輸入");
        }

        // 密碼加密
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());

        // 根據使用者輸入的資料建立User物件
        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        // 預設用 email 前綴作為使用者名稱
        String emailPrefix = registrationRequest.getEmail().split("@")[0];
        user.setUsername(emailPrefix);
        user.setPassword(encodedPassword);
        // 使用前端傳來的 role，若無則預設 STUDENT
        user.setRole(registrationRequest.getRole() != null ? registrationRequest.getRole() : UserRole.STUDENT);

        // 將User物件存入資料庫
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, emailPrefix, registrationRequest.getEmail(),
                user.getRole().name());
    }

    public AuthResponse login(UserLoginRequest request) {
        // 1. 用 Email 找使用者
        // Since findByEmail returns Optional<User>, we use orElseThrow to handle user
        // not found case
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("找不到使用者: " + request.getEmail()));

        // 2. 驗證密碼
        // passwordEncoder.matches(明文, 加密文)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密碼錯誤");
        }

        // 3. 密碼正確，產生 JWT Token
        String token = jwtUtil.generateToken(user.getEmail());

        // 4. 回傳包含 Token 和使用者資訊的包裹
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("找不到使用者: " + email));
    }

    // 忘記密碼->寄送密碼重設信
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("找不到使用者: " + email));

        // Generate Token (UUID)
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiryDate(LocalDateTime.now().plusMinutes(15)); // Valid for 15 mins

        userRepository.save(user);

        // Send Email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    // 密碼重設
    @Transactional
    public void resetPassword(String token, String newPassword) {

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // Check Expiry
        if (user.getResetTokenExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("請重新嘗試");
        }

        // Update Password
        user.setPassword(passwordEncoder.encode(newPassword));

        // Clear Token
        user.setResetToken(null);
        user.setResetTokenExpiryDate(null);

        userRepository.save(user);
    }

    public User updateUser(Long userId, UserProfileUpdateRequest userProfileUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (userProfileUpdateRequest.getUsername() != null) {
            user.setUsername(userProfileUpdateRequest.getUsername());
        }
        if (userProfileUpdateRequest.getEmail() != null) {
            user.setEmail(userProfileUpdateRequest.getEmail());
        }
        if (userProfileUpdateRequest.getSex() != null) {
            user.setSex(userProfileUpdateRequest.getSex());
        }
        try {
            if (userProfileUpdateRequest.getBirthdate() != null) {
                user.setBirthdate(LocalDate.parse(userProfileUpdateRequest.getBirthdate(), formatter));
            }
        } catch (Exception e) {
            System.out.println("Date parsing error, skipping birthdate.");
        }
        if (userProfileUpdateRequest.getBio() != null) {
            user.setBio(userProfileUpdateRequest.getBio());
        }
        if (userProfileUpdateRequest.getAvatarUrl() != null) {
            user.setAvatarUrl(userProfileUpdateRequest.getAvatarUrl());
        }
        return userRepository.save(user);
    }

    /**
     * 變更密碼 (需驗證舊密碼)
     */
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // 驗證舊密碼
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("目前密碼錯誤");
        }

        // 更新密碼
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
