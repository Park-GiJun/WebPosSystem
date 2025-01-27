<!-- SettingsView.vue -->
<template>
  <div class="p-6 space-y-6">
    <!-- Profile Settings -->
    <div class="bg-white rounded-lg shadow p-6">
      <h3 class="text-lg font-semibold mb-4">프로필 설정</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label class="block text-sm font-medium text-gray-700">이름</label>
          <input type="text" v-model="profile.name"
                 class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700">이메일</label>
          <input type="email" v-model="profile.email"
                 class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700">전화번호</label>
          <input type="tel" v-model="profile.phone"
                 class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700">부서</label>
          <select v-model="profile.department"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
            <option v-for="dept in departments" :key="dept" :value="dept">{{ dept }}</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Notification Settings -->
    <div class="bg-white rounded-lg shadow p-6">
      <h3 class="text-lg font-semibold mb-4">알림 설정</h3>
      <div class="space-y-4">
        <div v-for="notification in notifications" :key="notification.id"
             class="flex items-center justify-between">
          <div>
            <p class="font-medium">{{ notification.name }}</p>
            <p class="text-sm text-gray-500">{{ notification.description }}</p>
          </div>
          <Switch v-model="notification.enabled" />
        </div>
      </div>
    </div>

    <!-- Theme Settings -->
    <div class="bg-white rounded-lg shadow p-6">
      <h3 class="text-lg font-semibold mb-4">테마 설정</h3>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div v-for="theme in themes" :key="theme.id"
             @click="selectTheme(theme.id)"
             :class="[
               'p-4 rounded-lg cursor-pointer border-2',
               selectedTheme === theme.id ? 'border-blue-500' : 'border-transparent'
             ]">
          <div class="h-20 rounded-md" :style="{ backgroundColor: theme.color }"></div>
          <p class="mt-2 text-sm font-medium text-center">{{ theme.name }}</p>
        </div>
      </div>
    </div>

    <!-- Action Buttons -->
    <div class="flex justify-end space-x-4">
      <button class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
        취소
      </button>
      <button class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700">
        저장
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Switch } from '@headlessui/vue'

const departments = ['영업부', '마케팅부', 'IT부서', '인사부']

const profile = ref({
  name: '홍길동',
  email: 'hong@example.com',
  phone: '010-1234-5678',
  department: '영업부'
})

const notifications = ref([
  {
    id: 1,
    name: '이메일 알림',
    description: '중요 알림을 이메일로 받기',
    enabled: true
  },
  {
    id: 2,
    name: '푸시 알림',
    description: '실시간 알림 받기',
    enabled: true
  },
  {
    id: 3,
    name: '마케팅 알림',
    description: '프로모션 및 이벤트 알림',
    enabled: false
  }
])

const themes = ref([
  { id: 1, name: '라이트', color: '#ffffff' },
  { id: 2, name: '다크', color: '#1a1a1a' },
  { id: 3, name: '블루', color: '#3b82f6' },
  { id: 4, name: '그린', color: '#10b981' }
])

const selectedTheme = ref(1)

const selectTheme = (themeId) => {
  selectedTheme.value = themeId
}
</script>