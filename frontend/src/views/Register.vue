<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMainStore } from '../stores'

const store = useMainStore()
const router = useRouter()
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const type = ref('student')
const error = ref('')


const handleRegister = async () => {
  error.value = ''
  
  // 驗證
  if (!email.value || !password.value) {
    error.value = '請填寫所有欄位'
    return
  }
  
  if (password.value !== confirmPassword.value) {
    error.value = '密碼不一致'
    return
  }
  
  if (password.value.length < 6) {
    error.value = '密碼至少需要6個字元'
    return
  }
  
  
  try {
    await store.register(email.value, password.value, type.value)
    router.push(type.value === 'student' ? '/student' : '/instructor')
  } catch (err) {
    console.error('Registration failed:', err)
    error.value = err.response?.data?.message || '註冊失敗，請稍後再試'
  } 
}
</script>

<template>
  <div class="container py-5 d-flex justify-content-center">
    <div class="card p-4 col-md-5">
      <h2 class="text-center mb-4">註冊</h2>
      
      <div v-if="error" class="alert alert-danger">{{ error }}</div>
      
      <div class="btn-group w-100 mb-4">
        <button
          class="btn"
          :class="type === 'student' ? 'btn-dark' : 'btn-outline-dark'"
          @click="type = 'student'"
        >
          學生
        </button>
        <button
          class="btn"
          :class="type === 'instructor' ? 'btn-dark' : 'btn-outline-dark'"
          @click="type = 'instructor'"
        >
          講師
        </button>
      </div>
      
      <form @submit.prevent="handleRegister">
        <div class="mb-3">
          <label class="fw-bold">EMAIL</label>
          <input type="email" class="form-control" placeholder="信箱" v-model="email" required />
        </div>
        <div class="mb-3">
          <label class="fw-bold">密碼</label>
          <input
            type="password"
            class="form-control"
            placeholder="至少6個字元"
            v-model="password"
            required
          />
        </div>
        <div class="mb-4">
          <label class="fw-bold">確認密碼</label>
          <input
            type="password"
            class="form-control"
            placeholder="再次輸入密碼"
            v-model="confirmPassword"
            required
          />
        </div>
        <button class="btn btn-success w-100 btn-lg mb-3">
          註冊
        </button>
        <div class="text-center">已經有帳號了?<router-link to="/login">登入</router-link></div>
      </form>
    </div>
  </div>
</template>
