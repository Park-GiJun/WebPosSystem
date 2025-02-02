<!-- components/modals/SettlementModal.vue -->
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
              <DialogTitle class="text-lg font-bold mb-6">정산 내역</DialogTitle>

              <!-- 정산 요약 -->
              <div class="grid grid-cols-2 gap-4 mb-6">
                <div class="bg-blue-50 p-4 rounded-lg">
                  <h3 class="text-sm text-gray-600 mb-1">시재금</h3>
                  <p class="text-2xl font-bold">₩{{ initialCash.toLocaleString() }}</p>
                </div>
                <div class="bg-green-50 p-4 rounded-lg">
                  <h3 class="text-sm text-gray-600 mb-1">현재 현금</h3>
                  <p class="text-2xl font-bold">₩{{ currentCash.toLocaleString() }}</p>
                </div>
              </div>

              <!-- 결제 수단별 매출 -->
              <div class="mb-6">
                <h3 class="text-sm font-medium mb-2">결제 수단별 매출</h3>
                <div class="space-y-2">
                  <div v-for="method in paymentMethods" :key="method.name" class="flex justify-between p-3 bg-gray-50 rounded-lg">
                    <span>{{ method.name }}</span>
                    <span class="font-medium">₩{{ method.amount.toLocaleString() }}</span>
                  </div>
                </div>
              </div>

              <!-- 현금 내역 -->
              <div class="mb-6">
                <h3 class="text-sm font-medium mb-2">현금 입출금 내역</h3>
                <div class="bg-white border rounded-lg overflow-hidden">
                  <table class="w-full">
                    <thead class="bg-gray-50">
                    <tr>
                      <th class="px-4 py-2 text-left text-sm font-medium text-gray-500">시간</th>
                      <th class="px-4 py-2 text-left text-sm font-medium text-gray-500">구분</th>
                      <th class="px-4 py-2 text-right text-sm font-medium text-gray-500">금액</th>
                      <th class="px-4 py-2 text-left text-sm font-medium text-gray-500">메모</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    <tr v-for="record in cashRecords" :key="record.id">
                      <td class="px-4 py-2">{{ record.time }}</td>
                      <td class="px-4 py-2">
                          <span :class="[
                            'px-2 py-1 rounded-full text-xs',
                            record.type === '입금' ? 'bg-blue-100 text-blue-800' : 'bg-red-100 text-red-800'
                          ]">
                            {{ record.type }}
                          </span>
                      </td>
                      <td class="px-4 py-2 text-right">₩{{ record.amount.toLocaleString() }}</td>
                      <td class="px-4 py-2">{{ record.memo }}</td>
                    </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- 마감 버튼 -->
              <div class="flex gap-3">
                <button
                    @click="closeModal"
                    class="flex-1 px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
                >
                  닫기
                </button>
                <button
                    @click="closeRegister"
                    class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  마감하기
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

const props = defineProps({
  isOpen: Boolean,
})

const emit = defineEmits(['close'])

// 더미 데이터
const initialCash = ref(500000)
const currentCash = ref(687000)

const paymentMethods = ref([
  { name: '현금', amount: 487000 },
  { name: '신용카드', amount: 1250000 },
  { name: '삼성페이', amount: 350000 },
  { name: '카카오페이', amount: 763000 },
])

const cashRecords = ref([
  { id: 1, time: '09:00', type: '입금', amount: 500000, memo: '시재금' },
  { id: 2, time: '10:15', type: '입금', amount: 45000, memo: '현금 결제' },
  { id: 3, time: '11:30', type: '출금', amount: 8000, memo: '물품 구매' },
  { id: 4, time: '13:45', type: '입금', amount: 150000, memo: '현금 결제' },
])

const closeModal = () => {
  emit('close')
}

const closeRegister = () => {
  // 마감 처리 로직
  if (confirm('정말 마감하시겠습니까?')) {
    // API 호출
    console.log('마감 처리')
    closeModal()
  }
}
</script>