<!-- components/recipe/RecipeModal.vue -->
<template>
  <TransitionRoot appear :show="show" as="template">
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
            <DialogPanel class="w-full max-w-2xl transform overflow-hidden rounded-2xl bg-white p-6 shadow-xl">
              <DialogTitle class="text-lg font-bold mb-4">
                {{ recipe ? '레시피 수정' : '레시피 등록' }}
              </DialogTitle>

              <form @submit.prevent="submitRecipe" class="space-y-4">
                <!-- 상단 정보 영역 (코드, 이름, 카테고리, 가격, 조리시간) -->
                <div class="grid grid-cols-12 gap-4">
                  <!-- 왼쪽 영역 -->
                  <div class="col-span-6">
                    <!-- 코드 (필수) -->
                    <div class="mb-4" v-if="!isEditing">
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        레시피 코드
                        <span class="text-red-500">*</span>
                      </label>
                      <input
                          v-model="form.code"
                          type="text"
                          required
                          placeholder="예: RECIPE-001"
                          class="w-full px-4 py-2 border rounded-lg"
                          :class="{'border-red-500': errors.code}"
                      />
                      <p v-if="errors.code" class="text-xs text-red-500 mt-1">{{ errors.code }}</p>
                      <p v-else class="text-xs text-gray-500 mt-1">고유한 레시피 코드를 입력하세요.</p>
                    </div>

                    <!-- 레시피명 -->
                    <div class="mb-4">
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        레시피명
                        <span class="text-red-500">*</span>
                      </label>
                      <input
                          v-model="form.name"
                          type="text"
                          required
                          class="w-full px-4 py-2 border rounded-lg"
                          :class="{'border-red-500': errors.name}"
                      />
                      <p v-if="errors.name" class="text-xs text-red-500 mt-1">{{ errors.name }}</p>
                    </div>

                    <!-- 카테고리 -->
                    <div class="mb-4">
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        카테고리
                        <span class="text-red-500">*</span>
                      </label>
                      <select
                          v-model="form.categoryId"
                          required
                          class="w-full px-4 py-2 border rounded-lg"
                          :class="{'border-red-500': errors.categoryId}"
                      >
                        <option value="">카테고리 선택</option>
                        <option
                            v-for="category in categories"
                            :key="category.id"
                            :value="category.id"
                        >
                          {{ category.name }}
                        </option>
                      </select>
                      <p v-if="errors.categoryId" class="text-xs text-red-500 mt-1">{{ errors.categoryId }}</p>
                    </div>
                  </div>

                  <!-- 오른쪽 영역 -->
                  <div class="col-span-6">
                    <!-- 가격 및 조리 시간 -->
                    <div class="grid grid-cols-2 gap-4 mb-4">
                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">
                          판매가
                          <span class="text-red-500">*</span>
                        </label>
                        <input
                            v-model.number="form.price"
                            type="number"
                            min="0"
                            required
                            class="w-full px-4 py-2 border rounded-lg"
                            :class="{'border-red-500': errors.price}"
                        />
                        <p v-if="errors.price" class="text-xs text-red-500 mt-1">{{ errors.price }}</p>
                      </div>
                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">
                          원가
                          <span class="text-red-500">*</span>
                        </label>
                        <input
                            v-model.number="form.costPrice"
                            type="number"
                            min="0"
                            required
                            class="w-full px-4 py-2 border rounded-lg"
                            :class="{'border-red-500': errors.costPrice}"
                        />
                        <p v-if="errors.costPrice" class="text-xs text-red-500 mt-1">{{ errors.costPrice }}</p>
                      </div>
                    </div>

                    <div class="grid grid-cols-2 gap-4 mb-4">
                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">
                          조리 시간 (분)
                          <span class="text-red-500">*</span>
                        </label>
                        <input
                            v-model.number="form.cookingTime"
                            type="number"
                            min="1"
                            required
                            class="w-full px-4 py-2 border rounded-lg"
                            :class="{'border-red-500': errors.cookingTime}"
                        />
                        <p v-if="errors.cookingTime" class="text-xs text-red-500 mt-1">{{ errors.cookingTime }}</p>
                      </div>
                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">
                          단위
                          <span class="text-red-500">*</span>
                        </label>
                        <select
                            v-model="form.unit"
                            required
                            class="w-full px-4 py-2 border rounded-lg"
                            :class="{'border-red-500': errors.unit}"
                        >
                          <option value="">단위 선택</option>
                          <option v-for="unit in unitOptions" :key="unit.value" :value="unit.value">
                            {{ unit.label }}
                          </option>
                        </select>
                        <p v-if="errors.unit" class="text-xs text-red-500 mt-1">{{ errors.unit }}</p>
                      </div>
                    </div>

                    <!-- 상태 -->
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        상태
                      </label>
                      <div class="flex gap-4">
                        <label class="flex items-center">
                          <input
                              type="radio"
                              v-model="form.status"
                              value="ON_SALE"
                              class="mr-2"
                          />
                          <span>판매중</span>
                        </label>
                        <label class="flex items-center">
                          <input
                              type="radio"
                              v-model="form.status"
                              value="OUT_OF_STOCK"
                              class="mr-2"
                          />
                          <span>품절</span>
                        </label>
                        <label class="flex items-center">
                          <input
                              type="radio"
                              v-model="form.status"
                              value="DISCONTINUED"
                              class="mr-2"
                          />
                          <span>판매중지</span>
                        </label>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 재료 섹션 (좌우 레이아웃) - 높이 조정 -->
                <div class="flex space-x-4" style="height: 300px;">
                  <!-- 왼쪽: 추가된 재료 목록 -->
                  <div class="w-1/2 flex flex-col">
                    <label class="flex justify-between items-center mb-2">
                      <span class="text-sm font-medium text-gray-700">
                        재료 목록
                        <span class="text-red-500">*</span>
                      </span>
                      <span v-if="errors.ingredients" class="text-xs text-red-500">{{ errors.ingredients }}</span>
                    </label>

                    <div class="flex-1 border rounded overflow-hidden"> <!-- 테두리와 오버플로우 컨테이너 -->
                      <div
                          v-if="form.ingredients.length === 0"
                          class="h-full bg-gray-50 text-center text-gray-500 flex items-center justify-center p-4"
                      >
                        <p>추가된 재료가 없습니다.<br>오른쪽에서 재료를 선택해주세요.</p>
                      </div>

                      <div v-else class="h-full overflow-y-auto p-2">
                        <div
                            v-for="(ingredient, index) in form.ingredients"
                            :key="ingredient.id"
                            class="flex items-center bg-gray-50 p-3 rounded mb-2"
                        >
                          <div class="flex-1">
                            <div class="font-medium">{{ ingredient.name }}</div>
                            <div class="text-sm text-gray-600 flex items-center gap-2">
                              <input
                                  type="number"
                                  v-model.number="ingredient.amount"
                                  min="0.1"
                                  step="0.1"
                                  class="w-20 px-2 py-1 border rounded"
                              />
                              <select
                                  v-model="ingredient.unit"
                                  class="w-20 px-2 py-1 border rounded"
                              >
                                <option v-for="unit in unitOptions" :key="unit.value" :value="unit.value">
                                  {{ unit.label }}
                                </option>
                              </select>
                            </div>
                          </div>
                          <button
                              type="button"
                              @click="removeIngredient(index)"
                              class="text-red-500 hover:text-red-700"
                          >
                            <XIcon class="w-5 h-5" />
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 오른쪽: 재료 검색 및 선택 -->
                  <div class="w-1/2 flex flex-col">
                    <div class="flex justify-between items-center mb-2">
                      <label class="block text-sm font-medium text-gray-700">재료 선택</label>

                      <!-- 검색 입력 -->
                      <div class="relative w-48">
                        <input
                            type="text"
                            v-model="searchKeyword"
                            placeholder="검색..."
                            class="w-full px-3 py-1 pr-8 border rounded-lg text-sm"
                        />
                        <SearchIcon class="w-4 h-4 text-gray-400 absolute right-2 top-1.5" />
                      </div>
                    </div>

                    <!-- 재료 목록 컨테이너 -->
                    <div class="flex-1 border rounded overflow-hidden">
                      <!-- 로딩 상태 -->
                      <div v-if="isLoading" class="h-full flex flex-col items-center justify-center p-4">
                        <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500 mb-2"></div>
                        <p class="text-gray-600">재료 목록을 불러오는 중...</p>
                      </div>

                      <!-- 검색 결과 없음 -->
                      <div v-else-if="filteredIngredients.length === 0" class="h-full flex items-center justify-center p-4 text-gray-500">
                        일치하는 재료가 없습니다.
                      </div>

                      <!-- 재료 목록 테이블 -->
                      <div v-else class="h-full overflow-y-auto bg-white">
                        <table class="w-full">
                          <thead class="bg-gray-50 sticky top-0">
                          <tr>
                            <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">재료명</th>
                            <th class="px-2 py-2 text-center text-xs font-medium text-gray-500">단위</th>
                            <th class="px-2 py-2 text-center text-xs font-medium text-gray-500">선택</th>
                          </tr>
                          </thead>
                          <tbody class="divide-y divide-gray-200">
                          <tr v-for="ingredient in filteredIngredients" :key="ingredient.id">
                            <td class="px-3 py-2 text-sm">{{ ingredient.name }}</td>
                            <td class="px-2 py-2 text-center text-xs">{{ ingredient.unit }}</td>
                            <td class="px-2 py-2 text-center">
                              <!-- 재료 선택 → 수량 입력 인라인 -->
                              <div v-if="selectedIngredientId === ingredient.id" class="flex items-center space-x-1">
                                <input
                                    type="number"
                                    v-model.number="ingredientAmount"
                                    min="0.1"
                                    step="0.1"
                                    class="w-12 px-1 py-1 border rounded text-xs"
                                    placeholder="수량"
                                />
                                <select v-model="ingredientUnit" class="w-12 px-1 py-1 border rounded text-xs">
                                  <option :value="ingredient.unit">{{ ingredient.unit }}</option>
                                  <option v-for="unit in unitOptions" :key="unit.value" :value="unit.value">
                                    {{ unit.label }}
                                  </option>
                                </select>
                                <button
                                    @click="addSelectedIngredient(ingredient)"
                                    class="px-1 py-1 text-xs bg-blue-600 text-white rounded hover:bg-blue-700"
                                >
                                  추가
                                </button>
                              </div>
                              <button
                                  v-else
                                  @click="selectIngredient(ingredient)"
                                  class="px-2 py-1 text-xs bg-blue-100 text-blue-600 rounded-full hover:bg-blue-200"
                              >
                                선택
                              </button>
                            </td>
                          </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 조리 방법 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    조리 방법
                    <span class="text-red-500">*</span>
                  </label>
                  <textarea
                      v-model="form.instructions"
                      rows="3"
                      required
                      class="w-full px-4 py-2 border rounded-lg"
                      :class="{'border-red-500': errors.instructions}"
                  ></textarea>
                  <p v-if="errors.instructions" class="text-xs text-red-500 mt-1">{{ errors.instructions }}</p>
                </div>

                <!-- 재고 관련 (신규 등록 시에만 표시) -->
                <div v-if="!isEditing" class="grid grid-cols-3 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      초기 재고
                      <span class="text-red-500">*</span>
                    </label>
                    <input
                        type="number"
                        v-model.number="form.stock"
                        required
                        min="0"
                        class="w-full px-4 py-2 border rounded-lg"
                        :class="{'border-red-500': errors.stock}"
                    />
                    <p v-if="errors.stock" class="text-xs text-red-500 mt-1">{{ errors.stock }}</p>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      최소 재고
                      <span class="text-red-500">*</span>
                    </label>
                    <input
                        type="number"
                        v-model.number="form.minStock"
                        required
                        min="0"
                        class="w-full px-4 py-2 border rounded-lg"
                        :class="{'border-red-500': errors.minStock}"
                    />
                    <p v-if="errors.minStock" class="text-xs text-red-500 mt-1">{{ errors.minStock }}</p>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      최대 재고
                    </label>
                    <input
                        type="number"
                        v-model.number="form.maxStock"
                        min="0"
                        class="w-full px-4 py-2 border rounded-lg"
                        :class="{'border-red-500': errors.maxStock}"
                    />
                    <p v-if="errors.maxStock" class="text-xs text-red-500 mt-1">{{ errors.maxStock }}</p>
                  </div>
                </div>

                <!-- 버튼 영역 -->
                <div class="flex justify-end gap-3 mt-4">
                  <button
                      type="button"
                      @click="closeModal"
                      class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
                  >
                    취소
                  </button>
                  <button
                      type="submit"
                      :disabled="isSubmitting"
                      class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
                  >
                    {{ isSubmitting ? '저장 중...' : '저장' }}
                  </button>
                </div>
              </form>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle
} from '@headlessui/vue'
import { XIcon, SearchIcon } from 'lucide-vue-next'
import { useToast } from 'vue-toastification'
import axios from '@/plugins/axios'

const props = defineProps({
  show: Boolean,
  recipe: {
    type: Object,
    default: null
  },
  categories: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['close', 'submit'])
const toast = useToast()

// 모달 상태 관리
const isSubmitting = ref(false)
const searchKeyword = ref('')
const isLoading = ref(false)
const ingredients = ref([])
const selectedIngredientId = ref(null)
const ingredientAmount = ref(1)
const ingredientUnit = ref('')
const errors = ref({})

// 단위 옵션
const unitOptions = [
  { value: 'EA', label: '개' },
  { value: 'KG', label: 'kg' },
  { value: 'G', label: 'g' },
  { value: 'L', label: 'L' },
  { value: 'ML', label: 'ml' },
  { value: 'BOX', label: '박스' },
  { value: 'SET', label: '세트' },
  { value: 'PKG', label: '팩' }
]

// 레시피 폼 상태
const form = ref({
  code: '',
  name: '',
  categoryId: '',
  ingredients: [],
  price: 0,
  costPrice: 0,
  stock: 0,
  minStock: 0,
  maxStock: null,
  cookingTime: 0,
  instructions: '',
  status: 'ON_SALE',
  unit: 'EA',
  isTaxable: true
})

// 필터링된 재료 목록
const filteredIngredients = computed(() => {
  const keyword = searchKeyword.value.toLowerCase().trim();
  return ingredients.value.filter(ing =>
      ing.name.toLowerCase().includes(keyword)
  );
})

// 레시피 편집 여부 확인
const isEditing = computed(() => !!props.recipe)

// 레시피 데이터 초기화
watch(() => props.recipe, (recipe) => {
  if (recipe) {
    form.value = {
      name: recipe.name,
      categoryId: recipe.category?.id,
      ingredients: recipe.recipeComponents ? recipe.recipeComponents.map(comp => ({
        id: comp.ingredient.id,
        name: comp.ingredient.name,
        amount: comp.quantity,
        unit: comp.unit
      })) : [],
      price: recipe.price || 0,
      costPrice: recipe.costPrice || 0,
      cookingTime: recipe.cookingTime || 0,
      instructions: recipe.description || '',
      status: recipe.status || 'ON_SALE',
      unit: recipe.unit || 'EA'
    }
  } else {
    form.value = {
      code: '',
      name: '',
      categoryId: '',
      ingredients: [],
      price: 0,
      costPrice: 0,
      stock: 0,
      minStock: 0,
      maxStock: null,
      cookingTime: 0,
      instructions: '',
      status: 'ON_SALE',
      unit: 'EA',
      isTaxable: true
    }
  }
  errors.value = {}
}, { immediate: true })

// 재료 목록 불러오기
const fetchIngredients = async () => {
  try {
    isLoading.value = true
    const response = await axios.get('/products/raw-materials')
    ingredients.value = response.data.data || []
  } catch (error) {
    console.error('Failed to fetch ingredients:', error)
    toast.error('재료 목록을 불러오는데 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

// 재료 선택 (수량 입력 UI 표시)
const selectIngredient = (ingredient) => {
  selectedIngredientId.value = ingredient.id
  ingredientUnit.value = ingredient.unit || 'EA'
  ingredientAmount.value = 1
}

// 선택한 재료 추가
const addSelectedIngredient = (ingredient) => {
  if (ingredientAmount.value <= 0) {
    toast.error('수량은 0보다 커야 합니다.')
    return
  }

  const existingIndex = form.value.ingredients.findIndex(
      item => item.id === ingredient.id
  )

  if (existingIndex !== -1) {
    form.value.ingredients[existingIndex].amount = ingredientAmount.value
    form.value.ingredients[existingIndex].unit = ingredientUnit.value
    toast.info(`${ingredient.name} 수량이 업데이트되었습니다.`)
  } else {
    form.value.ingredients.push({
      id: ingredient.id,
      name: ingredient.name,
      amount: ingredientAmount.value,
      unit: ingredientUnit.value
    })
    toast.success(`${ingredient.name}이(가) 추가되었습니다.`)
  }

  // 선택 상태 초기화
  selectedIngredientId.value = null
}

// 재료 제거
const removeIngredient = (index) => {
  const ingredientName = form.value.ingredients[index].name
  form.value.ingredients.splice(index, 1)
  toast.info(`${ingredientName}이(가) 제거되었습니다.`)
}

// 유효성 검사
const validateForm = () => {
  errors.value = {}

  if (!isEditing.value && !form.value.code) {
    errors.value.code = '레시피 코드는 필수입니다'
  }

  if (!form.value.name) {
    errors.value.name = '레시피명은 필수입니다'
  }

  if (!form.value.categoryId) {
    errors.value.categoryId = '카테고리는 필수입니다'
  }

  if (form.value.ingredients.length === 0) {
    errors.value.ingredients = '최소 하나 이상의 재료가 필요합니다'
  }

  if (form.value.price < 0) {
    errors.value.price = '판매가는 0 이상이어야 합니다'
  }

  if (form.value.costPrice < 0) {
    errors.value.costPrice = '원가는 0 이상이어야 합니다'
  }

  if (form.value.cookingTime <= 0) {
    errors.value.cookingTime = '조리 시간은 0보다 커야 합니다'
  }

  if (!form.value.instructions) {
    errors.value.instructions = '조리 방법은 필수입니다'
  }

  if (!form.value.unit) {
    errors.value.unit = '단위는 필수입니다'
  }

  if (!isEditing.value) {
    if (form.value.stock < 0) {
      errors.value.stock = '재고는 0 이상이어야 합니다'
    }

    if (form.value.minStock < 0) {
      errors.value.minStock = '최소 재고는 0 이상이어야 합니다'
    }

    if (form.value.maxStock && form.value.maxStock < form.value.minStock) {
      errors.value.maxStock = '최대 재고는 최소 재고보다 커야 합니다'
    }
  }

  return Object.keys(errors.value).length === 0
}

// 모달 닫기
const closeModal = () => {
  emit('close')
  selectedIngredientId.value = null
  errors.value = {}
}

// 레시피 제출
const submitRecipe = async () => {
  if (!validateForm()) {
    toast.error('입력 양식을 확인해주세요.')
    return
  }

  try {
    isSubmitting.value = true

    const recipeData = {
      code: form.value.code,
      name: form.value.name,
      categoryId: form.value.categoryId,
      price: form.value.price,
      costPrice: form.value.costPrice,
      description: form.value.instructions,
      productType: 'RECIPE_PRODUCT',
      unit: form.value.unit,
      status: form.value.status,
      isTaxable: form.value.isTaxable
    }

    // 신규 등록 시에만 재고 정보 추가
    if (!isEditing.value) {
      recipeData.stock = form.value.stock
      recipeData.minStock = form.value.minStock
      recipeData.maxStock = form.value.maxStock
    }

    // 레시피 요소 추가
    recipeData.recipeComponents = form.value.ingredients.map(ing => ({
      ingredientId: ing.id,
      quantity: ing.amount,
      unit: ing.unit
    }))

    if (isEditing.value) {
      await axios.put(`/products/${props.recipe.id}`, { data: recipeData })
      toast.success('레시피가 수정되었습니다.')
    } else {
      await axios.post('/products', { data: recipeData })
      toast.success('레시피가 등록되었습니다.')
    }

    emit('submit')
    closeModal()
  } catch (error) {
    console.error('레시피 저장 오류:', error)

    if (error.response?.data?.errors) {
      errors.value = error.response.data.errors
    } else {
      toast.error('레시피 저장에 실패했습니다.')
    }
  } finally {
    isSubmitting.value = false
  }
}

// 모달이 열릴 때 재료 목록 로드
watch(() => props.show, (show) => {
  if (show) {
    fetchIngredients()
    selectedIngredientId.value = null
    errors.value = {}
  }
})

// 초기 로드
onMounted(() => {
  if (props.show) {
    fetchIngredients()
  }
})
</script>