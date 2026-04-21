<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMainStore } from '../stores'
import { ShoppingCart, Search, Grid } from 'lucide-vue-next'
import { getAllCategories } from '../api/categories'

const store = useMainStore()
const router = useRouter()
const searchQuery = ref('')
const isUserMenuOpen = ref(false)
const categories = ref([])
const loading = ref(true) 

const toggleUserMenu = () => {
  isUserMenuOpen.value = !isUserMenuOpen.value
}

// Close menu when clicking outside (using blur)
const closeUserMenu = () => {
  // Small delay to allow click event on menu items to fire before closing
  setTimeout(() => {
    isUserMenuOpen.value = false
  }, 200)
}

onMounted(async () => {
  try {
    const data = await getAllCategories()
    // 加入 "All" 選項，並提取分類名稱
    categories.value = ['All', ...data.map(cat => cat.name)]
  } catch (error) {
    console.error('Failed to load categories:', error)
    // 備用: 使用預設分類
    categories.value = ['All']
  } finally {
    loading.value = false
  }
})

const handleSearch = () => {
  if (searchQuery.value) router.push({ name: 'courses', query: { q: searchQuery.value } })
}

const handleLogout = () => {
  store.logout()
  router.push('/')
}
</script>

<template>
  <nav class="navbar navbar-expand-lg sticky-top">
    <div class="container-fluid px-4">
      <router-link to="/" class="navbar-brand d-flex align-items-center gap-2 pt-2">
        <img src="/logo/iKnowVation.png" alt="KnowVation" style="height: 28px" />
      </router-link>

      <div class="d-flex gap-2 order-lg-3 align-items-center">
        <template v-if="store.isLoggedIn">
          <!-- User Dropdown -->
          <div class="dropdown">
            <button
              class="btn btn-outline-dark d-flex align-items-center gap-2 dropdown-toggle"
              type="button"
              @click="toggleUserMenu"
              @blur="closeUserMenu"
              :aria-expanded="isUserMenuOpen"
            >
              <div
                class="rounded-circle bg-secondary d-flex align-items-center justify-content-center overflow-hidden"
                style="width: 24px; height: 24px"
              >
                <img
                  v-if="store.user.avatar"
                  :src="`/course-images/${store.user.avatar}`"
                  class="w-100 h-100 object-fit-cover"
                />
                <span v-else class="text-white small fw-bold">{{ (store.user.name || 'U')[0] }}</span>
              </div>
              <span class="d-none d-sm-inline small fw-bold">{{ store.user.name || 'User' }}</span>
            </button>
            <ul
              class="dropdown-menu dropdown-menu-end shadow-sm border-dark mt-2"
              :class="{ show: isUserMenuOpen }"
            >
              <!-- Student Menu -->
              <template v-if="store.userType === 'student'">
                <li>
                  <router-link :to="{ name: 'student-courses' }" class="dropdown-item"
                    >我的學習</router-link
                  >
                </li>
                <li>
                  <router-link :to="{ name: 'student-favorites' }" class="dropdown-item"
                    >我的最愛</router-link
                  >
                </li>
                <li>
                  <router-link :to="{ name: 'student-orders' }" class="dropdown-item"
                    >訂單歷史</router-link
                  >
                </li>
                <li>
                  <router-link :to="{ name: 'student-coupons' }" class="dropdown-item"
                    >我的優惠券</router-link
                  >
                </li>
                <li><hr class="dropdown-divider" /></li>
                <li>
                  <router-link :to="{ name: 'student-profile' }" class="dropdown-item"
                    >個人資訊</router-link
                  >
                </li>
              </template>

              <!-- Instructor Menu -->
              <template v-if="store.userType === 'instructor'">
                <li>
                  <router-link :to="{ name: 'instructor-courses' }" class="dropdown-item"
                    >課程管理</router-link
                  >
                </li>
                <li>
                  <router-link :to="{ name: 'instructor-earnings' }" class="dropdown-item"
                    >銷售數據</router-link
                  >
                </li>
                <li><hr class="dropdown-divider" /></li>
                <li>
                  <router-link :to="{ name: 'instructor-profile' }" class="dropdown-item"
                    >個人資訊</router-link
                  >
                </li>
              </template>

              <li><hr class="dropdown-divider" /></li>
              <li>
                <button @click="handleLogout" class="dropdown-item text-danger fw-bold">
                  登出
                </button>
              </li>
            </ul>
          </div>

          <!-- Cart (Only for students) -->
          <router-link 
            v-if="store.userType === 'student'"
            to="/cart" 
            class="btn btn-sm btn-primary position-relative ms-1"
          >
            <ShoppingCart :size="16" />
            <span
              v-if="store.cartItems.length"
              class="position-absolute top-0 start-100 translate-middle badge bg-danger text-white rounded-pill"
            >
              {{ store.cartItems.length }}
            </span>
          </router-link>
        </template>
        <template v-else>
          <router-link to="/login" class="btn btn-sm btn-outline-dark">登入</router-link>
          <router-link to="/register" class="btn btn-sm btn-success">註冊</router-link>
        </template>
      </div>

      <div class="collapse navbar-collapse mt-3 mt-lg-0" id="navbarNav">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item dropdown hover-dropdown">
            <a
              href="#"
              class="nav-link fw-bold d-flex align-items-center gap-1 text-black pt-3"
              role="button"
            >
              <Grid :size="20" />
              <span style="font-size: 14px">課程分類 </span>
            </a>
            <ul class="dropdown-menu shadow-sm border-dark">
              <li v-for="cat in categories" :key="cat">
                <router-link 
                  :to="cat === 'All' ? '/courses' : { path: '/courses', query: { cat: cat } }" 
                  class="dropdown-item"
                >{{ cat }}</router-link>
              </li>
            </ul>
          </li>
        </ul>

        <form class="d-flex me-3" @submit.prevent="handleSearch">
          <div class="input-group" style="min-width: 400px">
            <input
              type="text"
              class="form-control border-dark"
              placeholder="搜尋任何內容..."
              v-model="searchQuery"
            />
            <button class="btn btn-dark" type="submit"><Search :size="16" /></button>
          </div>
        </form>
      </div>

      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarNav"
      >
        <span class="navbar-toggler-icon"></span>
      </button>
    </div>
  </nav>
</template>

<style scoped>
.hover-dropdown:hover .dropdown-menu {
  display: block;
  margin-top: 0;
}
</style>
