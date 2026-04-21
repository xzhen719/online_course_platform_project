/**
 * ==========================================================
 * API SERVICE INDEX
 * ==========================================================
 * 
 * This file re-exports all API services for convenient importing.
 * 
 * USAGE EXAMPLES:
 * ---------------
 * 
 * Import specific functions:
 *   import { login, register } from '@/api'
 *   import { getAllCourses, getCourseById } from '@/api'
 * 
 * Import entire service:
 *   import { authApi, coursesApi, cartApi } from '@/api'
 *   await authApi.login(email, password)
 *   await coursesApi.getAllCourses()
 * 
 * Import axios instance for custom requests:
 *   import { api } from '@/api'
 *   await api.get('/api/custom-endpoint')
 */

// Export the axios instance
export { default as api } from './axios'

// Export all auth functions
export * from './auth'

// Export all courses functions
export * from './courses'

// Export all cart functions  
export * from './cart'

// Export all orders functions
export * from './orders'

// Export all categories functions
export * from './categories'

// Export all enrollments functions
export * from './enrollments'

// Export all chapters functions
export * from './chapters'

// Export all coupons functions
export * from './coupons'

// Export all reviews functions
export * from './reviews'

// Export all progress functions
export * from './progress'

export * from './openrouter'

// Also export as grouped objects for namespacing
// e.g. await authApi.login(email, password)
import * as authApi from './auth'
import * as coursesApi from './courses'
import * as cartApi from './cart'
import * as ordersApi from './orders'
import * as categoriesApi from './categories'
import * as enrollmentsApi from './enrollments'
import * as chaptersApi from './chapters'
import * as couponsApi from './coupons'
import * as reviewsApi from './reviews'
import * as progressApi from './progress'
import * as openrouterApi from './openrouter'

export {
    authApi,
    coursesApi,
    cartApi,
    ordersApi,
    categoriesApi,
    enrollmentsApi,
    chaptersApi,
    couponsApi,
    reviewsApi,
    progressApi,
    openrouterApi,
}
