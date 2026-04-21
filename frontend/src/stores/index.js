import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '../api/auth'
import { getAllCourses as fetchCoursesApi, getCourseById as fetchCourseApi, createCourse as createCourseApi, updateCourse as updateCourseApi, updateCourseStatus as updateCourseStatusApi, getCourseByUserId as fetchInstructorCoursesApi } from '../api/courses'
import { getMyCart as fetchCartApi, addToCart as addToCartApi, removeFromCart as removeFromCartApi } from '../api/cart'

import { getCourseReviews as fetchReviewApi, addReview as addReviewApi } from '../api/reviews'
import { getMyFavorites as fetchFavoritesApi, addFavorite as addFavoriteApi, removeFavorite as removeFavoriteApi } from '../api/favorites'
import { register as registerApi } from '../api/auth'
import { getMyCoupons as fetchCouponsApi } from '../api/coupons'


export const useMainStore = defineStore('main', () => {
  // Auth
  const isLoggedIn = ref(false)
  const userType = ref(null) // 'STUDENT' | 'INSTRUCTOR'
  const token = ref(null) // JWT token
  const user = ref({
    name: '',
    email: '',
    bio: '',
    sex: '',
    birthDate: ''
  })

  // Data
  const courses = ref([]) // 公開課程 (從API載入)
  const coursesLoading = ref(false)

  // Instructor Data
  const instructorCourses = ref([]) // 講師自己的課程
  const instructorCoursesLoading = ref(false)

  // Cart & Orders
  const cartItems = ref([])
  const favoriteCourseIds = ref([])
  const orders = ref([])


  // Coupons - loaded from API
  const availableCoupons = ref([])
  const couponsLoaded = ref(false)
  const appliedCoupon = ref(null)

  // Progress Tracking: { courseId: [chapterId1, chapterId2, ...] }
  const studentProgress = ref({})

  // Actions
  /**
   * Login with real API
   * 1. Calls backend /api/auth/login
   * 2. Stores JWT token in localStorage + state
   * 3. Updates user info from response
   * 4. Returns response for component to handle navigation
   */
  async function login(email, password) {
    // Call real API
    const response = await loginApi(email, password)

    // Store token in localStorage (persists across page refresh)
    localStorage.setItem('token', response.token)

    // Store user info in localStorage (for page refresh)
    localStorage.setItem('user', JSON.stringify({
      name: response.username,
      email: response.email,
      role: response.role
    }))

    // Update state
    token.value = response.token
    isLoggedIn.value = true
    userType.value = response.role.toLowerCase() // 'STUDENT' -> 'student'
    user.value.name = response.username
    user.value.email = response.email

    return response
  }

  function logout() {
    // Clear localStorage
    localStorage.removeItem('token')
    localStorage.removeItem('user')

    // Clear state
    isLoggedIn.value = false
    userType.value = null
    token.value = null
    user.value = { name: '', email: '', bio: '', sex: '', birthDate: '' }
    cartItems.value = []
    appliedCoupon.value = null
    studentProgress.value = {}
  }

  /**
   * Initialize auth state from localStorage
   * Call this on app startup (in App.vue or main.js)
   * This restores login state after page refresh
   */
  function initAuth() {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')

    if (savedToken && savedUser) {
      const existUser = JSON.parse(savedUser)
      token.value = savedToken
      isLoggedIn.value = true
      userType.value = existUser.role.toLowerCase()
      user.value.name = existUser.name
      user.value.email = existUser.email
    }
  }

  /**
   * Fetch all courses from API
   * 從後端載入課程列表
   */
  async function fetchCourses(categoryId = null) {
    coursesLoading.value = true
    try {
      const data = await fetchCoursesApi(categoryId)
      // 轉換後端CourseDto -> 前端格式
      courses.value = data.map(course => ({
        id: course.id,
        title: course.name,
        description: course.description,
        instructor: course.instructorName || 'Unknown',
        instructorId: course.instructorId,
        instructorDesc: course.instructorDesc,
        price: course.price,
        image: course.imageUrl,
        introVideoUrl: course.videoUrl,
        category: course.categoryName || 'Uncategorized',
        categoryId: course.categoryId,
        rating: course.ratingAverage || 0,
        reviewCount: course.ratingCount || 0,
        students: course.studentCount || 0,
        totalTime: course.totalTime || 0,
        totalChapters: course.totalChapters || 0,
        chapters: [], // Chapters需要另外API取得
        status: course.status || 'ON_SHELF'
      }))
    } catch (error) {
      console.error('Failed to fetch courses:', error)
    } finally {
      coursesLoading.value = false
    }
  }

  /**
   * Get single course by ID from API
   */
  async function getCourse(id) {
    try {
      const course = await fetchCourseApi(id)
      return {
        id: course.id,
        title: course.name,
        description: course.description,
        instructor: course.instructorName || 'Unknown',
        instructorId: course.instructorId,
        instructorDesc: course.instructorDesc,
        price: course.price,
        image: course.imageUrl,
        introVideoUrl: course.videoUrl,
        category: course.categoryName || 'Uncategorized',
        categoryId: course.categoryId,
        rating: course.ratingAverage || 0,
        reviewCount: course.ratingCount || 0,
        students: course.studentCount || 0,
        totalTime: course.totalTime || 0,
        totalChapters: course.totalChapters || 0,
        chapters: [],
        status: course.status || 'ON_SHELF'
      }
    } catch (error) {
      console.error('Failed to fetch course:', error)
      return null
    }
  }

  /**
   * Fetch instructor's own courses
   * 載入講師自己的課程列表
   */
  async function fetchInstructorCourses() {
    if (!isLoggedIn.value) return
    instructorCoursesLoading.value = true
    try {
      const data = await fetchInstructorCoursesApi()
      instructorCourses.value = data.map(course => ({
        id: course.id,
        title: course.name,
        description: course.description,
        instructor: course.instructorName || 'Unknown',
        instructorId: course.instructorId,
        price: course.price,
        image: course.imageUrl,
        introVideoUrl: course.videoUrl,
        category: course.categoryName || 'Uncategorized',
        categoryId: course.categoryId,
        rating: course.ratingAverage || 0,
        reviewCount: course.ratingCount || 0,
        students: course.studentCount || 0,
        totalTime: course.totalTime || 0,
        totalChapters: course.totalChapters || 0,
        status: course.status || 'EDITED'
      }))
    } catch (error) {
      console.error('Failed to fetch instructor courses:', error)
    } finally {
      instructorCoursesLoading.value = false
    }
  }

  async function register(email, password, type) {
    const data = await registerApi(email, password, type.toUpperCase())
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify({
      name: data.username,
      email: data.email,
      role: data.role
    }))
    token.value = data.token
    isLoggedIn.value = true
    user.value.email = email
    isLoggedIn.value = true
    userType.value = type
  }

  /**
   * 切換收藏狀態 (呼叫API)
   */
  async function toggleFavorite(courseId) {
    if (!isLoggedIn.value) {
      window.location.href = '/login'
      return
    }
    try {
      if (favoriteCourseIds.value.includes(courseId)) {
        // 移除收藏
        await removeFavoriteApi(courseId)
        favoriteCourseIds.value = favoriteCourseIds.value.filter((id) => id !== courseId)
      } else {
        // 加入收藏
        await addFavoriteApi(courseId)
        favoriteCourseIds.value.push(courseId)
      }
    } catch (error) {
      console.error('Failed to toggle favorite:', error)
    }
  }

  /**
   * 從後端載入收藏列表
   */
  async function fetchFavorites() {
    if (!isLoggedIn.value) return
    try {
      const data = await fetchFavoritesApi()
      // DTO 直接提供 courseId, filter(Boolean) 確保沒有 null 值
      favoriteCourseIds.value = data.map(fav => fav.courseId).filter(Boolean)
    } catch (error) {
      console.error('Failed to fetch favorites:', error)
    }
  }

  function toggleChapterCompletion(courseId, chapterId) {
    if (!studentProgress.value[courseId]) {
      studentProgress.value[courseId] = []
    }

    const idx = studentProgress.value[courseId].indexOf(chapterId)
    if (idx > -1) {
      studentProgress.value[courseId].splice(idx, 1) // Unmark
    } else {
      studentProgress.value[courseId].push(chapterId) // Mark
    }
  }



  /**
   * 從後端載入購物車
   */
  async function fetchCart() {
    if (!isLoggedIn.value) return
    try {
      const data = await fetchCartApi()
      // 轉換後端格式 -> 前端格式
      cartItems.value = data.items?.map(item => ({
        cartItemId: item.id, // 後端的cart_item ID (用於刪除)
        id: item.courseId,   // 課程ID
        title: item.courseName,
        price: item.price,
        image: item.imageUrl,
      })) || []
    } catch (error) {
      console.error('Failed to fetch cart:', error)
    }
  }

  /**
   * 加入購物車 (呼叫API)
   * 未登入會導向登入頁
   */
  async function addToCart(courseId) {
    if (!isLoggedIn.value) {
      window.location.href = '/login'
      return
    }
    try {
      await addToCartApi(courseId)
      // 重新載入購物車
      await fetchCart()
    } catch (error) {
      console.error('Failed to add to cart:', error)
      alert('請確認是否已在購物車或是已經購買過')
    }
  }

  /**
   * 移除購物車項目 (呼叫API)
   * 注意: 使用 cartItemId (後端ID), 不是 courseId
   */
  async function removeFromCart(cartItemId) {
    try {
      await removeFromCartApi(cartItemId)
      // 重新載入購物車
      await fetchCart()
    } catch (error) {
      console.error('Failed to remove from cart:', error)
    }
  }

  function clearCart() {
    cartItems.value = []
    appliedCoupon.value = null
  }

  // Load user's coupons from API
  async function loadCoupons() {
    if (couponsLoaded.value) return  // Already loaded
    try {
      const coupons = await fetchCouponsApi()
      availableCoupons.value = coupons
        .filter(c => !c.used)
        .filter(c => new Date(c.expiryDate) > new Date())  // Not expired
      couponsLoaded.value = true
    } catch (err) {
      console.error('Failed to load coupons:', err)
    }
  }

  async function applyCoupon(code) {
    // Load coupons if not already loaded
    if (!couponsLoaded.value) {
      await loadCoupons()
    }

    const coupon = availableCoupons.value.find((c) => c.code === code)
    if (coupon) {
      appliedCoupon.value = coupon
      return { success: true, message: '已套用優惠!' }
    }
    return { success: false, message: '無效的優惠券' }
  }

  function removeCoupon() {
    appliedCoupon.value = null
  }



  async function toggleCourseStatus(courseId) {
    const course = instructorCourses.value.find(c => c.id === courseId)
    if (!course) return

    const newStatus = course.status === 'ON_SHELF' ? 'OFF_SHELF' : 'ON_SHELF'
    try {
      await updateCourseStatusApi(courseId, newStatus)
      course.status = newStatus
      // Refresh public courses list to reflect status change
      await fetchCourses()
    } catch (error) {
      console.error('Failed to update course status:', error)
    }
  }

  /**
   * Add a rating to a course
   * @param {string} courseId - The ID of the course to rate
   * @param {Object} ratingData - The rating data to add
   * @param {number} ratingData.rating - The rating value (1-5)
   * @param {string} ratingData.comment - The rating comment
   */
  //1222
  async function addRating(courseId, ratingData) {
    //1222
    await addReviewApi(courseId, ratingData)
    const course = courses.value.find(c => c.id === courseId)
    //1222
    if (course) {
      if (!course.ratings) course.ratings = []
      course.ratings.unshift({
        id: `r-${Date.now()}`,
        date: new Date().toLocaleDateString(),
        ...ratingData
      })
      // Optional: Recalculate average rating logic could go here
    }
  }

  async function getReviews(courseId) {
    try {
      const data = await fetchReviewApi(courseId)
      const course = courses.value.find(c => c.id === courseId)
      if (!course) return

      // 後端dto轉換為前端格式
      course.ratings = data.map(r => ({
        id: r.id,
        name: r.userName,
        rating: r.rating,
        comment: r.comment,
        date: r.createdAt,
      })) || []
    } catch (error) {
      console.error('Failed to fetch reviews:', error)
    }
  }

  // Instructor Actions
  async function createCourse(courseData) {
    try {
      const created = await createCourseApi(courseData)
      // Refresh courses list after creation
      await fetchCourses()
      return created
    } catch (error) {
      console.error('Failed to create course:', error)
      throw error
    }
  }

  async function updateCourse(courseId, courseData) {
    try {
      const updated = await updateCourseApi(courseId, courseData)
      // Refresh courses list after update
      await fetchCourses()
      return updated
    } catch (error) {
      console.error('Failed to update course:', error)
      throw error
    }
  }



  /**
   * 設定某課程的已完成章節 (從API載入時使用)
   */
  function setCompletedChapters(courseId, chapterIds) {
    studentProgress.value[courseId] = chapterIds
  }

  // Getters
  const coursesWithFavorites = computed(() => {
    return courses.value.map((c) => ({
      ...c,
      isFavorite: favoriteCourseIds.value.includes(c.id),
    }))
  })

  const publishedCoursesWithFavorites = computed(() => {
    return coursesWithFavorites.value.filter((c) => c.status === 'ON_SHELF')
  })

  /**
 * 將 YouTube URL 轉換成 embed 格式
 * - https://www.youtube.com/watch?v=VIDEO_ID → https://www.youtube.com/embed/VIDEO_ID
 * - https://youtu.be/VIDEO_ID → https://www.youtube.com/embed/VIDEO_ID
 * - 已經是 embed 格式則不變
 */
  const getEmbedUrl = (url) => {
    if (!url) return null

    // 已經是 embed 格式
    if (url.includes('/embed/')) return url

    // youtube.com/watch?v=VIDEO_ID
    const watchMatch = url.match(/youtube\.com\/watch\?v=([^&]+)/)
    if (watchMatch) {
      return `https://www.youtube.com/embed/${watchMatch[1]}`
    }

    // youtu.be/VIDEO_ID
    const shortMatch = url.match(/youtu\.be\/([^?]+)/)
    if (shortMatch) {
      return `https://www.youtube.com/embed/${shortMatch[1]}`
    }

    // 其他格式 (Vimeo 等) 直接返回
    return url
  }

  return {
    isLoggedIn,
    userType,
    user,
    token,
    courses,
    coursesLoading,
    cartItems,
    favoriteCourseIds,
    orders,
    availableCoupons,
    appliedCoupon,
    studentProgress,
    instructorCourses,
    instructorCoursesLoading,
    login,
    logout,
    initAuth,
    fetchCourses,
    fetchInstructorCourses,
    getCourse,
    fetchCart,
    register,
    toggleFavorite,
    addToCart,
    removeFromCart,
    clearCart,
    applyCoupon,
    removeCoupon,

    createCourse,
    updateCourse,
    coursesWithFavorites,
    toggleChapterCompletion,
    setCompletedChapters,
    toggleCourseStatus,
    publishedCoursesWithFavorites,
    addRating,
    getReviews,
    fetchFavorites,
    getEmbedUrl
  }
})
