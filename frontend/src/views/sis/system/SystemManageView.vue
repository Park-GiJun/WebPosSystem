<!-- SystemManageView.vue -->
<template>
  <div class="p-6 space-y-6">
    <!-- System Status -->
    <div class="bg-white rounded-lg shadow p-6">
      <h3 class="text-lg font-semibold mb-4">시스템 상태</h3>
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div v-for="status in systemStatus" :key="status.name"
             class="p-4 rounded-lg" :class="status.bgColor">
          <div class="flex items-center justify-between">
            <span class="text-gray-600">{{ status.name }}</span>
            <component :is="status.icon" class="w-5 h-5" :class="status.iconColor"/>
          </div>
          <div class="mt-2">
            <span class="text-2xl font-bold" :class="status.textColor">{{ status.value }}</span>
            <span class="text-sm ml-1" :class="status.textColor">{{ status.unit }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Latest Logs -->
    <div class="bg-white rounded-lg shadow p-6">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-semibold">시스템 로그</h3>
        <button class="text-sm text-blue-600 hover:text-blue-800">전체보기</button>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead>
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">시간</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">레벨</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">메시지</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">IP</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="log in systemLogs" :key="log.id">
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ log.timestamp }}</td>
            <td class="px-6 py-4 whitespace-nowrap">
               <span :class="getLogLevelClass(log.level)" class="px-2 py-1 text-xs rounded-full">
                 {{ log.level }}
               </span>
            </td>
            <td class="px-6 py-4 text-sm text-gray-900">{{ log.message }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ log.ip }}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Configuration -->
    <div class="bg-white rounded-lg shadow p-6">
      <h3 class="text-lg font-semibold mb-4">시스템 설정</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div v-for="config in systemConfig" :key="config.id" class="space-y-2">
          <div class="flex justify-between items-center">
            <label class="text-sm font-medium text-gray-700">{{ config.name }}</label>
            <Switch v-model="config.enabled" class="ml-4" />
          </div>
          <p class="text-sm text-gray-500">{{ config.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Switch } from '@headlessui/vue'
import { Activity, Server, Database, Memory } from 'lucide-vue-next'

const systemStatus = ref([
  {
    name: 'CPU 사용량',
    value: '45',
    unit: '%',
    icon: Activity,
    bgColor: 'bg-blue-50',
    iconColor: 'text-blue-600',
    textColor: 'text-blue-900'
  },
  {
    name: '메모리 사용량',
    value: '3.2',
    unit: 'GB',
    icon: Memory,
    bgColor: 'bg-green-50',
    iconColor: 'text-green-600',
    textColor: 'text-green-900'
  },
  {
    name: '서버 상태',
    value: '정상',
    unit: '',
    icon: Server,
    bgColor: 'bg-yellow-50',
    iconColor: 'text-yellow-600',
    textColor: 'text-yellow-900'
  },
  {
    name: 'DB 연결',
    value: '연결됨',
    unit: '',
    icon: Database,
    bgColor: 'bg-purple-50',
    iconColor: 'text-purple-600',
    textColor: 'text-purple-900'
  }
])

const systemLogs = ref([
  {
    id: 1,
    timestamp: '2024-01-26 15:23:45',
    level: 'ERROR',
    message: 'Failed to connect to payment gateway',
    ip: '192.168.1.100'
  },
  {
    id: 2,
    timestamp: '2024-01-26 15:22:30',
    level: 'INFO',
    message: 'User authentication successful',
    ip: '192.168.1.101'
  },
  {
    id: 3,
    timestamp: '2024-01-26 15:21:15',
    level: 'WARNING',
    message: 'High memory usage detected',
    ip: '192.168.1.102'
  }
])

const systemConfig = ref([
  {
    id: 1,
    name: '자동 백업',
    description: '매일 자정에 시스템 데이터 자동 백업',
    enabled: true
  },
  {
    id: 2,
    name: '로그 보관',
    description: '시스템 로그 30일간 보관',
    enabled: true
  },
  {
    id: 3,
    name: '이메일 알림',
    description: '시스템 오류 발생 시 관리자에게 이메일 발송',
    enabled: false
  },
  {
    id: 4,
    name: '자동 업데이트',
    description: '새로운 버전 출시 시 자동 업데이트',
    enabled: false
  }
])

const getLogLevelClass = (level) => {
  const classes = {
    ERROR: 'bg-red-100 text-red-800',
    WARNING: 'bg-yellow-100 text-yellow-800',
    INFO: 'bg-blue-100 text-blue-800'
  }
  return classes[level]
}
</script>