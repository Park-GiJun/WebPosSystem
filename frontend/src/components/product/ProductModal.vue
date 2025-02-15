<!-- components/product/ProductModal.vue -->
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
                {{ editingProduct ? '상품 수정' : '상품 등록' }}
              </DialogTitle>

              <form @submit.prevent="handleSubmit" class="space-y-4">
                <!-- 신규 등록시에만 표시되는 필드들 -->
                <template v-if="!editingProduct">
                  <!-- 상품코드 -->
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      상품코드
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

                  <!-- 초기 재고 -->
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
                    <p v-if="errors.stock" class="mt-1 text-sm text-red-500">
                      {{ errors.stock }}
                    </p>
                  </div>

                  <!-- 과세 여부 -->
                  <div>
                    <label class="flex items-center">
                      <input
                          type="checkbox"
                          v-model="form.isTaxable"
                          class="rounded text-blue-600"
                      />
                      <span class="ml-2 text-sm text-gray-700">과세 상품</span>
                    </label>
                  </div>
                </template>

                <!-- 공통 필드 -->
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

                <!-- 상품명 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    상품명
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

                <!-- 가격 정보 -->
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      판매가
                      <span class="text-red-500">*</span>
                    </label>
                    <input
                        type="number"
                        v-model.number="form.price"
                        required
                        min="0"
                        class="w-full px-4 py-2 border rounded-lg"
                        :class="{'border-red-500': errors.price}"
                    />
                    <p v-if="errors.price" class="mt-1 text-sm text-red-500">
                      {{ errors.price }}
                    </p>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      원가
                      <span class="text-red-500">*</span>
                    </label>
                    <input
                        type="number"
                        v-model.number="form.costPrice"
                        required
                        min="0"
                        class="w-full px-4 py-2 border rounded-lg"
                        :class="{'border-red-500': errors.costPrice}"
                    />
                    <p v-if="errors.costPrice" class="mt-1 text-sm text-red-500">
                      {{ errors.costPrice }}
                    </p>
                  </div>
                </div>

                <!-- 재고 관리 -->
                <div class="grid grid-cols-2 gap-4">
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
                    <p v-if="errors.minStock" class="mt-1 text-sm text-red-500">
                      {{ errors.minStock }}
                    </p>
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
                    <p v-if="errors.maxStock" class="mt-1 text-sm text-red-500">
                      {{ errors.maxStock }}
                    </p>
                  </div>
                </div>

                <!-- 상품 설명 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    상품 설명
                  </label>
                  <textarea
                      v-model="form.description"
                      rows="3"
                      maxlength="500"
                      class="w-full px-4 py-2 border rounded-lg"
                      :class="{'border-red-500': errors.description}"
                  ></textarea>
                  <p v-if="errors.description" class="mt-1 text-sm text-red-500">
                    {{ errors.description }}
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
import { ref, computed, watch } from 'vue'
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
  editingProduct: {
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
  description: '',
  categoryId: '',
  price: 0,
  costPrice: 0,
  stock: 0,
  minStock: 0,
  maxStock: null,
  isTaxable: true,
  status: 'ON_SALE'
})

const errors = ref({})
const isSubmitting = ref(false)

// 유효성 검사
const validateForm = () => {
  errors.value = {}

  if (!form.value.code && !props.editingProduct) {
    errors.value.code = '상품코드는 필수입니다'
  }

  if (!form.value.name) {
    errors.value.name = '상품명은 필수입니다'
  }

  if (!form.value.categoryId) {
    errors.value.categoryId = '카테고리는 필수입니다'
  }

  if (form.value.price < 0) {
    errors.value.price = '가격은 0 이상이어야 합니다'
  }

  if (form.value.costPrice < 0) {
    errors.value.costPrice = '원가는 0 이상이어야 합니다'
  }

  if (!props.editingProduct && form.value.stock < 0) {
    errors.value.stock = '재고는 0 이상이어야 합니다'
  }

  if (form.value.minStock < 0) {
    errors.value.minStock = '최소재고는 0 이상이어야 합니다'
  }

  if (form.value.maxStock && form.value.maxStock < form.value.minStock) {
    errors.value.maxStock = '최대재고는 최소재고보다 커야 합니다'
  }

  return Object.keys(errors.value).length === 0
}

// 폼 초기화
watch(() => props.editingProduct, (product) => {
  if (product) {
    form.value = {
      name: product.name,
      description: product.description || '',
      categoryId: product.category.id,
      price: Number(product.price),
      costPrice: Number(product.costPrice),
      minStock: Number(product.minStock),
      maxStock: product.maxStock ? Number(product.maxStock) : null,
      status: product.status
    }
  } else {
    form.value = {
      code: '',
      name: '',
      description: '',
      categoryId: '',
      price: 0,
      costPrice: 0,
      stock: 0,
      minStock: 0,
      maxStock: null,
      isTaxable: true,
      status: 'ON_SALE'
    }
  }
}, { immediate: true })

// 폼 제출
const handleSubmit = async () => {
  try {
    if (!validateForm()) return

    isSubmitting.value = true

    const data = props.editingProduct
        ? {
          name: form.value.name,
          description: form.value.description || '',
          categoryId: Number(form.value.categoryId),
          price: Number(form.value.price),
          costPrice: Number(form.value.costPrice),
          minStock: Number(form.value.minStock),
          maxStock: form.value.maxStock ? Number(form.value.maxStock) : null,
          status: form.value.status
        }
        : {
          code: form.value.code,
          name: form.value.name,
          description: form.value.description || null,
          categoryId: Number(form.value.categoryId),
          price: Number(form.value.price),
          costPrice: Number(form.value.costPrice),
          stock: Number(form.value.stock),
          minStock: Number(form.value.minStock),
          maxStock: form.value.maxStock ? Number(form.value.maxStock) : null,
          isTaxable: Boolean(form.value.isTaxable),
          status: form.value.status
        }

    if (props.editingProduct) {
      await axios.put(`/products/${props.editingProduct.id}`, { data })
      toast.success('상품이 수정되었습니다')
    } else {
      await axios.post('/products', { data })
      toast.success('상품이 등록되었습니다')
    }

    emit('refresh')
    onClose()
  } catch (error) {
    if (error.response?.data?.errors) {
      errors.value = error.response.data.errors
    } else {
      toast.error('상품 저장에 실패했습니다')
    }
  } finally {
    isSubmitting.value = false
  }
}

const onClose = () => {
  emit('update:show', false)
  errors.value = {}
  form.value = {
    code: '',
    name: '',
    description: '',
    categoryId: '',
    price: 0,
    costPrice: 0,
    stock: 0,
    minStock: 0,
    maxStock: null,
    isTaxable: true,
    status: 'ON_SALE'
  }
}
</script>