<!-- AttendanceView.vue -->
<template>
  <div class="p-6 space-y-6">
    <!-- 날짜 선택 및 필터 -->
    <div class="bg-white p-4 rounded-lg shadow flex items-center justify-between">
      <div class="flex gap-4">
        <div class="flex items-center gap-2">
          <span class="text-sm font-medium text-gray-700">기간</span>
          <input type="date" v-model="filters.startDate" class="rounded-md border-gray-300">
          <span>~</span>
          <input type="date" v-model="filters.endDate" class="rounded-md border-gray-300">
        </div>
        <select v-model="filters.department" class="rounded-md border-gray-300">
          <option value="">전체 부서</option>
          <option v-for="dept in departments" :key="dept">{{ dept }}</option>
        </select>
      </div>
      <div class="flex gap-3">
        <button class="px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200">
          엑셀 다운로드
        </button>
        <button class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">
          조회
        </button>
      </div>
    </div>

    <!-- 근태 현황 요약 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">정상 출근</h4>
        <p class="mt-2 text-2xl font-bold text-blue-600">{{ summary.onTime }}명</p>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">지각</h4>
        <p class="mt-2 text-2xl font-bold text-yellow-600">{{ summary.late }}명</p>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">결근</h4>
        <p class="mt-2 text-2xl font-bold text-red-600">{{ summary.absent }}명</p>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">휴가</h4>
        <p class="mt-2 text-2xl font-bold text-green-600">{{ summary.vacation }}명</p>
      </div>
    </div>

    <!-- 근태 기록 테이블 -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead>
        <tr>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            날짜
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            이름
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            부서
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            출근시간
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            퇴근시간
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            근무시간
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            상태
          </th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
        <tr v-for="record in attendanceRecords" :key="record.id">
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
            {{ record.date }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex items-center">
              <div class="text-sm font-medium text-gray-900">{{ record.name }}</div>
            </div>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{ record.department }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{ record.checkIn }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{ record.checkOut }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{ record.workHours }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
              <span :class="getStatusClass(record.status)" class="px-2 py-1 text-xs rounded-full">
                {{ record.status }}
              </span>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const filters = ref({
  startDate: '',
  endDate: '',
  department: ''
})

const departments = ['영업부', '마케팅부', 'IT부서', '인사부']

const summary = ref({
  onTime: 42,
  late: 3,
  absent: 1,
  vacation: 4
})

const attendanceRecords = ref([
  {
    id: 1,
    date: '2024-01-26',
    name: '홍길동',
    department: '영업부',
    checkIn: '09:00:00',
    checkOut: '18:00:00',
    workHours: '8시간',
    status: '정상'
  },
  {
    id: 2,
    date: '2024-01-26',
    name: '김영희',
    department: 'IT부서',
    checkIn: '09:15:00',
    checkOut: '18:30:00',
    workHours: '8시간 15분',
    status: '지각'
  },
  {
    id: 3,
    date: '2024-01-26',
    name: '이철수',
    department: '마케팅부',
    checkIn: '-',
    checkOut: '-',
    workHours: '-',
    status: '휴가'
  }
])

const getStatusClass = (status) => {
  return {
    '정상': 'bg-green-100 text-green-800',
    '지각': 'bg-yellow-100 text-yellow-800',
    '결근': 'bg-red-100 text-red-800',
    '휴가': 'bg-blue-100 text-blue-800'
  }[status]
}
</script>