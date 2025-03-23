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
            <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-xl bg-white p-6 shadow-xl">
              <DialogTitle class="text-lg font-bold mb-2">영수증 상세</DialogTitle>

              <div class="mb-4">
                <div class="text-sm text-gray-500">영수증 번호: {{ receipt.receiptNumber }}</div>
                <div class="text-sm text-gray-500">날짜: {{ formatDateTime(receipt.saleDate) }}</div>
              </div>

              <!-- 영수증 상세 내용 -->
              <div class="bg-gray-50 p-4 rounded-lg mb-4 font-mono">
                <div class="text-center border-b pb-2 mb-2">
                  <h3 class="font-bold">카페 길준</h3>
                  <p class="text-xs">사업자번호: 123-45-67890</p>
                  <p class="text-xs">서울시 강남구 테헤란로 123</p>
                </div>

                <div class="text-xs border-b pb-2 mb-2">
                  <div class="flex justify-between">
                    <span>판매시간:</span>
                    <span>{{ formatDateTime(receipt.saleDate) }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span>담당자:</span>
                    <span>{{ receipt.user?.username || '시스템' }}</span>
                  </div>
                </div>

                <!-- 구매 상품 목록 -->
                <div class="text-xs border-b pb-2 mb-2">
                  <div class="grid grid-cols-12 font-bold border-b py-1">
                    <div class="col-span-6">상품</div>
                    <div class="col-span-2 text-right">단가</div>
                    <div class="col-span-2 text-right">수량</div>
                    <div class="col-span-2 text-right">금액</div>
                  </div>
                  <div v-for="item in receipt.items" :key="item.id" class="grid grid-cols-12 py-1">
                    <div class="col-span-6">{{ item.product?.name }}</div>
                    <div class="col-span-2 text-right">{{ formatNumber(item.unitPrice) }}</div>
                    <div class="col-span-2 text-right">{{ item.quantity }}</div>
                    <div class="col-span-2 text-right">{{ formatNumber(item.lineAmount) }}</div>
                  </div>
                </div>

                <!-- 결제 요약 -->
                <div class="text-xs mb-2">
                  <div class="flex justify-between">
                    <span>소계:</span>
                    <span>₩{{ formatNumber(calculateSubtotal()) }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span>부가세:</span>
                    <span>₩{{ formatNumber(receipt.taxAmount) }}</span>
                  </div>
                  <div v-if="receipt.discountAmount && receipt.discountAmount > 0" class="flex justify-between">
                    <span>할인:</span>
                    <span>₩{{ formatNumber(receipt.discountAmount) }}</span>
                  </div>
                  <div class="flex justify-between font-bold border-t pt-1 mt-1">
                    <span>합계:</span>
                    <span>₩{{ formatNumber(receipt.totalAmount) }}</span>
                  </div>
                </div>

                <!-- 결제 정보 -->
                <div class="text-xs border-t pt-2">
                  <div class="font-bold mb-1">결제 정보</div>
                  <div v-for="payment in receipt.payments" :key="payment.id" class="flex justify-between">
                    <span>{{ getPaymentMethodName(payment.paymentType) }}:</span>
                    <span>₩{{ formatNumber(payment.paymentAmount) }}</span>
                  </div>
                </div>

                <!-- 영수증 푸터 -->
                <div class="text-xs text-center mt-4 pt-2 border-t">
                  <p>이용해 주셔서 감사합니다.</p>
                  <p>다음에 또 방문해 주세요!</p>
                </div>
              </div>

              <!-- 액션 버튼 -->
              <div class="flex gap-3">
                <button
                    v-if="receipt.status === 'COMPLETED'"
                    @click="cancelReceipt"
                    class="flex-1 px-4 py-2 bg-red-100 text-red-600 rounded-lg hover:bg-red-200"
                >
                  취소하기
                </button>
                <button
                    @click="printReceipt"
                    class="flex-1 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200"
                >
                  인쇄하기
                </button>
                <button
                    @click="closeModal"
                    class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  닫기
                </button>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup>
import { ref } from 'vue'
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue'
import { useToast } from 'vue-toastification'
import axios from '@/plugins/axios.js'

const props = defineProps({
  isOpen: Boolean,
  receipt: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close'])
const toast = useToast()

// 닫기
const closeModal = () => {
  emit('close')
}

// 유틸리티 함수
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

// 소계 계산
const calculateSubtotal = () => {
  if (!props.receipt.items || props.receipt.items.length === 0) return 0
  return props.receipt.items.reduce((total, item) => total + Number(item.lineAmount), 0)
}

// 영수증 취소 처리
const cancelReceipt = async () => {
  try {
    if (!confirm('이 영수증을 취소하시겠습니까?')) {
      return
    }

    await axios.post(`/receipts/${props.receipt.receiptNumber}/cancel`)
    toast.success('영수증이 취소되었습니다.')
    emit('close')
    // 여기서 상위 컴포넌트에 재조회 이벤트를 발생시킬 수 있음
  } catch (error) {
    console.error('영수증 취소 실패:', error)
    toast.error('영수증 취소에 실패했습니다.')
  }
}

// 영수증 인쇄 처리
const printReceipt = () => {
  // 인쇄 창 열기
  const printWindow = window.open('', '_blank')

  // 인쇄할 HTML 생성
  const printContent = `
    <html>
    <head>
      <title>영수증 ${props.receipt.receiptNumber}</title>
      <style>
        body {
          font-family: monospace;
          padding: 20px;
          max-width: 300px;
          margin: 0 auto;
        }
        .header {
          text-align: center;
          margin-bottom: 10px;
          padding-bottom: 10px;
          border-bottom: 1px dashed #000;
        }
        .info {
          margin-bottom: 10px;
          font-size: 12px;
        }
        .items {
          margin-bottom: 10px;
          border-bottom: 1px dashed #000;
          padding-bottom: 10px;
        }
        .item {
          display: flex;
          justify-content: space-between;
          margin-bottom: 5px;
          font-size: 12px;
        }
        .total {
          margin-top: 10px;
          font-weight: bold;
        }
        .payment {
          margin-top: 10px;
          border-top: 1px dashed #000;
          padding-top: 10px;
        }
        .footer {
          margin-top: 20px;
          text-align: center;
          font-size: 12px;
        }
      </style>
    </head>
    <body onload="window.print(); window.close();">
      <div class="header">
        <h2>카페 길준</h2>
        <p>사업자번호: 123-45-67890</p>
        <p>서울시 강남구 테헤란로 123</p>
      </div>

      <div class="info">
        <div>영수증 번호: ${props.receipt.receiptNumber}</div>
        <div>판매 시간: ${formatDateTime(props.receipt.saleDate)}</div>
        <div>담당자: ${props.receipt.user?.username || '시스템'}</div>
      </div>

      <div class="items">
        ${props.receipt.items.map(item => `
          <div class="item">
            <span>${item.product?.name} x ${item.quantity}</span>
            <span>₩${formatNumber(item.lineAmount)}</span>
          </div>
        `).join('')}
      </div>

      <div>
        <div class="item">
          <span>소계:</span>
          <span>₩${formatNumber(calculateSubtotal())}</span>
        </div>
        <div class="item">
          <span>부가세:</span>
          <span>₩${formatNumber(props.receipt.taxAmount)}</span>
        </div>
        ${props.receipt.discountAmount && props.receipt.discountAmount > 0 ? `
          <div class="item">
            <span>할인:</span>
            <span>₩${formatNumber(props.receipt.discountAmount)}</span>
          </div>
        ` : ''}
        <div class="item total">
          <span>합계:</span>
          <span>₩${formatNumber(props.receipt.totalAmount)}</span>
        </div>
      </div>

      <div class="payment">
        <div>결제 정보</div>
        ${props.receipt.payments.map(payment => `
          <div class="item">
            <span>${getPaymentMethodName(payment.paymentType)}:</span>
            <span>₩${formatNumber(payment.paymentAmount)}</span>
          </div>
        `).join('')}
      </div>

      <div class="footer">
        <p>이용해 주셔서 감사합니다.</p>
        <p>다음에 또 방문해 주세요!</p>
      </div>
    </body>
    </html>
  `

  printWindow.document.write(printContent)
  printWindow.document.close()
}
</script>