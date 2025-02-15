// views/sis/inventory/InventoryHistoryView.vue
<template>
  <div class="p-6">
    <h2 class="text-2xl font-bold mb-6">재고 이력</h2>

    <!-- 검색 필터 -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="col-span-2">
          <label class="block text-sm font-medium text-gray-700 mb-1">
            조회 기간
          </label>
          <div class="flex gap-2">
            <input
                type="date"
                v-model="filters.startDate"
                class="flex-1 px-4 py-2 border rounded-lg"
            />
            <span class="flex items-center">~</span>
            <input
                type="date"
                v-model="filters.endDate"
                class="flex-1 px-4 py-2 border rounded-lg"
            />
          </div>
        </div>

        <BaseSelect
            v-model="filters.categoryId"
            :options="categoryOptions"
            label="카테고리"
            placeholder="전체"
        />

        <BaseInput
            v-model="filters.keyword"
            label="검색어"
            placeholder="상품명 또는 코드"
        />

        <div class="flex items-end">
          <BaseButton
              class="w-full"
              @click="fetchHistory"
          >
            검색
          </BaseButton>
        </div>
      </div>
    </div>

    <!-- 재고 이력 테이블 -->
    <div class="bg-white rounded-lg shadow">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">일시</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품코드</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품명</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">유형</th>
            <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">수량</th>
            <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">이전재고</th>
            <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">현재재고</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">담당자</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">비고</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="history in historyList" :key="history.id">
            <td class="px-6 py-4">{{ formatDateTime(history.timestamp) }}</td>
            <td class="px-6 py-4">{{ history.productCode }}</td>
            <td class="px-6 py-4">{{ history.productName }}</td>
            <td class="px-6 py-4">
              <MovementTypeBadge :type="history.type" />
            </td>
            <td class="px-6 py-4 text-right" :class="history.quantity >= 0 ? 'text-blue-600' : 'text-red-600'">
              {{ history.quantity >= 0 ? '+' : '' }}{{ history.quantity }}
            </td>
            <td class="px-6 py-4 text-right">{{ history.previousStock }}</td>
            <td class="px-6 py-4 text-right">{{ history.currentStock }}</td>
            <td class="px-6 py-4">{{ history.handler }}</td>
            <td class="px-6 py-4">{{ history.note }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <BasePagination
          v-model:page="currentPage"
          :total-pages="totalPages"
          :total-elements="totalElements"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseInput from '@/components/base/BaseInput.vue'
import BaseSelect from '@/components/base/BaseSelect.vue'
import BasePagination from '@/components/base/BasePagination.vue'
import MovementTypeBadge from '@/components/inventory/MovementTypeBadge.vue'
import axios from '@/plugins/axios'

const toast = useToast()

// State
const filters = ref({
  startDate: null,
  endDate: null,
  categoryId: '',
  keyword: ''
})
const currentPage = ref(1)
const historyList = ref([])
const totalElements = ref(0)
const totalPages = ref(0)
const categoryOptions = ref([])

// Methods
const fetchCategories = async () => {
  try {
    const response = await axios.get('/api/categories')
    categoryOptions.value = response.data.data.map(cat => ({
      value: cat.id,
      label: cat.name
    }))
  } catch (error) {
    toast.error('카테고리 목록을 불러오는데 실패했습니다.')
  }
}

const fetchHistory = async () => {
  try {
    const params = {
      page: currentPage.value - 1,
      size: 10,
      ...filters.value
    }

    const response = await axios.get('/api/inventory/history', { params })
    const data = response.data.data

    historyList.value = data.content
    totalElements.value = data.totalElements
    totalPages.value = data.totalPages
  } catch (error) {
    toast.error('재고 이력을 불러오는데 실패했습니다.')
  }
}

const formatDateTime = (datetime) => {
  return new Date(datetime).toLocaleString()
}

// Watch changes
watch(currentPage, () => {
  fetchHistory()
})

// Initial fetch
onMounted(() => {
  fetchCategories()
  fetchHistory()
})
</script>

