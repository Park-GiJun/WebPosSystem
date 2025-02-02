// components/modals/PaymentModal.vue
<template>
  <TransitionRoot appear :show="isOpen" as="template">
    <Dialog as="div" @close="$emit('close')" class="relative z-50">
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
            <DialogPanel class="w-full max-w-lg bg-white rounded-2xl p-6">
              <DialogTitle class="text-lg font-medium mb-4">결제 방법 선택</DialogTitle>

              <!-- 주문 정보 -->
              <div class="mb-6">
                <div class="bg-gray-50 p-4 rounded-lg">
                  <div class="flex justify-between mb-2">
                    <span class="text-gray-600">주문번호</span>
                    <span class="font-medium">#{{ orderNumber }}</span>
                  </div>
                  <div class="flex justify-between text-lg font-bold">
                    <span>결제 금액</span>
                    <span>₩{{ total.toLocaleString() }}</span>
                  </div>
                </div>
              </div>

              <!-- 결제 수단 선택 -->
              <div class="grid grid-cols-2 gap-4">
                <button
                    v-for="method in paymentMethods"
                    :key="method.id"
                    @click="handlePayment(method)"
                    :disabled="isProcessing"
                    class="relative p-6 border rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed group"
                >
                  <div class="text-center">
                    <span class="block text-lg font-medium mb-1">{{ method.name }}</span>
                  </div>
                </button>
              </div>

              <button
                  @click="$emit('close')"
                  :disabled="isProcessing"
                  class="mt-6 w-full px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                취소
              </button>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup>
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue'

defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  orderNumber: {
    type: String,
    required: true
  },
  total: {
    type: Number,
    required: true
  },
  paymentMethods: {
    type: Array,
    required: true
  },
  isProcessing: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'process-payment'])

const handlePayment = (method) => {
  emit('process-payment', method)
}
</script>