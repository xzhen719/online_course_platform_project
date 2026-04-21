<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMainStore } from '../stores'
import { Eye, EyeOff } from 'lucide-vue-next'

const store = useMainStore()
const router = useRouter()
const email = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const errorMsg = ref('')

/**
 * handleLogin - 處理登入
 * 
 * 流程:
 * 1. 顯示loading狀態
 * 2. 呼叫store.login() -> 內部會呼叫API
 * 3. 成功: 根據role導向不同頁面
 * 4. 失敗: 顯示錯誤訊息
 */
const handleLogin = async () => {
  // 清除之前的錯誤
  errorMsg.value = ''
  loading.value = true
  
  try {
    // 呼叫store的login (內部會呼叫API)
    const response = await store.login(email.value, password.value)
    
    // 根據role導向不同頁面
    // response.role = 'STUDENT' or 'INSTRUCTOR'
    if (response.role === 'STUDENT') {
      router.push('/student')
    } else if (response.role === 'INSTRUCTOR') {
      router.push('/instructor')
    } else {
      router.push('/')
    }
  } catch (error) {
    // 處理錯誤
    // error.response?.data 包含後端回傳的錯誤訊息
    errorMsg.value = error.response?.data?.message || '登入失敗，請確認帳號密碼'
    console.error('Login error:', error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="container py-5 d-flex justify-content-center">
    <div class="card p-4 col-md-5">
      <h2 class="text-center mb-4">登入</h2>
      
      <!-- 錯誤訊息 -->
      <div v-if="errorMsg" class="alert alert-danger">
        {{ errorMsg }}
      </div>
      
      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label class="fw-bold">EMAIL</label>
          <input 
            type="email" 
            class="form-control" 
            placeholder="信箱" 
            v-model="email" 
            required 
            :disabled="loading"
          />
        </div>
        <div class="mb-4">
          <label class="fw-bold">密碼</label>
          <div class="position-relative">
            <input
              :type="showPassword ? 'text' : 'password'"
              class="form-control"
              placeholder="密碼"
              v-model="password"
              required
              :disabled="loading"
            />
            <span
                class="position-absolute top-50 end-0 translate-middle-y me-3 text-secondary"
                style="cursor: pointer"
                @click="showPassword = !showPassword"
              >
                <component :is="showPassword ? EyeOff : Eye" :size="18" />
              </span>
          </div>
        </div>
        <button 
          class="btn btn-success w-100 btn-lg mb-3" 
          :disabled="loading"
        >
          {{ loading ? '登入' : '登入' }}
        </button>
        <div class="text-center">
          <router-link to="/register">創建帳號</router-link> |
          <router-link to="/forgot-password">忘記密碼</router-link>
        </div>
      </form>
    </div>
  </div>
</template>
