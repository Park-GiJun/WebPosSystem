<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 사이드바 -->
    <aside class="fixed inset-y-0 left-0 w-64 bg-indigo-700 text-white">
      <div class="p-6">
        <h1 class="text-2xl font-bold">영업정보시스템</h1>
      </div>
      <nav class="mt-6">
        <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="flex items-center px-6 py-3 text-gray-100 hover:bg-indigo-600"
            :class="{ 'bg-indigo-600': isCurrentRoute(item.path) }"
        >
          <component :is="item.icon" class="w-5 h-5 mr-3" />
          {{ item.name }}
        </router-link>
      </nav>
    </aside>

    <!-- 메인 컨텐츠 -->
    <main class="ml-64 p-8">
      <div class="flex justify-between items-center mb-8">
        <h2 class="text-2xl font-semibold">{{ currentPageTitle }}</h2>
        <div class="flex items-center space-x-4">
          <span>{{ authStore.user?.username }}</span>
          <button
              @click="handleLogout"
              class="px-4 py-2 text-sm text-white bg-indigo-600 rounded-md hover:bg-indigo-700"
          >
            로그아웃
          </button>
        </div>
      </div>
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  HomeIcon,
  ChartBarIcon,
  UsersIcon,
  CogIcon
} from '@heroicons/vue/24/outline'
import axios from '@/plugins/axios'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const menuItems = [
  { name: '대시보드', path: '/sis/dashboard', icon: HomeIcon },
  { name: '매출 관리', path: '/sis/sales', icon: ChartBarIcon },
  { name: '직원 관리', path: '/sis/employees', icon: UsersIcon },
  { name: '설정', path: '/sis/settings', icon: CogIcon }
]

const currentPageTitle = computed(() => {
  return menuItems.find(item => isCurrentRoute(item.path))?.name || ''
})

function isCurrentRoute(path) {
  return route.path === path
}

async function handleLogout() {
  try {
    await axios.post('/auth/logout')
    authStore.logout()
    await router.push('/login')
  } catch (error) {
    console.error('로그아웃 실패:', error)
  }
}
</script>