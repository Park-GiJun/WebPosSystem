// PosMain.vue
<template>
  <div class="h-full bg-gray-100">
    <PosHeader
        :store-name="storeName"
        :cashier-name="cashierName"
        :current-time="currentTime"
        @toggle-menu="toggleMenu"
    />

    <div class="flex h-[calc(100vh-8rem)]">
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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useToast } from 'vue-toastification'
import PosHeader from '@/components/pos/PosHeader.vue'
import ProductGrid from '@/components/pos/ProductGrid.vue'
import OrderList from '@/components/pos/OrderList.vue'
import MenuModal from '@/components/modals/MenuModal.vue'
import PaymentModal from '@/components/modals/PaymentModal.vue'
import ProductsModal from '@/components/modals/ProductsModal.vue'
import SalesModal from '@/components/modals/PosSalesModal.vue'
import SettlementModal from '@/components/modals/SettlementModal.vue'
import SettingsModal from '@/components/modals/SettingsModal.vue'
import {History ,BarChart2, Calculator, Settings} from "lucide-vue-next";

const toast = useToast()

// 모달 상태
const showProductsModal = ref(false)
const showSalesModal = ref(false)
const showSettlementModal = ref(false)
const showSettingsModal = ref(false)
const showPaymentModal = ref(false)
const isProcessing = ref(false)

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
    icon: History,
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
  { id: 1, name: '현금', icon: 'cash' },
  { id: 2, name: '신용카드', icon: 'card' },
  { id: 3, name: '삼성페이', icon: 'samsung' },
  { id: 4, name: '카카오페이', icon: 'kakao' },
])

// State
const selectedCategory = ref(categories.value[0].id)
const orderItems = ref([])
const orderNumber = ref(generateOrderNumber())
const isMenuOpen = ref(false)

// Computed
const filteredProducts = computed(() => {
  return products.value.filter(product => product.categoryId === selectedCategory.value)
})

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

const removeItem = (index) => {
  const item = orderItems.value[index]
  orderItems.value.splice(index, 1)
  toast.info(`${item.name} 삭제됨`)
}

const increaseQuantity = (index) => {
  orderItems.value[index].quantity++
}

const decreaseQuantity = (index) => {
  if (orderItems.value[index].quantity > 1) {
    orderItems.value[index].quantity--
  } else {
    removeItem(index)
  }
}

const clearOrder = () => {
  if (orderItems.value.length === 0) return

  if (confirm('주문을 취소하시겠습니까?')) {
    orderItems.value = []
    orderNumber.value = generateOrderNumber()
    toast.info('주문이 취소되었습니다')
  }
}

const processPayment = async (method) => {
  try {
    isProcessing.value = true
    showPaymentModal.value = false

    // 결제 데이터 구성
    const paymentData = {
      orderNumber: orderNumber.value,
      items: orderItems.value.map(item => ({
        id: item.id,
        name: item.name,
        quantity: item.quantity,
        price: item.price,
        total: item.price * item.quantity
      })),
      summary: {
        subtotal: subtotal.value,
        tax: tax.value,
        total: total.value
      },
      paymentMethod: method.name,
      cashier: cashierName.value,
      timestamp: new Date().toISOString()
    }

    // 결제 처리 시뮬레이션
    await new Promise(resolve => setTimeout(resolve, 1500))

    // 영수증 출력
    printReceipt(paymentData)

    // 주문 초기화
    orderItems.value = []
    orderNumber.value = generateOrderNumber()

    // 성공 메시지
    toast.success('결제가 완료되었습니다')

    // 판매 기록 저장 (실제로는 API 호출)
    saveSalesRecord(paymentData)

  } catch (error) {
    console.error('Payment failed:', error)
    toast.error('결제 처리 중 오류가 발생했습니다')
  } finally {
    isProcessing.value = false
  }
}

const printReceipt = (data) => {
  // 영수증 출력 시뮬레이션
  console.log('=== 영수증 ===')
  console.log(`주문번호: ${data.orderNumber}`)
  console.log(`일시: ${new Date().toLocaleString()}`)
  console.log(`\n상품:`)
  data.items.forEach(item => {
    console.log(`${item.name} x ${item.quantity} = ₩${item.total.toLocaleString()}`)
  })
  console.log(`\n소계: ₩${data.summary.subtotal.toLocaleString()}`)
  console.log(`부가세: ₩${data.summary.tax.toLocaleString()}`)
  console.log(`합계: ₩${data.summary.total.toLocaleString()}`)
  console.log(`\n결제방법: ${data.paymentMethod}`)
  console.log(`담당: ${data.cashier}`)
  console.log('============')
}

const saveSalesRecord = (data) => {
  // 판매 기록 저장 시뮬레이션
  console.log('판매 기록 저장:', data)
}
</script>