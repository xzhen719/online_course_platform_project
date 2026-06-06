package com.ocp.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocp.backend.AbstractIntegrationTest;
import com.ocp.backend.dto.request.UserLoginRequest;
import com.ocp.backend.dto.request.UserRegistrationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class AuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully register and then login with correct credentials")
    void registerAndLogin_Success() throws Exception {
        // 1. Arrange: Register a new user in the dynamic MySQL database
        UserRegistrationRequest registerRequest = new UserRegistrationRequest();
        registerRequest.setEmail("integration@example.com");
        registerRequest.setPassword("securePassword123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 2. Act: Attempt to login
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("integration@example.com");
        loginRequest.setPassword("securePassword123");

        // 3. Assert: Verify we get a 200 OK and a JWT Token back!
        // AuthResponse is flat: {token, username, email, role}
        // username is auto-generated from the email prefix by UserService
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.username").value("integration"));
    }

    @Test
    @DisplayName("Should fail login with incorrect password")
    void login_WithIncorrectPassword_Returns4xxError() throws Exception {
        // Arrange: Register a unique user for this test
        UserRegistrationRequest registerRequest = new UserRegistrationRequest();
        registerRequest.setEmail("fail_integration@example.com");
        registerRequest.setPassword("securePassword123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)));

        // Act: Login with wrong password
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("fail_integration@example.com");
        loginRequest.setPassword("wrongPassword!!!");

        // Assert: Expect a 4xx Client Error (like 400 Bad Request or 401 Unauthorized)
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError());
    }
}
