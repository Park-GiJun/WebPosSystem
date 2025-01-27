<!-- DashboardView.vue -->
<template>
  <div class="p-6 space-y-6">
    <!-- Monthly Sales Chart -->
    <div class="bg-white rounded-lg shadow">
      <div class="p-6">
        <h3 class="text-lg font-semibold">월간 매장별 매출</h3>
        <div class="h-[400px] mt-4">
          <MonthlyChart :data="monthlyData" />
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <!-- Best Selling Products -->
      <div class="bg-white rounded-lg shadow p-6">
        <h3 class="text-lg font-semibold mb-4">가장 많이 팔린 상품</h3>
        <div class="divide-y">
          <div v-for="item in bestProducts" :key="item.name"
               class="flex items-center justify-between py-3">
            <span class="text-gray-600">{{ item.name }}</span>
            <span class="font-medium">{{ item.count.toLocaleString() }}잔</span>
          </div>
        </div>
      </div>

      <!-- Payment Methods -->
      <div class="bg-white rounded-lg shadow p-6">
        <h3 class="text-lg font-semibold mb-4">결제 수단 통계</h3>
        <div class="h-[250px]">
          <PaymentChart :data="paymentData" />
        </div>
      </div>

      <!-- Coupon Usage -->
      <div class="bg-white rounded-lg shadow p-6">
        <h3 class="text-lg font-semibold mb-4">쿠폰 사용량</h3>
        <div class="flex flex-col items-center justify-center h-[250px]">
          <div class="text-4xl font-bold text-blue-600 mb-2">
            {{ couponCount.toLocaleString() }}
          </div>
          <p class="text-gray-500">이번 달 사용 쿠폰</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import MonthlyChart from '@/components/MonthlyChart.vue'
import PaymentChart from '@/components/PaymentChart.vue'

const monthlyData = ref([
  { month: '1월', store1: 4000, store2: 2400, store3: 3200 },
  { month: '2월', store1: 3000, store2: 1398, store3: 2800 },
  { month: '3월', store1: 2000, store2: 9800, store3: 2900 },
  { month: '4월', store1: 2780, store2: 3908, store3: 2000 },
  { month: '5월', store1: 1890, store2: 4800, store3: 2181 }
])

const bestProducts = ref([
  { name: '아메리카노', count: 1234 },
  { name: '카페라떼', count: 987 },
  { name: '카푸치노', count: 654 }
])

const paymentData = ref([
  { name: '신한', value: 400 },
  { name: '삼성', value: 300 },
  { name: '현대', value: 200 },
  { name: '국민', value: 278 }
])

const couponCount = ref(2345)
</script>