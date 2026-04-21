/**
 * ==========================================================
 * AUTHENTICATION API SERVICE
 * ==========================================================
 * 
 * This module handles all authentication-related API calls:
 * - Login
 * - Register
 * - Forgot Password
 * - Reset Password
 * 
 * HOW IT WORKS:
 * -------------
 * 1. Component calls authApi.login(email, password)
 * 2. This sends POST to /api/auth/login
 * 3. Backend returns { token, role, name, email }
 * 4. We return this to the component
 * 5. Component stores token in localStorage and updates Pinia store
 * 
 * SEPARATION OF CONCERNS:
 * -----------------------
 * - This file: ONLY handles HTTP requests
 * - Store: Handles state management (storing user info)
 * - Component: Handles UI and user interaction
 */

import api from './axios'

/**
 * Login user
 * 
 * @param {string} email - User's email
 * @param {string} password - User's password
 * @returns {Promise<Object>} - { token, role, name, email }
 * 
 * Backend endpoint: POST /api/auth/login
 * Request body: { email, password }
 */
export const login = async (email, password) => {
    const response = await api.post('/api/auth/login', {
        email,
        password,
    })
    // response.data為server回傳的response body
    // { token: "eyJ...", role: "STUDENT", name: "John", email: "john@..." }
    return response.data
}

/**
 * Register new user
 * 
 * @param {string} email - User's email
 * @param {string} password - User's password
 * @param {string} role - "STUDENT" or "INSTRUCTOR"
 * @returns {Promise<Object>} - The created user object
 * 
 * Backend endpoint: POST /api/auth/register
 */
export const register = async (email, password, role) => {
    const response = await api.post('/api/auth/register', {
        email,
        password,
        role
    })
    return response.data
}

/**
 * Request password reset email
 * 
 * @param {string} email - User's email
 * @returns {Promise<string>} - Success message
 * 
 * Backend endpoint: POST /api/auth/forgot-password
 */
export const forgotPassword = async (email) => {
    const response = await api.post('/api/auth/forgot-password', { email })
    return response.data
}

/**
 * Reset password with token from email
 * 
 * @param {string} token - Reset token from email link
 * @param {string} newPassword - New password
 * @returns {Promise<string>} - Success message
 * 
 * Backend endpoint: POST /api/auth/reset-password
 */
export const resetPassword = async (token, newPassword) => {
    const response = await api.post('/api/auth/reset-password', { token, newPassword })
    return response.data
}
