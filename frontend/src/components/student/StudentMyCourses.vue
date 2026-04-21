<script setup>
import { ref, computed, onMounted } from 'vue'
import { useMainStore } from '../../stores'
import { useRouter } from 'vue-router'
import ImagePlaceholder from '../ImagePlaceholder.vue'
import { Search, Play } from 'lucide-vue-next'
import { getMyEnrolledCourses } from '../../api/enrollments'
import { getCompletedChapters } from '../../api/progress'

const store = useMainStore()
const router = useRouter()
const searchQuery = ref('')
const enrolledCourses = ref([])
const loading = ref(true)
const error = ref('')



// 從API載入已購買的課程 + 每個課程的進度
onMounted(async () => {
  try {
    const data = await getMyEnrolledCourses()
    
    // 轉換後端 CourseDto 格式並載入進度
    const coursesWithProgress = await Promise.all(
      data.map(async (course) => {
        // 取得這個課程的完成進度
        let completedCount = 0
        try {
          const completedIds = await getCompletedChapters(course.id)
          completedCount = completedIds.length
          // 同步更新 store (給 CourseLearning 使用)
          store.setCompletedChapters(course.id, completedIds)
        } catch (e) {
          console.error(`Failed to load progress for course ${course.id}:`, e)
        }
        
        const totalChapters = course.totalChapters || 0
        const progress = totalChapters > 0 
          ? Math.round((completedCount / totalChapters) * 100) 
          : 0
        
        return {
          id: course.id,
          title: course.name,
          description: course.description,
          instructor: course.instructorName || 'Unknown',
          image: course.imageUrl,
          price: course.price,
          rating: course.ratingAverage || 0,
          students: course.studentCount || 0,
          totalChapters: course.totalChapters || 0,
          totalTime: course.totalTime || 0,
          completedCount,
          progress,
        }
      })
    )
    
    enrolledCourses.value = coursesWithProgress
  } catch (err) {
    console.error('Failed to load enrolled courses:', err)
    error.value = '無法載入課程，請稍後再試'
  } finally {
    loading.value = false
  }
})

// 過濾搜尋
const myCourses = computed(() => {
  return enrolledCourses.value
    .filter((c) => c.title.toLowerCase().includes(searchQuery.value.toLowerCase()))
})
</script>

<template>
  <div class="card p-4 bg-light mb-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h3 class="mb-0">我的課程</h3>
      <div class="input-group w-auto">
        <span class="input-group-text bg-white border-dark"><Search :size="16" /></span>
        <input
          type="text"
          class="form-control border-dark"
          placeholder="搜尋課程..."
          v-model="searchQuery"
        />
      </div>
    </div>
  </div>

  <!-- Loading State -->
  <div v-if="loading" class="text-center py-5">
    <div class="spinner-border text-primary" role="status"></div>
    <p class="mt-3 text-muted">載入中...</p>
  </div>

  <!-- Error State -->
  <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

  <!-- Courses Grid -->
  <div v-else class="row g-4">
    <div v-for="course in myCourses" :key="course.id" class="col-md-6">
      <div class="card h-100">
        <div class="position-relative" style="height: 160px; background: #000">
          <img
            v-if="course.image"
            :src="course.image"
            :alt="course.title"
            class="w-100 h-100 object-fit-cover"
          />
          <ImagePlaceholder v-else :text="course.title" height="100%" />
        </div>
        <div class="card-body">
          <h5 class="card-title text-truncate">{{ course.title }}</h5>
          <p class="small text-muted mb-2">{{ course.instructor }}</p>

          <div class="d-flex justify-content-between small fw-bold mb-1">
            <span>學習進度 ({{ course.completedCount }}/{{ course.totalChapters }})</span>
            <span>{{ course.progress }}%</span>
          </div>
          <div class="progress rounded-0.5 border border-dark mb-3" style="height: 12px">
            <div 
              class="progress-bar" 
              :class="course.progress === 100 ? 'bg-success' : 'bg-primary'"
              :style="{ width: course.progress + '%' }"
            ></div>
          </div>

          <button
            @click="router.push(`/learning/${course.id}`)"
            class="btn w-100"
            :class="course.progress === 100 ? 'btn-outline-success' : 'btn-success'"
          >
            <Play :size="16" class="me-2" /> 
            {{ course.progress === 100 ? '已完成' : course.progress === 0 ? '開始學習' : '繼續學習' }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- Empty State -->
    <div
      v-if="myCourses.length === 0 && !loading"
      class="col-12 text-center py-5 card bg-light border-dashed"
    >
      <h4 class="mb-2 fw-bold">找不到相關的課程</h4>
      <router-link to="/courses" class="btn btn-success mt-2 mx-auto" style="width: 200px">前往探索</router-link>
    </div>
  </div>
</template>
