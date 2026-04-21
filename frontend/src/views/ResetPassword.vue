<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Eye, EyeOff } from 'lucide-vue-next'
import { resetPassword } from '../api/auth'

const router = useRouter()
const route = useRoute()

const token = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const error = ref('')
const success = ref(false)
const loading = ref(false)

// Get token from URL query param
onMounted(() => {
  token.value = route.query.token || ''
  if (!token.value) {
    error.value = '無效的重設連結，請重新申請'
  }
})

const handleReset = async () => {
  error.value = ''
  
  if (!token.value) {
    error.value = '無效的重設連結'
    return
  }
  
  if (password.value !== confirmPassword.value) {
    error.value = '兩次輸入的密碼不相同'
    return
  }
  
  if (password.value.length < 6) {
    error.value = '密碼至少需要 6 個字元'
    return
  }

  loading.value = true
  
  try {
    await resetPassword(token.value, password.value)
    success.value = true
  } catch (err) {
    console.error('Failed to reset password:', err)
    error.value = err.response?.data || '重設失敗，連結可能已過期'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div
    class="container py-5 d-flex justify-content-center align-items-center"
    style="min-height: 80vh"
  >
    <div class="card p-5 col-md-5 shadow-lg">
      <div v-if="!success">
        <h2 class="mb-2 text-center">重新設定密碼</h2>

        <form @submit.prevent="handleReset">
          <div class="mb-3">
            <label class="fw-bold mb-1">新密碼</label>
            <div class="position-relative">
              <input
                :type="showPassword ? 'text' : 'password'"
                class="form-control pe-5"
                v-model="password"
                required
                :disabled="loading || !token"
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

          <div class="mb-4">
            <label class="fw-bold mb-1">再次輸入新密碼</label>
            <input 
              type="password" 
              class="form-control" 
              v-model="confirmPassword" 
              required 
              :disabled="loading || !token"
            />
          </div>

          <div v-if="error" class="alert alert-danger py-2 mb-3 small">{{ error }}</div>

          <button 
            class="btn btn-primary w-100 btn-lg mb-3" 
            :disabled="loading || !token"
          >
            {{ loading ? '處理中...' : '確認送出' }}
          </button>
        </form>
      </div>

      <div v-else class="text-center">
        <div class="alert alert-success border-dark fw-bold mb-4">已重設為新密碼!</div>
        <router-link to="/login" class="btn btn-primary w-100">前往登入</router-link>
      </div>
    </div>
  </div>
</template>
