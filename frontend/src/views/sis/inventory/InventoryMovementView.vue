// views/sis/inventory/InventoryMovementView.vue
<template>
  <div class="p-6 space-y-6">
    <!-- 헤더 -->
    <div class="flex justify-between items-center">
      <h2 class="text-2xl font-bold">입/출고 관리</h2>
      <div class="flex gap-2">
        <BaseButton @click="showStockInModal = true">
          입고 등록
        </BaseButton>
        <BaseButton @click="showStockOutModal = true">
          출고 등록
        </BaseButton>
      </div>
    </div>

    <!-- 검색 필터 -->
    <div class="bg-white rounded-lg shadow p-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="col-span-2">
          <label class="block text-sm font-medium text-gray-700 mb-1">기간</label>
          <div class="flex gap-2">
            <BaseInput
                type="date"
                v-model="filters.startDate"
                class="flex-1"
            />
            <span class="flex items-center">~</span>
            <BaseInput
                type="date"
                v-model="filters.endDate"
                class="flex-1"
            />
          </div>
        </div>

        <BaseSelect
            v-model="filters.type"
            :options="movementTypeOptions"
            label="유형"
            placeholder="전체"
        />

        <div class="flex items-end">
          <BaseButton
              class="w-full"
              @click="fetchMovements"
          >
            검색
          </BaseButton>
        </div>
      </div>
    </div>

    <!-- 요약 정보 -->
    <div class="grid grid-cols-3 gap-4">
      <div class="bg-white rounded-lg shadow p-4">
        <div class="text-sm text-gray-500 mb-1">총 입고</div>
        <div class="text-2xl font-bold text-blue-600">{{ totalInQuantity.toLocaleString() }}개</div>
      </div>
      <div class="bg-white rounded-lg shadow p-4">
        <div class="text-sm text-gray-500 mb-1">총 출고</div>
        <div class="text-2xl font-bold text-red-600">{{ totalOutQuantity.toLocaleString() }}개</div>
      </div>
      <div class="bg-white rounded-lg shadow p-4">
        <div class="text-sm text-gray-500 mb-1">총 건수</div>
        <div class="text-2xl font-bold">{{ totalCount.toLocaleString() }}건</div>
      </div>
    </div>

    <!-- 데이터 테이블 -->
    <div class="bg-white rounded-lg shadow">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">일시</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">유형</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품코드</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품명</th>
            <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">수량</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">이전재고</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">현재재고</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">담당자</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">비고</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="item in movements" :key="item.id">
            <td class="px-6 py-4">{{ formatDateTime(item.timestamp) }}</td>
            <td class="px-6 py-4">
              <StockMovementTypeBadge :type="item.type" />
            </td>
            <td class="px-6 py-4">{{ item.productCode }}</td>
            <td class="px-6 py-4">{{ item.productName }}</td>
            <td class="px-6 py-4 text-right">
                <span :class="item.type === 'OUT' ? 'text-red-600' : 'text-blue-600'">
                  {{ item.type === 'OUT' ? '-' : '+' }}{{ item.quantity.toLocaleString() }}
                </span>
            </td>
            <td class="px-6 py-4">{{ item.previousStock.toLocaleString() }}</td>
            <td class="px-6 py-4">{{ item.currentStock.toLocaleString() }}</td>
            <td class="px-6 py-4">{{ item.handler }}</td>
            <td class="px-6 py-4">{{ item.note }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <BasePagination
          v-model:page="currentPage"
          :total-pages="totalPages"
          :total-elements="totalElements"
          :is-first-page="isFirstPage"
          :is-last-page="isLastPage"
      />
    </div>

    <!-- 입고 모달 -->
    <BaseModal
        v-model="showStockInModal"
        title="입고 등록"
    >
      <StockMovementForm
          type="IN"
          :is-submitting="isSubmitting"
          @submit="handleStockIn"
          @cancel="showStockInModal = false"
      />
    </BaseModal>

    <!-- 출고 모달 -->
    <BaseModal
        v-model="showStockOutModal"
        title="출고 등록"
    >
      <StockMovementForm
          type="OUT"
          :is-submitting="isSubmitting"
          @submit="handleStockOut"
          @cancel="showStockOutModal = false"
      />
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useToast } from 'vue-toastification'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseInput from '@/components/base/BaseInput.vue'
import BaseSelect from '@/components/base/BaseSelect.vue'
import BaseModal from '@/components/base/BaseModal.vue'
import BasePagination from '@/components/base/BasePagination.vue'
import StockMovementTypeBadge from '@/components/inventory/StockMovementTypeBadge.vue'
import StockMovementForm from '@/components/inventory/StockMovementForm.vue'
import axios from '@/plugins/axios'

const toast = useToast()

// State
const filters = ref({
  startDate: null,
  endDate: null,
  type: ''
})

const currentPage = ref(1)
const movements = ref([])
const totalElements = ref(0)
const totalPages = ref(0)
const isFirstPage = ref(true)
const isLastPage = ref(true)
const totalInQuantity = ref(0)
const totalOutQuantity = ref(0)
const totalCount = ref(0)

const showStockInModal = ref(false)
const showStockOutModal = ref(false)
const isSubmitting = ref(false)

// Options
const movementTypeOptions = [
  { value: '', label: '전체' },
  { value: 'IN', label: '입고' },
  { value: 'OUT', label: '출고' }
]

// Methods
const fetchMovements = async () => {
  try {
    const params = {
      page: currentPage.value - 1,
      size: 10,
      startDate: filters.value.startDate,
      endDate: filters.value.endDate,
      type: filters.value.type
    }

    const response = await axios.get('/api/inventory/movements', { params })
    const data = response.data.data

    movements.value = data.content
    totalElements.value = data.totalElements
    totalPages.value = data.totalPages
    isFirstPage.value = data.first
    isLastPage.value = data.last
    totalInQuantity.value = data.totalInQuantity
    totalOutQuantity.value = data.totalOutQuantity
    totalCount.value = data.totalElements
  } catch (error) {
    toast.error('입/출고 내역을 불러오는데 실패했습니다.')
  }
}

const handleStockIn = async (data) => {
  try {
    isSubmitting.value = true
    await axios.post('/api/inventory/movement/in', data)
    toast.success('입고가 등록되었습니다.')
    showStockInModal.value = false
    fetchMovements()
  } catch (error) {
    toast.error('입고 등록에 실패했습니다.')
  } finally {
    isSubmitting.value = false
  }
}

const handleStockOut = async (data) => {
  try {
    isSubmitting.value = true
    await axios.post('/api/inventory/movement/out', data)
    toast.success('출고가 등록되었습니다.')
    showStockOutModal.value = false
    fetchMovements()
  } catch (error) {
    toast.error('출고 등록에 실패했습니다.')
  } finally {
    isSubmitting.value = false
  }
}

const formatDateTime = (datetime) => {
  return new Date(datetime).toLocaleString()
}

// 페이지 변경 감지
watch(currentPage, () => {
  fetchMovements()
})

// 초기 데이터 로드
fetchMovements()
</script>