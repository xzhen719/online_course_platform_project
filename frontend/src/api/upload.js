/**
 * ==========================================================
 * FILE UPLOAD API SERVICE
 * ==========================================================
 * 
 * Handles image upload for courses:
 * - Allowed types: JPEG, PNG, GIF, WebP
 * - Max size: 5MB
 * 
 * Usage:
 * const imageUrl = await uploadImage(selectedFile)
 */

import api from './axios'

/**
 * Upload an image file
 * 
 * @param {File} file - The image file to upload
 * @returns {Promise<string>} - The URL of the uploaded image
 * 
 * Backend endpoint: POST /api/upload
 * Response: The full URL to access the uploaded image
 *           e.g. "http://localhost:8080/uploads/uuid-123.jpg"
 */
export const uploadImage = async (file) => {
    // Create FormData for multipart/form-data upload
    const formData = new FormData()
    formData.append('file', file)

    const response = await api.post('/api/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })

    // Backend returns the URL as plain text
    return response.data
}
