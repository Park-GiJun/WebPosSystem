<!-- InventoryMovementView.vue -->
<template>
  <div class="p-6 space-y-6">
    <!-- 입출고 등록 버튼 -->
    <div class="flex justify-end space-x-4">
      <button @click="openInbound" class="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
        입고 등록
      </button>
      <button @click="openOutbound" class="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700">
        출고 등록
      </button>
    </div>

    <!-- 필터 -->
    <div class="bg-white p-4 rounded-lg shadow">
      <div class="grid grid-cols-5 gap-4">
        <div>
          <label class="text-sm font-medium text-gray-700">기간</label>
          <div class="mt-1 flex items-center gap-2">
            <input type="date" v-model="filters.startDate" class="rounded-md border-gray-300">
            <span>~</span>
            <input type="date" v-model="filters.endDate" class="rounded-md border-gray-300">
          </div>
        </div>
        <div>
          <label class="text-sm font-medium text-gray-700">유형</label>
          <select v-model="filters.type" class="mt-1 w-full rounded-md border-gray-300">
            <option value="">전체</option>
            <option value="inbound">입고</option>
            <option value="outbound">출고</option>
          </select>
        </div>
        <div>
          <label class="text-sm font-medium text-gray-700">상품</label>
          <input
              type="text"
              v-model="filters.product"
              placeholder="상품명 검색"
              class="mt-1 w-full rounded-md border-gray-300"
          >
        </div>
        <div>
          <label class="text-sm font-medium text-gray-700">창고</label>
          <select v-model="filters.location" class="mt-1 w-full rounded-md border-gray-300">
            <option value="">전체 창고</option>
            <option v-for="location in locations" :key="location">{{ location }}</option>
          </select>
        </div>
        <div class="flex items-end">
          <button @click="search" class="w-full bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
            검색
          </button>
        </div>
      </div>
    </div>

    <!-- 입출고 내역 -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead>
        <tr>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">일시</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">유형</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">상품명</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">수량</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">창고</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">담당자</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">비고</th>
        </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
        <tr v-for="movement in movements" :key="movement.id">
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ movement.datetime }}</td>
          <td class="px-6 py-4 whitespace-nowrap">
              <span :class="getTypeClass(movement.type)" class="px-2 py-1 text-xs rounded-full">
                {{ movement.type === 'inbound' ? '입고' : '출고' }}
              </span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ movement.product }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ movement.quantity }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ movement.location }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ movement.manager }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ movement.note }}</td>
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
  type: '',
  product: '',
  location: ''
})

const locations = ['본사 창고', '강남점 창고', '홍대점 창고']

const movements = ref([
  {
    id: 1,
    datetime: '2024-01-26 15:30',
    type: 'inbound',
    product: '에티오피아 예가체프',
    quantity: 100,
    location: '본사 창고',
    manager: '김영희',
    note: '정기 입고'
  },
  {
    id: 2,
    datetime: '2024-01-26 14:20',
    type: 'outbound',
    product: '바닐라 시럽',
    quantity: 24,
    location: '강남점 창고',
    manager: '이철수',
    note: '강남점 출고'
  },
  {
    id: 3,
    datetime: '2024-01-26 11:15',
    type: 'inbound',
    product: '크로와상',
    quantity: 50,
    location: '홍대점 창고',
    manager: '박지민',
    note: '긴급 발주'
  }
])

const getTypeClass = (type) => {
  return type === 'inbound' ? 'bg-blue-100 text-blue-800' : 'bg-green-100 text-green-800'
}

const openInbound = () => {
  // 입고 등록 모달
}

const openOutbound = () => {
  // 출고 등록 모달
}

const search = () => {
  // 검색 로직
}
</script>