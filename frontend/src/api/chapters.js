/**
 * ==========================================================
 * CHAPTERS API SERVICE
 * ==========================================================
 * 
 * Handles course chapter operations:
 * - Get public syllabus (without video URLs)
 * - Get full chapters (with video URLs, for enrolled users)
 * - Add chapter (instructor only)
 * 
 * PUBLIC vs PROTECTED CHAPTERS:
 * -----------------------------
 * - Syllabus: Anyone can see chapter titles (to preview course content)
 * - Full chapters: Only enrolled students can see video URLs
 */

import api from './axios'

/**
 * Get public syllabus (chapter titles only, no video URLs)
 * 
 * @param {number} courseId - Course ID
 * @returns {Promise<Array>} - Array of chapter info (without video URLs)
 */
export const getCourseSyllabus = async (courseId) => {
    const response = await api.get(`/api/courses/${courseId}/syllabus`)
    return response.data
}

/**
 * Get full chapters with video URLs (enrolled users only)
 * 
 * @param {number} courseId - Course ID
 * @returns {Promise<Array>} - Array of full chapter objects
 * 
 * NOTE: Returns 403 if user is not enrolled in the course
 */
export const getCourseChapters = async (courseId) => {
    const response = await api.get(`/api/courses/${courseId}/chapters`)
    return response.data
}

/**
 * Add chapter to course (instructor only)
 * 
 * @param {number} courseId - Course ID
 * @param {Object} chapterData - Chapter data
 * @param {string} chapterData.title - Chapter title
 * @param {string} chapterData.description - Chapter description
 * @param {string} chapterData.videoUrl - Video URL
 * @param {number} chapterData.sortOrder - Display order
 * @returns {Promise<Object>} - Created chapter object
 */
export const addChapter = async (courseId, chapterData) => {
    const response = await api.post(`/api/courses/${courseId}/chapters`, chapterData)
    return response.data
}

/**
 * Get chapters for instructor (course owner only)
 * 
 * @param {number} courseId - Course ID
 * @returns {Promise<Array>} - Array of full chapter objects
 */
export const getInstructorChapters = async (courseId) => {
    const response = await api.get(`/api/instructor/courses/${courseId}/chapters`)
    return response.data
}

/**
 * Update chapter (instructor only)
 * 
 * @param {number} chapterId - Chapter ID
 * @param {Object} chapterData - Chapter data to update
 * @returns {Promise<Object>} - Updated chapter object
 */
export const updateChapter = async (chapterId, chapterData) => {
    const response = await api.put(`/api/chapters/${chapterId}`, chapterData)
    return response.data
}
