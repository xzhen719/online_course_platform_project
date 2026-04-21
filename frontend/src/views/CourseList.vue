<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useMainStore } from '../stores'
import { useRoute } from 'vue-router'
import CourseCard from '../components/CourseCard.vue'
import { getAllCategories } from '../api/categories'

const store = useMainStore()
const route = useRoute()
const query = ref('')
const selectedCat = ref('All')
const categories = ref([])
const loading = ref(true)

// 從API載入分類
onMounted(async () => {
  try {
    const data = await getAllCategories()
    await store.fetchCourses()
    categories.value = ['All', ...data.map(cat => cat.name)]
  } catch (error) {
    console.error('Failed to load categories:', error)
    categories.value = ['All']
  } finally {
    loading.value = false
  }
  
  // 從URL讀取分類參數
  if (route.query.cat) {
    selectedCat.value = route.query.cat
  }
  // 從URL讀取搜尋參數 (從Navbar搜尋傳入)
  if (route.query.q) {
    query.value = route.query.q
  }
})

// 監聽路由變化 (當從Navbar點擊不同分類時)
watch(() => route.query.cat, (newCat) => {
  selectedCat.value = newCat || 'All'
})

// 監聽路由搜尋參數變化 (從Navbar搜尋)
watch(() => route.query.q, (newQuery) => {
  query.value = newQuery || ''
})

const filtered = computed(() => {
  const searchTerm = query.value.toLowerCase()
  return store.publishedCoursesWithFavorites.filter((c) => {
    // 搜尋課程標題和講師名稱
    const matchQ = c.title.toLowerCase().includes(searchTerm) || 
                   c.instructor.toLowerCase().includes(searchTerm)
    const matchC = selectedCat.value === 'All' || c.category === selectedCat.value
    return matchQ && matchC
  })
})
</script>

<template>
  <div class="container py-5">
    <div class="row mb-4">
      <div class="col-12">
        <h1 class="mb-4">探索課程</h1>
        <div class="card p-4 bg-light">
          <div class="row g-3">
            <div class="col-md-8">
              <input
                type="text"
                class="form-control"
                placeholder="輸入課程或講師名稱..."
                v-model="query"
              />
            </div>
            <div class="col-md-4">
              <select class="form-select" v-model="selectedCat" :disabled="loading">
                <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
              </select>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-4">
      <div v-for="course in filtered" :key="course.id" class="col-md-6 col-lg-3">
        <CourseCard :course="course" />
      </div>
      <div v-if="filtered.length === 0" class="col-12 text-center py-5">
        <h4>暫時找不到相關的課程....</h4>
      </div>
    </div>
  </div>
</template>
