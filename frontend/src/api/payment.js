/**
 * ==========================================================
 * PAYMENT API SERVICE
 * ==========================================================
 * 
 * Handles ECPay payment integration:
 * - Submit payment (get ECPay form HTML)
 * 
 * PAYMENT FLOW:
 * -------------
 * 1. User clicks "確認支付" in Checkout
 * 2. Frontend calls createOrder() → Get orderId
 * 3. Frontend calls submitPayment(orderId) → Get HTML form
 * 4. Frontend injects HTML into page
 * 5. HTML contains auto-submit form → Redirects to ECPay
 * 6. User pays on ECPay
 * 7. ECPay calls our backend callback (ReturnURL)
 * 8. ECPay redirects user to ClientBackURL (/payment/result)
 */

import api from './axios'

/**
 * Submit payment to ECPay
 * 
 * @param {number} orderId - The order ID to pay for
 * @returns {Promise<string>} - HTML form that auto-redirects to ECPay
 * 
 * The returned HTML contains:
 * - A form with hidden fields (all ECPay params)
 * - Auto-submit JavaScript
 * 
 * Frontend should inject this HTML into the page to redirect to ECPay.
 */
export const submitPayment = async (orderId) => {
    const response = await api.post(`/api/payment/submit/${orderId}`)
    return response.data // This is HTML string
}
