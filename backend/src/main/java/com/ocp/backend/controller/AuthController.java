package com.ocp.backend.controller;

import com.ocp.backend.dto.request.UserLoginRequest;
import com.ocp.backend.dto.request.UserRegistrationRequest;
import com.ocp.backend.dto.response.AuthResponse;
import com.ocp.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 回傳JSON data
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // POST http://localhost:8080/api/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRegistrationRequest request) {

        AuthResponse response = userService.registerNewUser(request);

        // return 200 with new user data
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request) {
        // 呼叫 Service 進行登入
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
