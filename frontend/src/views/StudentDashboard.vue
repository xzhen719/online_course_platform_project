<script setup>
import { useMainStore } from '../stores'
import { BookOpen, Heart, ShoppingBag, User, Ticket } from 'lucide-vue-next'

const store = useMainStore()

const menuItems = [
  { id: 'courses', label: '我的課程', icon: BookOpen, routeName: 'student-courses' },
  { id: 'favorites', label: '我的最愛', icon: Heart, routeName: 'student-favorites' },
  { id: 'orders', label: '訂單歷史', icon: ShoppingBag, routeName: 'student-orders' },
  { id: 'coupons', label: '我的優惠券', icon: Ticket, routeName: 'student-coupons' },
  { id: 'profile', label: '個人檔案', icon: User, routeName: 'student-profile' },
]
</script>

<template>
  <div class="container py-5">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-lg-3 mb-4">
        <div class="card p-4 text-center sticky-top" style="top: 100px; z-index: 1">
          <div
            class="bg-warning border border-dark rounded-circle mx-auto d-flex align-items-center justify-content-center mb-3"
            style="width: 80px; height: 80px; font-size: 2rem"
          >
            {{ store.user.name ? store.user.name[0]?.toUpperCase() : '?' }}
          </div>
          <h5 class="fw-bold text-truncate">{{ store.user.name }}</h5>
          <small class="text-muted mb-4 d-block">{{ store.user.email }}</small>

          <div class="list-group list-group-flush text-start">
            <router-link
              v-for="item in menuItems"
              :key="item.id"
              :to="{ name: item.routeName }"
              class="list-group-item list-group-item-action d-flex align-items-center gap-2 py-3 fw-bold  "
              active-class="bg-dark text-white"
            >
              <component :is="item.icon" :size="18" /> {{ item.label.toUpperCase() }}
            </router-link>
          </div>
        </div>
      </div>

      <!-- Content Area -->
      <div class="col-lg-9">
        <router-view />
      </div>
    </div>
  </div>
</template>
