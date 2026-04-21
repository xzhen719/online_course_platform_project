/**
 * ==========================================================
 * CATEGORIES API SERVICE
 * ==========================================================
 * 
 * Handles course category operations:
 * - Get all categories
 * - Get category by ID
 * 
 * CATEGORIES ARE PUBLIC:
 * ----------------------
 * No authentication needed - anyone can view categories.
 */

import api from './axios'

/**
 * Get all categories
 * 
 * @returns {Promise<Array>} - Array of category objects
 * 
 * Response structure:
 * [
 *   { id: 1, name: "Programming" },
 *   { id: 2, name: "Design" },
 *   ...
 * ]
 */
export const getAllCategories = async () => {
    const response = await api.get('/api/categories')
    return response.data
}

/**
 * Get category by ID
 * 
 * @param {number} id - Category ID
 * @returns {Promise<Object>} - Category object
 */
export const getCategoryById = async (id) => {
    const response = await api.get(`/api/categories/${id}`)
    return response.data
}
