/**
 * Favorites API Service
 * - Get user's favorites
 * - Add course to favorites
 * - Remove course from favorites
 */

import api from './axios'

/**
 * Get user's favorite courses
 * @returns {Promise<Array>} - Array of Favorite objects with course info
 */
export const getMyFavorites = async () => {
    const response = await api.get('/api/favorites')
    return response.data
}

/**
 * Add course to favorites
 * @param {number} courseId - Course ID to add
 * @returns {Promise<Object>} - Created favorite object
 */
export const addFavorite = async (courseId) => {
    const response = await api.post(`/api/favorites/add/${courseId}`)
    return response.data
}

/**
 * Remove course from favorites
 * @param {number} courseId - Course ID to remove
 * @returns {Promise<boolean>} - Success status
 */
export const removeFavorite = async (courseId) => {
    const response = await api.delete(`/api/favorites/remove/${courseId}`)
    return response.data
}
