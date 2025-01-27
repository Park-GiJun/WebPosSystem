<!-- SalesStatusView.vue -->
<template>
  <div class="p-6 space-y-6">
    <!-- 매출 개요 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div v-for="stat in salesStats" :key="stat.name"
           class="bg-white p-6 rounded-lg shadow">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-sm text-gray-600">{{ stat.name }}</p>
            <p class="mt-2 text-2xl font-bold">{{ formatNumber(stat.value) }}{{ stat.unit }}</p>
          </div>
          <component :is="stat.icon"
                     class="w-8 h-8"
                     :class="stat.iconColor" />
        </div>
        <div class="mt-2 flex items-center text-sm">
          <ArrowUp v-if="stat.trend > 0" class="w-4 h-4 text-green-500" />
          <ArrowDown v-else class="w-4 h-4 text-red-500" />
          <span :class="stat.trend > 0 ? 'text-green-500' : 'text-red-500'">
           {{ Math.abs(stat.trend) }}%
         </span>
          <span class="ml-2 text-gray-500">전월 대비</span>
        </div>
      </div>
    </div>

    <!-- 시간대별 매출 그래프 -->
    <div class="bg-white p-6 rounded-lg shadow">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-semibold">시간대별 매출</h3>
        <select v-model="selectedDate" class="rounded-md border-gray-300">
          <option v-for="date in dates" :key="date" :value="date">{{ date }}</option>
        </select>
      </div>
      <div class="h-[400px]">
        <HourlyChart :data="hourlyData" />
      </div>
    </div>

    <!-- 매장별 매출 순위 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold mb-4">매장별 매출 순위</h3>
        <div class="space-y-4">
          <div v-for="(store, index) in storeRankings" :key="store.id"
               class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
            <div class="flex items-center">
             <span class="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold">
               {{ index + 1 }}
             </span>
              <div class="ml-4">
                <p class="font-medium">{{ store.name }}</p>
                <p class="text-sm text-gray-500">판매건수: {{ store.transactions }}건</p>
              </div>
            </div>
            <div class="text-right">
              <p class="font-bold">{{ formatNumber(store.revenue) }}원</p>
              <p class="text-sm" :class="store.growth >= 0 ? 'text-green-500' : 'text-red-500'">
                {{ store.growth > 0 ? '+' : '' }}{{ store.growth }}%
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- 결제수단별 비율 -->
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold mb-4">결제수단별 비율</h3>
        <div class="h-[300px]">
          <PaymentMethodChart :data="paymentMethodData" />
        </div>
      </div>
    </div>

    <!-- 최근 거래 내역 -->
    <div class="bg-white p-6 rounded-lg shadow overflow-hidden">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-semibold">최근 거래 내역</h3>
        <button class="text-blue-600 hover:text-blue-800">전체보기</button>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead>
          <tr>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              주문번호
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              매장
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              상품
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              결제금액
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              결제수단
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              시간
            </th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="transaction in recentTransactions" :key="transaction.id">
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ transaction.orderId }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ transaction.store }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ transaction.items }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ formatNumber(transaction.amount) }}원
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ transaction.paymentMethod }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ transaction.timestamp }}
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ArrowUp, ArrowDown, TrendingUp, Users, CreditCard, ShoppingBag } from 'lucide-vue-next'
import HourlyChart from '@/components/HourlyChart.vue'
import PaymentMethodChart from '@/components/PaymentMethodChart.vue'

// 매출 통계 데이터
const salesStats = [
  {
    name: '일간 매출',
    value: 2450000,
    unit: '원',
    trend: 12.5,
    icon: TrendingUp,
    iconColor: 'text-blue-500'
  },
  {
    name: '거래건수',
    value: 342,
    unit: '건',
    trend: 8.2,
    icon: ShoppingBag,
    iconColor: 'text-green-500'
  },
  {
    name: '평균 객단가',
    value: 15600,
    unit: '원',
    trend: -2.3,
    icon: CreditCard,
    iconColor: 'text-purple-500'
  },
  {
    name: '방문객수',
    value: 289,
    unit: '명',
    trend: 5.7,
    icon: Users,
    iconColor: 'text-orange-500'
  }
]

// 시간대별 매출 데이터
const dates = ['2024-01-26', '2024-01-25', '2024-01-24']
const selectedDate = ref(dates[0])
const hourlyData = ref([
  { hour: '08:00', sales: 150000 },
  { hour: '09:00', sales: 280000 },
  { hour: '10:00', sales: 420000 },
  { hour: '11:00', sales: 680000 },
  { hour: '12:00', sales: 920000 },
  { hour: '13:00', sales: 850000 },
  { hour: '14:00', sales: 460000 },
  { hour: '15:00', sales: 380000 },
  { hour: '16:00', sales: 420000 },
  { hour: '17:00', sales: 580000 },
  { hour: '18:00', sales: 750000 },
  { hour: '19:00', sales: 620000 }
])

// 매장별 매출 순위
const storeRankings = [
  {
    id: 1,
    name: '강남점',
    revenue: 12500000,
    transactions: 845,
    growth: 12.3
  },
  {
    id: 2,
    name: '홍대점',
    revenue: 9800000,
    transactions: 678,
    growth: 8.7
  },
  {
    id: 3,
    name: '신촌점',
    revenue: 8900000,
    transactions: 524,
    growth: -2.1
  },
  {
    id: 4,
    name: '건대점',
    revenue: 7600000,
    transactions: 489,
    growth: 5.4
  }
]

// 결제수단별 데이터
const paymentMethodData = [
  { name: '신용카드', value: 65 },
  { name: '현금', value: 15 },
  { name: '간편결제', value: 12 },
  { name: '기프티콘', value: 8 }
]

// 최근 거래 내역
const recentTransactions = [
  {
    id: 1,
    orderId: 'ORD-2401260001',
    store: '강남점',
    items: '아메리카노 외 2건',
    amount: 15000,
    paymentMethod: '신용카드',
    timestamp: '2024-01-26 15:23'
  },
  {
    id: 2,
    orderId: 'ORD-2401260002',
    store: '홍대점',
    items: '카페라떼 외 1건',
    amount: 11000,
    paymentMethod: '간편결제',
    timestamp: '2024-01-26 15:21'
  },
  {
    id: 3,
    orderId: 'ORD-2401260003',
    store: '신촌점',
    items: '아이스티 외 3건',
    amount: 23000,
    paymentMethod: '신용카드',
    timestamp: '2024-01-26 15:18'
  }
]

// 숫자 포맷팅 함수
const formatNumber = (value) => {
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}
</script>