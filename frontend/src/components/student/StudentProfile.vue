<script setup>
import { ref, onMounted } from 'vue'
import { useMainStore } from '../../stores'
import { Save, Lock, User, Mail, Calendar, Users, FileText } from 'lucide-vue-next'
import { getProfile, updateProfile, changePassword } from '../../api/user'

const store = useMainStore()
const loading = ref(true)
const saving = ref(false)
const changingPassword = ref(false)
const error = ref('')

const form = ref({
  username: '',
  email: '',
  sex: '',
  birthdate: '',
  bio: ''
})

const passwordForm = ref({ current: '', new: '', confirm: '' })

// 載入使用者資料
onMounted(async () => {
  try {
    const data = await getProfile()
    form.value = {
      username: data.username || '',
      email: data.email || '',
      sex: data.sex || '',
      birthdate: data.birthdate || '',
      bio: data.bio || ''
    }
  } catch (err) {
    console.error('Failed to load profile:', err)
    error.value = '無法載入個人資料'
  } finally {
    loading.value = false
  }
})

// 更新個人資料
const updateUserProfile = async () => {
  saving.value = true
  try {
    const response = await updateProfile(form.value)
    
    // 如果 email 有變更，後端會回傳新的 token
    if (response.newToken) {
      localStorage.setItem('token', response.newToken)
      // 更新 localStorage 中的 user 資訊
      const savedUser = JSON.parse(localStorage.getItem('user') || '{}')
      savedUser.email = response.profile.email
      localStorage.setItem('user', JSON.stringify(savedUser))
    }
    
    // 更新 store 中的使用者資訊
    store.user.name = response.profile.username
    store.user.email = response.profile.email
    
    alert('個人資料已更新！')
  } catch (err) {
    console.error('Failed to update profile:', err)
    alert('更新失敗，請稍後再試')
  } finally {
    saving.value = false
  }
}

// 變更密碼
const updatePassword = async () => {
  if (!passwordForm.value.current || !passwordForm.value.new) {
    alert('請填寫完整密碼欄位')
    return
  }
  if (passwordForm.value.new !== passwordForm.value.confirm) {
    alert('兩次輸入的密碼不相同!')
    return
  }

  changingPassword.value = true
  try {
    await changePassword(passwordForm.value.current, passwordForm.value.new)
    alert('密碼已更新！')
    passwordForm.value = { current: '', new: '', confirm: '' }
  } catch (err) {
    console.error('Failed to change password:', err)
    alert(err.response?.data || '密碼更新失敗')
  } finally {
    changingPassword.value = false
  }
}
</script>

<template>
  <div v-if="loading" class="text-center py-5">
    <div class="spinner-border text-primary"></div>
    <p class="mt-3 text-muted">載入中...</p>
  </div>

  <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

  <div v-else class="row g-4">
    <!-- General Info -->
    <div class="col-md-12">
      <div class="card h-100">
        <div class="card-header bg-dark text-white fw-bold">個人檔案設定</div>
        <div class="card-body p-4">
          <div class="row mb-4 align-items-center">
            <div class="col-auto">
              <div
                class="bg-warning border border-dark d-flex align-items-center justify-content-center rounded-circle"
                style="width: 80px; height: 80px; font-size: 2rem"
              >
                {{ form.username ? form.username[0]?.toUpperCase() : '?' }}
              </div>
            </div>
            <div class="col">
              <h5 class="mb-1">{{ form.username }}</h5>
              <span class=" badge text-black small">{{ store.userType === 'student' ? '學生' : '講師' }}</span>
            </div>
          </div>

          <form @submit.prevent="updateUserProfile">
            <div class="mb-3">
              <label class="fw-bold mb-1"><User :size="14" /> 使用者名稱</label>
              <input type="text" class="form-control" v-model="form.username" />
            </div>
            <div class="mb-3">
              <label class="fw-bold mb-1"><Mail :size="14" /> EMAIL</label>
              <input type="email" class="form-control" v-model="form.email" />
            </div>

            <div class="row mb-3">
              <div class="col-md-6">
                <label class="fw-bold mb-1"><Users :size="14" /> 性別</label>
                <select class="form-select" v-model="form.sex">
                  <option value="">請選擇</option>
                  <option value="Male">男</option>
                  <option value="Female">女</option>
                  <option value="Other">其他</option>
                </select>
              </div>
              <div class="col-md-6">
                <label class="fw-bold mb-1"><Calendar :size="14" /> 生日</label>
                <input type="date" class="form-control" v-model="form.birthdate" />
              </div>
            </div>

            <div class="mb-3">
              <label class="fw-bold mb-1"><FileText :size="14" /> 個人簡介</label>
              <textarea class="form-control" rows="3" v-model="form.bio" placeholder="介紹一下自己..."></textarea>
            </div>

            <button type="submit" class="btn btn-primary" :disabled="saving">
              <Save :size="16" /> {{ saving ? '儲存' : '儲存' }}
            </button>
          </form>
        </div>
      </div>
    </div>

    <!-- Security -->
    <div class="col-md-12">
      <div class="card">
        <div class="card-header bg-dark text-white fw-bold">密碼設定</div>
        <div class="card-body p-4">
          <form @submit.prevent="updatePassword">
            <div class="mb-3">
              <label class="fw-bold mb-1">現有密碼</label>
              <input type="password" class="form-control" v-model="passwordForm.current" />
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="fw-bold mb-1">新密碼</label>
                <input type="password" class="form-control" v-model="passwordForm.new" />
              </div>
              <div class="col-md-6 mb-3">
                <label class="fw-bold mb-1">再次輸入新密碼</label>
                <input type="password" class="form-control" v-model="passwordForm.confirm" />
              </div>
            </div>
            <button type="submit" class="btn btn-secondary"><Lock :size="16" /> 確認修改</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
