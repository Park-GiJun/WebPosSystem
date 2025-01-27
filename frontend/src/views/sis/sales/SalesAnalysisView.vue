<!-- SalesAnalysisView.vue -->
<template>
  <div class="p-6 space-y-6">
    <!-- 분석 기간/대상 선택 -->
    <div class="bg-white p-6 rounded-lg shadow">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="text-sm font-medium text-gray-700">분석 기간</label>
          <select v-model="filters.period" class="mt-1 w-full rounded-md border-gray-300">
            <option value="day">일간</option>
            <option value="week">주간</option>
            <option value="month">월간</option>
            <option value="year">연간</option>
          </select>
        </div>
        <div>
          <label class="text-sm font-medium text-gray-700">매장 선택</label>
          <select v-model="filters.store" class="mt-1 w-full rounded-md border-gray-300">
            <option value="all">전체 매장</option>
            <option v-for="store in stores" :key="store.id" :value="store.id">
              {{ store.name }}
            </option>
          </select>
        </div>
        <div>
          <label class="text-sm font-medium text-gray-700">상품 카테고리</label>
          <select v-model="filters.category" class="mt-1 w-full rounded-md border-gray-300">
            <option value="all">전체 카테고리</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </option>
          </select>
        </div>
        <div class="flex items-end">
          <button @click="analyzeData" class="w-full bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
            분석하기
          </button>
        </div>
      </div>
    </div>

    <!-- 매출 분석 결과 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-gray-500 text-sm">총 매출액</h4>
        <div class="mt-2 flex justify-between items-end">
          <span class="text-2xl font-bold">{{ formatNumber(analytics.totalRevenue) }}원</span>
          <span :class="getGrowthClass(analytics.revenueGrowth)">
           {{ analytics.revenueGrowth > 0 ? '+' : '' }}{{ analytics.revenueGrowth }}%
         </span>
        </div>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-gray-500 text-sm">평균 객단가</h4>
        <div class="mt-2 flex justify-between items-end">
          <span class="text-2xl font-bold">{{ formatNumber(analytics.averageOrder) }}원</span>
          <span :class="getGrowthClass(analytics.orderGrowth)">
           {{ analytics.orderGrowth > 0 ? '+' : '' }}{{ analytics.orderGrowth }}%
         </span>
        </div>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-gray-500 text-sm">재구매율</h4>
        <div class="mt-2 flex justify-between items-end">
          <span class="text-2xl font-bold">{{ analytics.repeatRate }}%</span>
          <span :class="getGrowthClass(analytics.repeatGrowth)">
           {{ analytics.repeatGrowth > 0 ? '+' : '' }}{{ analytics.repeatGrowth }}%
         </span>
        </div>
      </div>
    </div>

    <!-- 매출 트렌드 -->
    <div class="bg-white p-6 rounded-lg shadow">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-lg font-semibold">매출 트렌드</h3>
        <div class="flex gap-2">
          <button
              v-for="type in trendTypes"
              :key="type.value"
              @click="selectedTrendType = type.value"
              :class="[
             'px-3 py-1 rounded-md text-sm',
             selectedTrendType === type.value
               ? 'bg-blue-600 text-white'
               : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
           ]"
          >
            {{ type.label }}
          </button>
        </div>
      </div>
      <div class="h-[400px]">
        <SalesTrendChart :data="analytics.trendData" :type="selectedTrendType" />
      </div>
    </div>

    <!-- 상품 분석 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold mb-4">인기 상품 TOP 5</h3>
        <div class="space-y-4">
          <div v-for="product in analytics.topProducts" :key="product.id"
               class="flex items-center p-3 bg-gray-50 rounded-lg">
           <span class="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold">
             {{ product.rank }}
           </span>
            <div class="ml-4 flex-1">
              <div class="flex justify-between">
                <span class="font-medium">{{ product.name }}</span>
                <span class="text-gray-600">{{ formatNumber(product.sales) }}원</span>
              </div>
              <div class="mt-1 flex justify-between text-sm">
                <span class="text-gray-500">{{ product.quantity }}건</span>
                <span :class="getGrowthClass(product.growth)">
                 {{ product.growth > 0 ? '+' : '' }}{{ product.growth }}%
               </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold mb-4">카테고리별 매출 비중</h3>
        <div class="h-[300px]">
          <CategoryChart :data="analytics.categoryData" />
        </div>
      </div>
    </div>

    <!-- 고객 분석 -->
    <div class="bg-white p-6 rounded-lg shadow">
      <h3 class="text-lg font-semibold mb-4">고객 분석</h3>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div>
          <h4 class="text-gray-500 text-sm mb-3">연령대별 매출 분포</h4>
          <div class="space-y-3">
            <div v-for="age in analytics.ageDistribution" :key="age.group"
                 class="flex items-center">
              <span class="w-20 text-sm text-gray-600">{{ age.group }}</span>
              <div class="flex-1 h-4 bg-gray-100 rounded-full overflow-hidden">
                <div class="h-full bg-blue-600" :style="{ width: age.percentage + '%' }"></div>
              </div>
              <span class="ml-3 text-sm">{{ age.percentage }}%</span>
            </div>
          </div>
        </div>

        <div>
          <h4 class="text-gray-500 text-sm mb-3">시간대별 방문 패턴</h4>
          <div class="h-[200px]">
            <TimePatternChart :data="analytics.timePattern" />
          </div>
        </div>

        <div>
          <h4 class="text-gray-500 text-sm mb-3">구매 빈도</h4>
          <div class="space-y-3">
            <div v-for="freq in analytics.purchaseFrequency" :key="freq.type"
                 class="flex justify-between items-center">
              <span class="text-sm text-gray-600">{{ freq.type }}</span>
              <span class="font-medium">{{ freq.count }}명</span>
              <span class="text-sm" :class="getGrowthClass(freq.growth)">
               {{ freq.growth > 0 ? '+' : '' }}{{ freq.growth }}%
             </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import SalesTrendChart from '@/components/SalesTrendChart.vue'
import CategoryChart from '@/components/CategoryChart.vue'
import TimePatternChart from '@/components/TimePatternChart.vue'

// 필터 상태
const filters = ref({
  period: 'month',
  store: 'all',
  category: 'all'
})

// 매장 목록
const stores = [
  { id: 1, name: '강남점' },
  { id: 2, name: '홍대점' },
  { id: 3, name: '신촌점' },
  { id: 4, name: '건대점' }
]

// 카테고리 목록
const categories = [
  { id: 1, name: '커피' },
  { id: 2, name: '논커피' },
  { id: 3, name: '베이커리' },
  { id: 4, name: '디저트' }
]

// 트렌드 타입
const trendTypes = [
  { value: 'revenue', label: '매출액' },
  { value: 'quantity', label: '판매량' },
  { value: 'customer', label: '고객수' }
]
const selectedTrendType = ref('revenue')

// 분석 데이터
const analytics = ref({
  totalRevenue: 125680000,
  revenueGrowth: 12.5,
  averageOrder: 15600,
  orderGrowth: 8.2,
  repeatRate: 45.8,
  repeatGrowth: 5.3,

  // 트렌드 데이터
  trendData: [
    { date: '2024-01-01', revenue: 3800000, quantity: 450, customer: 380 },
    { date: '2024-01-02', revenue: 4200000, quantity: 520, customer: 420 },
    { date: '2024-01-03', revenue: 3900000, quantity: 480, customer: 390 },
    // ... 추가 데이터
  ],

  // 인기 상품
  topProducts: [
    { rank: 1, id: 1, name: '아메리카노', sales: 12500000, quantity: 2840, growth: 15.2 },
    { rank: 2, id: 2, name: '카페라떼', sales: 9800000, quantity: 1960, growth: 8.7 },
    { rank: 3, id: 3, name: '카푸치노', sales: 7600000, quantity: 1520, growth: -2.1 },
    { rank: 4, id: 4, name: '바닐라라떼', sales: 6900000, quantity: 1380, growth: 5.4 },
    { rank: 5, id: 5, name: '카라멜마끼아또', sales: 5800000, quantity: 1160, growth: 3.2 }
  ],

  // 카테고리 데이터
  categoryData: [
    { name: '커피', value: 65 },
    { name: '논커피', value: 15 },
    { name: '베이커리', value: 12 },
    { name: '디저트', value: 8 }
  ],

  // 연령대별 분포
  ageDistribution: [
    { group: '10대', percentage: 5 },
    { group: '20대', percentage: 35 },
    { group: '30대', percentage: 30 },
    { group: '40대', percentage: 20 },
    { group: '50대 이상', percentage: 10 }
  ],

  // 시간대별 패턴
  timePattern: [
    { hour: '08:00', count: 45 },
    { hour: '10:00', count: 80 },
    { hour: '12:00', count: 120 },
    { hour: '14:00', count: 85 },
    { hour: '16:00', count: 70 },
    { hour: '18:00', count: 95 },
    { hour: '20:00', count: 65 }
  ],

  // 구매 빈도
  purchaseFrequency: [
    { type: '신규 고객', count: 450, growth: 12.5 },
    { type: '재방문 고객', count: 820, growth: 8.2 },
    { type: '단골 고객', count: 340, growth: 15.4 }
  ]
})

// 숫자 포맷팅
const formatNumber = (value) => {
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 증감율 색상 클래스
const getGrowthClass = (growth) => {
  return growth > 0 ? 'text-green-500' : 'text-red-500'
}

// 데이터 분석 함수
const analyzeData = () => {
  // API 호출 및 데이터 업데이트 로직
  console.log('Analyzing with filters:', filters.value)
}
</script>