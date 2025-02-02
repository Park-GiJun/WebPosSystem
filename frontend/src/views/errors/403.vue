<!-- views/errors/403.vue -->
<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center p-4">
    <div class="text-center">
      <h1 class="text-9xl font-bold text-gray-300">403</h1>
      <p class="mt-4 text-2xl font-semibold text-gray-600">접근 권한이 없습니다</p>
      <p class="mt-2 text-gray-500">이 페이지에 대한 접근 권한이 없습니다.</p>
      <router-link
          to="/"
          class="inline-block mt-8 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
      >
        홈으로 돌아가기
      </router-link>
    </div>
  </div>
</template>