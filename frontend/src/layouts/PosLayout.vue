<!-- POSLayout.vue -->
<template>
  <div class="h-full bg-gray-100">
    <!-- Top Bar -->
    <header class="bg-white shadow-sm p-4 flex justify-between items-center">
      <div class="flex items-center space-x-4">
        <h1 class="text-xl font-bold" @click="selectMenu">WEB POS</h1>
        <span class="text-gray-500">{{ currentDate }}</span>
      </div>
      <div class="flex items-center space-x-4">
        <span>{{ storeName }}</span>
        <button @click="logout" class="text-red-600">로그아웃</button>
      </div>
    </header>

    <!-- Main Content -->
    <main class="h-[calc(100vh-4rem)]">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const storeName = ref('카페 길준')
const currentDate = ref(new Date().toLocaleDateString())

const logout = () => {
  localStorage.removeItem('token')
  router.push('/')
}

const selectMenu = () => {
  router.push('/system')
}
</script>