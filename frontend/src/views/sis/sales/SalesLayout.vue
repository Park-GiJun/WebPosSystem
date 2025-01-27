<!-- SalesLayout.vue -->
<template>
  <div class="min-h-full">
    <!-- 서브 네비게이션 -->
    <div class="bg-white border-b">
      <div class="px-6 py-3">
        <nav class="flex space-x-4">
          <router-link
              v-for="item in navItems"
              :key="item.path"
              :to="item.path"
              class="px-3 py-2 text-sm font-medium rounded-md"
              :class="[
             isCurrentRoute(item.path)
               ? 'bg-blue-100 text-blue-700'
               : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
           ]"
          >
            {{ item.name }}
          </router-link>
        </nav>
      </div>
    </div>

    <!-- 컨텐츠 영역 -->
    <div class="h-full">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()

const navItems = [
  { name: '매출 현황', path: '/sis/sales/status' },
  { name: '매출 분석', path: '/sis/sales/analysis' },
  { name: '매출 리포트', path: '/sis/sales/report' }
]

const isCurrentRoute = (path) => route.path === path
</script>