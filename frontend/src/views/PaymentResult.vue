<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { CheckCircle, XCircle, Loader2 } from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()
const status = ref('loading') // 'loading', 'success', 'error'

onMounted(() => {
  // ECPay 會帶參數回來
  // RtnCode=1 表示成功，其他值表示失敗
  setTimeout(() => {
    const rtnCode = route.query.RtnCode
    
    // 如果有 RtnCode 參數，根據它判斷
    if (rtnCode !== undefined) {
      status.value = rtnCode === '1' ? 'success' : 'error'
    } else {
      // 沒有參數時，假設成功（後端 callback 已處理）
      status.value = 'success'
    }
  }, 1500)
})
</script>

<template>
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card p-5 text-center">
          <!-- Loading -->
          <div v-if="status === 'loading'">
            <div class="spinner-border text-primary mb-4" style="width: 4rem; height: 4rem;"></div>
            <h3>處理中...</h3>
            <p class="text-muted">正在確認您的付款狀態</p>
          </div>
          
          <!-- Success -->
          <div v-else-if="status === 'success'">
            <CheckCircle :size="80" class="text-success mb-4" />
            <h2 class="text-success mb-3">付款成功！</h2>
            <p class="text-muted mb-4">
              感謝您的購買！您現在可以開始學習課程了。
            </p>
            <div class="d-grid gap-2">
              <router-link to="/student/courses" class="btn btn-success btn-lg">
                開始學習
              </router-link>
              <router-link to="/" class="btn btn-outline-secondary">
                回到首頁
              </router-link>
            </div>
          </div>
          
          <!-- Error -->
          <div v-else>
            <XCircle :size="80" class="text-danger mb-4" />
            <h2 class="text-danger mb-3">付款失敗</h2>
            <p class="text-muted mb-4">
              很抱歉，付款過程發生問題。請稍後再試或聯繫客服。
            </p>
            <div class="d-grid gap-2">
              <router-link to="/cart" class="btn btn-primary btn-lg">
                返回購物車
              </router-link>
              <router-link to="/" class="btn btn-outline-secondary">
                回到首頁
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
