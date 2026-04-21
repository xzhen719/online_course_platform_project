/**
 * ==========================================================
 * LEARNING PROGRESS API SERVICE
 * ==========================================================
 * 
 * Handles learning progress tracking:
 * - Mark chapter as completed
 * - Get completed chapters for a course
 */

import api from './axios'

/**
 * Mark chapter as completed
 * 
 * @param {number} chapterId - Chapter ID to mark complete
 * @returns {Promise<Object>} - Learning progress object
 */
export const markChapterComplete = async (chapterId) => {
    const response = await api.post(`/api/progress/${chapterId}`)
    return response.data
}

/**
 * Get completed chapter IDs for a course
 * 
 * @param {number} courseId - Course ID
 * @returns {Promise<Array<number>>} - Array of completed chapter IDs
 */
export const getCompletedChapters = async (courseId) => {
    const response = await api.get(`/api/progress/course/${courseId}`)
    return response.data
}
