<!-- views/errors/404.vue -->
<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center p-4">
    <div class="text-center">
      <h1 class="text-9xl font-bold text-gray-300">404</h1>
      <p class="mt-4 text-2xl font-semibold text-gray-600">페이지를 찾을 수 없습니다</p>
      <p class="mt-2 text-gray-500">요청하신 페이지가 존재하지 않거나 삭제되었습니다.</p>
      <router-link
          to="/"
          class="inline-block mt-8 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
      >
        홈으로 돌아가기
      </router-link>
    </div>
  </div>
</template>