<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMainStore } from '../stores'
import { Receipt } from 'lucide-vue-next'
import { getAllOrders } from '../api/orders'
import { cancelOrder } from '../api/orders'

const route = useRoute()
const router = useRouter()
const store = useMainStore()
const loading = ref(true)

// 轉換 route.params.id 為數字來比較
const order = computed(() => {
  const orderId = Number(route.params.id)
  return store.orders.find((o) => o.id === orderId)
})

// 格式化狀態顯示
const getStatusLabel = (status) => {
  const statusMap = {
    'PENDING': '待付款',
    'PAID': '已付款',
    'CANCELLED': '已取消',
    'REFUNDED': '已退款'
  }
  return statusMap[status] || status
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '—'
  const date = new Date(dateString)
  return date.toLocaleString('zh-TW')
}

// 載入訂單資料
onMounted(async () => {
  try {
    // 每次都重新載入訂單 (確保資料最新)
    store.orders = await getAllOrders()
  } catch (error) {
    console.error('Failed to load orders:', error)
  } finally {
    loading.value = false
  }
})

// 取消訂單
const handleCancelOrder = async () => {
  try {
    await cancelOrder(order.value.id)
    router.back()
  } catch (error) {
    console.error('Failed to cancel order:', error)
    alert('取消訂單失敗')
  }
}
</script>

<template>
  <div v-if="loading" class="container py-5 text-center">
    <div class="spinner-border text-primary"></div>
    <p class="mt-3 text-muted">載入中...</p>
  </div>

  <div v-else-if="order" class="container py-5">
    <div class="row justify-content-center">
      <div class="col-lg-8">
        <div class="card">
          <!-- Header -->
          <div class="card-body">
            <div class="d-flex align-items-center gap-3 mb-3">
              <Receipt :size="32" class="text-primary" />
              <div>
                <h4 class="mb-0">訂單詳情</h4>
                <div>{{ order.merchantTradeNo }}</div>
              </div>
            </div>
            <div class="row text-muted small">
              <div class="col-6 col-md-4">
                <div class="fw-bold">建立時間</div>
                <div>{{ formatDate(order.createdAt) }}</div>
              </div>
              <div class="col-6 col-md-4">
                <div class="fw-bold">付款狀態</div>
                <div>{{ getStatusLabel(order.status) }}</div>
              </div>
              <div v-if="order.paymentDate" class="col-6 col-md-4 mt-2 mt-md-0">
                <div class="fw-bold">付款時間</div>
                <div>{{ formatDate(order.paymentDate) }}</div>
              </div>
            </div>
          </div>

          <hr class="my-0" />

          <!-- Order Items -->
          <div class="card-body">
            <h5 class="mb-3">訂單項目</h5>
            <div v-if="order.items && order.items.length > 0">
              <div
                v-for="item in order.items"
                :key="item.id"
                class="d-flex align-items-center gap-3 py-2 border-bottom"
              >
                <img 
                  v-if="item.courseImage" 
                  :src="item.courseImage" 
                  class="rounded" 
                  style="width: 60px; height: 40px; object-fit: cover;"
                />
                <div class="flex-grow-1">
                  <div>{{ item.courseName }}</div>
                </div>
                <div class="text-end">${{ item.priceAtPurchase }}</div>
              </div>
            </div>
            <div v-else class="text-muted">
              無訂單項目
            </div>
          </div>

          <hr class="my-0" />

          <!-- Price Summary -->
          <div class="card-body">
            <h5 class="mb-3">價格明細</h5>
            <div class="d-flex justify-content-between mb-2">
              <span>小計</span>
              <span>${{ order.totalAmount }}</span>
            </div>
            <div v-if="order.discountAmount > 0" class="d-flex justify-content-between mb-2 text-success">
              <span>折扣</span>
              <span>-${{ order.discountAmount }}</span>
            </div>
            <hr />
            <div class="d-flex justify-content-between h5 mb-0">
              <span>總計</span>
              <span class="text-primary">${{ order.finalAmount }}</span>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="mt-4 d-flex gap-3 justify-content-center">
          <button v-if="order.status === 'PENDING'" class="btn btn-danger" @click="handleCancelOrder">取消訂單</button>
          <router-link :to="{ name: 'student-orders' }" class="btn btn-outline-dark">
            返回訂單列表
          </router-link>
          <router-link :to="{ name: 'student-courses' }" class="btn btn-primary">
            前往我的課程
          </router-link>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="container py-5 text-center">
    <h4>找不到此訂單</h4>
    <router-link to="/student/orders" class="btn btn-primary mt-3">返回訂單列表</router-link>
  </div>
</template>
