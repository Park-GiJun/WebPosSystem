// components/pos/OrderList.vue
<template>
  <div class="w-96 bg-white shadow-lg flex flex-col">
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
        <div class="flex-1">
          <h4 class="font-medium">{{ item.name }}</h4>
          <div class="flex items-center mt-2">
            <button
                @click="decreaseQuantity(index)"
                class="w-8 h-8 rounded-full bg-gray-200 hover:bg-gray-300 flex items-center justify-center"
            >-</button>
            <span class="mx-3 w-8 text-center">{{ item.quantity }}</span>
            <button
                @click="increaseQuantity(index)"
                class="w-8 h-8 rounded-full bg-gray-200 hover:bg-gray-300 flex items-center justify-center"
            >+</button>
          </div>
        </div>
        <div class="text-right">
          <p class="font-medium">₩{{ (item.price * item.quantity).toLocaleString() }}</p>
          <button
              @click="removeItem(index)"
              class="text-red-500 text-sm mt-1 hover:text-red-600"
          >삭제</button>
        </div>
      </div>

      <div v-if="orderItems.length === 0" class="text-center text-gray-500 mt-8">
        상품을 선택해주세요
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
            :disabled="orderItems.length === 0"
            class="px-4 py-3 bg-gray-200 rounded-lg hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          주문취소
        </button>
        <button
            @click="$emit('showPayment')"
            :disabled="orderItems.length === 0"
            class="px-4 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          결제
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useToast } from 'vue-toastification'

const props = defineProps({
  orderItems: {
    type: Array,
    required: true
  },
  orderNumber: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['update:orderItems', 'showPayment'])
const toast = useToast()

const subtotal = computed(() => {
  return props.orderItems.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

const tax = computed(() => Math.floor(subtotal.value * 0.1))
const total = computed(() => subtotal.value + tax.value)

const removeItem = (index) => {
  const newItems = [...props.orderItems]
  const item = newItems[index]
  newItems.splice(index, 1)
  emit('update:orderItems', newItems)
  toast.info(`${item.name} 삭제됨`)
}

const increaseQuantity = (index) => {
  const newItems = [...props.orderItems]
  newItems[index].quantity++
  emit('update:orderItems', newItems)
}

const decreaseQuantity = (index) => {
  const newItems = [...props.orderItems]
  if (newItems[index].quantity > 1) {
    newItems[index].quantity--
    emit('update:orderItems', newItems)
  } else {
    removeItem(index)
  }
}

const clearOrder = () => {
  if (props.orderItems.length === 0) return

  if (confirm('주문을 취소하시겠습니까?')) {
    emit('update:orderItems', [])
    toast.info('주문이 취소되었습니다')
  }
}
</script>
