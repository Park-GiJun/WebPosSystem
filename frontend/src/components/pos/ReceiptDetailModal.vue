<!-- components/modals/ReceiptDetailModal.vue -->
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
            <DialogPanel class="w-full max-w-2xl transform overflow-hidden rounded-xl bg-white p-6 shadow-xl">
              <DialogTitle class="text-lg font-bold mb-6">영수증 상세</DialogTitle>

              <div v-if="isLoading" class="text-center py-8">
                <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500 mx-auto"></div>
                <p class="mt-4 text-gray-600">영수증 정보를 불러오는 중...</p>
              </div>

              <div v-else-if="receipt" class="space-y-6">
                <!-- 영수증 헤더 정보 -->
                <div class="border-b pb-4">
                  <div class="grid grid-cols-2 gap-4">
                    <div>
                      <h3 class="text-sm text-gray-500">영수증 번호</h3>
                      <p class="font-medium">{{ receipt.receiptNumber }}</p>
                    </div>
                    <div>
                      <h3 class="text-sm text-gray-500">발행일</h3>
                      <p class="font-medium">{{ formatDate(receipt.saleDate) }}</p>
                    </div>
                    <div>
                      <h3 class="text-sm text-gray-500">처리자</h3>
                      <p class="font-medium">{{ receipt.user.username }}</p>
                    </div>
                    <div>
                      <h3 class="text-sm text-gray-500">상태</h3>
                      <span :class="getStatusClass(receipt.status)" class="px-2 py-1 text-xs rounded-full">
                        {{ getStatusLabel(receipt.status) }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- 상품 목록 -->
                <div>
                  <h3 class="text-sm font-medium mb-2">상품 목록</h3>
                  <table class="w-full">
                    <thead class="bg-gray-50">
                    <tr>
                      <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">상품명</th>
                      <th class="px-4 py-2 text-right text-xs font-medium text-gray-500">단가</th>
                      <th class="px-4 py-2 text-right text-xs font-medium text-gray-500">수량</th>
                      <th class="px-4 py-2 text-right text-xs font-medium text-gray-500">금액</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    <tr v-for="item in receipt.items" :key="item.id">
                      <td class="px-4 py-2">{{ item.product.name }}</td>
                      <td class="px-4 py-2 text-right">₩{{ formatNumber(item.unitPrice) }}</td>
                      <td class="px-4 py-2 text-right">{{ item.quantity }}</td>
                      <td class="px-4 py-2 text-right">₩{{ formatNumber(item.lineAmount) }}</td>
                    </tr>
                    </tbody>
                    <tfoot class="bg-gray-50">
                    <tr>
                      <td colspan="3" class="px-4 py-2 text-right font-medium">소계</td>
                      <td class="px-4 py-2 text-right">₩{{ formatNumber(receipt.totalAmount) }}</td>
                    </tr>
                    <tr>
                      <td colspan="3" class="px-4 py-2 text-right font-medium">부가세</td>
                      <td class="px-4 py-2 text-right">₩{{ formatNumber(receipt.taxAmount) }}</td>
                    </tr>
                    <tr v-if="receipt.discountAmount && receipt.discountAmount > 0">
                      <td colspan="3" class="px-4 py-2 text-right font-medium">할인</td>
                      <td class="px-4 py-2 text-right">₩{{ formatNumber(receipt.discountAmount) }}</td>
                    </tr>
                    <tr>
                      <td colspan="3" class="px-4 py-2 text-right font-bold">합계</td>
                      <td class="px-4 py-2 text-right font-bold">₩{{ formatNumber(receipt.totalAmount) }}</td>
                    </tr>
                    </tfoot>
                  </table>
                </div>

                <!-- 결제 정보 -->
                <div>
                  <h3 class="text-sm font-medium mb-2">결제 정보</h3>
                  <div class="bg-gray-50 p-4 rounded-lg space-y-2">
                    <div v-for="(payment, index) in receipt.payments" :key="index" class="flex justify-between">
                      <span>{{ getPaymentTypeLabel(payment.paymentType) }}</span>
                      <span>₩{{ formatNumber(payment.paymentAmount) }}</span>
                    </div>
                  </div>
                </div>

                <!-- 버튼 영역 -->
                <div class="flex justify-end gap-3 mt-6">
                  <button
                      @click="closeModal"
                      class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
                  >
                    닫기
                  </button>
                  <button
                      v-if="receipt.status === 'COMPLETED'"
                      @click="confirmCancelReceipt"
                      class="px-4 py-2 bg-red-100 text-red-800 rounded-lg hover:bg-red-200"
                  >
                    취소
                  </button>
                  <button
                      @click="printReceiptDetail"
                      class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                  >
                    인쇄
                  </button>
                </div>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue'
import { useToast } from 'vue-toastification'
import { posService } from '@/services/posService'

const props = defineProps({
  isOpen: Boolean,
  receiptNumber: String
})

const emit = defineEmits(['close', 'canceled'])
const toast = useToast()

const isLoading = ref(false)
const receipt = ref(null)

// 영수증 정보 불러오기
const fetchReceipt = async () => {
  if (!props.receiptNumber) return

  try {
    isLoading.value = true
    receipt.value = await posService.getReceipt(props.receiptNumber)
  } catch (error) {
    console.error('Failed to fetch receipt:', error)
    toast.error('영수증 정보를 불러오는데 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

// 영수증 취소 확인
const confirmCancelReceipt = () => {
  if (confirm('이 영수증을 취소하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
    cancelReceipt()
  }
}

// 영수증 취소
const cancelReceipt = async () => {
  try {
    isLoading.value = true
    await posService.cancelReceipt(props.receiptNumber)
    toast.success('영수증이 취소되었습니다.')
    emit('canceled')
    closeModal()
  } catch (error) {
    console.error('Failed to cancel receipt:', error)
    toast.error('영수증 취소에 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

// 영수증 인쇄
const printReceiptDetail = () => {
  if (!receipt.value) return

  const printWindow = window.open('', '_blank')
  const content = generatePrintableContent(receipt.value)

  printWindow.document.write(content)
  printWindow.document.close()
  printWindow.print()
  printWindow.close()
}

// 인쇄 가능한 영수증 HTML 생성
const generatePrintableContent = (receipt) => {
  return `
    <!DOCTYPE html>
    <html>
    <head>
      <title>영수증 ${receipt.receiptNumber}</title>
      <style>
        body { font-family: 'Courier New', monospace; font-size: 12px; width: 300px; margin: 0 auto; }
        .header { text-align: center; margin-bottom: 20px; }
        .divider { border-top: 1px dashed #000; margin: 10px 0; }
        .item { display: flex; justify-content: space-between; margin-bottom: 5px; }
        .right { text-align: right; }
        .total { font-weight: bold; margin-top: 10px; }
        .footer { text-align: center; margin-top: 20px; font-size: 10px; }
      </style>
    </head>
    <body>
      <div class="header">
        <h1>영수증</h1>
        <p>영수증 번호: ${receipt.receiptNumber}</p>
        <p>발행일: ${formatDate(receipt.saleDate)}</p>
      </div>
      <div class="divider"></div>
      <div class="items">
        ${receipt.items.map(item => `
          <div class="item">
            <span>${item.product.name} x ${item.quantity}</span>
            <span>₩${formatNumber(item.lineAmount)}</span>
          </div>
        `).join('')}
      </div>
      <div class="divider"></div>
      <div class="summary">
        <div class="item">
          <span>소계</span>
          <span>₩${formatNumber(receipt.totalAmount)}</span>
        </div>
        <div class="item">
          <span>부가세</span>
          <span>₩${formatNumber(receipt.taxAmount)}</span>
        </div>
        ${receipt.discountAmount && receipt.discountAmount > 0 ? `
          <div class="item">
            <span>할인</span>
            <span>₩${formatNumber(receipt.discountAmount)}</span>
          </div>
        ` : ''}
        <div class="item total">
          <span>합계</span>
          <span>₩${formatNumber(receipt.totalAmount)}</span>
        </div>
      </div>
      <div class="divider"></div>
      <div class="payment">
        ${receipt.payments.map(payment => `
          <div class="item">
            <span>${getPaymentTypeLabel(payment.paymentType)}</span>
            <span>₩${formatNumber(payment.paymentAmount)}</span>
          </div>
        `).join('')}
      </div>
      <div class="divider"></div>
      <div class="footer">
        <p>담당자: ${receipt.user.username}</p>
        <p>이용해주셔서 감사합니다!</p>
      </div>
    </body>
    </html>
  `
}

// 모달 닫기
const closeModal = () => {
  emit('close')
}

// 상태에 따른 클래스 반환
const getStatusClass = (status) => {
  const classes = {
    'COMPLETED': 'bg-green-100 text-green-800',
    'CANCELED': 'bg-red-100 text-red-800',
    'REFUNDED': 'bg-yellow-100 text-yellow-800',
    'PENDING': 'bg-blue-100 text-blue-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

// 상태에 따른 레이블 반환
const getStatusLabel = (status) => {
  const labels = {
    'COMPLETED': '완료',
    'CANCELED': '취소',
    'REFUNDED': '환불',
    'PENDING': '대기'
  }
  return labels[status] || status
}

// 결제 유형에 따른 레이블 반환
const getPaymentTypeLabel = (type) => {
  const labels = {
    'CASH': '현금',
    'CREDIT_CARD': '신용카드',
    'SAMSUNG_PAY': '삼성페이',
    'KAKAO_PAY': '카카오페이',
    'GIFT_CARD': '상품권',
    'POINT': '포인트'
  }
  return labels[type] || type
}

// 날짜 포맷팅
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString()
}

// 숫자 포맷팅
const formatNumber = (value) => {
  if (value === undefined || value === null) return '0'
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 모달이 열릴 때 영수증 정보 불러오기
onMounted(() => {
  if (props.isOpen && props.receiptNumber) {
    fetchReceipt()
  }
})

// 영수증 번호가 변경될 때 영수증 정보 다시 불러오기
watch(() => [props.isOpen, props.receiptNumber], ([isOpen, receiptNumber]) => {
  if (isOpen && receiptNumber) {
    fetchReceipt()
  }
})
</script>