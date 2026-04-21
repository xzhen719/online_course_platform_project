<script setup>
defineProps({
  text: String,
  height: { type: String, default: '200px' },
  className: String
})
</script>

<template>
  <div 
    class="image-placeholder d-flex align-items-center justify-content-center text-center p-2 fw-bold"
    :class="className"
    :style="{ height: height }"
  >
    <div class="content">
      <span>[IMG]</span>
      <div v-if="text" class="small mt-1 text-uppercase">{{ text }}</div>
    </div>
  </div>
</template>

<style scoped>
.image-placeholder {
  background: repeating-linear-gradient(
    45deg,
    #e9ecef,
    #e9ecef 10px,
    #dee2e6 10px,
    #dee2e6 20px
  );
  color: #495057;
  border-bottom: 3px solid #000;
  overflow: hidden;
}
.content {
  background: white;
  padding: 5px 10px;
  border: 2px solid #000;
  box-shadow: 3px 3px 0 #000;
}
</style>