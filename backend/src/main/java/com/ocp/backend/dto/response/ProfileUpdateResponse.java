package com.ocp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileUpdateResponse {
    private UserProfileDto profile;
    private String newToken; // Only set when email changes
}
