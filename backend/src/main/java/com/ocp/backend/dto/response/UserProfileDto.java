package com.ocp.backend.dto.response;

import lombok.Data;

@Data
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String sex;
    private String birthdate;
    private String bio;
    private String avatarUrl;
    private String role;
}
