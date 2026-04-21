import { createRouter, createWebHistory } from 'vue-router'
// Views
import HomeView from '../views/HomeView.vue'
import CourseList from '../views/CourseList.vue'
import CourseDetail from '../views/CourseDetail.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import ForgotPassword from '../views/ForgotPassword.vue'
import ResetPassword from '../views/ResetPassword.vue'
import Cart from '../views/Cart.vue'
import Checkout from '../views/Checkout.vue'
import OrderDetail from '../views/OrderDetail.vue'
import PaymentResult from '../views/PaymentResult.vue'
import StudentDashboard from '../views/StudentDashboard.vue'
import CourseLearning from '../views/CourseLearning.vue'
import InstructorDashboard from '../views/InstructorDashboard.vue'
import CourseEditor from '../views/CourseEditor.vue'
import CourseChapters from '../views/CourseChapters.vue'

// Student Components
import StudentMyCourses from '../components/student/StudentMyCourses.vue'
import StudentFavorites from '../components/student/StudentFavorites.vue'
import StudentOrders from '../components/student/StudentOrders.vue'
import StudentCoupons from '../components/student/StudentCoupons.vue'
import StudentProfile from '../components/student/StudentProfile.vue'

// Instructor Components
import InstructorCourses from '../components/instructor/InstructorCourses.vue'
import InstructorEarnings from '../components/instructor/InstructorEarnings.vue'
import InstructorProfile from '../components/instructor/InstructorProfile.vue'

const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/courses', name: 'courses', component: CourseList },
  { path: '/course/:id', name: 'course-detail', component: CourseDetail },
  { path: '/login', name: 'login', component: Login },
  { path: '/register', name: 'register', component: Register },
  { path: '/forgot-password', name: 'forgot-password', component: ForgotPassword },
  { path: '/reset-password', name: 'reset-password', component: ResetPassword },
  { path: '/cart', name: 'cart', component: Cart },
  { path: '/checkout', name: 'checkout', component: Checkout },
  { path: '/order/:id', name: 'order-detail', component: OrderDetail },
  { path: '/payment/result', name: 'payment-result', component: PaymentResult },
  {
    path: '/student',
    component: StudentDashboard,
    children: [
      { path: '', redirect: 'student/courses' },
      { path: 'courses', name: 'student-courses', component: StudentMyCourses },
      { path: 'favorites', name: 'student-favorites', component: StudentFavorites },
      { path: 'orders', name: 'student-orders', component: StudentOrders },
      { path: 'coupons', name: 'student-coupons', component: StudentCoupons },
      { path: 'profile', name: 'student-profile', component: StudentProfile },
    ],
  },
  { path: '/learning/:id', name: 'course-learning', component: CourseLearning },
  {
    path: '/instructor',
    component: InstructorDashboard,
    children: [
      { path: '', redirect: '/instructor/courses' },
      { path: 'courses', name: 'instructor-courses', component: InstructorCourses },
      { path: 'earnings', name: 'instructor-earnings', component: InstructorEarnings },
      { path: 'profile', name: 'instructor-profile', component: InstructorProfile },
    ],
  },
  { path: '/instructor/course/new', name: 'course-create', component: CourseEditor },
  { path: '/instructor/course/new/chapters', name: 'course-create-chapters', component: CourseChapters },
  { path: '/instructor/course/edit/:id', name: 'course-edit', component: CourseEditor },
  {
    path: '/instructor/course/edit/:id/chapters',
    name: 'course-chapters',
    component: CourseChapters,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

export default router
