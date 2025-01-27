<!-- PosMain.vue -->
<template>
  <div class="h-full bg-gray-100">
    <!-- Top Header -->
    <header class="flex-none bg-white shadow-sm p-4 flex justify-between items-center">
      <div class="flex items-center gap-4">
        <button @click="toggleMenu" class="text-gray-600">
          <Menu class="w-6 h-6" />
        </button>
        <span class="font-bold">매장명: {{ storeName }}</span>
      </div>
      <div class="flex items-center gap-4">
        <span>{{ currentTime }}</span>
        <span>{{ cashierName }}</span>
      </div>
    </header>

    <!-- Main Content -->
    <div class="flex h-[calc(100vh-8rem)]">
      <!-- Product Selection (Left Side) -->
      <div class="flex-1 p-4 flex flex-col">
        <!-- Categories -->
        <div class="flex-none mb-4">
          <div class="flex gap-2 overflow-x-auto">
            <button
                v-for="cat in categories"
                :key="cat.id"
                @click="selectedCategory = cat.id"
                :class="[
          'px-6 py-3 rounded-lg whitespace-nowrap font-medium',
          selectedCategory === cat.id ? 'bg-blue-600 text-white' : 'bg-white hover:bg-gray-50'
        ]"
            >
              {{ cat.name }}
            </button>
          </div>
        </div>

        <!-- Products Grid -->
        <div class="grid grid-cols-4 gap-4 overflow-auto flex-1">
          <button
              v-for="product in filteredProducts"
              :key="product.id"
              @click="addToOrder(product)"
              class="bg-white h-24 rounded-lg shadow hover:shadow-md transition-shadow flex flex-col justify-center items-center"
          >
            <h3 class="font-medium">{{ product.name }}</h3>
            <p class="text-blue-600">₩{{ product.price.toLocaleString() }}</p>
          </button>
      </div>

        <!-- Additional Actions -->
        <div class="grid grid-cols-3 gap-4 mt-4">
          <button class="bg-white p-4 rounded-lg shadow hover:shadow-md">
            <span class="font-medium">고객 검색</span>
          </button>
          <button class="bg-white p-4 rounded-lg shadow hover:shadow-md">
            <span class="font-medium">쿠폰 사용</span>
          </button>
          <button class="bg-white p-4 rounded-lg shadow hover:shadow-md">
            <span class="font-medium">프로모션</span>
          </button>
        </div>
      </div>

      <!-- Order List (Right Side) -->
      <div class="w-96 flex flex-col">
        <div class="flex flex-col h-full">
          <!-- Order Header -->
          <div class="p-4 border-b">
            <div class="flex justify-between items-center mb-2">
              <h2 class="text-lg font-bold">주문 내역</h2>
              <span class="text-gray-600">주문번호: #{{ orderNumber }}</span>
            </div>
          </div>

          <!-- Order Items -->
          <div class="flex-1 overflow-y-auto p-4">
            <div
                v-for="(item, index) in orderItems"
                :key="index"
                class="flex justify-between items-center mb-4 bg-gray-50 p-3 rounded-lg"
            >
              <div>
                <h4 class="font-medium">{{ item.name }}</h4>
                <div class="flex items-center mt-2">
                  <button
                      @click="decreaseQuantity(index)"
                      class="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center"
                  >-</button>
                  <span class="mx-3 w-8 text-center">{{ item.quantity }}</span>
                  <button
                      @click="increaseQuantity(index)"
                      class="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center"
                  >+</button>
                </div>
              </div>
              <div class="text-right">
                <p class="font-medium">₩{{ (item.price * item.quantity).toLocaleString() }}</p>
                <button
                    @click="removeItem(index)"
                    class="text-red-500 text-sm mt-1"
                >삭제</button>
              </div>
            </div>
          </div>

          <!-- Order Summary -->
          <div class="border-t p-4">
            <div class="space-y-2 mb-4">
              <div class="flex justify-between">
                <span>소계</span>
                <span>₩{{ subtotal.toLocaleString() }}</span>
              </div>
              <div class="flex justify-between">
                <span>부가세 (10%)</span>
                <span>₩{{ tax.toLocaleString() }}</span>
              </div>
              <div class="flex justify-between text-lg font-bold">
                <span>총계</span>
                <span>₩{{ total.toLocaleString() }}</span>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="grid grid-cols-2 gap-4">
              <button
                  @click="clearOrder"
                  class="px-4 py-3 bg-gray-200 rounded-lg hover:bg-gray-300"
              >
                주문취소
              </button>
              <button
                  @click="showPaymentModal = true"
                  :disabled="!orderItems.length"
                  class="px-4 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
              >
                결제
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Menu Modal -->
    <TransitionRoot appear :show="isMenuOpen" as="template">
      <Dialog as="div" @close="isMenuOpen = false" class="relative z-50">
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
              <DialogPanel class="w-full max-w-3xl bg-white rounded-2xl p-6">
                <DialogTitle class="text-lg font-medium mb-4">메뉴</DialogTitle>

                <div class="grid grid-cols-2 gap-4">
                  <button
                      v-for="menu in menuItems"
                      :key="menu.id"
                      @click="handleMenuClick(menu.action)"
                      class="flex items-center gap-3 p-4 bg-gray-50 rounded-lg hover:bg-gray-100"
                  >
                    <component :is="menu.icon" class="w-6 h-6" />
                    <div class="text-left">
                      <h3 class="font-medium">{{ menu.name }}</h3>
                      <p class="text-sm text-gray-500">{{ menu.description }}</p>
                    </div>
                  </button>
                </div>

                <button
                    @click="isMenuOpen = false"
                    class="mt-6 w-full px-4 py-2 bg-gray-100 rounded-lg"
                >
                  닫기
                </button>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>

    <!-- Payment Modal -->
    <TransitionRoot appear :show="showPaymentModal" as="template">
      <Dialog as="div" @close="showPaymentModal = false" class="relative z-50">
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
              <DialogPanel class="w-full max-w-md bg-white rounded-2xl p-6">
                <DialogTitle class="text-lg font-medium mb-4">결제 방법 선택</DialogTitle>

                <div class="grid grid-cols-2 gap-4">
                  <button
                      v-for="method in paymentMethods"
                      :key="method.id"
                      @click="processPayment(method)"
                      class="p-4 border rounded-lg hover:bg-gray-50"
                  >
                    {{ method.name }}
                  </button>
                </div>

                <button
                    @click="showPaymentModal = false"
                    class="mt-4 w-full px-4 py-2 bg-gray-100 rounded-lg"
                >
                  취소
                </button>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Menu, History, BarChart2, Calculator, Settings } from 'lucide-vue-next'
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue'

// Store Data
const storeName = ref('카페 길준')
const cashierName = ref('박길준')
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
  { id: 1, categoryId: 1, name: '아메리카노', price: 4500, image: '/api/placeholder/400/320' },
  { id: 2, categoryId: 1, name: '카페라떼', price: 5000, image: '/api/placeholder/400/320' },
  { id: 3, categoryId: 1, name: '카푸치노', price: 5000, image: '/api/placeholder/400/320' },
  { id: 4, categoryId: 2, name: '초코라떼', price: 5500, image: '/api/placeholder/400/320' },
  { id: 5, categoryId: 2, name: '녹차라떼', price: 5500, image: '/api/placeholder/400/320' },
  { id: 6, categoryId: 3, name: '얼그레이', price: 4500, image: '/api/placeholder/400/320' },
  { id: 7, categoryId: 3, name: '캐모마일', price: 4500, image: '/api/placeholder/400/320' },
  { id: 8, categoryId: 4, name: '치즈케이크', price: 6500, image: '/api/placeholder/400/320' },
])

const paymentMethods = ref([
  { id: 1, name: '현금' },
  { id: 2, name: '신용카드' },
  { id: 3, name: '삼성페이' },
  { id: 4, name: '카카오페이' },
])

// State
const selectedCategory = ref(categories.value[0].id)
const orderItems = ref([])
const orderNumber = ref(Math.floor(Math.random() * 1000) + 1)
const isMenuOpen = ref(false)
const showPaymentModal = ref(false)

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
const toggleMenu = () => {
  isMenuOpen.value = true
}

const handleMenuClick = (action) => {
  isMenuOpen.value = false
  // 각 메뉴 액션에 따른 처리
  switch (action) {
    case 'products':
      router.push('/pos/products')
      break
    case 'sales':
      showSalesModal()
      break
    case 'settlement':
      showSettlementModal()
      break
    case 'settings':
      router.push('/pos/settings')
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
  orderItems.value.splice(index, 1)
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
  orderItems.value = []
  orderNumber.value = Math.floor(Math.random() * 1000) + 1
}

const processPayment = async (method) => {
  try {
    // API 호출하여 결제 처리
    const paymentData = {
      orderNumber: orderNumber.value,
      items: orderItems.value,
      total: total.value,
      paymentMethod: method.name,
      timestamp: new Date().toISOString()
    }

    await api.processPayment(paymentData)

    // 영수증 출력
    printReceipt(paymentData)

    // 주문 초기화
    clearOrder()
    showPaymentModal.value = false

    // 성공 메시지
    toast.success('결제가 완료되었습니다')
  } catch (error) {
    console.error('Payment failed:', error)
    toast.error('결제 처리 중 오류가 발생했습니다')
  }
}

const printReceipt = (data) => {
  // 영수증 출력 로직
  console.log('Printing receipt:', data)
}
</script>