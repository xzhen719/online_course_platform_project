<script setup>
import { ref, computed } from 'vue'
import { useMainStore } from '../../stores'
import { storeToRefs } from 'pinia'
import { Users, Star, Download } from 'lucide-vue-next'

const store = useMainStore()
const { courses } = storeToRefs(store)

// Filters
const selectedCourseId = ref('all')
// CHANGED: Combined Sort Field and Order into one ref
const sortBy = ref('revenue-desc')

// Computed Data
const processedCourses = computed(() => {
  let result = courses.value.map((c) => ({
    ...c,
    revenue: c.price * c.students,
  }))

  // Filter by Course
  if (selectedCourseId.value !== 'all') {
    result = result.filter((c) => c.id === selectedCourseId.value)
  }

  // CHANGED: Updated sort logic to parse combined value
  result.sort((a, b) => {
    const [field, order] = sortBy.value.split('-') // e.g. 'revenue', 'desc'
    let valA = a[field]
    let valB = b[field]

    if (order === 'asc') return valA - valB
    return valB - valA
  })

  return result
})

const totalRevenue = computed(() => processedCourses.value.reduce((sum, c) => sum + c.revenue, 0))

// Export to CSV
const exportToCSV = () => {
  const headers = ['課程名稱', '分類', '學生數', '評分', '收益']
  const rows = processedCourses.value.map((c) => [
    c.title,
    c.category,
    c.students,
    c.rating.toFixed(1),
    c.revenue,
  ])

  const csvContent = [headers, ...rows]
    .map((row) => row.map((cell) => `"${cell}"`).join(','))
    .join('\n')

  const BOM = '\uFEFF'
  const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `銷售數據_${new Date().toLocaleDateString('zh-TW')}.csv`
  link.click()
  URL.revokeObjectURL(url)
}
</script>

<template>
  <!-- Header -->
  <div class="card p-4 mb-4 bg-light d-flex flex-row justify-content-between align-items-center">
    <div>
      <h3 class="mb-0">銷售數據</h3>
    </div>
    
      <div class="text-end">
        <div class="h2 text-success fw-bold mb-0">${{ totalRevenue.toLocaleString() }}</div>
        <small class="text-muted">總收益</small>
      </div>
    
  </div>

  <!-- Filters -->
  <div class="card p-4 mb-4">
    <div class="row g-3 align-items-end">
      <div class="col-md-6">
        <label class="fw-bold small mb-1">課程篩選</label>
        <select class="form-select text-muted" v-model="selectedCourseId">
          <option value="all">所有課程</option>
          <option v-for="c in courses" :key="c.id" :value="c.id">{{ c.title }}</option>
        </select>
      </div>
      <!-- CHANGED: Combined Sort/Order Column -->
      <div class="col-md-6">
        <label class="fw-bold small mb-1">排序</label>
        <select class="form-select text-muted" v-model="sortBy">
          <option value="revenue-desc">總收益 (由高到低)</option>
          <option value="revenue-asc">總收益 (由低到高)</option>
          <option value="students-desc">總學生 (由高到低)</option>
          <option value="students-asc">總學生 (由低到高)</option>
          <option value="rating-desc">評分 (由高到低)</option>
          <option value="rating-asc">評分 (由低到高)</option>
        </select>
      </div>
    </div>
  </div>

  <!-- Course Data Table -->
  <div class="card">
    <div class="d-flex align-items-center  gap-4 ">
      <button class="btn btn-sm btn-outline-primary d-flex align-items-center gap-2 mt-2 ms-2 shadow-sm border-2 rounded" @click="exportToCSV">
          <Download :size="16" />
          匯出 CSV
      </button>
    </div>
    
    <div class="table-responsive">
      <table class="table table-hover align-middle mb-0">
        <thead class="bg-light">
          <tr>
            <th class="ps-4" style="width: 25%">課程</th>
            <th class="text-center" style="width: 25%">學生</th>
            <th class="text-center" style="width: 25%">評分</th>
            <th class="text-center" style="width: 25%">收益</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="course in processedCourses" :key="course.id">
            <td class="ps-4">
              <div class="fw-bold text-truncate" style="max-width: 200px">
                {{ course.title }}
              </div>
              <div class="small text-muted">{{ course.category }}</div>
            </td>
            <td class="text-center">
              <span
                class="badge bg-secondary bg-opacity-10 text-dark d-inline-flex align-items-center justify-content-center"
                style="min-width: 80px"
              >
                <Users :size="12" class="me-1" /> {{ course.students.toLocaleString() }}
              </span>
            </td>
            <td class="text-center text-warning fw-bold">
              {{ course.rating.toFixed(1) }} <Star :size="12" fill="currentColor" />
            </td>
            <td class="text-center fw-bold text-success">
              ${{ course.revenue.toLocaleString() }}
            </td>
          </tr>
          <tr v-if="processedCourses.length === 0">
            <td colspan="4" class="text-center py-4 text-muted">
              沒有符合條件的課程
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
