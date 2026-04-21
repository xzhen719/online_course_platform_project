/**
 * ==========================================================
 * CART API SERVICE
 * ==========================================================
 * 
 * Handles shopping cart operations:
 * - Get user's cart
 * - Add course to cart
 * - Remove item from cart
 * 
 * ALL ENDPOINTS ARE PROTECTED:
 * ----------------------------
 * Every cart endpoint requires authentication because
 * the cart is tied to a specific user.
 * 
 * The backend knows which user by reading the JWT token,
 * which contains the user's email.
 */

import api from './axios'

/**
 * Get current user's cart
 * 
 * @returns {Promise<Object>} - Cart object with items array and totalPrice
 * 
 * Response structure:
 * {
 *   id: 1,
 *   items: [
 *     { id: 1, courseId: 5, courseName: "Vue.js", price: 49.99, imageUrl: "..." },
 *     { id: 2, courseId: 6, courseName: "React", price: 59.99, imageUrl: "..." }
 *   ],
 *   totalPrice: 109.98
 * }
 */
export const getMyCart = async () => {
    const response = await api.get('/api/cart')
    return response.data
}

/**
 * Add course to cart
 * 
 * @param {number} courseId - ID of course to add
 * @returns {Promise<string>} - Success message
 * 
 * Note: Backend will check if course already in cart
 */
export const addToCart = async (courseId) => {
    const response = await api.post(`/api/cart/add/${courseId}`)
    return response.data
}

/**
 * Remove item from cart
 * 
 * @param {number} cartItemId - ID of cart item (NOT course ID!)
 * @returns {Promise<string>} - Success message
 * 
 * IMPORTANT: This uses cartItemId, not courseId!
 * The cartItemId is the ID of the item in the cart table.
 */
export const removeFromCart = async (cartItemId) => {
    const response = await api.delete(`/api/cart/remove/${cartItemId}`)
    return response.data
}
