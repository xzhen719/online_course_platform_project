<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMainStore } from '../stores'
import { getCourseChapters } from '../api/chapters'
import { generateGiftCoupon } from '../api/coupons'
import { markChapterComplete, getCompletedChapters } from '../api/progress'

import { 
  Play,
  CheckCircle,
  ChevronLeft,
  Menu,
  X,
  MessageSquare,
  AlertCircle,
  Ticket,
  PartyPopper,
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const store = useMainStore()
const courseId = computed(() => parseInt(route.params.id))
const course = computed(() => store.courses.find((c) => c.id === courseId.value))


const sidebarOpen = ref(true)
const currentChapterIndex = ref(0)
const showRewardModal = ref(false)
const earnedCoupon = ref(null)
const chapters = ref([])
const loadingChapters = ref(true)
const chaptersError = ref('')

const newRating = ref({ rating: 0, comment: '' })

// 從API載入章節
onMounted(async () => {
  if (!courseId.value) return
  
  try {
    const data = await getCourseChapters(courseId.value)
    // 轉換後端 ChapterDto 格式
    chapters.value = data.map(ch => ({
      id: ch.id,
      title: ch.title,
      summary: ch.summary,
      description: ch.description,
      videoUrl: ch.videoUrl,
      index: ch.index
    }))
    
    // 載入已完成的章節進度
    const completedIds = await getCompletedChapters(courseId.value)
    // 更新 store 的進度狀態
    store.setCompletedChapters(courseId.value, completedIds)

    
  } catch (err) {
    console.error('Failed to load chapters:', err)
    if (err.response?.status === 403) {
      chaptersError.value = '您尚未購買此課程'
      setTimeout(() => {
        router.push(`/course/${courseId.value}`)
      }, 2000)
    } else {
      chaptersError.value = '無法載入章節'
    }
  } finally {
    loadingChapters.value = false
  }
})

const submitRating = async () => {
  try {
    await store.addRating(course.value.id, {
      rating: newRating.value.rating,
      comment: newRating.value.comment,
    })
    // 重置評論
    newRating.value = { rating: 0, comment: '' }
    alert('感謝您的評論!')
  } catch (error) {
    console.error('Failed to submit rating:', error)
    alert('評論提交失敗，請稍後再試')
  }
}



const currentChapter = computed(() => chapters.value[currentChapterIndex.value])

// Progress Logic
const completedChapters = computed(() => store.studentProgress[course.value?.id] || [])
const isCurrentCompleted = computed(() =>
  completedChapters.value.includes(currentChapter.value?.id),
)

const progressPercentage = computed(() => {
  if (chapters.value.length === 0) return 0
  return Math.round((completedChapters.value.length / chapters.value.length) * 100)
})

const toggleComplete = async () => {
  if (currentChapter.value && !isCurrentCompleted.value) {
    try {
      // 先記錄目前完成數 (在 store 更新前)
      const previousCompletedCount = completedChapters.value.length
      
      // 呼叫 API 標記章節完成
      await markChapterComplete(currentChapter.value.id)
      // 更新本地 store (UI 即時回饋)
      store.toggleChapterCompletion(course.value.id, currentChapter.value.id)
      
      // 檢查是否是最後一個章節完成 (完成後進度會是100%)
      const newCompletedCount = previousCompletedCount + 1
      if (newCompletedCount === chapters.value.length) {
        // 100% 完成 - 產生優惠券
        try {
          const coupon = await generateGiftCoupon(course.value.id)
          earnedCoupon.value = {
            code: coupon.code,
            description: `恭喜完成課程! 獲得 ${coupon.discountPercent}% 折扣優惠券`
          }
          showRewardModal.value = true
        } catch (couponError) {
          console.error('Failed to generate coupon:', couponError)
        }
      }
    } catch (error) {
      console.error('Failed to mark chapter complete:', error)
    }
  }
}
</script>

<template>
  <div v-if="course" class="d-flex flex-column vh-100 overflow-hidden">
    <!-- Header -->
    <header
      class="navbar bg-white border-bottom border-dark px-4 py-2 d-flex justify-content-between flex-shrink-0"
      style="height: 60px"
    >
      <div class="d-flex align-items-center gap-3">
        <button
          @click="router.push({ name: 'student-courses' })"
          class="btn btn-sm btn-outline-dark"
        >
          <ChevronLeft :size="16" />
        </button>
        <h5 class="mb-0 text-truncate" style="max-width: 300px">{{ course.title }}</h5>
      </div>
      <button class="btn btn-sm btn-light d-lg-none" @click="sidebarOpen = !sidebarOpen">
        <Menu :size="20" />
      </button>
    </header>

    <div class="d-flex flex-grow-1 overflow-hidden position-relative">
      <!-- Main Content -->
      <main class="flex-grow-1 bg-light overflow-auto p-4 d-flex flex-column">
        <div class="container-fluid" style="max-width: 1000px">
          <!-- Error State -->
          <div v-if="chaptersError" class="alert alert-danger text-center">
            {{ chaptersError }}
          </div>
          
          <div v-else-if="chapters.length > 0">
            <!-- Video Player -->
            <div
              class="ratio bg-dark mb-4 border border-dark shadow"
              style="--bs-aspect-ratio: 45%"
            >
              <iframe
                v-if="currentChapter.videoUrl"
                :src="store.getEmbedUrl(currentChapter.videoUrl)"
                allowfullscreen
                class="w-100 h-100"
              ></iframe>
              <div v-else class="d-flex align-items-center justify-content-center text-white">
                <div class="text-center">
                  <AlertCircle :size="48" class="mb-3 opacity-50" />
                  <p>此章節尚未有影片</p>
                </div>
              </div>
            </div>

            <!-- Info -->
            <div class="d-flex justify-content-between align-items-start mb-4">
              <div>
                <h2>{{ currentChapter.title }}</h2>
                <p class="text-muted fw-bold">{{ currentChapter.summary }}</p>
              </div>

              <button
                @click="toggleComplete"
                class="btn"
                :class="isCurrentCompleted ? 'btn-success' : 'btn-outline-dark'"
              >
                <CheckCircle :size="18" class="me-2" />
                {{ isCurrentCompleted ? '已完成' : '完成' }}
              </button>
            </div>

            <!-- Description -->
            <div class="card p-4 mb-4">
              <h5 class="mb-3">章節內容</h5>
              <div class="text-muted" style="white-space: pre-wrap">
                {{ currentChapter.description || '尚無章節內容...' }}
              </div>
            </div>

            <!-- Rating & Comments -->
            <div class="card p-4">
              <h5 class="mb-3"><MessageSquare :size="18" class="me-2" /> 你的評論</h5>

              <div class="mb-2">
                <label class="form-label fw-bold small">評分</label>
                <div class="d-flex gap-1">
                  <button
                    v-for="star in 5"
                    :key="star"
                    id="star"
                    @click="newRating.rating = star"
                    class="btn btn-sm p-0 border-0"
                  >
                    <component
                      :is="star <= newRating.rating ? 'span' : 'span'"
                      class="fs-4"
                      :class="star <= newRating.rating ? 'text-warning' : 'text-muted opacity-25'"
                      >★</component
                    >
                  </button>
                </div>
              </div>

              <div class="mb-2">
                <label class="form-label fw-bold small">留言</label>
                <textarea
                  class="form-control"
                  rows="3"
                  placeholder="你想對這個課程說些什麼..."
                  v-model="newRating.comment"
                ></textarea>
              </div>

              <div class="text-end">
                <button @click="submitRating" class="btn btn-primary" :disabled="!newRating.rating">
                  送出
                </button>
              </div>
            </div>
          </div>

          <div v-else-if="!loadingChapters" class="text-center py-5">
            <h3>此課程尚未建立章節</h3>
            <button @click="router.back()" class="btn btn-secondary mt-3">返回</button>
          </div>
        </div>
      </main>

      <!-- Sidebar -->
      <aside
        class="bg-white border-start border-dark d-flex flex-column transition-all"
        :class="sidebarOpen ? 'sidebar-open' : 'sidebar-closed'"
        style="width: 350px; z-index: 10"
      >
        <div class="p-3 border-bottom border-dark bg-light">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <h6 class="mb-0 fw-bold">課程進度</h6>
            <button class="btn btn-sm d-lg-none" @click="sidebarOpen = false">
              <X :size="16" />
            </button>
          </div>
          <!-- Progress Bar -->
          <div class="progress" style="height: 10px">
            <div
              class="progress-bar bg-success"
              role="progressbar"
              :style="{ width: progressPercentage + '%' }"
            ></div>
          </div>
          <small class="text-muted mt-1 d-block">{{ progressPercentage }}% 完成</small>
        </div>

        <div class="overflow-auto flex-grow-1">
          <div class="list-group list-group-flush">
            <!--prettier-ignore-->
            <button 
              v-for="(chapter, idx) in chapters" 
              :key="chapter.id"
              class="list-group-item list-group-item-action py-3 border-bottom"
              :class="{'bg-primary bg-opacity-10 fw-bold': idx === currentChapterIndex}"
              @click="currentChapterIndex = idx; if(window.innerWidth < 992) sidebarOpen = false;"
            >
              <div class="d-flex justify-content-between align-items-center mb-1">
                <small class="text-muted">章節 {{ idx + 1 }}</small>
                <span v-if="completedChapters.includes(chapter.id)" class="text-success"><CheckCircle :size="14"/></span>
              </div>
              <div>{{ chapter.title }}</div>
              
            </button>
          </div>
        </div>
      </aside>
    </div>

    <!-- Reward Modal Overlay -->
    <div v-if="showRewardModal" class="modal-backdrop fade show"></div>
    <div v-if="showRewardModal" class="modal fade show d-block" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg text-center p-4">
          <!-- Close Button -->
          <button 
            type="button" 
            class="btn-close position-absolute top-0 end-0 m-3" 
            @click="showRewardModal = false"
          ></button>
          
          <div class="modal-body">
            <div class="mb-3 text-warning">
              <PartyPopper :size="64" />
            </div>
            <h2 class="fw-bold mb-3">恭喜完成課程所有章節!</h2>
            <p class="lead mb-4">優惠券已派發至「我的優惠券」頁面!</p>

            <div class="card bg-light border-dashed mb-4 p-3" v-if="earnedCoupon">
              <div class="d-flex align-items-center justify-content-center gap-2 mb-2 text-primary">
                <Ticket :size="24" />
                <h5 class="mb-0 fw-bold">{{ earnedCoupon.code }}</h5>
              </div>
              <p class="mb-0 text-muted small">{{ earnedCoupon.description }}</p>
            </div>

            <div class="d-grid gap-2">
              <button
                type="button"
                class="btn btn-primary"
                @click="router.push('/student/coupons')"
              >
                查看我的優惠券
              </button>
              <button
                type="button"
                class="btn btn-outline-secondary"
                @click="showRewardModal = false"
              >
                繼續學習
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@media (max-width: 991.98px) {
  aside {
    position: absolute;
    right: 0;
    top: 0;
    bottom: 0;
    transform: translateX(100%);
    transition: transform 0.3s ease;
  }
  aside.sidebar-open {
    transform: translateX(0);
    box-shadow: -5px 0 15px rgba(0, 0, 0, 0.1);
  }
}
.border-dashed {
  border: 2px dashed #dee2e6;
}

#star {
  box-shadow: none;
}
</style>
