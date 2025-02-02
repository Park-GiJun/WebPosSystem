<!-- components/modals/SalesModal.vue -->
<template>
  <TransitionRoot appear :show="isOpen" as="template">
    <Dialog as="div" @close="closeModal" class="relative z-50">
      <TransitionChild
          enter="duration-300 ease-out"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="duration-200 ease-in"
          leave-from="opacity-100"
          leave-to="opacity-0"
      >
        <div class="fixed inset-0 bg-black/25" />
      </TransitionChild>

      <div class="fixed inset-0 overflow-y-auto">
        <div class="flex min-h-full items-center justify-center p-4">
          <TransitionChild
              enter="duration-300 ease-out"
              enter-from="opacity-0 scale-95"
              enter-to="opacity-100 scale-100"
              leave="duration-200 ease-in"
              leave-from="opacity-100 scale-100"
              leave-to="opacity-0 scale-95"
          >
            <DialogPanel class="w-full max-w-4xl transform overflow-hidden rounded-xl bg-white p-6 shadow-xl">
              <DialogTitle class="text-lg font-bold mb-6">오늘 매출 현황</DialogTitle>

              <!-- 매출 요약 -->
              <div class="grid grid-cols-3 gap-4 mb-6">
                <div class="bg-blue-50 p-4 rounded-lg">
                  <h3 class="text-sm text-gray-600 mb-1">총 매출</h3>
                  <p class="text-2xl font-bold">₩{{ totalSales.toLocaleString() }}</p>
                </div>
                <div class="bg-green-50 p-4 rounded-lg">
                  <h3 class="text-sm text-gray-600 mb-1">거래 건수</h3>
                  <p class="text-2xl font-bold">{{ transactionCount }}건</p>
                </div>
                <div class="bg-purple-50 p-4 rounded-lg">
                  <h3 class="text-sm text-gray-600 mb-1">평균 객단가</h3>
                  <p class="text-2xl font-bold">₩{{ averageOrder.toLocaleString() }}</p>
                </div>
              </div>

              <!-- 시간대별 매출 차트 -->
              <div class="mb-6">
                <h3 class="text-sm font-medium mb-2">시간대별 매출</h3>
                <div class="bg-white p-4 rounded-lg">
                  <ApexChart
                      type="area"
                      height="300"
                      :options="chartOptions"
                      :series="chartSeries"
                  />
                </div>
              </div>

              <!-- 결제수단 차트 -->
              <div class="mb-6">
                <h3 class="text-sm font-medium mb-2">결제수단 비율</h3>
                <div class="grid grid-cols-2 gap-4">
                  <div class="bg-white p-4 rounded-lg">
                    <ApexChart
                        type="donut"
                        height="250"
                        :options="pieChartOptions"
                        :series="pieChartSeries"
                    />
                  </div>
                  <div class="bg-white p-4 rounded-lg">
                    <div class="space-y-3">
                      <div v-for="method in paymentMethods" :key="method.name"
                           class="flex justify-between p-2 bg-gray-50 rounded">
                        <span>{{ method.name }}</span>
                        <span class="font-medium">₩{{ method.amount.toLocaleString() }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 거래 내역 -->
              <div>
                <h3 class="text-sm font-medium mb-2">최근 거래 내역</h3>
                <div class="overflow-x-auto">
                  <table class="w-full">
                    <thead class="bg-gray-50">
                    <tr>
                      <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">시간</th>
                      <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">주문번호</th>
                      <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">금액</th>
                      <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">결제수단</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    <tr v-for="transaction in transactions" :key="transaction.id">
                      <td class="px-6 py-4">{{ transaction.time }}</td>
                      <td class="px-6 py-4">#{{ transaction.orderNumber }}</td>
                      <td class="px-6 py-4 text-right">₩{{ transaction.amount.toLocaleString() }}</td>
                      <td class="px-6 py-4">{{ transaction.paymentMethod }}</td>
                    </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <button
                  @click="closeModal"
                  class="mt-6 w-full px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
              >
                닫기
              </button>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue'
import ApexChart from 'vue3-apexcharts'

const props = defineProps({
  isOpen: Boolean,
})

const emit = defineEmits(['close'])

// 더미 데이터
const totalSales = ref(2850000)
const transactionCount = ref(142)
const averageOrder = computed(() => Math.floor(totalSales.value / transactionCount.value))

const closeModal = () => {
  emit('close')
}

// 시간대별 매출 차트 설정
const chartOptions = {
  chart: {
    toolbar: {
      show: false
    }
  },
  dataLabels: {
    enabled: false
  },
  stroke: {
    curve: 'smooth',
    width: 2
  },
  xaxis: {
    categories: ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00'],
  },
  yaxis: {
    labels: {
      formatter: (value) => `₩${value.toLocaleString()}`
    }
  },
  tooltip: {
    y: {
      formatter: (value) => `₩${value.toLocaleString()}`
    }
  },
  fill: {
    type: 'gradient',
    gradient: {
      shadeIntensity: 1,
      opacityFrom: 0.7,
      opacityTo: 0.3,
    }
  },
  colors: ['#3B82F6']
}

const chartSeries = [
  {
    name: '매출',
    data: [180000, 250000, 320000, 450000, 380000, 290000, 220000]
  }
]

// 파이 차트 설정
const paymentMethods = ref([
  { name: '신용카드', amount: 1250000 },
  { name: '현금', amount: 487000 },
  { name: '삼성페이', amount: 350000 },
  { name: '카카오페이', amount: 763000 },
])

const pieChartOptions = {
  chart: {
    type: 'donut',
  },
  labels: paymentMethods.value.map(method => method.name),
  colors: ['#3B82F6', '#10B981', '#6366F1', '#F59E0B'],
  legend: {
    position: 'bottom'
  },
  plotOptions: {
    pie: {
      donut: {
        size: '70%'
      }
    }
  }
}

const pieChartSeries = paymentMethods.value.map(method => method.amount)

// 거래 내역 데이터
const transactions = ref([
  { id: 1, time: '15:42', orderNumber: '235', amount: 15000, paymentMethod: '신용카드' },
  { id: 2, time: '15:38', orderNumber: '234', amount: 8500, paymentMethod: '카카오페이' },
  { id: 3, time: '15:31', orderNumber: '233', amount: 12000, paymentMethod: '현금' },
  { id: 4, time: '15:25', orderNumber: '232', amount: 21000, paymentMethod: '삼성페이' },
  { id: 5, time: '15:18', orderNumber: '231', amount: 9500, paymentMethod: '신용카드' },
])
</script>