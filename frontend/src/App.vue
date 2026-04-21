<script setup>
import { onMounted } from 'vue'
import Navbar from './components/Navbar.vue'
import Footer from './components/Footer.vue'
import { useMainStore } from './stores'

const store = useMainStore()

// 在app載入時:
// 1. 從localStorage恢復登入狀態
// 2. 從API載入課程資料
// 3. 載入購物車 (已登入用戶)
onMounted(async () => {
  store.initAuth()
  await store.fetchCourses()  // 等待課程載入完成
  store.fetchCart() // 只有已登入才會真的載入
})
</script>

<template>
  <div class="d-flex flex-column min-vh-100">
    <Navbar />
    <main class="flex-grow-1">
      <router-view />
    </main>
    <Footer />
  </div>
</template>