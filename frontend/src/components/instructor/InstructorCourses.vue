<script setup>
import { useMainStore } from '../../stores'
import { useRouter } from 'vue-router'
import { Edit, Eye, Plus, ArrowDownUp } from 'lucide-vue-next'
import ImagePlaceholder from '../ImagePlaceholder.vue'
import { storeToRefs } from 'pinia'
import { onMounted } from 'vue'

const store = useMainStore()
const router = useRouter()
const { instructorCourses } = storeToRefs(store)

const toggleStatus = (id) => {
  store.toggleCourseStatus(id)
}

onMounted(() => {
  store.fetchInstructorCourses()
})
</script>

<template>
  <div class="d-flex justify-content-between align-items-center mb-4 card p-4 flex-row bg-light">
    <h3 class="mb-0">課程管理</h3>
    <router-link to="/instructor/course/new" class="btn btn-primary">
      <Plus :size="18" /> 建立課程
    </router-link>
  </div>

  <div class="d-flex flex-column gap-3">
    <div v-for="course in instructorCourses" :key="course.id" class="card overflow-hidden">
      <div class="row g-0">
        <div class="col-md-3">
          <div style="height: 180px; background: #000" class="overflow-hidden">
            <img
              v-if="course.image"
              :src="course.image"
              class="w-100 h-100 object-fit-cover"
            />
            <ImagePlaceholder
              v-else
              :text="course.title"
              height="100%"
              className="h-100 border-bottom-0 border-end"
            />
          </div>
        </div>
        <div class="col-md-9 p-3 d-flex flex-column justify-content-between">
          <div>
            <div class="d-flex justify-content-between align-items-start">
              <h5 class="fw-bold">{{ course.title }}</h5>
              <span
                class="badge rounded"
                :class="course.status === 'ON_SHELF' ? 'bg-success' : 'bg-secondary'"
              >
                {{ course.status === 'ON_SHELF' ? '已上架' : '已下架' }}
              </span>
            </div>
            <p class="small text-muted mb-2">{{ course.category }} / ${{ course.price }}</p>
            <div class="d-flex gap-3 small fw-bold">
              <span><Eye :size="14" /> {{ course.students }} Students</span>
              <span>⭐ {{ course.rating }} Rating</span>
            </div>
            
          </div>

          <div class="mt-3 d-flex gap-2">
            <button
              @click="router.push(`/instructor/course/edit/${course.id}`)"
              class="btn btn-sm btn-outline-primary"
            >
              <Edit :size="16" /> 編輯
            </button>
            <button
              @click="toggleStatus(course.id)"
              class="btn btn-sm"
              :class="course.status === 'ON_SHELF' ? 'btn-outline-dark' : 'btn-outline-success'"
            >
              <ArrowDownUp :size="16" />
              {{ course.status === 'ON_SHELF' ? '下架' : '上架' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
