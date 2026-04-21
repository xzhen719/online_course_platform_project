package com.ocp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocp.backend.dto.request.ChangePasswordRequest;
import com.ocp.backend.dto.request.UserProfileUpdateRequest;
import com.ocp.backend.dto.response.ProfileUpdateResponse;
import com.ocp.backend.dto.response.UserProfileDto;
import com.ocp.backend.entity.User;
import com.ocp.backend.service.UserService;
import com.ocp.backend.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 取得目前登入使用者的個人資料
     * GET /api/user/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(mapToDto(user));
    }

    /**
     * 更新個人資料
     * PUT /api/user/profile
     * 若 email 有變更，會回傳新的 JWT Token
     */
    @PutMapping("/profile")
    public ResponseEntity<ProfileUpdateResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserProfileUpdateRequest request) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        String orgEmail = user.getEmail();

        User updated = userService.updateUser(user.getId(), request);

        // 如果 email 有變更，產生新的 JWT Token
        String newToken = null;
        if (request.getEmail() != null && !orgEmail.equals(updated.getEmail())) {
            newToken = jwtUtil.generateToken(updated.getEmail());
        }

        return ResponseEntity.ok(new ProfileUpdateResponse(mapToDto(updated), newToken));
    }

    /**
     * 變更密碼
     * PUT /api/user/password
     */
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequest request) {
        User user = userService.getUserByEmail(userDetails.getUsername());

        try {
            userService.changePassword(user.getId(), request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok("密碼已更新");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 轉換 User Entity -> DTO
     */
    private UserProfileDto mapToDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setSex(user.getSex());
        dto.setBirthdate(user.getBirthdate() != null ? user.getBirthdate().toString() : null);
        dto.setBio(user.getBio());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRole(user.getRole().name());
        return dto;
    }
}
