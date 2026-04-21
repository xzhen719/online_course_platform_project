/**
 * ==========================================================
 * ENROLLMENTS API SERVICE
 * ==========================================================
 * 
 * Handles user's enrolled courses:
 * - Get my enrolled courses
 * 
 * WHAT ARE ENROLLMENTS?
 * ---------------------
 * When a user pays for a course, they get "enrolled".
 * This gives them access to:
 * - Full chapter videos
 * - Learning progress tracking
 * - Course reviews
 */

import api from './axios'

/**
 * Get current user's enrolled courses
 * 
 * @returns {Promise<Array>} - Array of enrolled course objects
 * 
 * Response structure:
 * [
 *   {
 *     id: 1,
 *     name: "Vue.js Complete Guide",
 *     description: "...",
 *     imageUrl: "...",
 *     instructorName: "John Doe"
 *   },
 *   ...
 * ]
 */
export const getMyEnrolledCourses = async () => {
    const response = await api.get('/api/enrollments/me')
    return response.data
}
