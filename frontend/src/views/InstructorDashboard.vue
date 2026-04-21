<script setup>
import { useMainStore } from '../stores'
import { DollarSign, BookOpen, User } from 'lucide-vue-next'

const store = useMainStore()

const menuItems = [
  { id: 'courses', label: '課程管理', icon: BookOpen, routeName: 'instructor-courses' },
  { id: 'earnings', label: '銷售數據', icon: DollarSign, routeName: 'instructor-earnings' },
  { id: 'profile', label: '個人檔案', icon: User, routeName: 'instructor-profile' },
]
</script>

<template>
  <div class="container py-5">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-lg-3 mb-4">
        <div class="card p-4 text-center sticky-top" style="top: 100px; z-index: 1">
          <div
            class="bg-info border border-dark rounded-circle mx-auto d-flex align-items-center justify-content-center mb-3"
            style="width: 80px; height: 80px; font-size: 2rem"
          >
            {{ store.user.name ? store.user.name[0]?.toUpperCase() : '?' }}
          </div>
          <h5 class="fw-bold text-truncate">{{ store.user.name }}</h5>
          <small class="text-muted mb-4 d-block">{{ store.user.email }}</small>

          <div class="list-group list-group-flush text-start ">
            <router-link
              v-for="item in menuItems"
              :key="item.id"
              :to="{ name: item.routeName }"
              class="list-group-item list-group-item-action d-flex align-items-center gap-2 py-3 fw-bold"
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

