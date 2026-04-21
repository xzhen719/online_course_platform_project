/**
 * User Profile API Service
 * - Get current user profile
 * - Update user profile
 */

import api from './axios'

/**
 * Get current user's profile
 * @returns {Promise<Object>} - User profile data
 */
export const getProfile = async () => {
    const response = await api.get('/api/user/profile')
    return response.data
}

/**
 * Update user profile
 * @param {Object} profileData - Profile data to update
 * @param {string} profileData.username - Username
 * @param {string} profileData.email - Email (if changed, new token will be issued)
 * @param {string} profileData.sex - Gender
 * @param {string} profileData.birthdate - Birthdate (yyyy-MM-dd)
 * @param {string} profileData.bio - Bio/description
 * @returns {Promise<Object>} - { profile: {...}, newToken: string|null }
 */
export const updateProfile = async (profileData) => {
    const response = await api.put('/api/user/profile', profileData)
    return response.data
}

/**
 * Change password
 * @param {string} currentPassword - Current password
 * @param {string} newPassword - New password
 * @returns {Promise<string>} - Success message
 */
export const changePassword = async (currentPassword, newPassword) => {
    const response = await api.put('/api/user/password', { currentPassword, newPassword })
    return response.data
}
