package com.ocp.backend.dto.request;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String username;
    private String email;
    private String sex;
    private String birthdate;
    private String bio;
    private String avatarUrl;
}