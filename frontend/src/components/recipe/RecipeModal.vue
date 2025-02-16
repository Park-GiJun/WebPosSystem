<!-- components/recipe/RecipeModal.vue -->
<template>
  <TransitionRoot appear :show="show" as="template">
    <Dialog as="div" @close="onClose" class="relative z-50">
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
            <DialogPanel class="w-full max-w-xl transform overflow-hidden rounded-2xl bg-white p-6 shadow-xl">
              <DialogTitle class="text-lg font-medium leading-6 text-gray-900 mb-4">
                {{ editingRecipe ? '레시피 수정' : '레시피 등록' }}
              </DialogTitle>

              <form @submit.prevent="handleSubmit" class="space-y-4">
                <!-- 상품 선택 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    상품
                    <span class="text-red-500">*</span>
                  </label>
                  <select
                      v-model="form.productId"
                      required
                      class="w-full px-4 py-2 border rounded-lg"
                      :class="{'border-red-500': errors.productId}"
                      :disabled="editingRecipe"
                  >
                    <option value="">상품 선택</option>
                    <option
                        v-for="product in products"
                        :key="product.id"
                        :value="product.id"
                    >
                      {{ product.name }}
                    </option>
                  </select>
                  <p v-if="errors.productId" class="mt-1 text-sm text-red-500">
                    {{ errors.productId }}
                  </p>
                </div>

                <!-- 레시피 이름 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    레시피 이름
                    <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="text"
                      v-model="form.name"
                      required
                      maxlength="100"
                      class="w-full px-4 py-2 border rounded-lg"
                      :class="{'border-red-500': errors.name}"
                  />
                  <p v-if="errors.name" class="mt-1 text-sm text-red-500">
                    {{ errors.name }}
                  </p>
                </div>

                <!-- 소요 시간 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    소요 시간 (분)
                    <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="number"
                      v-model.number="form.preparationTime"
                      required
                      min="1"
                      class="w-full px-4 py-2 border rounded-lg"
                      :class="{'border-red-500': errors.preparationTime}"
                  />
                  <p v-if="errors.preparationTime" class="mt-1 text-sm text-red-500">
                    {{ errors.preparationTime }}
                  </p>
                </div>

                <!-- 필요 도구 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    필요 도구
                  </label>
                  <div class="space-y-2">
                    <div v-for="(tool, index) in form.tools" :key="index" class="flex gap-2">
                      <input
                          type="text"
                          v-model="form.tools[index]"
                          class="flex-1 px-4 py-2 border rounded-lg"
                          placeholder="도구 이름"
                      />
                      <button
                          type="button"
                          @click="removeTool(index)"
                          class="px-3 py-2 text-red-600 hover:bg-red-50 rounded-lg"
                      >
                        삭제
                      </button>
                    </div>
                    <button
                        type="button"
                        @click="addTool"
                        class="w-full px-4 py-2 text-sm border border-dashed rounded-lg hover:bg-gray-50"
                    >
                      도구 추가
                    </button>
                  </div>
                </div>

                <!-- 레시피 단계 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    레시피 단계
                    <span class="text-red-500">*</span>
                  </label>
                  <div class="space-y-2">
                    <div
                        v-for="(step, index) in form.steps"
                        :key="index"
                        class="flex gap-2"
                    >
                      <div class="flex-none w-12 h-12 flex items-center justify-center bg-gray-100 rounded-lg">
                        {{ index + 1 }}
                      </div>
                      <div class="flex-1">
                        <textarea
                            v-model="form.steps[index].description"
                            rows="2"
                            class="w-full px-4 py-2 border rounded-lg"
                            :class="{'border-red-500': errors[`steps.${index}`]}"
                            placeholder="단계 설명"
                        ></textarea>
                        <p v-if="errors[`steps.${index}`]" class="mt-1 text-sm text-red-500">
                          {{ errors[`steps.${index}`] }}
                        </p>
                      </div>
                      <button
                          type="button"
                          @click="removeStep(index)"
                          class="px-3 py-2 text-red-600 hover:bg-red-50 rounded-lg"
                      >
                        삭제
                      </button>
                    </div>
                    <button
                        type="button"
                        @click="addStep"
                        class="w-full px-4 py-2 text-sm border border-dashed rounded-lg hover:bg-gray-50"
                    >
                      단계 추가
                    </button>
                  </div>
                </div>

                <!-- 주의사항 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    주의사항
                  </label>
                  <textarea
                      v-model="form.notes"
                      rows="3"
                      maxlength="500"
                      class="w-full px-4 py-2 border rounded-lg"
                      :class="{'border-red-500': errors.notes}"
                  ></textarea>
                  <p v-if="errors.notes" class="mt-1 text-sm text-red-500">
                    {{ errors.notes }}
                  </p>
                </div>

                <!-- 버튼 영역 -->
                <div class="flex justify-end gap-3 mt-6">
                  <button
                      type="button"
                      @click="onClose"
                      class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200"
                  >
                    취소
                  </button>
                  <button
                      type="submit"
                      :disabled="isSubmitting"
                      class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:opacity-50"
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
import { ref, watch } from 'vue'
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue'
import axios from '@/plugins/axios'
import { useToast } from 'vue-toastification'

const props = defineProps({
  show: Boolean,
  products: {
    type: Array,
    default: () => []
  },
  editingRecipe: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:show', 'refresh'])
const toast = useToast()

// 폼 상태
const form = ref({
  productId: '',
  name: '',
  preparationTime: 0,
  tools: [],
  steps: [],
  notes: ''
})

const errors = ref({})
const isSubmitting = ref(false)

// 도구 추가/제거
const addTool = () => {
  form.value.tools.push('')
}

const removeTool = (index) => {
  form.value.tools.splice(index, 1)
}

// 단계 추가/제거
const addStep = () => {
  form.value.steps.push({ description: '' })
}

const removeStep = (index) => {
  form.value.steps.splice(index, 1)
}

// 유효성 검사
const validateForm = () => {
  errors.value = {}

  if (!form.value.productId) {
    errors.value.productId = '상품을 선택해주세요'
  }

  if (!form.value.name) {
    errors.value.name = '레시피 이름은 필수입니다'
  }

  if (form.value.preparationTime < 1) {
    errors.value.preparationTime = '소요 시간은 1분 이상이어야 합니다'
  }

  form.value.steps.forEach((step, index) => {
    if (!step.description) {
      errors.value[`steps.${index}`] = '단계 설명은 필수입니다'
    }
  })

  return Object.keys(errors.value).length === 0
}

// 폼 초기화
watch(() => props.editingRecipe, (recipe) => {
  if (recipe) {
    form.value = {
      productId: recipe.productId,
      name: recipe.name,
      preparationTime: recipe.preparationTime,
      tools: [...recipe.tools],
      steps: [...recipe.steps],
      notes: recipe.notes || ''
    }
  } else {
    form.value = {
      productId: '',
      name: '',
      preparationTime: 0,
      tools: [],
      steps: [],
      notes: ''
    }
  }
}, { immediate: true })

// 폼 제출
const handleSubmit = async () => {
  try {
    if (!validateForm()) return

    isSubmitting.value = true

    if (props.editingRecipe) {
      await axios.put(`/api/recipes/${props.editingRecipe.id}`, {
        data: form.value
      })
      toast.success('레시피가 수정되었습니다')
    } else {
      await axios.post('/api/recipes', {
        data: form.value
      })
      toast.success('레시피가 등록되었습니다')
    }

    emit('refresh')
    onClose()
  } catch (error) {
    if (error.response?.data?.errors) {
      errors.value = error.response.data.errors
    } else {
      toast.error('레시피 저장에 실패했습니다')
    }
  } finally {
    isSubmitting.value = false
  }
}

const onClose = () => {
  emit('update:show', false)
  errors.value = {}
  form.value = {
    productId: '',
    name: '',
    preparationTime: 0,
    tools: [],
    steps: [],
    notes: ''
  }
}
</script>