<script setup>
import { computed, ref, onMounted } from 'vue'
import { useMainStore } from '../stores'
import { useRouter } from 'vue-router'
import { Trash2, Ticket } from 'lucide-vue-next'
import ImagePlaceholder from '../components/ImagePlaceholder.vue'


const store = useMainStore()
const router = useRouter()
const couponCode = ref('')
const couponMessage = ref('')
const couponStatus = ref('') // 'success' or 'error'



const subtotal = computed(() => store.cartItems.reduce((sum, item) => sum + item.price, 0))

const discountAmount = computed(() => {
  if (!store.appliedCoupon) 
    return 0
  else{
    return subtotal.value * (store.appliedCoupon.discountPercent/100)
  }
})

const total = computed(() => {
  const t = subtotal.value - discountAmount.value
  return t > 0 ? t : 0
})

const applyCoupon = async () => {
  if (!couponCode.value) return
  const result = await store.applyCoupon(couponCode.value)
  couponMessage.value = result.message
  couponStatus.value = result.success ? 'success' : 'error'

  if (result.success) {
    couponCode.value = ''
    // Clear message after 3 seconds
    setTimeout(() => {
      couponMessage.value = ''
    }, 3000)
  }
}

const removeCoupon = () => {
  store.removeCoupon()
}

const checkout = () => {
  store.isLoggedIn ? router.push('/checkout') : router.push('/login')
}

</script>

<template>
  <div class="container py-5">
    <h2>購物車</h2>
    <div v-if="store.cartItems.length" class="row g-4">
      <div class="col-lg-8">
        <div class="card p-3 mb-3" v-for="item in store.cartItems" :key="item.id">
          <div class="d-flex align-items-center">
            <img
              :src="item.image"
              :alt="item.title"
              height="80px"
              class="me-3 rounded"
              style="width: 120px"
            />
            <div class="flex-grow-1">
              <h5 class="mb-0">{{ item.title }}</h5>
              <small class="text-muted">{{ item.instructor }}</small>
            </div>
            <div class="text-end">
              <div class="fw-bold mb-2">${{ item.price }}</div>
              <button @click="store.removeFromCart(item.cartItemId)" class="btn btn-sm btn-outline-danger">
                <Trash2 :size="16" />
              </button>
            </div>
          </div>
        </div>
      </div>
      <div class="col-lg-4">
        <div class="card p-4 position-sticky" style="top: 100px">
          <h4>訂單明細</h4>

          <!-- Subtotal -->
          <div class="d-flex justify-content-between mb-3">
            <span>訂單金額</span>
            <span>${{ subtotal }}</span>
          </div>

          <!-- Coupon Section -->
          <div class="mb-3">
            <div v-if="!store.appliedCoupon" class="input-group">
              <span class="input-group-text bg-white border-1"
                ><Ticket :size="16"
              /></span>
              <input
                type="text"
                class="form-control  ps-0 border-1"
                placeholder="優惠碼"
                v-model="couponCode"
              />
              <button
                class="btn btn-outline-dark border-1 shadow-none rounded-end"
                type="button"
                @click="applyCoupon"
              >
                兌換
              </button>
            </div>

            <div
              v-if="couponMessage"
              class="small mt-1"
              :class="couponStatus === 'success' ? 'text-success' : 'text-danger'"
            >
              {{ couponMessage }}
            </div>

            <div
              v-if="store.appliedCoupon"
              class="alert alert-success d-flex justify-content-between align-items-center p-2 mt-2 mb-0 small"
            >
              <div>
                <strong>{{ store.appliedCoupon.code }}</strong> 已套用
              </div>
              <button
                @click="removeCoupon"
                class="btn-close btn-close-white small"
                aria-label="Remove"
              ></button>
            </div>
          </div>

          <!-- Discount -->
          <div v-if="store.appliedCoupon" class="d-flex justify-content-between mb-3 text-success">
            <span>折扣</span>
            <span>- ${{ discountAmount }}</span>
          </div>

          <hr v-if="store.appliedCoupon" />

          <!-- Total -->
          <div class="d-flex justify-content-between mb-4 fs-5 fw-bold">
            <span>總金額</span>
            <span>${{ total }}</span>
          </div>

          <button @click="checkout" class="btn btn-primary w-100">結帳</button>
        </div>
      </div>
    </div>
    <div v-else class="text-center py-5">
      <h4>購物車是空的</h4>
      <router-link to="/courses" class="btn btn-secondary mt-3">快去逛逛吧</router-link>
    </div>
  </div>
</template>
