<!-- components/category/CategoryModal.vue -->
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
            <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 shadow-xl">
              <DialogTitle class="text-lg font-medium leading-6 text-gray-900 mb-4">
                {{ editingCategory ? '카테고리 수정' : '카테고리 추가' }}
              </DialogTitle>

              <form @submit.prevent="handleSubmit" class="space-y-4">
                <!-- 카테고리 코드 -->
                <div v-if="!editingCategory">
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    카테고리 코드
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

                <!-- 상위 카테고리 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    상위 카테고리
                  </label>
                  <select
                      v-model="form.parentId"
                      class="w-full px-4 py-2 border rounded-lg"
                  >
                    <option value="">없음</option>
                    <option
                        v-for="category in availableCategories"
                        :key="category.id"
                        :value="category.id"
                    >
                      {{ category.name }}
                    </option>
                  </select>
                </div>

                <!-- 카테고리명 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    카테고리명
                    <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="text"
                      v-model="form.name"
                      required
                      class="w-full px-4 py-2 border rounded-lg"
                  />
                </div>

                <!-- 정렬 순서 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    정렬 순서
                  </label>
                  <input
                      type="number"
                      v-model="form.orderNum"
                      class="w-full px-4 py-2 border rounded-lg"
                  />
                </div>

                <!-- 설명 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    설명
                  </label>
                  <textarea
                      v-model="form.description"
                      rows="3"
                      class="w-full px-4 py-2 border rounded-lg"
                  ></textarea>
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
  editingCategory: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:show', 'refresh'])
const toast = useToast()

const form = ref({
  code: '',       // 추가: 카테고리 코드
  name: '',
  parentId: '',
  orderNum: 0,
  description: ''
})

const isSubmitting = ref(false)

// 사용 가능한 상위 카테고리 목록 (현재 카테고리와 그 하위 카테고리들 제외)
const availableCategories = computed(() => {
  if (!props.editingCategory) return props.categories
  return props.categories.filter(cat =>
      cat.id !== props.editingCategory.id
  )
})

// 편집 모드일 때 폼 초기화
watch(() => props.editingCategory, (category) => {
  if (category) {
    form.value = {
      parentId: category.parentId || '',
      name: category.name,
      orderNum: category.orderNum,
      description: category.description
    }
  }
}, { immediate: true })

const errors = ref({})

const validateForm = () => {
  errors.value = {}

  if (!form.value.code && !props.editingCategory) {
    errors.value.code = '카테고리 코드는 필수입니다'
  }

  if (!form.value.name) {
    errors.value.name = '카테고리명은 필수입니다'
  }

  if (form.value.orderNum === undefined || form.value.orderNum === null) {
    errors.value.orderNum = '정렬순서는 필수입니다'
  }

  return Object.keys(errors.value).length === 0
}

const handleSubmit = async () => {
  try {
    if (!validateForm()) return

    isSubmitting.value = true

    const data = props.editingCategory
        ? {
          name: form.value.name,
          parentId: form.value.parentId || null,
          orderNum: form.value.orderNum,
          description: form.value.description
        }
        : {
          code: form.value.code,
          name: form.value.name,
          parentId: form.value.parentId || null,
          orderNum: form.value.orderNum,
          description: form.value.description
        }

    if (props.editingCategory) {
      await axios.put(`/categories/${props.editingCategory.id}`, { data })
      toast.success('카테고리가 수정되었습니다')
    } else {
      await axios.post('/categories', { data })
      toast.success('카테고리가 추가되었습니다')
    }

    emit('refresh')
    onClose()
  } catch (error) {
    if (error.response?.data?.errors) {
      errors.value = error.response.data.errors
    } else {
      toast.error('카테고리 저장에 실패했습니다')
    }
  } finally {
    isSubmitting.value = false
  }
}

const onClose = () => {
  emit('update:show', false)
  form.value = {
    parentId: '',
    name: '',
    orderNum: 0,
    description: ''
  }
}
</script>