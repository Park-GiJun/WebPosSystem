<!-- views/errors/401.vue -->
<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center p-4">
    <div class="text-center">
      <h1 class="text-9xl font-bold text-gray-300">401</h1>
      <p class="mt-4 text-2xl font-semibold text-gray-600">인증이 필요합니다</p>
      <p class="mt-2 text-gray-500">로그인이 필요한 페이지입니다.</p>
      <router-link
          to="/login"
          class="inline-block mt-8 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
      >
        로그인하기
      </router-link>
    </div>
  </div>
</template>