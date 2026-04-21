<script setup>
import { ref, computed, onMounted } from 'vue'
import { useMainStore } from '../stores'
import { useRouter, useRoute } from 'vue-router'
import { createOrder, getAllOrders } from '../api/orders'
import { submitPayment } from '../api/payment'

const store = useMainStore()
const router = useRouter()
const route = useRoute()
const processing = ref(false)
const loading = ref(true)
const errorMsg = ref('')

// 現有的待付款訂單 (如果從訂單列表進來)
const existingOrder = ref(null)

// 判斷是從購物車還是訂單列表進來
const isExistingOrder = computed(() => existingOrder.value!==null)

// 計算訂單項目
const items = computed(() => {
  if (isExistingOrder.value) {
    // 從現有訂單取得項目
    return existingOrder.value.items.map(item => ({
      id: item.id,
      title: item.courseName,
      price: item.priceAtPurchase,
      image: item.courseImage
    }))
  }
  // 從購物車取得項目
  return store.cartItems
})

// 計算小計
const subtotal = computed(() => {
  if (isExistingOrder.value) {
    return existingOrder.value.totalAmount
  }
  return items.value.reduce((sum, i) => sum + i.price, 0)
})

// 計算折扣金額
const discountAmount = computed(() => {
  if (isExistingOrder.value) {
    return existingOrder.value.discountAmount || 0
  }
  if (!store.appliedCoupon) return 0
  if (store.appliedCoupon.type === 'percent') {
    return Math.round(subtotal.value * store.appliedCoupon.discount)
  } else if (store.appliedCoupon.type === 'fixed') {
    if (store.appliedCoupon.minSpend && subtotal.value < store.appliedCoupon.minSpend) return 0
    return store.appliedCoupon.discount
  }
  return 0
})

// 計算總金額
const total = computed(() => {
  if (isExistingOrder.value) {
    return existingOrder.value.finalAmount
  }
  const t = subtotal.value - discountAmount.value
  return t > 0 ? t : 0
})

// 取得coupon code
const couponCode = computed(() => store.appliedCoupon?.code || null)

/**
 * 處理付款流程
 */
const handlePayment = async () => {
  processing.value = true
  errorMsg.value = ''
  
  try {
    let orderId
    
    if (isExistingOrder.value) {
      // 使用現有訂單的 ID
      orderId = existingOrder.value.id
    } else {
      // 從購物車建立新訂單
      const order = await createOrder(couponCode.value)
      console.log('Order created:', order)
      orderId = order.id
      
      // 清空前端購物車狀態 (後端已清空)
      store.clearCart()
    }
    
    // 取得 ECPay 表單 HTML
    const ecpayHtml = await submitPayment(orderId)
    console.log('ECPay form received')
    
    // 注入 ECPay 表單到頁面並手動 submit
    const container = document.createElement('div')
    container.innerHTML = ecpayHtml
    document.body.appendChild(container)
    
    const form = container.querySelector('form')
    if (form) {
      form.submit()
    }
    
  } catch (error) {
    console.error('Payment error:', error)
    errorMsg.value = error.response?.data?.message || '付款失敗，請稍後再試'
    processing.value = false
  }
}

// 初始化
onMounted(async () => {
  if (!store.isLoggedIn) {
    router.push('/login')
    return
  }
  
  // 檢查是否有 orderId query param (從訂單列表進來)
  const orderId = route.query.orderId
  if (orderId) {
    try {
      // 載入訂單資料
      if (store.orders.length === 0) {
        store.orders = await getAllOrders()
      }
      const order = store.orders.find(o => o.id === Number(orderId))
      if (order && order.status === 'PENDING') {
        existingOrder.value = order
      } else {
        errorMsg.value = '找不到此待付款訂單'
      }
    } catch (error) {
      console.error('Failed to load order:', error)
      errorMsg.value = '載入訂單失敗'
    }
  } else {
    // 從購物車進來，檢查購物車是否為空
    if (store.cartItems.length === 0) {
      router.push('/cart')
      return
    }
  }
  
  loading.value = false
})
</script>

<template>
  <div v-if="loading" class="container py-5 text-center">
    <div class="spinner-border text-primary"></div>
    <p class="mt-3 text-muted">載入中...</p>
  </div>

  <div v-else class="container py-5">
    <!-- 錯誤訊息 -->
    <div v-if="errorMsg" class="alert alert-danger mb-4">
      {{ errorMsg }}
    </div>
    
    <div class="row">
      <div class="col-lg-8">
        <!-- 訂單項目預覽 -->
        <div class="card p-4 mb-4" v-if="items.length > 0">
          <h4>訂單項目</h4>
          <div v-for="item in items" :key="item.id" class="d-flex align-items-center gap-3 py-2 border-bottom">
            <img 
              v-if="item.image" 
              :src="item.image" 
              class="rounded" 
              style="width: 60px; height: 40px; object-fit: cover;"
            />
            <span class="flex-grow-1">{{ item.title }}</span>
            <span>${{ item.price }}</span>
          </div>
        </div>

        <div class="card p-4 mb-4">
          <h4>付款方式</h4>
          <p class="text-muted mb-3">點擊確認支付後，將導向綠界金流進行付款</p>
          
          <div class="d-flex gap-3 flex-wrap">
            <div class="border rounded p-3 text-center" style="min-width: 100px;">
              <i class="bi bi-credit-card fs-3 d-block mb-1"></i>
              <small>信用卡</small>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-lg-4">
        <div class="card p-4">
          <h4>訂單明細</h4>

          <div class="d-flex justify-content-between mb-2">
            <span>小計</span>
            <span>${{ subtotal }}</span>
          </div>
          <div v-if="discountAmount > 0" class="d-flex justify-content-between mb-2 text-success">
            <span>折扣 <span v-if="couponCode && !isExistingOrder ">({{ couponCode }})</span></span>
            <span>-${{ discountAmount }}</span>
          </div>
          <hr />
          <div class="h2 text-primary mb-3">${{ total }}</div>

          <button
            @click="handlePayment"
            class="btn btn-success w-100 btn-lg"
            :disabled="processing || items.length === 0"
          >
            <span v-if="processing">
              <span class="spinner-border spinner-border-sm me-2"></span>
              處理中...
            </span>
            <span v-else>確認支付</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
