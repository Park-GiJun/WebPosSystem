<!-- PosMain.vue -->
<template>
  <div class="h-full bg-gray-100">
    <!-- Header -->
    <header class="bg-white shadow-sm p-4 flex justify-between items-center">
      <div class="flex items-center gap-4">
        <button @click="toggleMenu" class="text-gray-600">
          <Menu class="w-6 h-6" />
        </button>
        <span class="font-bold">매장명: {{ storeName }}</span>
      </div>
      <div class="flex items-center gap-4">
        <button @click="showReceiptHistoryModal = true" class="flex items-center gap-2 text-blue-600 hover:text-blue-800">
          <Receipt class="w-5 h-5" />
          영수증 조회
        </button>
        <span>{{ currentTime }}</span>
        <span>{{ cashierName }}</span>
      </div>
    </header>

    <!-- Main Content -->
    <main class="h-[calc(100vh-4rem)]">
      <div class="flex h-full">
        <ProductGrid
            :categories="categories"
            :products="products"
            @add-item="addToOrder"
        />

        <OrderList
            v-model:order-items="orderItems"
            :order-number="orderNumber"
            @show-payment="showPaymentModal = true"
        />
      </div>
    </main>

    <!-- Menu Modal -->
    <MenuModal
        :is-open="isMenuOpen"
        :menu-items="menuItems"
        @close="isMenuOpen = false"
        @menu-click="handleMenuClick"
    />

    <!-- Payment Modal -->
    <PaymentModal
        :is-open="showPaymentModal"
        :order-number="orderNumber"
        :total="total"
        :payment-methods="paymentMethods"
        :is-processing="isProcessing"
        @close="showPaymentModal = false"
        @process-payment="processPayment"
    />

    <!-- Other modals -->
    <ProductsModal :is-open="showProductsModal" @close="showProductsModal = false" />
    <SalesModal :is-open="showSalesModal" @close="showSalesModal = false" />
    <SettlementModal :is-open="showSettlementModal" @close="showSettlementModal = false" />
    <SettingsModal :is-open="showSettingsModal" @close="showSettingsModal = false" />

    <!-- Receipt History Modal -->
    <ReceiptHistoryModal
        :is-open="showReceiptHistoryModal"
        @close="showReceiptHistoryModal = false"
        @select="showReceiptDetail"
    />

    <!-- Receipt Detail Modal -->
    <ReceiptDetailModal
        :is-open="showReceiptDetailModal"
        :receipt-number="selectedReceiptNumber"
        @close="showReceiptDetailModal = false"
        @canceled="handleReceiptCanceled"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useToast } from 'vue-toastification'
import { Menu, Store, ShoppingCart, Receipt } from 'lucide-vue-next'
import PosHeader from '@/components/pos/PosHeader.vue'
import ProductGrid from '@/components/pos/ProductGrid.vue'
import OrderList from '@/components/pos/OrderList.vue'
import MenuModal from '@/components/modals/MenuModal.vue'
import PaymentModal from '@/components/modals/PaymentModal.vue'
import ProductsModal from '@/components/modals/ProductsModal.vue'
import SalesModal from '@/components/modals/PosSalesModal.vue'
import SettlementModal from '@/components/modals/SettlementModal.vue'
import SettingsModal from '@/components/modals/SettingsModal.vue'
import ReceiptHistoryModal from '@/components/modals/ReceiptHistoryModal.vue'
import ReceiptDetailModal from '@/components/modals/ReceiptDetailModal.vue'
import { posService } from '@/services/posService'

const toast = useToast()

// 모달 상태
const showProductsModal = ref(false)
const showSalesModal = ref(false)
const showSettlementModal = ref(false)
const showSettingsModal = ref(false)
const showPaymentModal = ref(false)
const showReceiptHistoryModal = ref(false)
const showReceiptDetailModal = ref(false)
const isProcessing = ref(false)
const isMenuOpen = ref(false)
const selectedReceiptNumber = ref('')

// Store Data
const storeName = ref('카페')
const cashierName = ref('홍길동')
const currentTime = ref(new Date().toLocaleString())

// Update time every second
setInterval(() => {
  currentTime.value = new Date().toLocaleString()
}, 1000)

// Menu Items
const menuItems = [
  {
    id: 1,
    name: '상품 조회',
    description: '상품 및 재고 현황 조회',
    icon: Store,
    action: 'products'
  },
  {
    id: 2,
    name: '오늘 매출',
    description: '금일 매출 현황',
    icon: BarChart2,
    action: 'sales'
  },
  {
    id: 3,
    name: '정산 내역',
    description: '시재 및 정산 내역',
    icon: Calculator,
    action: 'settlement'
  },
  {
    id: 4,
    name: '설정',
    description: '시스템 설정',
    icon: Settings,
    action: 'settings'
  }
]

// Categories and Products
const categories = ref([
  { id: 1, name: '커피' },
  { id: 2, name: '논커피' },
  { id: 3, name: '티' },
  { id: 4, name: '디저트' },
  { id: 5, name: '베이커리' }
])

const products = ref([
  { id: 1, categoryId: 1, name: '아메리카노', price: 4500 },
  { id: 2, categoryId: 1, name: '카페라떼', price: 5000 },
  { id: 3, categoryId: 1, name: '카푸치노', price: 5000 },
  { id: 4, categoryId: 2, name: '초코라떼', price: 5500 },
  { id: 5, categoryId: 2, name: '녹차라떼', price: 5500 },
  { id: 6, categoryId: 3, name: '얼그레이', price: 4500 },
  { id: 7, categoryId: 3, name: '캐모마일', price: 4500 },
  { id: 8, categoryId: 4, name: '치즈케이크', price: 6500 },
])

const paymentMethods = ref([
  { id: 1, name: 'CASH', label: '현금', icon: 'cash' },
  { id: 2, name: 'CREDIT_CARD', label: '신용카드', icon: 'card' },
  { id: 3, name: 'SAMSUNG_PAY', label: '삼성페이', icon: 'samsung' },
  { id: 4, name: 'KAKAO_PAY', label: '카카오페이', icon: 'kakao' },
])

// State
const orderItems = ref([])
const orderNumber = ref(generateOrderNumber())

// Computed
const subtotal = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

const tax = computed(() => Math.floor(subtotal.value * 0.1))
const total = computed(() => subtotal.value + tax.value)

// Methods
function generateOrderNumber() {
  const date = new Date()
  const year = date.getFullYear().toString().slice(-2)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0')
  return `${year}${month}${day}${random}`
}

const toggleMenu = () => {
  isMenuOpen.value = true
}

const handleMenuClick = (action) => {
  isMenuOpen.value = false
  switch (action) {
    case 'products':
      showProductsModal.value = true
      break
    case 'sales':
      showSalesModal.value = true
      break
    case 'settlement':
      showSettlementModal.value = true
      break
    case 'settings':
      showSettingsModal.value = true
      break
  }
}

const addToOrder = (product) => {
  const existingItem = orderItems.value.find(item => item.id === product.id)
  if (existingItem) {
    existingItem.quantity++
  } else {
    orderItems.value.push({ ...product, quantity: 1 })
  }
}

// 결제 처리
const processPayment = async (method) => {
  try {
    isProcessing.value = true
    showPaymentModal.value = false

    // 결제 데이터 구성
    const paymentData = {
      paymentType: method.name,
      paymentAmount: total.value,
      approvalNumber: generateApprovalNumber(),
      cardInfo: method.name === 'CREDIT_CARD' ? '1234-****-****-5678' : null
    }

    // 영수증 데이터 구성
    const receiptData = {
      items: orderItems.value.map(item => ({
        productId: item.id,
        quantity: item.quantity,
        unitPrice: item.price
      })),
      payments: [paymentData]
    }

    // 영수증 생성 API 호출
    const receipt = await posService.createReceipt(receiptData)

    // 영수증 출력
    printReceipt(receipt)

    // 주문 초기화
    orderItems.value = []
    orderNumber.value = generateOrderNumber()

    // 성공 메시지
    toast.success(`결제가 완료되었습니다. 영수증 번호: ${receipt.receiptNumber}`)

  } catch (error) {
    console.error('Payment failed:', error)
    toast.error('결제 처리 중 오류가 발생했습니다')
  } finally {
    isProcessing.value = false
  }
}

// 승인번호 생성 함수
const generateApprovalNumber = () => {
  const date = new Date()
  const year = date.getFullYear().toString().slice(-2)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `${year}${month}${day}${random}`
}

// 영수증 출력 함수
const printReceipt = (receipt) => {
  console.log('=== 영수증 ===')
  console.log(`영수증 번호: ${receipt.receiptNumber}`)
  console.log(`일시: ${new Date(receipt.saleDate).toLocaleString()}`)
  console.log(`\n상품:`)
  receipt.items.forEach(item => {
    console.log(`${item.product.name} x ${item.quantity} = ₩${item.lineAmount.toLocaleString()}`)
  })
  console.log(`\n소계: ₩${receipt.totalAmount.toLocaleString()}`)
  console.log(`부가세: ₩${receipt.taxAmount.toLocaleString()}`)

  if (receipt.discountAmount && receipt.discountAmount > 0) {
    console.log(`할인: ₩${receipt.discountAmount.toLocaleString()}`)
  }

  console.log(`합계: ₩${receipt.totalAmount.toLocaleString()}`)

  receipt.payments.forEach(payment => {
    console.log(`\n결제방법: ${payment.paymentType}`)
    console.log(`결제금액: ₩${payment.paymentAmount.toLocaleString()}`)
    if (payment.approvalNumber) {
      console.log(`승인번호: ${payment.approvalNumber}`)
    }
    if (payment.cardInfo) {
      console.log(`카드정보: ${payment.cardInfo}`)
    }
  })

  console.log(`\n담당: ${receipt.user.username}`)
  console.log('============')
}

// 영수증 상세 보기
const showReceiptDetail = (receiptNumber) => {
  selectedReceiptNumber.value = receiptNumber
  showReceiptDetailModal.value = true
  showReceiptHistoryModal.value = false
}

// 영수증 취소 처리
const handleReceiptCanceled = () => {
  toast.success('영수증이 성공적으로 취소되었습니다.')
}
</script>