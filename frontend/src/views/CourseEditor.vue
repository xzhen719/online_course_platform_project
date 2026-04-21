<script setup>
import { ArrowRight } from 'lucide-vue-next'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAllCategories } from '../api/categories'
import { addChapter as addChapterApi } from '../api/chapters'
import { createCourse as createCourseApi } from '../api/courses'
import { generateDescription as generateDescriptionApi } from '../api/openrouter'
import { uploadImage } from '../api/upload'
import { useCourseDraft } from '../composables/useCourseDraft'
import { useMainStore } from '../stores'


const store = useMainStore()
const router = useRouter()
const route = useRoute()

// Mode detection
const courseId = route.params.id
const isEdit = !!courseId

// State
const categories = ref([])
const saving = ref(false)
const uploading = ref(false)
const { courseInfo, chapters, chapterCount, setCourseInfo, startCreating, isCreating, reset } = useCourseDraft()

const form = ref({
  name: '',
  price: 0,
  categoryId: null,
  description: '',
  imageUrl: '',
  videoUrl: '',
  instructorName: '',
})

const aiResponse = ref('');
const generating = ref(false);

// Strip markdown formatting to plain text
function stripMarkdown(text) {
  return text
    .replace(/#{1,6}\s?/g, '')           // Headers
    .replace(/\*\*([^*]+)\*\*/g, '$1')   // Bold
    .replace(/\*([^*]+)\*/g, '$1')       // Italic
    .replace(/__([^_]+)__/g, '$1')       // Bold
    .replace(/_([^_]+)_/g, '$1')         // Italic
    .replace(/`([^`]+)`/g, '$1')         // Inline code
    .replace(/\[([^\]]+)\]\([^)]+\)/g, '$1')  // Links
    .replace(/^[-*+]\s+/gm, '• ')        // Unordered lists
    .replace(/^\d+\.\s+/gm, '')          // Ordered lists
    .replace(/\n{3,}/g, '\n\n')          // Multiple newlines
    .trim()
}

const fetchAiResponse = async () => {
  if (!form.value.name?.trim()) {
    alert('請先輸入課程名稱')
    return
  }
  
  generating.value = true;
  aiResponse.value = '';
  
  const prompt = `
你是一位課程簡介撰寫助手。

請根據以下課程資訊，撰寫課程簡介，包含：
- 課程特色
- 課程目標
- 適合哪些學生

請注意：
- 不要虛構具體網站或課程來源
- 使用繁體中文回答
- 請用純文字回答，不要使用 markdown 格式
- 最多200字

課程資訊：
課程名稱：${form.value.name}
課程分類：${form.value.categoryId || '未選擇'}
`.trim();

  try {
    const response = await generateDescriptionApi(prompt);
    const content = response.content || '（無法取得 AI 回應）';
    form.value.description = stripMarkdown(content);
  } catch (err) {
    console.error(err);
    alert('取得 AI 回應失敗，請稍後再試');
  } finally {
    generating.value = false;
  }
};


// Cancel and clear form
function handleCancel() {
  if (!isEdit) {
    reset()  // Clear the draft
  }
  router.push('/instructor')
}

// Sync form to draft in create mode
watch(form, (val) => {
  if (!isEdit) setCourseInfo(val)
}, { deep: true })

onMounted(async () => {
  // Load categories
  try {
    categories.value = await getAllCategories()
  } catch (e) {
    console.error('Failed to fetch categories:', e)
  }

  if (isEdit) {
    // Edit mode: load from API
    const course = await store.getCourse(courseId)
    if (course) {
      form.value = {
        name: course.title,
        price: course.price,
        categoryId: course.categoryId,
        description: course.description,
        imageUrl: course.image,
        videoUrl: course.introVideoUrl,
        instructorName: course.instructor,
        totalChapters: course.totalChapters,
      }
    }
  } else {
    // Create mode
    if (isCreating.value && courseInfo.value.name) {
      form.value = { ...courseInfo.value }
    } else {
      reset()
      startCreating()
    }
  }
})

// Navigate to chapters
function goToChapters() {
  if (!form.value.name?.trim()) {
    alert('請先輸入課程名稱')
    return
  }
  
  if (isEdit) {
    // Save then navigate
    store.updateCourse(courseId, form.value)
      .then(() => router.push(`/instructor/course/edit/${courseId}/chapters`))
      .catch(() => alert('儲存失敗'))
  } else {
    setCourseInfo(form.value)
    router.push('/instructor/course/new/chapters')
  }
}

// Handle file upload
async function handleFileUpload(event) {
  const file = event.target.files[0]
  if (!file) return

  uploading.value = true
  try {
    const imageUrl = await uploadImage(file)
    form.value.imageUrl = imageUrl
  } catch (e) {
    console.error('Upload failed:', e)
    alert('上傳失敗: ' + (e.response?.data || e.message))
  } finally {
    uploading.value = false
  }
}

// Save course
async function save() {
  if (isEdit) {
    // Edit mode: update course
    try {
      await store.updateCourse(courseId, form.value)
      router.push('/instructor')
      store.fetchCourses()
    } catch (e) {
      alert('儲存失敗')
    }
  } else {
    // Create mode: create course + chapters
    const validChapters = chapters.value.filter(ch => ch.title?.trim())
    if (validChapters.length === 0) {
      alert('請先建立至少一個章節')
      return
    }

    saving.value = true
    try {
      const newCourse = await createCourseApi(form.value)
      for (const ch of validChapters) {
        await addChapterApi(newCourse.id, {
          title: ch.title,
          summary: ch.summary,
          description: ch.description,
          videoUrl: ch.videoUrl,
          duration: ch.duration || 0,
        })
      }
      reset()
      await store.fetchInstructorCourses()
      await store.fetchCourses()
      alert('課程建立成功!')
      router.push('/instructor')
    } catch (e) {
      alert('建立失敗: ' + (e.response?.data?.message || e.message))
    } finally {
      saving.value = false
    }
  }
}
</script>

<template>
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-lg-8">
        <div class="card p-4">
          <!-- Header -->
          <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
              <h2 class="mb-0">{{ isEdit ? '編輯課程' : '建立課程' }}</h2>
            </div>
            <button
              type="button"
              class="btn btn-outline-primary d-flex align-items-center gap-2"
              @click="goToChapters"
            >
              {{ isEdit ? `管理章節(${form.totalChapters})` : `建立章節 (${chapterCount})` }}
              <ArrowRight :size="16" />
            </button>
          </div>

          <!-- Form -->
          <form @submit.prevent="save">
            <div class="mb-3">
              <label class="fw-bold">課程名稱</label>
              <input type="text" class="form-control" v-model="form.name" required />
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="fw-bold">課程定價 ($)</label>
                <input type="number" class="form-control" v-model="form.price" required />
              </div>
              <div class="col-md-6 mb-3">
                <label class="fw-bold">課程分類</label>
                <select class="form-select" v-model="form.categoryId">
                  <option :value="null">請選擇分類</option>
                  <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                    {{ cat.name }}
                  </option>
                </select>
              </div>
            </div>

            <div class="mb-3">
              <label class="fw-bold">講師名稱</label>
              <input type="text" class="form-control" v-model="form.instructorName" />
            </div>

            <div class="mb-3">
              <label class="fw-bold">課程縮圖</label>
              <input type="file" class="form-control" accept="image/*" @change="handleFileUpload" :disabled="uploading" />
              <div v-if="uploading" class="text-muted small mt-1">上傳中...</div>
              <div v-else-if="form.imageUrl" class="mt-2">
                <img :src="form.imageUrl" class="img-thumbnail" style="max-height: 120px" />
              </div>
            </div>

            <div class="mb-3">
              <label class="fw-bold">課程簡介影片</label>
              <input type="text" class="form-control" v-model="form.videoUrl" placeholder="https://youtube.com/..." />
            </div>

           <div class="mb-4">
              <label class="fw-bold">課程簡介</label>
              <textarea class="form-control" rows="4" v-model="form.description" :disabled="generating"></textarea>
              <div class="d-flex justify-content-end mt-1">
                <button 
                  type="button"
                  @click="fetchAiResponse"
                  class="btn btn-outline-primary btn-sm rounded-pill shadow-none border-2"
                  :disabled="generating"
                >
                  <span v-if="generating" class="spinner-border spinner-border-sm me-1"></span>
                  {{ generating ? '生成中...' : '一鍵生成' }}
                </button>
              </div>
            </div>

            <div class="d-flex gap-3">
              <button class="btn btn-success btn-lg" :disabled="saving">
                {{ saving ? '儲存中...' : '儲存' }}
              </button>
              <button type="button" @click="handleCancel" class="btn btn-outline-dark btn-lg">取消</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
