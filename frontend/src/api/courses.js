/**
 * ==========================================================
 * COURSES API SERVICE
 * ==========================================================
 * 
 * Handles all course-related API calls:
 * - Get all courses (with optional category filter)
 * - Get single course by ID
 * - Create course (instructor only)
 * 
 * PUBLIC vs PROTECTED ENDPOINTS:
 * ------------------------------
 * - GET /api/courses → Public (anyone can view)
 * - GET /api/courses/{id} → Public
 * - POST /api/courses → Protected (needs JWT token)
 * 
 * The axios interceptor automatically adds the token for protected routes.
 */

import api from './axios'

/**
 * Get all courses
 * 
 * @param {number|null} categoryId - Optional category filter
 * @returns {Promise<Array>} - Array of course objects
 * 
 * Examples:
 *   getAllCourses()        → GET /api/courses
 *   getAllCourses(3)       → GET /api/courses?categoryId=3
 */
export const getAllCourses = async (categoryId = null) => {
    // Build params object for query string
    const params = {}
    if (categoryId) {
        params.categoryId = categoryId
    }

    const response = await api.get('/api/courses', { params })
    return response.data
}

/**
 * Get single course by ID
 * 
 * @param {number} id - Course ID
 * @returns {Promise<Object>} - Course object with full details
 */
export const getCourseById = async (id) => {
    const response = await api.get(`/api/courses/${id}`)
    return response.data
}

export const getCourseByUserId = async () => {
    const response = await api.get(`/api/instructor/courses`)
    return response.data
}

/**
 * Create new course (Instructor only)
 * 
 * @param {Object} courseData - Course creation data
 * @param {string} courseData.name - Course name
 * @param {string} courseData.description - Course description
 * @param {number} courseData.price - Course price
 * @param {number} courseData.categoryId - Category ID
 * @param {string} courseData.imageUrl - Cover image URL
 * @param {string} courseData.introVideoUrl - Intro video URL
 * @returns {Promise<Object>} - Created course object
 * 
 * NOTE: This endpoint requires authentication.
 * The JWT token is automatically added by axios interceptor.
 */
export const createCourse = async (courseData) => {
    const response = await api.post('/api/courses', courseData)
    return response.data
}

export const updateCourse = async (id, courseData) => {
    const response = await api.put(`/api/courses/${id}`, courseData)
    return response.data
}

export const updateCourseStatus = async (id, status) => {
    const response = await api.put(`/api/courses/${id}/status?status=${status}`)
    return response.data
}
