<script setup>
import { computed, onMounted, ref } from 'vue'
import axios from 'axios'
import { useMainStore } from '../stores'
import CourseCard from '../components/CourseCard.vue'
import { ArrowRight, BookOpen, Users, Award, TrendingUp } from 'lucide-vue-next'
import ImagePlaceholder from '../components/ImagePlaceholder.vue'

const store = useMainStore()
const featured = computed(() => store.publishedCoursesWithFavorites.slice(0, 4))
const quote = ref('')
const author = ref('')
const quoteLoading = ref(true)

onMounted(async() => {
  try {
    const response = await axios.get('https://api.api-ninjas.com/v2/randomquotes', {
      headers: {
        'X-Api-Key': import.meta.env.VITE_QUOTE_API_KEY
      }
    })
    quote.value = response.data[0].quote
    author.value = response.data[0].author
  } catch (error) {
    quote.value = 'Learning is a treasure that will follow its owner everywhere.'
    author.value = 'Chinese Proverb'
  } finally {
    quoteLoading.value = false
  }
})
</script>

<template>
  <div>
    <!-- Hero -->
    <section class="bg-light py-5 border-bottom border-dark">
      <div class="container-fluid px-5">
        <div class="row align-items-center">
          <div class="col-lg-4">
            <h1 class="display-3 fw-bold mb-4">
              每日一句
              <p style="font-size: 0.7em; color: #3790c7">
                開啟學習
              </p>
            </h1>
            <p v-if="quoteLoading" class="lead mb-4 font-monospace text-muted">
              Just one sec...
            </p>
            <p v-else class="lead mb-4 font-monospace">
              "{{ quote }}" -<i class="fs-6">{{ author }}</i>
            </p>
            <div class="d-flex gap-3">
              <router-link to="/courses" class="btn btn-success btn-lg"
                >探索 <ArrowRight :size="20"
              /></router-link>
              <router-link to="/register" class="btn btn-outline-dark btn-lg"
                >立刻註冊
              </router-link>
            </div>
          </div>
          <div class="col-lg-8 mt-4 mt-lg-0">
            <div class="card p-2 bg-dark">
              <div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
                <div class="carousel-indicators">
                  <button
                    type="button"
                    data-bs-target="#heroCarousel"
                    data-bs-slide-to="0"
                    class="active"
                    aria-current="true"
                    aria-label="Slide 1"
                  ></button>
                  <button
                    type="button"
                    data-bs-target="#heroCarousel"
                    data-bs-slide-to="1"
                    aria-label="Slide 2"
                  ></button>
                  <button
                    type="button"
                    data-bs-target="#heroCarousel"
                    data-bs-slide-to="2"
                    aria-label="Slide 3"
                  ></button>
                </div>
                <div class="carousel-inner">
                  <div class="carousel-item active">
                    <img
                      src="`/campaign/campaign1.png`"
                      alt="campaign1"
                      class="d-block w-100"
                      style="height: 400px"
                    />
                  </div>
                  <div class="carousel-item">
                    <img
                      src="`/campaign/campaign2.jpg`"
                      alt="campaign2"
                      class="d-block w-100"
                      style="height: 400px"
                    />
                  </div>
                  <div class="carousel-item">
                    <img
                      src="`/campaign/campaign3.jpg`"
                      alt="campaign3"
                      class="d-block w-100"
                      style="height: 400px"
                    />
                  </div>
                </div>
                <button
                  class="carousel-control-prev"
                  type="button"
                  data-bs-target="#heroCarousel"
                  data-bs-slide="prev"
                >
                  <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                  <span class="visually-hidden">Previous</span>
                </button>
                <button
                  class="carousel-control-next"
                  type="button"
                  data-bs-target="#heroCarousel"
                  data-bs-slide="next"
                >
                  <span class="carousel-control-next-icon" aria-hidden="true"></span>
                  <span class="visually-hidden">Next</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured -->
    <section class="py-5">
      <div class="container">
        <div class="d-flex justify-content-between align-items-end mb-4">
          <h2>熱門課程</h2>
          <router-link to="/courses" class="btn btn-sm btn-outline-dark">所有課程</router-link>
        </div>
        <div class="row g-4">
          <div v-for="course in featured" :key="course.id" class="col-md-6 col-lg-3">
            <CourseCard :course="course" />
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
