<!-- views/errors/500.vue -->
<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center p-4">
    <div class="text-center">
      <h1 class="text-9xl font-bold text-gray-300">500</h1>
      <p class="mt-4 text-2xl font-semibold text-gray-600">서버 오류</p>
      <p class="mt-2 text-gray-500">일시적인 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.</p>
      <button
          @click="refreshPage"
          class="inline-block mt-8 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
      >
        새로고침
      </button>
    </div>
  </div>
</template>

<script setup>
const refreshPage = () => {
  window.location.reload();
}
</script>