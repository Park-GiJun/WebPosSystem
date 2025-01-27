<!-- PaymentHistoryView.vue -->
<template>
  <div class="p-6 space-y-6">
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
          <label class="text-sm font-medium text-gray-700">매장</label>
          <select v-model="filters.store" class="mt-1 w-full rounded-md border-gray-300">
            <option value="">전체 매장</option>
            <option v-for="store in stores" :key="store">{{ store }}</option>
          </select>
        </div>
        <div>
          <label class="text-sm font-medium text-gray-700">결제수단</label>
          <select v-model="filters.paymentMethod" class="mt-1 w-full rounded-md border-gray-300">
            <option value="">전체</option>
            <option v-for="method in paymentMethods" :key="method">{{ method }}</option>
          </select>
        </div>
        <div>
          <label class="text-sm font-medium text-gray-700">금액</label>
          <div class="mt-1 flex items-center gap-2">
            <input type="number" v-model="filters.minAmount" placeholder="최소" class="rounded-md border-gray-300">
            <span>~</span>
            <input type="number" v-model="filters.maxAmount" placeholder="최대" class="rounded-md border-gray-300">
          </div>
        </div>
        <div class="flex items-end">
          <button class="w-full bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
            검색
          </button>
        </div>
      </div>
    </div>

    <!-- 결제 요약 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">전체 결제</h4>
        <p class="mt-2 text-2xl font-bold">{{ formatNumber(summary.total) }}건</p>
        <p class="mt-1 text-sm text-gray-500">{{ formatNumber(summary.totalAmount) }}원</p>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">카드 결제</h4>
        <p class="mt-2 text-2xl font-bold">{{ formatNumber(summary.card) }}건</p>
        <p class="mt-1 text-sm text-gray-500">{{ formatNumber(summary.cardAmount) }}원</p>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">현금 결제</h4>
        <p class="mt-2 text-2xl font-bold">{{ formatNumber(summary.cash) }}건</p>
        <p class="mt-1 text-sm text-gray-500">{{ formatNumber(summary.cashAmount) }}원</p>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <h4 class="text-sm text-gray-500">취소/환불</h4>
        <p class="mt-2 text-2xl font-bold text-red-600">{{ formatNumber(summary.refund) }}건</p>
        <p class="mt-1 text-sm text-gray-500">{{ formatNumber(summary.refundAmount) }}원</p>
      </div>
    </div>

    <!-- 결제 내역 테이블 -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead>
        <tr>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            결제일시
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            주문번호
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            매장
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            결제수단
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            상품
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            결제금액
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            상태
          </th>
          <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
            관리
          </th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
        <tr v-for="payment in payments" :key="payment.id">
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
            {{ payment.datetime }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
            {{ payment.orderId }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{ payment.store }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{ payment.method }}
          </td>
          <td class="px-6 py-4 text-sm text-gray-500">
            {{ payment.items }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
            {{ formatNumber(payment.amount) }}원
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
             <span :class="getStatusClass(payment.status)" class="px-2 py-1 text-xs rounded-full">
               {{ payment.status }}
             </span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
            <button class="text-blue-600 hover:text-blue-900">상세</button>
            <button v-if="payment.status === '결제완료'"
                    class="ml-3 text-red-600 hover:text-red-900">취소</button>
          </td>
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
  store: '',
  paymentMethod: '',
  minAmount: '',
  maxAmount: ''
})

const stores = ['강남점', '홍대점', '신촌점', '건대점']
const paymentMethods = ['신용카드', '현금', '간편결제']

const summary = ref({
  total: 1250,
  totalAmount: 12500000,
  card: 980,
  cardAmount: 9800000,
  cash: 270,
  cashAmount: 2700000,
  refund: 15,
  refundAmount: 150000
})

const payments = ref([
  {
    id: 1,
    datetime: '2024-01-26 15:30:25',
    orderId: 'ORD20240126-001',
    store: '강남점',
    method: '신용카드',
    items: '아메리카노 외 2건',
    amount: 15000,
    status: '결제완료'
  },
  {
    id: 2,
    datetime: '2024-01-26 15:28:10',
    orderId: 'ORD20240126-002',
    store: '홍대점',
    method: '간편결제',
    items: '카페라떼 외 1건',
    amount: 11000,
    status: '결제완료'
  },
  {
    id: 3,
    datetime: '2024-01-26 15:25:55',
    orderId: 'ORD20240126-003',
    store: '신촌점',
    method: '현금',
    items: '바닐라라떼',
    amount: 8000,
    status: '취소'
  }
])

const formatNumber = (value) => {
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const getStatusClass = (status) => {
  return {
    '결제완료': 'bg-green-100 text-green-800',
    '취소': 'bg-red-100 text-red-800',
    '환불': 'bg-yellow-100 text-yellow-800'
  }[status]
}
</script>