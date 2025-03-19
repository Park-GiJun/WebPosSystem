<!-- components/recipe/TeleportIngredientsModal.vue -->
<template>
  <Teleport to="body">
    <div v-if="show" class="fixed inset-0 z-[100] overflow-y-auto">
      <!-- Backdrop overlay -->
      <div
          class="fixed inset-0 bg-black bg-opacity-50 transition-opacity"
          @click="$emit('close')"
      ></div>

      <!-- Modal container -->
      <div class="flex min-h-full items-center justify-center p-4">
        <div
            class="relative w-full max-w-xl transform overflow-hidden rounded-xl bg-white p-6 shadow-xl transition-all"
            @click.stop
        >
          <!-- Header -->
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-lg font-bold">재료 선택</h2>
            <button
                type="button"
                @click="$emit('close')"
                class="text-gray-400 hover:text-gray-500"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <!-- Search input -->
          <div class="mb-4">
            <div class="relative">
              <input
                  type="text"
                  v-model="searchKeyword"
                  placeholder="재료명 검색..."
                  class="w-full px-4 py-2 pr-10 border rounded-lg"
              />
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400 absolute right-3 top-2.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>

          <!-- Loading state -->
          <div v-if="isLoading" class="p-4 text-center">
            <div class="flex justify-center">
              <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
            </div>
            <p class="mt-2 text-gray-600">재료 목록을 불러오는 중...</p>
          </div>

          <!-- Empty state -->
          <div v-else-if="filteredIngredients.length === 0" class="p-4 text-center text-gray-500">
            일치하는 재료가 없습니다.
          </div>

          <!-- Ingredients list -->
          <div v-else class="h-72 overflow-y-auto border rounded-lg">
            <table class="w-full">
              <thead class="bg-gray-50 sticky top-0">
              <tr>
                <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">재료명</th>
                <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">기본 단위</th>
                <th class="px-4 py-3 text-right text-sm font-medium text-gray-500">단가</th>
                <th class="px-4 py-3 text-center text-sm font-medium text-gray-500">수량</th>
                <th class="px-4 py-3 text-center text-sm font-medium text-gray-500">추가</th>
              </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
              <tr v-for="ingredient in filteredIngredients" :key="ingredient.id">
                <td class="px-4 py-3">{{ ingredient.name }}</td>
                <td class="px-4 py-3">{{ ingredient.unit }}</td>
                <td class="px-4 py-3 text-right">
                  {{ formatPrice(ingredient.unitPrice) }}
                </td>
                <td class="px-4 py-3">
                  <div class="flex gap-2 items-center">
                    <input
                        type="number"
                        v-model="ingredient.amount"
                        min="0.1"
                        step="0.1"
                        class="w-16 px-2 py-1 border rounded"
                        placeholder="수량"
                    />
                    <select
                        v-model="ingredient.selectedUnit"
                        class="w-16 px-2 py-1 border rounded"
                    >
                      <option :value="ingredient.unit">{{ ingredient.unit }}</option>
                      <option value="g">g</option>
                      <option value="ml">ml</option>
                      <option value="개">개</option>
                      <option value="컵">컵</option>
                    </select>
                  </div>
                </td>
                <td class="px-4 py-3 text-center">
                  <button
                      @click="addIngredient(ingredient)"
                      :disabled="!ingredient.amount"
                      class="px-3 py-1 text-sm bg-blue-100 text-blue-600 rounded-full hover:bg-blue-200 disabled:opacity-50"
                  >
                    추가
                  </button>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import axios from '@/plugins/axios'
import { useToast } from 'vue-toastification'

const props = defineProps({
  show: Boolean
})

const emit = defineEmits(['close', 'select'])
const toast = useToast()

const searchKeyword = ref('')
const isLoading = ref(false)
const ingredients = ref([])

// 필터링된 재료 목록
const filteredIngredients = computed(() => {
  const keyword = searchKeyword.value.toLowerCase().trim()
  return ingredients.value.filter(ing =>
      ing.name.toLowerCase().includes(keyword)
  )
})

// 재료 목록 초기화
const prepareIngredients = (items) => {
  return items.map(item => ({
    ...item,
    amount: 1,
    selectedUnit: item.unit
  }))
}

// 재료 목록 불러오기
const fetchIngredients = async () => {
  try {
    isLoading.value = true
    const response = await axios.get('/products/raw-materials')
    ingredients.value = prepareIngredients(response.data.data || [])
  } catch (error) {
    console.error('Failed to fetch ingredients:', error)
    toast.error('재료 목록을 불러오는데 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

// 가격 포맷팅
const formatPrice = (price) => {
  return price ? `₩${Number(price).toLocaleString()}` : '-'
}

// 재료 추가
const addIngredient = (ingredient) => {
  if (!ingredient.amount) {
    toast.error('수량을 입력해주세요.')
    return
  }

  emit('select', {
    id: ingredient.id,
    name: ingredient.name,
    amount: ingredient.amount,
    unit: ingredient.selectedUnit
  })

  // 추가 후 수량 초기화
  ingredient.amount = 1
  toast.success(`${ingredient.name}이(가) 추가되었습니다.`)
}

// 모달 오픈 시 재료 목록 로드
watch(() => props.show, (show) => {
  if (show) {
    fetchIngredients()
  }
})

// 초기 로드
onMounted(() => {
  if (props.show) {
    fetchIngredients()
  }
})
</script>