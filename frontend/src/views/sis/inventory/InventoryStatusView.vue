// views/sis/inventory/InventoryStatusView.vue
<template>
  <div class="p-6">
    <h2 class="text-2xl font-bold mb-6">재고 현황</h2>

    <!-- 검색 필터 -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <BaseSelect
            v-model="filters.categoryId"
            :options="categoryOptions"
            label="카테고리"
            placeholder="전체"
        />

        <BaseSelect
            v-model="filters.stockStatus"
            :options="stockStatusOptions"
            label="재고 상태"
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
              @click="fetchInventoryStatus"
          >
            검색
          </BaseButton>
        </div>
      </div>
    </div>

    <!-- 재고 현황 테이블 -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <div class="p-4 border-b">
        <div class="flex justify-between items-center">
          <div class="flex gap-4">
            <span>총 상품: {{ totalProducts }}개</span>
            <span>|</span>
            <span>총 재고: {{ totalStock }}개</span>
          </div>
          <BaseButton @click="exportToExcel">
            엑셀 다운로드
          </BaseButton>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품코드</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">카테고리</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품명</th>
            <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">현재고</th>
            <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">최소재고</th>
            <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">최대재고</th>
            <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">상태</th>
            <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">관리</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="item in inventoryItems" :key="item.id">
            <td class="px-6 py-4">{{ item.productCode }}</td>
            <td class="px-6 py-4">{{ item.categoryName }}</td>
            <td class="px-6 py-4">{{ item.productName }}</td>
            <td class="px-6 py-4 text-right">{{ item.currentStock }}</td>
            <td class="px-6 py-4 text-right">{{ item.minStock }}</td>
            <td class="px-6 py-4 text-right">{{ item.maxStock }}</td>
            <td class="px-6 py-4">
              <StockStatusBadge :status="item.stockStatus" />
            </td>
            <td class="px-6 py-4">
              <div class="flex justify-center gap-2">
                <BaseButton
                    variant="secondary"
                    @click="showStockAdjustModal(item)"
                >
                  조정
                </BaseButton>
                <BaseButton
                    variant="secondary"
                    @click="showStockHistoryModal(item)"
                >
                  이력
                </BaseButton>
              </div>
            </td>
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

    <!-- 재고 조정 모달 -->
    <BaseModal
        v-model="showAdjustModal"
        title="재고 조정"
    >
      <form @submit.prevent="handleStockAdjust" class="space-y-4">
        <div class="bg-gray-50 p-4 rounded-lg mb-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <div class="text-sm text-gray-500">상품코드</div>
              <div class="font-medium">{{ selectedItem?.productCode }}</div>
            </div>
            <div>
              <div class="text-sm text-gray-500">상품명</div>
              <div class="font-medium">{{ selectedItem?.productName }}</div>
            </div>
            <div>
              <div class="text-sm text-gray-500">현재 재고</div>
              <div class="font-medium">{{ selectedItem?.currentStock }}개</div>
            </div>
          </div>
        </div>

        <BaseInput
            v-model="adjustForm.quantity"
            type="number"
            label="조정 수량"
            required
        />

        <BaseTextarea
            v-model="adjustForm.reason"
            label="조정 사유"
            required
        />

        <div class="flex justify-end gap-2">
          <BaseButton
              type="button"
              variant="secondary"
              @click="showAdjustModal = false"
          >
            취소
          </BaseButton>
          <BaseButton
              type="submit"
              :disabled="isSubmitting"
          >
            저장
          </BaseButton>
        </div>
      </form>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useToast } from 'vue-toastification'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseInput from '@/components/base/BaseInput.vue'
import BaseSelect from '@/components/base/BaseSelect.vue'
import BaseTextarea from '@/components/base/BaseTextarea.vue'
import BaseModal from '@/components/base/BaseModal.vue'
import BasePagination from '@/components/base/BasePagination.vue'
import StockStatusBadge from '@/components/inventory/StockStatusBadge.vue'
import axios from '@/plugins/axios'

const toast = useToast()

// State
const filters = ref({
  categoryId: '',
  stockStatus: '',
  keyword: ''
})
const currentPage = ref(1)
const inventoryItems = ref([])
const totalElements = ref(0)
const totalPages = ref(0)
const totalProducts = ref(0)
const totalStock = ref(0)
const showAdjustModal = ref(false)
const selectedItem = ref(null)
const isSubmitting = ref(false)

const adjustForm = ref({
  quantity: 0,
  reason: ''
})

// Options
const categoryOptions = ref([])
const stockStatusOptions = [
  { value: 'NORMAL', label: '정상' },
  { value: 'LOW', label: '부족' },
  { value: 'OUT_OF_STOCK', label: '품절' },
  { value: 'EXCESS', label: '과다' }
]

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

const fetchInventoryStatus = async () => {
  try {
    const params = {
      page: currentPage.value - 1,
      size: 10,
      ...filters.value
    }

    const response = await axios.get('/api/inventory/status', { params })
    const data = response.data.data

    inventoryItems.value = data.content
    totalElements.value = data.totalElements
    totalPages.value = data.totalPages
    totalProducts.value = data.totalProducts
    totalStock.value = data.totalStock
  } catch (error) {
    toast.error('재고 현황을 불러오는데 실패했습니다.')
  }
}

const showStockAdjustModal = (item) => {
  selectedItem.value = item
  showAdjustModal.value = true
  adjustForm.value = {
    quantity: 0,
    reason: ''
  }
}

const handleStockAdjust = async () => {
  try {
    isSubmitting.value = true

    await axios.post(`/api/inventory/${selectedItem.value.id}/adjust`, {
      quantity: Number(adjustForm.value.quantity),
      reason: adjustForm.value.reason
    })

    toast.success('재고가 조정되었습니다.')
    showAdjustModal.value = false
    fetchInventoryStatus()
  } catch (error) {
    toast.error('재고 조정에 실패했습니다.')
  } finally {
    isSubmitting.value = false
  }
}

const exportToExcel = async () => {
  try {
    const response = await axios.get('/api/inventory/status/export', {
      params: filters.value,
      responseType: 'blob'
    })

    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', '재고현황.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } catch (error) {
    toast.error('엑셀 다운로드에 실패했습니다.')
  }
}

// Watch
watch(currentPage, () => {
  fetchInventoryStatus()
})

// Initial fetch
fetchCategories()
fetchInventoryStatus()
</script>