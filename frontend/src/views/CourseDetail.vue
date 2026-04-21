<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useMainStore } from '../stores'
import ImagePlaceholder from '../components/ImagePlaceholder.vue'
import { Star, Clock, Users, Heart, ShoppingCart } from 'lucide-vue-next'
import { getCourseSyllabus } from '../api/chapters'

const route = useRoute()
const store = useMainStore()
const courseId = computed(() => parseInt(route.params.id))
const course = computed(() => store.coursesWithFavorites.find((c) => c.id === courseId.value))
const chapters = ref([])

const formatDate = (dateString) => {
  if (!dateString) return '—'
  const date = new Date(dateString)
  return date.toLocaleString('zh-TW')
}


// 載入課程資料、章節大綱、評論
onMounted(async () => {
  await store.getCourse(courseId.value)
  await store.getReviews(courseId.value)
  
  // 載入章節大綱 (公開API，不需要已購買)
  try {
    const data = await getCourseSyllabus(courseId.value)
    chapters.value = data.map(ch => ({
      id: ch.id,
      title: ch.title,
      summary: ch.summary,
      description: ch.description,
    }))
  } catch (error) {
    console.error('Failed to load syllabus:', error)
  }
})
</script>

<template>
  <div v-if="course">
    <!-- Header -->
    <div class="bg-light text-black py-5 border-bottom border-4 border-dark">
      <div class="container">
        <div class="row d-flex">
          <div class="col-lg-8">
            <span class="badge bg-primary text-white mb-2">{{ course.category }}</span>
            <h1 class="display-5 fw-bold">{{ course.title }}</h1>
            <p class="lead font-monospace">{{ course.instructor }}</p>
            <div class="d-flex gap-3 mt-3">
              <span><Star class="text-warning" :size="18" /> {{ course.rating }}</span>
              <span><Users :size="18" /> {{ course.students }}</span>
              <span><Clock :size="18" /> {{ course.totalTime }} min</span>
            </div>
          </div>
          <div class="col-lg-4">
    
          <iframe
            v-if="course.introVideoUrl"
            :src="store.getEmbedUrl(course.introVideoUrl)"
            allowfullscreen
            class="w-100 h-100 rounded"
          ></iframe>
          </div>
        </div>
      </div>
    </div>

    <div class="container py-5">
      <div class="row">
        <!-- Content -->
        <div class="col-lg-8">
          <div class="card mb-4 p-4">
            <h4>課程簡介</h4>
            <p>{{ course.description || '這堂課將帶你了解......' }}</p>
          </div>

          <div class="card p-4">
            <h4>課程內容</h4>

            <div
              class="accordion accordion-flush"
              id="chapters"
              v-if="chapters.length"
            >
              <div
                class="accordion-item border border-dark mb-2"
                v-for="(chapter, idx) in chapters"
                :key="chapter.id"
              >
                <h2 class="accordion-header">
                  <button
                    class="accordion-button fw-bold d-flex justify-content-between align-items-center"
                    :class="{ collapsed: idx !== 0 }"
                    type="button"
                    data-bs-toggle="collapse"
                    :data-bs-target="'#c' + idx"
                  >
                    <div class="d-flex w-100 justify-content-between pe-3">
                      <span>{{ chapter.title }}</span>
                      <small class="text-muted fw-normal">{{ chapter.summary }}</small>
                    </div>
                  </button>
                </h2>
                <div
                  :id="'c' + idx"
                  class="accordion-collapse collapse "
                  :class="{ show: idx === 0 }"
                  data-bs-parent="#chapters"
                >
                  <div class="accordion-body text-muted small">
                    {{ chapter.description || '尚未有章節簡介...' }}
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="text-center py-4 text-muted border rounded bg-light">
              尚無課程章節
            </div>
          </div>

          <!-- Reviews Section -->
          <div class="card p-4 mt-4">
            <h4>學生評論</h4>
            <div v-if="course.ratings && course.ratings.length > 0">
              <div v-for="rating in course.ratings" :key="rating.id" class="border-bottom py-3">
                <div class="d-flex justify-content-between mb-1">
                  <span class="fw-bold">{{ rating.name }}</span>
                  <small class="text-muted">{{ formatDate(rating.date) }}</small>
                </div>
                <div class="mb-2 text-warning">
                  <span v-for="n in 5" :key="n">{{ n <= rating.rating ? '★' : '☆' }}</span>
                </div>
                <p class="mb-0 text-muted">{{ rating.comment }}</p>
              </div>
            </div>
            <div v-else class="text-center py-4 text-muted">
              目前尚無評論，快來成為第一個吧!
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        <div class="col-lg-4">
          <div class="card p-3 position-sticky" style="top: 100px">
            <!-- Video Preview or Image -->
            <div class="mb-3 rounded overflow-hidden" style="height: 200px; background: #000">
              <img
                :src="course.image"
                :alt="course.title"
                class="w-100 h-100 object-fit-cover"
              />
            </div>

            <div class="align-items-center gap-2 mb-3">
              <h4>{{ course.title }}</h4>
              <h2 class="text-primary mb-0">${{ course.price }}</h2>
              <span v-if="course.originalPrice" class="text-decoration-line-through text-muted"
                >${{ course.originalPrice }}</span
              >
            </div>
            <div class="d-grid gap-2">
              <button @click="store.addToCart(course.id)" class="btn btn-primary btn-lg">
                <ShoppingCart :size="20" /> 加入購物車
              </button>
              <button @click="store.toggleFavorite(course.id)" class="btn btn-outline-dark">
                <Heart
                  :size="20"
                  :fill="course.isFavorite ? 'red' : 'none'"
                  :class="course.isFavorite ? 'text-danger' : ''"
                />
                {{ course.isFavorite ? '已收藏' : '加到我的最愛' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="container py-5 text-center">
    <h2>找不到類似名稱的課程....</h2>
  </div>
</template>
