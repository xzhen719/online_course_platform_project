<script setup>
import { computed, onMounted } from 'vue'
import { useMainStore } from '../../stores'
import CourseCard from '../CourseCard.vue'

const store = useMainStore()
const favorites = computed(() => store.coursesWithFavorites.filter((c) => c.isFavorite))

// 載入收藏列表
onMounted(() => {
  store.fetchFavorites()
})
</script>

<template>
  <div class="card p-4 bg-light mb-4">
    <h3 class="mb-0">我的最愛({{ favorites.length }})</h3>
  </div>

  <div v-if="favorites.length > 0" class="row g-4">
    <div v-for="course in favorites" :key="course.id" class="col-md-6">
      <CourseCard :course="course" />
    </div>
  </div>

  <div v-else class="text-center py-5 card bg-light border-dashed">
    <h4 class="text-dark">還沒有喜歡的課程嗎?</h4>
    <router-link to="/courses" class="btn w-30 btn-success mt-2 mx-auto">快去逛逛吧!</router-link>
  </div>
</template>
