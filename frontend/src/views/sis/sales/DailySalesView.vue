<!-- views/sis/sales/DailySalesView.vue -->
<template>
  <div class="p-6">
    <div class="mb-6 flex justify-between items-center">
      <h2 class="text-2xl font-bold">일별 매출 현황</h2>
      <div class="flex gap-4">
        <input
            type="date"
            v-model="selectedDate"
            class="px-4 py-2 border rounded-lg"
        />
        <button
            @click="fetchDailySales"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          조회
        </button>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-gray-500 mb-2">총 매출</h3>
        <p class="text-3xl font-bold">₩{{ totalSales.toLocaleString() }}</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-gray-500 mb-2">거래 건수</h3>
        <p class="text-3xl font-bold">{{ transactionCount }}건</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-gray-500 mb-2">평균 객단가</h3>
        <p class="text-3xl font-bold">₩{{ averagePerTransaction.toLocaleString() }}</p>
      </div>
    </div>

    <!-- Hourly Sales Chart -->
    <div class="bg-white p-6 rounded-lg shadow mb-6">
      <h3 class="text-lg font-semibold mb-4">시간대별 매출</h3>
      <div class="h-80">
        <LineChart :data="hourlySales" />
      </div>
    </div>

    <!-- Payment Methods & Categories -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold mb-4">결제 수단별 비율</h3>
        <div class="h-64">
          <PieChart :data="paymentMethods" />
        </div>
      </div>
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold mb-4">카테고리별 매출</h3>
        <div class="h-64">
          <BarChart :data="categorySales" />
        </div>
      </div>
    </div>

    <!-- Transaction List -->
    <div class="bg-white rounded-lg shadow">
      <div class="p-6 border-b">
        <h3 class="text-lg font-semibold">거래 내역</h3>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">시간</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">주문번호</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">금액</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">결제수단</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상태</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="transaction in transactions" :key="transaction.id">
            <td class="px-6 py-4">{{ transaction.time }}</td>
            <td class="px-6 py-4">#{{ transaction.orderNumber }}</td>
            <td class="px-6 py-4">₩{{ transaction.amount.toLocaleString() }}</td>
            <td class="px-6 py-4">{{ transaction.paymentMethod }}</td>
            <td class="px-6 py-4">
                <span :class="[
                  'px-2 py-1 rounded-full text-xs',
                  transaction.status === '완료' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                ]">
                  {{ transaction.status }}
                </span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { LineChart, PieChart, BarChart } from '@/components/charts'

// State
const selectedDate = ref(new Date().toISOString().split('T')[0])
const totalSales = ref(2850000)
const transactionCount = ref(142)
const averagePerTransaction = ref(Math.floor(2850000 / 142))

// Chart Data
const hourlySales = ref([
  { time: '09:00', sales: 180000 },
  { time: '10:00', sales: 250000 },
  { time: '11:00', sales: 320000 },
  { time: '12:00', sales: 450000 },
  { time: '13:00', sales: 380000 },
  { time: '14:00', sales: 290000 },
  { time: '15:00', sales: 220000 },
])

const paymentMethods = ref([
  { name: '신용카드', value: 65 },
  { name: '현금', value: 15 },
  { name: '삼성페이', value: 12 },
  { name: '카카오페이', value: 8 },
])

const categorySales = ref([
  { category: '커피', sales: 1200000 },
  { category: '논커피', sales: 800000 },
  { category: '티', sales: 450000 },
  { category: '디저트', sales: 400000 },
])

const transactions = ref([
  { id: 1, time: '15:42', orderNumber: '235', amount: 15000, paymentMethod: '신용카드', status: '완료' },
  { id: 2, time: '15:38', orderNumber: '234', amount: 8500, paymentMethod: '카카오페이', status: '완료' },
  { id: 3, time: '15:31', orderNumber: '233', amount: 12000, paymentMethod: '현금', status: '취소' },
  { id: 4, time: '15:25', orderNumber: '232', amount: 21000, paymentMethod: '삼성페이', status: '완료' },
  { id: 5, time: '15:18', orderNumber: '231', amount: 9500, paymentMethod: '신용카드', status: '완료' },
])

// Methods
const fetchDailySales = async () => {
  try {
    // API call to fetch daily sales data
    const response = await fetch(`/api/sales/daily?date=${selectedDate.value}`)
    const data = await response.json()
    // Update state with fetched data
  } catch (error) {
    console.error('Failed to fetch daily sales:', error)
  }
}

onMounted(() => {
  fetchDailySales()
})
</script>