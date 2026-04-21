<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCourseDraft } from '../composables/useCourseDraft'
import { getInstructorChapters, addChapter as addChapterApi, updateChapter as updateChapterApi } from '../api/chapters'
import { generateDescription as generateDescriptionApi } from '../api/openrouter'
import { Plus, ArrowLeft, ChevronDown, ChevronUp, Save } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()

// Mode detection
const courseId = route.params.id
const isCreateMode = !courseId

// Draft composable
const { courseInfo, chapters: draftChapters, setChapters, isCreating } = useCourseDraft()

// Local state
const chapters = ref([])
const originalChapters = ref([]) // Track original state to detect changes
const expandedId = ref(null)
const loading = ref(false)
const saving = ref(false) // Track saving state
const generatingId = ref(null)  // Track which chapter is generating

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

// Generate chapter description using AI
async function generateChapterDescription(chapter) {
  if (!chapter.title?.trim()) {
    alert('請先輸入章節名稱')
    return
  }
  
  generatingId.value = chapter.id
  
  const prompt = `
你是一位課程章節內容撰寫助手。

請根據以下章節資訊，撰寫章節內容描述，包含：
- 本章節的學習目標
- 主要教學內容
- 學習此章節後能獲得什麼

請注意：
- 不要虛構具體網站或課程來源
- 使用繁體中文回答
- 請用純文字回答，不要使用 markdown 格式
- 內容簡潔，約 100-150 字

章節資訊：
課程名稱：${courseInfo.value?.name || '未命名課程'}
章節名稱：${chapter.title}
章節簡介：${chapter.summary || '無'}
`.trim();

  try {
    const response = await generateDescriptionApi(prompt);
    const content = response.content || '（無法取得 AI 回應）';
    chapter.description = stripMarkdown(content);
  } catch (err) {
    console.error(err);
    alert('取得 AI 回應失敗，請稍後再試');
  } finally {
    generatingId.value = null;
  }
}

onMounted(async () => {
  if (isCreateMode) {
    // CREATE MODE: check draft exists
    if (!isCreating.value) {
      router.push('/instructor/course/new')
      return
    }
    chapters.value = [...draftChapters.value]
  } else {
    // EDIT MODE: load from instructor API
    loading.value = true
    try {
      const data = await getInstructorChapters(courseId)
      const loadedChapters = data.map(ch => ({
        id: ch.id,
        title: ch.title,
        summary: ch.summary,
        description: ch.description,
        videoUrl: ch.videoUrl,
        duration: ch.duration || 0,
      }))
      chapters.value = loadedChapters
      // Store original state to detect changes later
      originalChapters.value = JSON.parse(JSON.stringify(loadedChapters))
    } catch (e) {
      console.error('Failed to load chapters:', e)
      alert('無法載入章節')
      router.push('/instructor')
    } finally {
      loading.value = false
    }
  }
})

// Auto-sync to draft in create mode
watch(chapters, (val) => {
  if (isCreateMode) {
    setChapters([...val])
  }
}, { deep: true })

function toggleExpand(id) {
  expandedId.value = expandedId.value === id ? null : id
}

function addChapter() {
  const newId = `temp-${Date.now()}`
  chapters.value.push({
    id: newId,
    title: '',
    summary: '',
    description: '',
    videoUrl: '',
    duration: 0,
  })
  expandedId.value = newId
}

// Check if chapter has been modified
function isChapterModified(chapter) {
  const original = originalChapters.value.find(c => c.id === chapter.id)
  if (!original) return false
  return (
    chapter.title !== original.title ||
    chapter.summary !== original.summary ||
    chapter.description !== original.description ||
    chapter.videoUrl !== original.videoUrl
  )
}

// Save chapters (edit mode only)
async function saveChapters() {
  if (isCreateMode) return
  
  const validChapters = chapters.value.filter(ch => ch.title?.trim())
  if (validChapters.length === 0) {
    alert('請至少輸入一個章節標題')
    return
  }
  
  saving.value = true
  try {
    for (const ch of validChapters) {
      // Check if this is a new chapter (temp ID)
      if (String(ch.id).startsWith('temp-')) {
        // Create new chapter
        const newChapter = await addChapterApi(courseId, {
          title: ch.title,
          summary: ch.summary,
          description: ch.description,
          videoUrl: ch.videoUrl,
          duration: ch.duration || 0,
        })
        // Replace temp ID with real ID
        ch.id = newChapter.id
      } else if (isChapterModified(ch)) {
        // Update existing chapter if modified
        await updateChapterApi(ch.id, {
          title: ch.title,
          summary: ch.summary,
          description: ch.description,
          videoUrl: ch.videoUrl,
          duration: ch.duration || 0,
        })
      }
    }
    // Update original chapters reference
    originalChapters.value = JSON.parse(JSON.stringify(chapters.value))
    alert('章節儲存成功！')
  } catch (e) {
    console.error('Failed to save chapters:', e)
    alert('儲存失敗: ' + (e.response?.data?.message || e.message))
  } finally {
    saving.value = false
  }
}

function goBack() {
  if (isCreateMode) {
    router.push('/instructor/course/new')
  } else {
    router.push(`/instructor/course/edit/${courseId}`)
  }
}
</script>

<template>
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-lg-10">
        <!-- Header -->
        <div class="d-flex align-items-center justify-content-between mb-4">
          <button @click="goBack" class="btn btn-outline-secondary d-flex align-items-center">
            <ArrowLeft :size="18" class="me-1" /> 返回課程資訊
          </button>
          <!-- Save button for edit mode -->
          <button 
            v-if="!isCreateMode" 
            @click="saveChapters" 
            class="btn btn-success d-flex align-items-center gap-2"
            :disabled="saving"
          >
            <span v-if="saving" class="spinner-border spinner-border-sm"></span>
            <Save v-else :size="18" />
            {{ saving ? '儲存中...' : '儲存章節' }}
          </button>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="text-center py-5">載入中...</div>

        <!-- Empty State -->
        <div v-else-if="chapters.length === 0" class="text-center py-5 border rounded bg-light">
          <p class="text-muted mb-3">目前尚未有任何章節</p>
          <button @click="addChapter" class="btn btn-primary">
            <Plus :size="18" /> 新增章節
          </button>
        </div>

        <!-- Chapters List -->
        <div v-else class="d-flex flex-column gap-3">
          <div v-for="(ch, idx) in chapters" :key="ch.id" class="card shadow-sm">
            <!-- Header -->
            <div class="card-header bg-white d-flex align-items-center py-3"
                 :class="{ 'border-bottom-0': expandedId !== ch.id }">
              <span class=" bg-none text-black me-2">{{ idx + 1 }}.</span>
              <div class="flex-grow-1 d-flex gap-2">
                <input v-model="ch.title" class="form-control" placeholder="章節名稱" />
                <input v-model="ch.summary" class="form-control text-muted" placeholder="簡短說明..." />
              </div>
              <button @click="toggleExpand(ch.id)" class="btn btn-sm btn-outline-secondary ms-2">
                <component :is="expandedId === ch.id ? ChevronUp : ChevronDown" :size="18" />
              </button>
            </div>

            <!-- Body (Collapsible) -->
            <div v-if="expandedId === ch.id" class="card-body bg-light border-top">
              <div class="mb-3">
                <label class="form-label fw-bold small">影片連結</label>
                <input v-model="ch.videoUrl" class="form-control" placeholder="https://youtube.com/..." />
              </div>
              <div>
                <div class="d-flex align-items-center mb-1">
                  <label class="form-label fw-bold small mb-0">章節內容</label>
                  <button 
                    type="button"
                    @click="generateChapterDescription(ch)"
                    class="btn btn-outline-primary btn-sm rounded-pill shadow-none border-2 ms-auto"
                    :disabled="generatingId === ch.id"
                  >
                    <span v-if="generatingId === ch.id" class="spinner-border spinner-border-sm me-1"></span>
                    {{ generatingId === ch.id ? '生成中...' : '一鍵生成' }}
                  </button>
                </div>
                <textarea 
                  v-model="ch.description" 
                  class="form-control" 
                  rows="3" 
                  placeholder="這章將帶你了解..."
                  :disabled="generatingId === ch.id"
                ></textarea>
              </div>
            </div>
          </div>

          <button @click="addChapter" class="btn btn-outline-dark py-3">
            <Plus :size="18" /> 新增章節
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
