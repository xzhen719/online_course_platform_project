/**
 * Handles course review operations:
 * - Get all reviews for a course (public)
 * - Add review (enrolled users only)
 */

import api from './axios'

/**
 * Get all reviews for a course
 * 
 * @param {number} courseId - Course ID
 * @returns {Promise<Array>} - Array of review objects
 */
export const getCourseReviews = async (courseId) => {
    const response = await api.get(`/api/courses/${courseId}/reviews`)
    return response.data
}

/**
 * Add review to course (enrolled users only)
 * 
 * @param {number} courseId - Course ID
 * @param {Object} reviewData - Review data
 * @param {number} reviewData.rating - Star rating (1-5)
 * @param {string} reviewData.comment - Review comment
 * @returns {Promise<Object>} - Created review object
 */
export const addReview = async (courseId, reviewData) => {
    const response = await api.post(`/api/courses/${courseId}/reviews`, reviewData)
    return response.data
}
