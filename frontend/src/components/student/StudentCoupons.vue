<script setup>
import { ref, onMounted } from 'vue'
import { Ticket, Copy, Check } from 'lucide-vue-next'
import { getMyCoupons } from '../../api/coupons'

const coupons = ref([])
const copiedCode = ref(null)

// 從API載入優惠券
onMounted(async () => {
  try {
    const data = await getMyCoupons()
    // 轉換後端格式 -> 前端格式
    // 只顯示未使用的優惠券
    coupons.value = data
      .filter(c => !c.used)
      .map(c => ({
        id: c.id,
        code: c.code,
        discountPercent: c.discountPercent,
        expiryDate: c.expiryDate,
        description: `使用此優惠券可獲得 ${c.discountPercent}% 折扣`
      }))
  } catch (err) {
    console.error('Failed to load coupons:', err)
  }
})

const copyToClipboard = async (code) => {
  try {
    await navigator.clipboard.writeText(code)
    copiedCode.value = code
    setTimeout(() => {
      copiedCode.value = null
    }, 2000)
  } catch (err) {
    console.error('Failed to copy: ', err)
  }
}

// 格式化到期日
const formatDate = (dateStr) => {
  if (!dateStr) return '無期限'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-TW')
}
</script>

<template>
  <div class="card">
    <div class="card-header bg-white py-3">
      <h5 class="mb-0 fw-bold d-flex align-items-center gap-2">
        <Ticket class="text-primary" /> 我的優惠券
      </h5>
    </div>
    <div class="card-body">
      <!-- Empty State -->
      <div v-if="coupons.length === 0" class="text-center py-5 text-muted">
        <Ticket :size="48" class="mb-3 opacity-25" />
        <p>目前沒有可用的優惠券</p>
      </div>

      <!-- Coupons Grid -->
      <div class="row g-3" v-else>
        <div class="col-md-6" v-for="coupon in coupons" :key="coupon.code">
          <div class="card h-100 border-dashed coupon-card">
            <div class="card-body d-flex flex-column justify-content-between">
              <div>
                <div class="d-flex justify-content-between align-items-start mb-2">
                  <span class="badge bg-primary bg-opacity-10 text-black px-3 py-2 rounded-pill">
                    {{ coupon.discountPercent }}% OFF
                  </span>
                  <small class="text-muted">到期: {{ formatDate(coupon.expiryDate) }}</small>
                </div>
                <h5 class="card-title fw-bold">{{ coupon.code }}</h5>
                <p class="card-text text-muted small">{{ coupon.description }}</p>
              </div>

              <div class="mt-3 pt-3 border-top">
                <button
                  @click="copyToClipboard(coupon.code)"
                  class="btn btn-outline-primary btn-sm w-100 d-flex align-items-center justify-content-center gap-2"
                >
                  <span v-if="copiedCode === coupon.code"><Check :size="16" /> 已複製!</span>
                  <span v-else><Copy :size="16" /> 複製優惠碼</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.border-dashed {
  border: 2px dashed #dee2e6;
  transition: all 0.2s;
}
.coupon-card:hover {
  border-color: var(--bs-primary);
  background-color: #f8f9fa;
}
</style>
