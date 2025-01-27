<!-- views/sis/inventory/InventoryHistoryView.vue -->
<template>
  <div class="p-6">
    <div class="mb-6 flex justify-between items-center">
      <h2 class="text-2xl font-bold">재고 이력 조회</h2>
      <div class="flex gap-4">
        <select
            v-model="selectedPeriod"
            class="px-4 py-2 border rounded-lg"
        >
          <option value="7">최근 7일</option>
          <option value="30">최근 30일</option>
          <option value="90">최근 90일</option>
          <option value="custom">직접 설정</option>
        </select>
        <div v-if="selectedPeriod === 'custom'" class="flex gap-2">
          <input
              type="date"
              v-model="startDate"
              class="px-4 py-2 border rounded-lg"
          />
          <span class="flex items-center">~</span>
          <input
              type="date"
              v-model="endDate"
              class="px-4 py-2 border rounded-lg"
          />
        </div>
        <button
            @click="fetchHistory"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          조회
        </button>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-gray-500 mb-2">총 입고량</h3>
        <p class="text-3xl font-bold">{{ totalInbound.toLocaleString() }}개</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-gray-500 mb-2">총 출고량</h3>
        <p class="text-3xl font-bold">{{ totalOutbound.toLocaleString() }}개</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-gray-500 mb-2">재고 변동률</h3>
        <p class="text-3xl font-bold" :class="stockChangeRate >= 0 ? 'text-blue-600' : 'text-red-600'">
          {{ stockChangeRate >= 0 ? '+' : '' }}{{ stockChangeRate }}%
        </p>
      </div>
    </div>

    <!-- History Filter -->
    <div class="bg-white p-6 rounded-lg shadow mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <select v-model="selectedCategory" class="px-4 py-2 border rounded-lg">
          <option value="">전체 카테고리</option>
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
        <select v-model="selectedProduct" class="px-4 py-2 border rounded-lg">
          <option value="">전체 상품</option>
          <option v-for="product in filteredProducts" :key="product.id" :value="product.id">
            {{ product.name }}
          </option>
        </select>
        <select v-model="historyType" class="px-4 py-2 border rounded-lg">
          <option value="all">전체 이력</option>
          <option value="inbound">입고</option>
          <option value="outbound">출고</option>
          <option value="adjustment">조정</option>
        </select>
        <button
            @click="applyFilter"
            class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
        >
          필터 적용
        </button>
      </div>
    </div>

    <!-- Stock Change Chart -->
    <div class="bg-white p-6 rounded-lg shadow mb-6">
      <h3 class="text-lg font-semibold mb-4">재고 변동 추이</h3>
      <div class="h-80">
        <LineChart
            :data="stockChanges"
            xKey="date"
            yKey="stock"
            color="#3B82F6"
        />
      </div>
    </div>

    <!-- History Table -->
    <div class="bg-white rounded-lg shadow">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">일시</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품코드</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품명</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">유형</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">수량</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">이전재고</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">현재재고</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">담당자</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">비고</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="history in historyList" :key="history.id">
            <td class="px-6 py-4">{{ formatDate(history.timestamp) }}</td>
            <td class="px-6 py-4">{{ history.productCode }}</td>
            <td class="px-6 py-4">{{ history.productName }}</td>
            <td class="px-6 py-4">
                <span :class="[
                  'px-2 py-1 rounded-full text-xs',
                  history.type === '입고' ? 'bg-green-100 text-green-800' :
                  history.type === '출고' ? 'bg-blue-100 text-blue-800' :
                  'bg-yellow-100 text-yellow-800'
                ]">
                  {{ history.type }}
                </span>
            </td>
            <td class="px-6 py-4" :class="history.quantity >= 0 ? 'text-blue-600' : 'text-red-600'">
              {{ history.quantity >= 0 ? '+' : '' }}{{ history.quantity }}
            </td>
            <td class="px-6 py-4">{{ history.previousStock }}</td>
            <td class="px-6 py-4">{{ history.currentStock }}</td>
            <td class="px-6 py-4">{{ history.handler }}</td>
            <td class="px-6 py-4">{{ history.note }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="flex justify-between items-center px-6 py-4 border-t">
        <div class="text-sm text-gray-500">
          총 {{ totalRecords }}건
        </div>
        <div class="flex gap-2">
          <button
              v-for="page in totalPages"
              :key="page"
              @click="currentPage = page"
              :class="[
              'px-3 py-1 rounded',
              currentPage === page ? 'bg-blue-600 text-white' : 'bg-gray-100'
            ]"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import  LineChart from '@/components/charts/LineChart.vue'

// State
const selectedPeriod = ref('7')
const startDate = ref('')
const endDate = ref('')
const selectedCategory = ref('')
const selectedProduct = ref('')
const historyType = ref('all')
const currentPage = ref(1)

const totalInbound = ref(1850)
const totalOutbound = ref(1420)
const stockChangeRate = ref(8.5)

// Sample Data
const categories = ref([
  { id: 1, name: '커피' },
  { id: 2, name: '논커피' },
  { id: 3, name: '티' },
  { id: 4, name: '디저트' },
])

const products = ref([
  { id: 1, categoryId: 1, name: '아메리카노', code: 'P001' },
  { id: 2, categoryId: 1, name: '카페라떼', code: 'P002' },
  { id: 3, categoryId: 2, name: '초코라떼', code: 'P003' },
  { id: 4, categoryId: 3, name: '얼그레이', code: 'P004' },
])

const stockChanges = ref([
  { date: '2024-01-20', stock: 1200 },
  { date: '2024-01-21', stock: 1350 },
  { date: '2024-01-22', stock: 1280 },
  { date: '2024-01-23', stock: 1420 },
  { date: '2024-01-24', stock: 1380 },
  { date: '2024-01-25', stock: 1520 },
  { date: '2024-01-26', stock: 1630 },
])

const historyList = ref([
  {
    id: 1,
    timestamp: '2024-01-26T14:30:00',
    productCode: 'P001',
    productName: '아메리카노',
    type: '입고',
    quantity: 100,
    previousStock: 150,
    currentStock: 250,
    handler: '김관리',
    note: '정기 입고'
  },
  {
    id: 2,
    timestamp: '2024-01-26T13:15:00',
    productCode: 'P002',
    productName: '카페라떼',
    type: '출고',
    quantity: -30,
    previousStock: 120,
    currentStock: 90,
    handler: '이직원',
    note: '판매'
  },
  {
    id: 3,
    timestamp: '2024-01-26T11:45:00',
    productCode: 'P003',
    productName: '초코라떼',
    type: '조정',
    quantity: -5,
    previousStock: 85,
    currentStock: 80,
    handler: '박매니저',
    note: '재고실사'
  },
])

// Computed
const filteredProducts = computed(() => {
  if (!selectedCategory.value) return products.value
  return products.value.filter(p => p.categoryId === selectedCategory.value)
})

const totalRecords = ref(42)
const totalPages = computed(() => Math.ceil(totalRecords.value / 10))

// Methods
const fetchHistory = async () => {
  try {
    // API call to fetch history data
    const response = await fetch('/api/inventory/history', {
      method: 'POST',
      body: JSON.stringify({
        period: selectedPeriod.value,
        startDate: startDate.value,
        endDate: endDate.value,
        category: selectedCategory.value,
        product: selectedProduct.value,
        type: historyType.value,
        page: currentPage.value
      })
    })
    const data = await response.json()
    // Update state with fetched data
  } catch (error) {
    console.error('Failed to fetch history:', error)
  }
}

const applyFilter = () => {
  currentPage.value = 1
  fetchHistory()
}

const formatDate = (timestamp) => {
  return new Date(timestamp).toLocaleString()
}
</script>