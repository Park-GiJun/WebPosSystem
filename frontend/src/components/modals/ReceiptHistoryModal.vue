<!-- components/modals/ReceiptHistoryModal.vue -->
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
              <DialogTitle class="text-lg font-bold mb-6">영수증 내역</DialogTitle>

              <!-- 검색 필터 -->
              <div class="bg-gray-50 p-4 rounded-lg mb-6">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">기간</label>
                    <div class="flex gap-2">
                      <input
                          type="date"
                          v-model="filters.startDate"
                          class="w-full px-3 py-2 border rounded-lg"
                      />
                      <span class="flex items-center">~</span>
                      <input
                          type="date"
                          v-model="filters.endDate"
                          class="w-full px-3 py-2 border rounded-lg"
                      />
                    </div>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">결제 수단</label>
                    <select
                        v-model="filters.paymentMethod"
                        class="w-full px-3 py-2 border rounded-lg"
                    >
                      <option value="">전체</option>
                      <option value="CREDIT_CARD">신용카드</option>
                      <option value="CASH">현금</option>
                      <option value="SAMSUNG_PAY">삼성페이</option>
                      <option value="KAKAO_PAY">카카오페이</option>
                      <option value="GIFT_CARD">기프트카드</option>
                      <option value="POINT">포인트</option>
                    </select>
                  </div>
                  <div class="flex items-end">
                    <button
                        @click="searchReceipts"
                        class="w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                      검색
                    </button>
                  </div>
                </div>
              </div>

              <!-- 영수증 목록 -->
              <div class="overflow-x-auto">
                <table class="w-full">
                  <thead class="bg-gray-50">
                  <tr>
                    <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">영수증번호</th>
                    <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">날짜</th>
                    <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">금액</th>
                    <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">결제수단</th>
                    <th class="px-4 py-3 text-center text-sm font-medium text-gray-500">상태</th>
                    <th class="px-4 py-3 text-center text-sm font-medium text-gray-500">상세</th>
                  </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-200">
                  <tr v-for="receipt in receipts" :key="receipt.receiptNumber" class="hover:bg-gray-50">
                    <td class="px-4 py-3 text-sm">{{ receipt.receiptNumber }}</td>
                    <td class="px-4 py-3 text-sm">{{ formatDateTime(receipt.saleDate) }}</td>
                    <td class="px-4 py-3 text-sm font-medium">₩{{ formatNumber(receipt.totalAmount) }}</td>
                    <td class="px-4 py-3 text-sm">
                      <div v-for="payment in receipt.payments" :key="payment.id" class="text-xs">
                        {{ getPaymentMethodName(payment.paymentType) }}
                        <span v-if="receipt.payments.length > 1">
                          (₩{{ formatNumber(payment.paymentAmount) }})
                        </span>
                      </div>
                    </td>
                    <td class="px-4 py-3 text-center">
                      <span :class="getStatusBadgeClass(receipt.status)" class="px-2 py-1 rounded-full text-xs">
                        {{ getStatusName(receipt.status) }}
                      </span>
                    </td>
                    <td class="px-4 py-3 text-center">
                      <button
                          @click="showReceiptDetail(receipt)"
                          class="px-3 py-1 text-xs bg-blue-100 text-blue-600 rounded-full hover:bg-blue-200"
                      >
                        상세보기
                      </button>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>

              <!-- 페이지네이션 -->
              <div class="flex justify-between items-center mt-4 px-2">
                <div class="text-sm text-gray-500">
                  총 {{ totalReceipts }}건
                </div>
                <div class="flex gap-2">
                  <button
                      v-for="page in pageNumbers"
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

              <div class="flex justify-end mt-6">
                <button
                    @click="closeModal"
                    class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200"
                >
                  닫기
                </button>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>

    <!-- 영수증 상세 모달 -->
    <ReceiptDetailModal
        v-if="selectedReceipt"
        :is-open="showDetailModal"
        :receipt="selectedReceipt"
        @close="showDetailModal = false"
    />
  </TransitionRoot>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue'
import { useToast } from 'vue-toastification'
import axios from '@/plugins/axios.js'
import ReceiptDetailModal from './ReceiptDetailModal.vue'

const props = defineProps({
  isOpen: Boolean
})

const emit = defineEmits(['close'])
const toast = useToast()

// 상태 관리
const receipts = ref([])
const isLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalReceipts = ref(0)
const totalPages = ref(1)
const showDetailModal = ref(false)
const selectedReceipt = ref(null)

// 필터
const filters = ref({
  startDate: getDefaultStartDate(),
  endDate: getDefaultEndDate(),
  paymentMethod: ''
})

// 초기 날짜 설정 (현재 월의 1일부터 오늘까지)
function getDefaultStartDate() {
  const today = new Date()
  const firstDay = new Date(today.getFullYear(), today.getMonth(), 1)
  return firstDay.toISOString().slice(0, 10)
}

function getDefaultEndDate() {
  const today = new Date()
  return today.toISOString().slice(0, 10)
}

// 페이지네이션
const pageNumbers = computed(() => {
  if (totalPages.value <= 5) {
    return Array.from({ length: totalPages.value }, (_, i) => i + 1)
  }

  const current = currentPage.value
  if (current <= 3) {
    return [1, 2, 3, 4, 5]
  }
  if (current >= totalPages.value - 2) {
    return Array.from({ length: 5 }, (_, i) => totalPages.value - 4 + i)
  }
  return [current - 2, current - 1, current, current + 1, current + 2]
})

// 영수증 조회
const fetchReceipts = async () => {
  try {
    isLoading.value = true

    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      startDate: filters.value.startDate && `${filters.value.startDate}T00:00:00`,
      endDate: filters.value.endDate && `${filters.value.endDate}T23:59:59`,
      paymentMethod: filters.value.paymentMethod || null
    }

    const response = await axios.get('/receipts', { params })

    if (response.data && response.data.data) {
      receipts.value = response.data.data.content || []
      totalReceipts.value = response.data.data.totalElements || 0
      totalPages.value = response.data.data.totalPages || 1
    }
  } catch (error) {
    console.error('영수증 목록 조회 실패:', error)
    toast.error('영수증 내역을 불러오는데 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

// 검색 실행
const searchReceipts = () => {
  currentPage.value = 1
  fetchReceipts()
}

// 영수증 상세 보기
const showReceiptDetail = (receipt) => {
  selectedReceipt.value = receipt
  showDetailModal.value = true
}

// 유틸리티 함수들
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
  const date = new Date(dateTimeStr)
  return date.toLocaleString()
}

const formatNumber = (value) => {
  return Number(value).toLocaleString()
}

const getPaymentMethodName = (type) => {
  const methodNames = {
    'CREDIT_CARD': '신용카드',
    'CASH': '현금',
    'SAMSUNG_PAY': '삼성페이',
    'KAKAO_PAY': '카카오페이',
    'GIFT_CARD': '기프트카드',
    'POINT': '포인트'
  }
  return methodNames[type] || type
}

const getStatusName = (status) => {
  const statusNames = {
    'COMPLETED': '완료',
    'CANCELED': '취소',
    'REFUNDED': '환불',
    'PENDING': '대기'
  }
  return statusNames[status] || status
}

const getStatusBadgeClass = (status) => {
  const statusClasses = {
    'COMPLETED': 'bg-green-100 text-green-800',
    'CANCELED': 'bg-red-100 text-red-800',
    'REFUNDED': 'bg-yellow-100 text-yellow-800',
    'PENDING': 'bg-blue-100 text-blue-800'
  }
  return statusClasses[status] || 'bg-gray-100 text-gray-800'
}

// 모달 닫기
const closeModal = () => {
  emit('close')
}

// 페이지 변경 감지
watch(currentPage, () => {
  fetchReceipts()
})

// 모달이 열릴 때 데이터 로드
watch(() => props.isOpen, (isOpen) => {
  if (isOpen) {
    fetchReceipts()
  }
})

// 컴포넌트 마운트 시 초기화
onMounted(() => {
  if (props.isOpen) {
    fetchReceipts()
  }
})
</script>