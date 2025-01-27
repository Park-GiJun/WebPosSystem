<template>
  <div class="p-6 space-y-6">
    <!-- 필터 영역 -->
    <div class="bg-white p-4 rounded-lg shadow flex gap-4">
      <select v-model="period" class="rounded-md border-gray-300">
        <option value="day">일간</option>
        <option value="week">주간</option>
        <option value="month">월간</option>
        <option value="year">연간</option>
      </select>
      <select v-model="store" class="rounded-md border-gray-300">
        <option value="">전체 매장</option>
        <option v-for="s in stores" :key="s">{{ s }}</option>
      </select>
    </div>

    <!-- 주요 통계 -->
    <div class="grid grid-cols-4 gap-4">
      <StatsCard
          title="총 결제금액"
          :value="`${formatNumber(stats.totalAmount)}원`"
          :growth="stats.amountGrowth"
      />
      <StatsCard
          title="결제 건수"
          :value="`${formatNumber(stats.totalCount)}건`"
          :growth="stats.countGrowth"
      />
      <StatsCard
          title="평균 결제금액"
          :value="`${formatNumber(stats.averageAmount)}원`"
          :growth="stats.averageGrowth"
      />
      <StatsCard
          title="환불율"
          :value="`${stats.refundRate}%`"
          :growth="-stats.refundGrowth"
      />
    </div>

    <!-- 차트 영역 -->
    <div class="grid grid-cols-2 gap-6">
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold mb-4">결제 추이</h3>
        <div class="h-[300px]">
          <PaymentTrendChart :data="trendData" />
        </div>
      </div>
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold mb-4">결제수단 비율</h3>
        <div class="h-[300px]">
          <PaymentMethodChart :data="methodData" />
        </div>
      </div>
    </div>

    <div class="grid grid-cols-2 gap-6">
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold mb-4">매장별 매출</h3>
        <div class="h-[300px]">
          <StoreRevenueChart :data="storeData" />
        </div>
      </div>
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold mb-4">시간대별 결제</h3>
        <div class="h-[300px]">
          <HourlyPatternChart :data="hourlyData" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import StatsCard from '@/components/StatsCard.vue'
import PaymentTrendChart from '@/components/PaymentTrendChart.vue'
import PaymentMethodChart from '@/components/PaymentMethodChart.vue'
import StoreRevenueChart from '@/components/StoreRevenueChart.vue'
import HourlyPatternChart from '@/components/HourlyPatternChart.vue'

const period = ref('month')
const store = ref('')
const stores = ['강남점', '홍대점', '신촌점', '건대점']

const stats = ref({
  totalAmount: 125680000,
  amountGrowth: 12.5,
  totalCount: 8560,
  countGrowth: 8.2,
  averageAmount: 14680,
  averageGrowth: 4.3,
  refundRate: 1.2,
  refundGrowth: -0.3
})

const trendData = ref([
  { date: '01-01', amount: 4200000 },
  { date: '01-02', amount: 3800000 },
  { date: '01-03', amount: 4500000 },
  { date: '01-04', amount: 4100000 },
  { date: '01-05', amount: 4800000 }
])

const methodData = ref([
  { name: '신용카드', value: 65 },
  { name: '현금', value: 15 },
  { name: '간편결제', value: 12 },
  { name: '기프티콘', value: 8 }
])

const storeData = ref([
  { name: '강남점', value: 45200000 },
  { name: '홍대점', value: 38600000 },
  { name: '신촌점', value: 25400000 },
  { name: '건대점', value: 16480000 }
])

const hourlyData = ref([
  { hour: '08:00', count: 45 },
  { hour: '10:00', count: 80 },
  { hour: '12:00', count: 120 },
  { hour: '14:00', count: 85 },
  { hour: '16:00', count: 70 },
  { hour: '18:00', count: 95 }
])

const formatNumber = (value) => {
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}
</script>