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
                <!-- 기본 정보 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    레시피명
                    <span class="text-red-500">*</span>
                  </label>
                  <input
                      v-model="form.name"
                      type="text"
                      required
                      class="w-full px-4 py-2 border rounded-lg"
                  />
                </div>

                <!-- 카테고리 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    카테고리
                    <span class="text-red-500">*</span>
                  </label>
                  <select
                      v-model="form.categoryId"
                      required
                      class="w-full px-4 py-2 border rounded-lg"
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
                </div>

                <!-- 재료 섹션 -->
                <div>
                  <div class="flex justify-between items-center mb-2">
                    <label class="block text-sm font-medium text-gray-700">
                      재료
                      <span class="text-red-500">*</span>
                    </label>
                    <button
                        type="button"
                        @click="openIngredientsModal"
                        class="px-3 py-1 bg-blue-100 text-blue-600 rounded hover:bg-blue-200"
                    >
                      재료 추가
                    </button>
                  </div>

                  <div
                      v-if="form.ingredients.length === 0"
                      class="p-4 bg-gray-50 text-center text-gray-500 rounded"
                  >
                    추가된 재료가 없습니다.
                  </div>

                  <div v-else class="space-y-2">
                    <div
                        v-for="(ingredient, index) in form.ingredients"
                        :key="ingredient.id"
                        class="flex items-center bg-gray-50 p-3 rounded"
                    >
                      <div class="flex-1">
                        <div class="font-medium">{{ ingredient.name }}</div>
                        <div class="text-sm text-gray-600 flex items-center gap-2">
                          <input
                              type="number"
                              v-model.number="ingredient.amount"
                              min="0.1"
                              step="0.1"
                              class="w-24 px-2 py-1 border rounded"
                          />
                          <select
                              v-model="ingredient.unit"
                              class="w-24 px-2 py-1 border rounded"
                          >
                            <option value="g">g</option>
                            <option value="ml">ml</option>
                            <option value="개">개</option>
                            <option value="컵">컵</option>
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

                <!-- 가격 및 조리 시간 -->
                <div class="grid grid-cols-2 gap-4">
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
                    />
                  </div>
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
                    />
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
                      rows="4"
                      required
                      class="w-full px-4 py-2 border rounded-lg"
                  ></textarea>
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
                          value="ACTIVE"
                          class="mr-2"
                      />
                      <span>활성</span>
                    </label>
                    <label class="flex items-center">
                      <input
                          type="radio"
                          v-model="form.status"
                          value="INACTIVE"
                          class="mr-2"
                      />
                      <span>비활성</span>
                    </label>
                  </div>
                </div>

                <!-- 버튼 영역 -->
                <div class="flex justify-end gap-3 mt-6">
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

  <!-- 재료 선택 모달 -->
  <IngredientsModal
      :show="isIngredientsModalOpen"
      @close="closeIngredientsModal"
      @select="openIngredientQuantityModal"
  />

  <!-- 재료 수량 입력 모달 -->
  <IngredientQuantityModal
      v-if="selectedIngredient"
      :show="isIngredientQuantityModalOpen"
      :ingredient="selectedIngredient"
      @close="closeIngredientQuantityModal"
      @add="addIngredient"
  />
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle
} from '@headlessui/vue'
import { XIcon } from 'lucide-vue-next'
import { useToast } from 'vue-toastification'
import axios from '@/plugins/axios'

import IngredientsModal from './IngredientsModal.vue'
import IngredientQuantityModal from './IngredientQuantityModal.vue'

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

// 모달 상태를 완전히 독립적으로 관리
const isIngredientsModalOpen = ref(false)
const isIngredientQuantityModalOpen = ref(false)
const selectedIngredient = ref(null)
const isSubmitting = ref(false)

// 레시피 폼 상태
const form = ref({
  name: '',
  categoryId: '',
  ingredients: [],
  price: 0,
  cookingTime: 0,
  instructions: '',
  status: 'ACTIVE'
})

// 레시피 편집 여부 확인
const isEditing = computed(() => !!props.recipe)

// 레시피 데이터 초기화
watch(() => props.recipe, (recipe) => {
  if (recipe) {
    form.value = {
      name: recipe.name,
      categoryId: recipe.categoryId,
      ingredients: [...recipe.ingredients],
      price: recipe.price,
      cookingTime: recipe.cookingTime,
      instructions: recipe.instructions,
      status: recipe.status || 'ACTIVE'
    }
  } else {
    form.value = {
      name: '',
      categoryId: '',
      ingredients: [],
      price: 0,
      cookingTime: 0,
      instructions: '',
      status: 'ACTIVE'
    }
  }
}, { immediate: true })

// 재료 선택 관련 메서드
const openIngredientsModal = () => {
  // 1차 모달만 열기
  isIngredientsModalOpen.value = true
  // 2차 모달 상태는 유지
  isIngredientQuantityModalOpen.value = false
}

const closeIngredientsModal = () => {
  // 1차 모달만 닫기
  isIngredientsModalOpen.value = false
}

const openIngredientQuantityModal = (ingredient) => {
  selectedIngredient.value = ingredient
  // isIngredientQuantityModalOpen.value = true
}

const closeIngredientQuantityModal = () => {
  // 2차 모달만 닫기
  isIngredientQuantityModalOpen.value = false
  selectedIngredient.value = null
}

const addIngredient = (ingredientData) => {
  const existingIndex = form.value.ingredients.findIndex(
      item => item.id === ingredientData.id
  )

  if (existingIndex !== -1) {
    form.value.ingredients[existingIndex] = ingredientData
    toast.info(`${ingredientData.name} 수량이 업데이트되었습니다.`)
  } else {
    form.value.ingredients.push(ingredientData)
    toast.success(`${ingredientData.name}이(가) 추가되었습니다.`)
  }

  // 2차 모달만 닫기 - 1차 모달은 열린 상태 유지
  isIngredientQuantityModalOpen.value = false;
}

const removeIngredient = (index) => {
  const ingredientName = form.value.ingredients[index].name
  form.value.ingredients.splice(index, 1)
  toast.info(`${ingredientName}이(가) 제거되었습니다.`)
}

// 모달 닫기
const closeModal = () => {
  emit('close')
}

// 레시피 제출
const submitRecipe = async () => {
  try {
    isSubmitting.value = true

    const recipeData = {
      ...form.value,
      productType: 'RECIPE_PRODUCT',
      ingredients: form.value.ingredients.map(ing => ({
        ingredientId: ing.id,
        quantity: ing.amount,
        unit: ing.unit
      }))
    }

    if (isEditing.value) {
      await axios.put(`/products/${props.recipe.id}`, { data: recipeData })
      toast.success('레시피가 수정되었습니다.')
    } else {
      await axios.post('/products', { data: recipeData })
      toast.success('레시피가 등록되었습니다.')
    }

    emit('submit', recipeData)
    closeModal()
  } catch (error) {
    console.error('레시피 저장 오류:', error)
    toast.error('레시피 저장에 실패했습니다.')
  } finally {
    isSubmitting.value = false
  }
}
</script>