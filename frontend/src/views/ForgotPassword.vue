<script setup>
import { ref } from 'vue'
import { forgotPassword } from '../api/auth'

const email = ref('')
const sent = ref(false)
const loading = ref(false)
const error = ref('')

const handleReset = async () => {
  if (!email.value) return
  
  loading.value = true
  error.value = ''
  
  try {
    await forgotPassword(email.value)
    sent.value = true
  } catch (err) {
    console.error('Failed to send reset email:', err)
    error.value = err.response?.data || '發送失敗，請稍後再試'
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
    <div class="card p-5 col-md-5 text-center shadow-lg">
      <div v-if="!sent">
        <h2 class="mb-2">重設密碼</h2>
        <p class="text-muted mb-4">請輸入您註冊時使用的信箱</p>
        <form @submit.prevent="handleReset">
          <div class="mb-3 text-start">
            <label class="fw-bold mb-1">EMAIL</label>
            <input
              type="email"
              class="form-control"
              v-model="email"
              required
              placeholder="USER@XXXXXX.com"
              :disabled="loading"
            />
          </div>
          <div v-if="error" class="alert alert-danger py-2 mb-3 small">{{ error }}</div>
          <button class="btn btn-primary w-100 btn-lg mb-3" :disabled="loading">
            {{ loading ? '發送中...' : '送出' }}
          </button>
        </form>
      </div>

      <div v-else>
        <div class="alert alert-success border-dark rounded-0.5 fw-bold">
          密碼重設信已寄至 <strong>{{ email }}</strong>!
        </div>
        <p class="text-muted small">請檢查您的信箱，點擊信中的連結重設密碼</p>
      </div>

      <router-link to="/login" class="btn btn-outline-dark w-100">回到登入頁</router-link>
    </div>
  </div>
</template>
