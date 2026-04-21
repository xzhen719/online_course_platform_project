<script setup>
import { useMainStore } from '../../stores'
import { Package, Eye } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { getAllOrders } from '../../api/orders'
import { onMounted, ref } from 'vue'

const store = useMainStore()
const router = useRouter()
const loading = ref(true)

const goToPayment = (orderId) => {
  router.push({ name: 'checkout', query: { orderId } })
}

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

onMounted(async () => {
  try {
    const data = await getAllOrders()
    store.orders = data
  } catch (error) {
    console.error('Failed to fetch orders:', error)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="card p-4 bg-light mb-4">
    <h3 class="mb-0">訂單歷史</h3>
  </div>

  <div v-if="loading" class="text-center py-5">
    <div class="spinner-border text-primary"></div>
    <p class="mt-3 text-muted">載入中...</p>
  </div>

  <div v-else-if="store.orders.length > 0" class="card">
    <div class="table-responsive">
      <table class="table table-hover mb-0 align-middle small">
        <thead class="table-light">
          <tr>
            <th class="text-nowrap">訂單編號</th>
            <th class="text-nowrap">建立時間</th>
            <th class="text-nowrap text-center">項目數</th>
            <th class="text-nowrap text-end">總金額</th>
            <th class="text-nowrap text-center">折扣</th>
            <th class="text-nowrap text-end">實付金額</th>
            <th class="text-nowrap text-center">狀態</th>
            <th class="text-nowrap text-center"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in store.orders" :key="order.id">
            <td>
              <span class="font-monospace">{{ order.merchantTradeNo }}</span>
            </td>
            <td>{{ formatDate(order.createdAt) }}</td>
            <td class="text-center">{{ order.items?.length || 0 }}</td>
            <td class="text-end">${{ order.totalAmount }}</td>
            <td class="text-center text-success">
              <span v-if="order.discountAmount > 0">-${{ order.discountAmount }}</span>
              <span v-else class="text-muted">—</span>
            </td>
            <td class="text-center fw-bold">${{ order.finalAmount }}</td>
            <td class="text-center">
              <span class="badge bg-white text-dark border">
                {{ getStatusLabel(order.status) }}
              </span>
            </td>
            <td class="text-end">
              <div class="d-flex gap-2 justify-content-end">
                <button
                  v-if="order.status === 'PENDING'"
                  @click="goToPayment(order.id)"
                  class="btn btn-primary text-white border-0 text-nowrap"
                  style="padding: 0.4em 0.85em; font-size: 0.85rem; cursor: pointer;"
                >
                  結帳
                </button>
                <button
                  @click="router.push(`/order/${order.id}`)"
                  class="btn btn-outline-dark text-nowrap"
                  style="padding: 0.4em 0.85em; font-size: 0.85rem; cursor: pointer;"
                >
                  <Eye :size="14" class="me-1" /> 詳情
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <div v-else class="text-center py-5 card align-items-center">
    <h4 class="ms-1">還沒有訂單呦~</h4>
    <router-link to="/courses" class="btn btn-success mt-2 mx-auto" style="width: 200px">來去逛逛</router-link>
  </div>
</template>
