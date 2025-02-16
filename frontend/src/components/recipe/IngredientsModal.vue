<!-- components/ingredients/IngredientsModal.vue -->
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
                {{ editingIngredient ? '재료 수정' : '재료 등록' }}
              </DialogTitle>

              <form @submit.prevent="handleSubmit" class="space-y-4">
                <!-- 재료 코드 (신규 등록시에만) -->
                <div v-if="!editingIngredient">
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    재료 코드
                    <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="text"
                      v-model="form.code"
                      required
                      maxlength="20"
                      class="w-full px-4 py-2 border rounded-lg"
                      :class="{'border-red-500': errors.code}"
                  />
                  <p v-if="errors.code" class="mt-1 text-sm text-red-500">
                    {{ errors.code }}
                  </p>
                </div>

                <!-- 재료명 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    재료명
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
                  <p v-if="errors.categoryId" class="mt-1 text-sm text-red-500">
                    {{ errors.categoryId }}
                  </p>
                </div>

                <!-- 단위 정보 -->
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      기본 단위
                      <span class="text-red-500">*</span>
                    </label>
                    <select
                        v-model="form.unit"
                        required
                        class="w-full px-4 py-2 border rounded-lg"
                        :class="{'border-red-500': errors.unit}"
                    >
                      <option value="">단위 선택</option>
                      <option value="g">그램(g)</option>
                      <option value="ml">밀리리터(ml)</option>
                      <option value="ea">개(ea)</option>
                      <option value="pack">팩(pack)</option>
                    </select>
                    <p v-if="errors.unit" class="mt-1 text-sm text-red-500">
                      {{ errors.unit }}
                    </p>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      포장 단위
                    </label>
                    <div class="flex gap-2">
                      <input
                          type="number"
                          v-model.number="form.packageSize"
                          min="0"
                          class="flex-1 px-4 py-2 border rounded-lg"
                          :class="{'border-red-500': errors.packageSize}"
                      />
                      <span class="py-2">{{ form.unit }}</span>
                    </div>
                    <p v-if="errors.packageSize" class="mt-1 text-sm text-red-500">
                      {{ errors.packageSize }}
                    </p>
                  </div>
                </div>

                <!-- 가격 정보 -->
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      단가 (1{{ form.unit }}당)
                      <span class="text-red-500">*</span>
                    </label>
                    <div class="flex gap-2 items-center">
                      <span>₩</span>
                      <input
                          type="number"
                          v-model.number="form.unitPrice"
                          required
                          min="0"
                          step="0.1"
                          class="flex-1 px-4 py-2 border rounded-lg"
                          :class="{'border-red-500': errors.unitPrice}"
                      />
                    </div>
                    <p v-if="errors.unitPrice" class="mt-1 text-sm text-red-500">
                      {{ errors.unitPrice }}
                    </p>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      포장 가격
                    </label>
                    <div class="flex gap-2 items-center">
                      <span>₩</span>
                      <input
                          type="number"
                          v-model.number="form.packagePrice"
                          min="0"
                          class="flex-1 px-4 py-2 border rounded-lg"
                          :class="{'border-red-500': errors.packagePrice}"
                      />
                    </div>
                    <p v-if="errors.packagePrice" class="mt-1 text-sm text-red-500">
                      {{ errors.packagePrice }}
                    </p>
                  </div>
                </div>

                <!-- 재고 관리 -->
                <div class="grid grid-cols-3 gap-4">
                  <div v-if="!editingIngredient">
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      초기 재고
                      <span class="text-red-500">*</span>
                    </label>
                    <div class="flex gap-2">
                      <input
                          type="number"
                          v-model.number="form.stock"
                          required
                          min="0"
                          class="flex-1 px-4 py-2 border rounded-lg"
                          :class="{'border-red-500': errors.stock}"
                      />
                      <span class="py-2">{{ form.unit }}</span>
                    </div>
                    <p v-if="errors.stock" class="mt-1 text-sm text-red-500">
                      {{ errors.stock }}
                    </p>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      최소 재고
                      <span class="text-red-500">*</span>
                    </label>
                    <div class="flex gap-2">
                      <input
                          type="number"
                          v-model.number="form.minStock"
                          required
                          min="0"
                          class="flex-1 px-4 py-2 border rounded-lg"
                          :class="{'border-red-500': errors.minStock}"
                      />
                      <span class="py-2">{{ form.unit }}</span>
                    </div>
                    <p v-if="errors.minStock" class="mt-1 text-sm text-red-500">
                      {{ errors.minStock }}
                    </p>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      최대 재고
                    </label>
                    <div class="flex gap-2">
                      <input
                          type="number"
                          v-model.number="form.maxStock"
                          min="0"
                          class="flex-1 px-4 py-2 border rounded-lg"
                          :class="{'border-red-500': errors.maxStock}"
                      />
                      <span class="py-2">{{ form.unit }}</span>
                    </div>
                    <p v-if="errors.maxStock" class="mt-1 text-sm text-red-500">
                      {{ errors.maxStock }}
                    </p>
                  </div>
                </div>

                <!-- 보관 정보 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    보관 방법
                  </label>
                  <textarea
                      v-model="form.storageInstructions"
                      rows="2"
                      maxlength="200"
                      class="w-full px-4 py-2 border rounded-lg"
                      :class="{'border-red-500': errors.storageInstructions}"
                      placeholder="온도, 습도 등 보관 시 주의사항"
                  ></textarea>
                  <p v-if="errors.storageInstructions" class="mt-1 text-sm text-red-500">
                    {{ errors.storageInstructions }}
                  </p>
                </div>

                <!-- 유통기한 -->
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      유통기한 (일)
                    </label>
                    <input
                        type="number"
                        v-model.number="form.shelfLife"
                        min="0"
                        class="w-full px-4 py-2 border rounded-lg"
                        :class="{'border-red-500': errors.shelfLife}"
                    />
                    <p v-if="errors.shelfLife" class="mt-1 text-sm text-red-500">
                      {{ errors.shelfLife }}
                    </p>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      알레르기 유발
                    </label>
                    <select
                        v-model="form.allergen"
                        class="w-full px-4 py-2 border rounded-lg"
                    >
                      <option value="">해당 없음</option>
                      <option value="milk">우유</option>
                      <option value="egg">계란</option>
                      <option value="nuts">견과류</option>
                      <option value="wheat">밀</option>
                      <option value="soy">대두</option>
                    </select>
                  </div>
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
  categories: {
    type: Array,
    default: () => []
  },
  editingIngredient: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:show', 'refresh'])
const toast = useToast()

// 폼 상태
const form = ref({
  code: '',
  name: '',
  categoryId: '',
  unit: '',
  packageSize: 0,
  unitPrice: 0,
  packagePrice: 0,
  stock: 0,
  minStock: 0,
  maxStock: null,
  storageInstructions: '',
  shelfLife: 0,
  allergen: ''
})

const errors = ref({})
const isSubmitting = ref(false)

// 유효성 검사
const validateForm = () => {
  errors.value = {}

  if (!form.value.code && !props.editingIngredient) {
    errors.value.code = '재료 코드를 입력하세요.'
  }
  if (!form.value.name) {
    errors.value.name = '재료명을 입력하세요.'
  }
  if (!form.value.categoryId) {
    errors.value.categoryId = '카테고리를 선택하세요.'
  }
  if (!form.value.unit) {
    errors.value.unit = '기본 단위를 선택하세요.'
  }
  if (form.value.unitPrice <= 0) {
    errors.value.unitPrice = '단가는 0보다 커야 합니다.'
  }
  if (form.value.minStock < 0) {
    errors.value.minStock = '최소 재고는 0 이상이어야 합니다.'
  }
  if (form.value.maxStock !== null && form.value.maxStock < form.value.minStock) {
    errors.value.maxStock = '최대 재고는 최소 재고보다 커야 합니다.'
  }

  return Object.keys(errors.value).length === 0
}

// 폼 제출 처리
const handleSubmit = async () => {
  if (!validateForm()) return

  isSubmitting.value = true
  try {
    if (props.editingIngredient) {
      await axios.put(`/ingredients/${props.editingIngredient.id}`, form.value)
      toast.success('재료 정보가 수정되었습니다.')
    } else {
      await axios.post('/ingredients', form.value)
      toast.success('새로운 재료가 등록되었습니다.')
    }
    emit('refresh')
    onClose()
  } catch (error) {
    toast.error('저장 중 오류가 발생했습니다.')
  } finally {
    isSubmitting.value = false
  }
}

// 모달 닫기
const onClose = () => {
  emit('update:show', false)
}

// 편집 모드에서 데이터 설정
watch(
    () => props.editingIngredient,
    (newIngredient) => {
      if (newIngredient) {
        form.value = { ...newIngredient }
      } else {
        form.value = {
          code: '',
          name: '',
          categoryId: '',
          unit: '',
          packageSize: 0,
          unitPrice: 0,
          packagePrice: 0,
          stock: 0,
          minStock: 0,
          maxStock: null,
          storageInstructions: '',
          shelfLife: 0,
          allergen: ''
        }
      }
    },
    { immediate: true }
)
</script>
