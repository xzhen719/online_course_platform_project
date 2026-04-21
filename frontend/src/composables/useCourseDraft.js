/**
 * COURSE DRAFT COMPOSABLE
 * Shared state for course creation wizard.
 * Data persists in memory until instructor saves or cancels.
 */

import { ref, computed } from 'vue'

// Shared state (module-level, persists across navigation)
const courseInfo = ref({
    name: '',
    price: 0,
    categoryId: null,
    description: '',
    imageUrl: '',
    videoUrl: '',
    instructorName: '',
})

const chapters = ref([])
const isCreating = ref(false)

export function useCourseDraft() {
    const chapterCount = computed(() => chapters.value.length)

    function setCourseInfo(info) {
        courseInfo.value = { ...info }
    }

    function setChapters(list) {
        chapters.value = list
    }

    function startCreating() {
        isCreating.value = true
    }

    function reset() {
        courseInfo.value = {
            name: '',
            price: 0,
            categoryId: null,
            description: '',
            imageUrl: '',
            videoUrl: '',
            instructorName: '',
        }
        chapters.value = []
        isCreating.value = false
    }

    return {
        courseInfo,
        chapters,
        isCreating,
        chapterCount,
        setCourseInfo,
        setChapters,
        startCreating,
        reset,
    }
}
