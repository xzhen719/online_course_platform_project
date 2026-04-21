/**
 * ==========================================================
 * COUPONS API SERVICE
 * ==========================================================
 * 
 * Handles user's coupon operations:
 * - Get my coupons
 * - Generate gift coupon (course completion reward)
 */

import api from './axios'

/**
 * Get current user's coupons
 * 
 * @returns {Promise<Array>} - Array of coupon objects
 */
export const getMyCoupons = async () => {
    const response = await api.get('/api/coupons/my-coupons')
    return response.data
}

/**
 * Generate a gift coupon (course completion reward)
 * 
 * @param {number} courseId - The course that was completed
 * @returns {Promise<Object>} - Generated coupon object
 * Response structure:
 * {
 *   id: 1,
 *   code: "OCP-ABC12",
 *   discountPercent: 10,
 *   expiryDate: "2025-01-05T..."
 * }
 */
export const generateGiftCoupon = async (courseId) => {
    const response = await api.post('/api/coupons/gift-coupon', { courseId })
    return response.data
}
