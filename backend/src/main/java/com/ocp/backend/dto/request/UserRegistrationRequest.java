package com.ocp.backend.dto.request;

import com.ocp.backend.entity.UserRole;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    // Request會回傳回來的欄位
    private String email;
    private String password; // 需要加密
    private UserRole role;
}
