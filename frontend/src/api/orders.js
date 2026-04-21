/**
 * ==========================================================
 * ORDERS API SERVICE
 * ==========================================================
 * 
 * Handles order creation and management:
 * - Create order from cart
 * 
 * HOW ORDERS WORK:
 * ----------------
 * 1. User adds courses to cart
 * 2. User goes to checkout, optionally applies coupon
 * 3. Frontend calls createOrder(couponCode)
 * 4. Backend:
 *    - Gets user's cart items
 *    - Calculates total with discount
 *    - Creates Order and OrderItems
 *    - Clears the cart
 *    - Returns the Order
 * 5. User then proceeds to payment
 */

import api from './axios'

/**
 * Create order from current cart
 * 
 * @param {string|null} couponCode - Optional coupon code for discount
 * @returns {Promise<Object>} - Order object with items and pricing
 * 
 * Response structure:
 * {
 *   id: 1,
 *   merchantTradeNo: "OCP1703...",
 *   totalAmount: 99.98,
 *   discountAmount: 10.00,
 *   finalAmount: 89.98,
 *   status: "PENDING",
 *   items: [...],
 *   createdAt: "2025-01-20T10:30:00"
 * }
 */
export const createOrder = async (couponCode = null) => {
    // Build query params
    const params = {}
    if (couponCode) {
        params.couponCode = couponCode
    }

    const response = await api.post('/api/orders', null, { params })
    return response.data
}

export const getAllOrders = async () => {
    const response = await api.get('/api/orders/my-orders')
    return response.data
}

//post, cause we are only changing the status of the order.
export const cancelOrder = async (orderId) => {
    const response = await api.post(`/api/orders/cancel/${orderId}`)
    return response.data
}
