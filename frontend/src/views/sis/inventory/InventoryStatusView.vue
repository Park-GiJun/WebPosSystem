<!-- InventoryStatusView.vue -->
<template>
  <div class="p-6 space-y-6">
    <!-- 필터 -->
    <div class="bg-white p-4 rounded-lg shadow flex items-center gap-4">
      <input
          type="text"
          v-model="search"
          placeholder="상품명 또는 코드 검색"
          class="w-64 rounded-md border-gray-300"
      >
      <select v-model="filters.category" class="rounded-md border-gray-300">
        <option value="">전체 카테고리</option>
        <option v-for="category in categories" :key="category">{{ category }}</option>
      </select>
      <select v-model="filters.location" class="rounded-md border-gray-300">
        <option value="">전체 위치</option>
        <option v-for="location in locations" :key="location">{{ location }}</option>
      </select>
      <button class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">
        검색
      </button>
    </div>

    <!-- 재고 현황 요약 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">총 재고 수량</h4>
        <p class="mt-2 text-2xl font-bold">{{ totalStock }}개</p>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">총 재고 금액</h4>
        <p class="mt-2 text-2xl font-bold">{{ formatNumber(totalValue) }}원</p>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">안전재고 미달</h4>
        <p class="mt-2 text-2xl font-bold text-red-600">{{ lowStock }}개</p>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">과다재고</h4>
        <p class="mt-2 text-2xl font-bold text-yellow-600">{{ excessStock }}개</p>
      </div>
    </div>

    <!-- 재고 목록 -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead>
        <tr>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">상품코드</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">상품명</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">카테고리</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">현재고</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">적정재고</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">단가</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">재고금액</th>
          <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">상태</th>
        </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
        <tr v-for="item in inventory" :key="item.id">
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ item.code }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ item.name }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ item.category }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ item.stock }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ item.optimal }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ formatNumber(item.price) }}원</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ formatNumber(item.value) }}원</td>
          <td class="px-6 py-4 whitespace-nowrap">
             <span :class="getStatusClass(item.status)" class="px-2 py-1 text-xs rounded-full">
               {{ item.status }}
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

const search = ref('')
const filters = ref({
  category: '',
  location: ''
})

const categories = ['원두', '시럽', '베이커리', '머그컵']
const locations = ['본사 창고', '강남점 창고', '홍대점 창고']

// 재고 현황 요약
const totalStock = ref(2580)
const totalValue = ref(45680000)
const lowStock = ref(12)
const excessStock = ref(8)

// 재고 목록
const inventory = ref([
  {
    id: 1,
    code: 'P001',
    name: '에티오피아 예가체프',
    category: '원두',
    stock: 250,
    optimal: 200,
    price: 25000,
    value: 6250000,
    status: '정상'
  },
  {
    id: 2,
    code: 'P002',
    name: '바닐라 시럽',
    category: '시럽',
    stock: 50,
    optimal: 100,
    price: 12000,
    value: 600000,
    status: '부족'
  },
  {
    id: 3,
    code: 'P003',
    name: '크로와상',
    category: '베이커리',
    stock: 180,
    optimal: 120,
    price: 2500,
    value: 450000,
    status: '과다'
  }
])

const formatNumber = (value) => {
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const getStatusClass = (status) => {
  return {
    '정상': 'bg-green-100 text-green-800',
    '부족': 'bg-red-100 text-red-800',
    '과다': 'bg-yellow-100 text-yellow-800'
  }[status]
}
</script>