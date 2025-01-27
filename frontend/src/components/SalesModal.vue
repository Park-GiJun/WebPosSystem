<!-- SalesModal.vue -->
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
        <div class="fixed inset-0 bg-black bg-opacity-25" />
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
            <DialogPanel class="w-full max-w-4xl bg-white rounded-2xl p-6">
              <DialogTitle class="text-lg font-bold mb-4">오늘 매출 현황</DialogTitle>

              <!-- Summary Cards -->
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

              <!-- Sales Chart -->
              <div class="mb-6">
                <h3 class="text-sm font-medium mb-2">시간대별 매출</h3>
                <div class="h-64">
                  <LineChart :data="hourlyData" />
                </div>
              </div>

              <!-- Payment Methods -->
              <div class="mb-6">
                <h3 class="text-sm font-medium mb-2">결제 수단별 비율</h3>
                <div class="h-64">
                  <PieChart :data="paymentMethodData" />
                </div>
              </div>

              <!-- Recent Transactions -->
              <div class="mb-6">
                <h3 class="text-sm font-medium mb-2">최근 거래 내역</h3>
                <div class="bg-gray-50 rounded-lg">
                  <table class="w-full">
                    <thead>
                    <tr class="text-left text-sm text-gray-500">
                      <th class="p-3">시간</th>
                      <th class="p-3">주문번호</th>
                      <th class="p-3">금액</th>
                      <th class="p-3">결제수단</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="transaction in recentTransactions" :key="transaction.id" class="border-t">
                      <td class="p-3">{{ transaction.time }}</td>
                      <td class="p-3">#{{ transaction.orderNumber }}</td>
                      <td class="p-3">₩{{ transaction.amount.toLocaleString() }}</td>
                      <td class="p-3">{{ transaction.paymentMethod }}</td>
                    </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <button
                  @click="closeModal"
                  class="w-full px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
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
import { LineChart, PieChart } from '@/components/charts'

const props = defineProps({
  isOpen: Boolean,
})

const emit = defineEmits(['close'])

// Sample data - 실제로는 API에서 가져와야 함
const totalSales = ref(2850000)
const transactionCount = ref(142)
const averageOrder = computed(() => Math.floor(totalSales.value / transactionCount.value))

const hourlyData = ref([
  { time: '09:00', sales: 180000 },
  { time: '10:00', sales: 250000 },
  { time: '11:00', sales: 320000 },
  { time: '12:00', sales: 450000 },
  { time: '13:00', sales: 380000 },
  { time: '14:00', sales: 290000 },
  { time: '15:00', sales: 220000 },
])

const paymentMethodData = ref([
  { name: '신용카드', value: 65 },
  { name: '현금', value: 15 },
  { name: '삼성페이', value: 12 },
  { name: '카카오페이', value: 8 },
])

const recentTransactions = ref([
  { id: 1, time: '15:42', orderNumber: '235', amount: 15000, paymentMethod: '신용카드' },
  { id: 2, time: '15:38', orderNumber: '234', amount: 8500, paymentMethod: '카카오페이' },
  { id: 3, time: '15:31', orderNumber: '233', amount: 12000, paymentMethod: '현금' },
  { id: 4, time: '15:25', orderNumber: '232', amount: 21000, paymentMethod: '삼성페이' },
  { id: 5, time: '15:18', orderNumber: '231', amount: 9500, paymentMethod: '신용카드' },
])

const closeModal = () => {
  emit('close')
}
</script>