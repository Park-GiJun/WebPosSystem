<!-- components/modals/ProductsModal.vue -->
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
            <DialogPanel class="w-full max-w-4xl transform overflow-hidden rounded-xl bg-white p-6 shadow-xl">
              <DialogTitle class="text-lg font-bold mb-6">상품 조회</DialogTitle>

              <!-- 검색 필터 -->
              <div class="mb-6 flex gap-4">
                <select v-model="selectedCategory" class="px-4 py-2 border rounded-lg">
                  <option value="">전체 카테고리</option>
                  <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                    {{ cat.name }}
                  </option>
                </select>
                <input
                    type="text"
                    v-model="searchKeyword"
                    placeholder="상품명 검색"
                    class="flex-1 px-4 py-2 border rounded-lg"
                />
              </div>

              <!-- 상품 목록 -->
              <div class="overflow-x-auto">
                <table class="w-full">
                  <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품코드</th>
                    <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">카테고리</th>
                    <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품명</th>
                    <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">가격</th>
                    <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">재고</th>
                    <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">상태</th>
                  </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-200">
                  <tr v-for="product in filteredProducts" :key="product.id">
                    <td class="px-6 py-4">{{ product.code }}</td>
                    <td class="px-6 py-4">{{ product.category }}</td>
                    <td class="px-6 py-4">{{ product.name }}</td>
                    <td class="px-6 py-4 text-right">₩{{ product.price.toLocaleString() }}</td>
                    <td class="px-6 py-4 text-right">{{ product.stock }}</td>
                    <td class="px-6 py-4">
                        <span :class="[
                          'px-2 py-1 rounded-full text-xs',
                          product.status === '판매중' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                        ]">
                          {{ product.status }}
                        </span>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>

              <button
                  @click="closeModal"
                  class="mt-6 w-full px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
              >
                닫기
              </button>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup>
import { ref, computed } from 'vue'
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

const closeModal = () => {
  emit('close')
}

// 더미 데이터
const categories = ref([
  { id: 1, name: '커피' },
  { id: 2, name: '논커피' },
  { id: 3, name: '티' },
  { id: 4, name: '디저트' },
])

const products = ref([
  { id: 1, code: 'P001', category: '커피', name: '아메리카노', price: 4500, stock: 100, status: '판매중' },
  { id: 2, code: 'P002', category: '커피', name: '카페라떼', price: 5000, stock: 80, status: '판매중' },
  { id: 3, code: 'P003', category: '논커피', name: '초코라떼', price: 5500, stock: 0, status: '품절' },
  { id: 4, code: 'P004', category: '티', name: '얼그레이', price: 4500, stock: 50, status: '판매중' },
])

const selectedCategory = ref('')
const searchKeyword = ref('')

const filteredProducts = computed(() => {
  return products.value.filter(product => {
    const matchesCategory = !selectedCategory.value || product.category === selectedCategory.value
    const matchesKeyword = !searchKeyword.value ||
        product.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    return matchesCategory && matchesKeyword
  })
})
</script>