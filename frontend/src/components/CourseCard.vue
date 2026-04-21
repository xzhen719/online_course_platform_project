<script setup>
import { useRouter } from 'vue-router'
import { useMainStore } from '../stores'
import { Star, Clock, Users, Heart, ShoppingCart } from 'lucide-vue-next'
import ImagePlaceholder from './ImagePlaceholder.vue'

const props = defineProps({ course: Object })
const store = useMainStore()
const router = useRouter()

const toggleFav = (e) => {
  e.stopPropagation()
  store.toggleFavorite(props.course.id)
}

</script>

<template>
  <div
    class="card h-100 position-relative"
    @click="router.push(`/course/${course.id}`)"
    style="cursor: pointer"
  >
    <div
      class="card-img-top overflow-hidden border-bottom border-3 border-dark"
      style="height: 180px"
      v-if="course.image"
    >
      <img
        :src="course.image"
        :alt="course.title"
        class="w-100 h-100 object-fit-cover"
      />
    </div>
    <ImagePlaceholder v-else :text="course.category" height="180px" />

    <div class="card-body d-flex flex-column">
      <div class="small text-primary fw-bold mb-1">{{ course.category }}</div>
      <h5 class="card-title text-truncate py-1" style="line-height: 1.5">{{ course.title }}</h5>
      <p class="card-text small text-muted mb-2">by {{ course.instructor }}</p>

      <div class="d-flex justify-content-between small text-muted mb-3">
        <span><Clock :size="14" /> {{ course.totalTime }} min</span>
        <span><Users :size="14" /> {{ course.students.toLocaleString() }}</span>
      </div>

      <div class="d-flex align-items-center gap-1 mb-2">
        <Star :size="16" fill="#ffe66d" class="text-warning" />
        <span class="fw-bold">{{ course.rating }}</span>
        <span class="text-muted small">({{ course.reviewCount }})</span>
      </div>

      <div class="mt-auto d-flex align-items-center justify-content-between">
        <div>
          <span class="h5 fw-bold me-2">${{ course.price }}</span>
        </div>

        <div class="d-flex gap-2">
          <button @click="toggleFav" class="btn btn-sm btn-outline-dark">
            <Heart
              :size="16"
              :fill="course.isFavorite ? '#ff6b6b' : 'none'"
              :class="{ 'text-danger': course.isFavorite }"
            />
          </button>
          <button class="btn btn-sm btn-outline-dark" @click.stop="store.addToCart(course.id)">
            <ShoppingCart :size="16" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
