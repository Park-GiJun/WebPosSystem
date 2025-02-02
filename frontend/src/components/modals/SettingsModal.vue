<!-- components/modals/SettingsModal.vue -->
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
            <DialogPanel class="w-full max-w-xl transform overflow-hidden rounded-xl bg-white p-6 shadow-xl">
              <DialogTitle class="text-lg font-bold mb-6">설정</DialogTitle>

              <!-- 설정 섹션 -->
              <div class="space-y-6">
                <!-- 기본 정보 -->
                <div>
                  <h3 class="text-sm font-medium text-gray-700 mb-3">기본 정보</h3>
                  <div class="space-y-3">
                    <div>
                      <label class="block text-sm text-gray-600 mb-1">매장명</label>
                      <input
                          type="text"
                          v-model="settings.storeName"
                          class="w-full px-4 py-2 border rounded-lg"
                      />
                    </div>
                    <div>
                      <label class="block text-sm text-gray-600 mb-1">사업자번호</label>
                      <input
                          type="text"
                          v-model="settings.businessNumber"
                          class="w-full px-4 py-2 border rounded-lg"
                      />
                    </div>
                  </div>
                </div>

                <!-- 영수증 설정 -->
                <div>
                  <h3 class="text-sm font-medium text-gray-700 mb-3">영수증 설정</h3>
                  <div class="space-y-3">
                    <div class="flex items-center gap-2">
                      <input
                          type="checkbox"
                          id="printReceipt"
                          v-model="settings.printReceipt"
                      />
                      <label for="printReceipt" class="text-sm text-gray-600">결제 시 영수증 자동 출력</label>
                    </div>
                    <div>
                      <label class="block text-sm text-gray-600 mb-1">영수증 하단 문구</label>
                      <textarea
                          v-model="settings.receiptFooter"
                          rows="2"
                          class="w-full px-4 py-2 border rounded-lg"
                      ></textarea>
                    </div>
                  </div>
                </div>

                <!-- 결제 설정 -->
                <div>
                  <h3 class="text-sm font-medium text-gray-700 mb-3">결제 설정</h3>
                  <div class="space-y-3">
                    <div class="grid grid-cols-2 gap-4">
                      <div v-for="method in settings.paymentMethods" :key="method.id">
                        <div class="flex items-center gap-2">
                          <input
                              type="checkbox"
                              :id="method.id"
                              v-model="method.enabled"
                          />
                          <label :for="method.id" class="text-sm text-gray-600">
                            {{ method.name }}
                          </label>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 프린터 설정 -->
                <div>
                  <h3 class="text-sm font-medium text-gray-700 mb-3">프린터 설정</h3>
                  <div class="space-y-3">
                    <div>
                      <label class="block text-sm text-gray-600 mb-1">영수증 프린터</label>
                      <select v-model="settings.receiptPrinter" class="w-full px-4 py-2 border rounded-lg">
                        <option value="">선택하세요</option>
                        <option v-for="printer in printers" :key="printer.id" :value="printer.id">
                          {{ printer.name }}
                        </option>
                      </select>
                    </div>
                    <button
                        @click="testPrint"
                        class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
                    >
                      테스트 출력
                    </button>
                  </div>
                </div>
              </div>

              <!-- 버튼 영역 -->
              <div class="flex gap-3 mt-6">
                <button
                    @click="closeModal"
                    class="flex-1 px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
                >
                  취소
                </button>
                <button
                    @click="saveSettings"
                    class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  저장
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
const settings = ref({
  storeName: '카페 길준',
  businessNumber: '123-45-67890',
  printReceipt: true,
  receiptFooter: '이용해 주셔서 감사합니다.\n다음에 또 방문해 주세요!',
  receiptPrinter: 'printer1',
  paymentMethods: [
    { id: 'cash', name: '현금', enabled: true },
    { id: 'card', name: '신용카드', enabled: true },
    { id: 'samsung', name: '삼성페이', enabled: true },
    { id: 'kakao', name: '카카오페이', enabled: true },
  ],
})

const printers = ref([
  { id: 'printer1', name: 'BIXOLON SRP-350III' },
  { id: 'printer2', name: 'EPSON TM-T88V' },
])

const closeModal = () => {
  emit('close')
}

const testPrint = () => {
  // 테스트 출력 로직
  console.log('테스트 출력')
}

const saveSettings = () => {
  // 설정 저장 로직
  console.log('설정 저장', settings.value)
  closeModal()
}
</script>
