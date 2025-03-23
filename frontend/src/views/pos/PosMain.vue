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
          :defaultCategories="categories"
          :defaultProducts="products"
          @add-item="addToOrder"
      />

      <OrderList
          v-model:orderItems="orderItems"
          :orderNumber="orderNumber"
          :isProcessing="isProcessing"
          @showPayment="showPaymentModal = true"
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
import { ref, computed, onMounted } from 'vue'
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
import { History, BarChart2, Calculator, Settings } from "lucide-vue-next"
import { posService } from '@/services/posService'
import axios from '@/plugins/axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const toast = useToast()

// 모달 상태
const showProductsModal = ref(false)
const showSalesModal = ref(false)
const showSettlementModal = ref(false)
const showSettingsModal = ref(false)
const showPaymentModal = ref(false)
const isProcessing = ref(false)
const isLoading = ref(false)

// Store Data
const storeName = ref('카페 길준')
const cashierName = ref(localStorage.getItem('username') || '홍길동')
const currentTime = ref(new Date().toLocaleString('ko-KR'))
const userId = ref(localStorage.getItem('userId'))

// Update time every second
const timeInterval = setInterval(() => {
  currentTime.value = new Date().toLocaleString('ko-KR')
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

// State
const categories = ref([])
const products = ref([])
const orderItems = ref([])
const orderNumber = ref(generateOrderNumber())
const isMenuOpen = ref(false)

// 결제 수단 매핑 (백엔드 enum에 맞춤)
const paymentMethods = ref([
  { id: 'CASH', name: '현금', icon: 'cash' },
  { id: 'CREDIT_CARD', name: '신용카드', icon: 'card' },
  { id: 'SAMSUNG_PAY', name: '삼성페이', icon: 'samsung' },
  { id: 'KAKAO_PAY', name: '카카오페이', icon: 'kakao' }
])

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
  // 재고 확인 (RECIPE_PRODUCT 타입은 재고 제한이 없다고 가정)
  if (product.stock <= 0 && product.productType !== 'RECIPE_PRODUCT') {
    toast.error(`${product.name}은(는) 품절입니다.`)
    return
  }

  const existingItem = orderItems.value.find(item => item.id === product.id)
  if (existingItem) {
    existingItem.quantity++
    toast.info(`${product.name} 수량 증가`)
  } else {
    orderItems.value.push({ ...product, quantity: 1 })
    toast.success(`${product.name} 추가됨`)
  }
}

const fetchInitialData = async () => {
  try {
    isLoading.value = true

    // 카테고리 불러오기
    const categoriesResponse = await posService.getCategories()
    categories.value = categoriesResponse.content || []

    // 상품 불러오기
    const productsResponse = await posService.getProducts({
      status: 'ON_SALE',
      sort: 'price,asc'
    })
    products.value = (productsResponse.content || []).filter(
        product => product.productType !== 'RAW_MATERIAL'
    )
  } catch (error) {
    console.error('데이터 로드 실패:', error)
    toast.error('초기 데이터를 불러오는데 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

const processPayment = async (method) => {
  try {
    isProcessing.value = true
    showPaymentModal.value = false

    // 결제 요청 데이터 구성
    const receiptRequest = {
      items: orderItems.value.map(item => ({
        productId: item.id,
        quantity: item.quantity,
        unitPrice: item.price
      })),
      payments: [
        {
          paymentType: method.id,
          paymentAmount: total.value,
          approvalNumber: generateApprovalNumber(),
          cardInfo: method.id === 'CREDIT_CARD' ? '신용카드 정보' : null
        }
      ]
    }

    // 영수증 API 호출
    const response = await axios.post('/receipts', { data: receiptRequest })
    const receiptData = response.data.data

    // 영수증 출력
    printReceipt({
      receiptNumber: receiptData.receiptNumber,
      items: orderItems.value,
      subtotal: subtotal.value,
      tax: tax.value,
      total: total.value,
      paymentMethod: method.name,
      cashier: cashierName.value
    })

    // 주문 초기화
    orderItems.value = []
    orderNumber.value = generateOrderNumber()

    // 성공 메시지
    toast.success('결제가 완료되었습니다')

  } catch (error) {
    console.error('Payment failed:', error)

    if (error.response && error.response.data) {
      toast.error(`결제 실패: ${error.response.data.message || '결제 처리 중 오류가 발생했습니다'}`)
    } else {
      toast.error('결제 처리 중 오류가 발생했습니다')
    }

    // 개발 목적으로 에러 발생 시에도 주문 초기화 (실제 환경에서는 제거)
    orderItems.value = []
    orderNumber.value = generateOrderNumber()
  } finally {
    isProcessing.value = false
  }
}

// 승인번호 생성 함수
const generateApprovalNumber = () => {
  const dateStr = new Date().toISOString().replace(/[-:T.Z]/g, '').slice(0, 14)
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `APV${dateStr}${random}`
}

const printReceipt = (data) => {
  // 영수증 출력 시뮬레이션
  console.log('=== 영수증 ===')
  console.log(`영수증번호: ${data.receiptNumber}`)
  console.log(`일시: ${new Date().toLocaleString('ko-KR')}`)
  console.log(`\n상품:`)
  data.items.forEach(item => {
    console.log(`${item.name} x ${item.quantity} = ₩${(item.price * item.quantity).toLocaleString()}`)
  })
  console.log(`\n소계: ₩${data.subtotal.toLocaleString()}`)
  console.log(`부가세: ₩${data.tax.toLocaleString()}`)
  console.log(`합계: ₩${data.total.toLocaleString()}`)
  console.log(`\n결제방법: ${data.paymentMethod}`)
  console.log(`담당: ${data.cashier}`)
  console.log('============')

  // 실제로는 영수증 출력 API 호출
  // 예: posService.printReceipt(data.receiptNumber)
}

// 초기 데이터 로드
onMounted(() => {
  fetchInitialData()

  // 컴포넌트 언마운트 시 타이머 정리
  return () => {
    clearInterval(timeInterval)
  }
})
</script>