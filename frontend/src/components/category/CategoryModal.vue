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
            <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-xl bg-white p-6 shadow-xl">
              <DialogTitle class="text-lg font-bold mb-4">
                {{ isEditing ? '카테고리 수정' : '카테고리 등록' }}
              </DialogTitle>

              <form @submit.prevent="handleSubmit" class="space-y-4">
                <!-- 신규 등록시에만 카테고리 코드 입력 가능 -->
                <div v-if="!isEditing">
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    카테고리 코드 <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="text"
                      v-model="form.code"
                      required
                      maxlength="20"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      :class="{'border-red-500': errors.code}"
                  />
                  <p v-if="errors.code" class="mt-1 text-sm text-red-500">
                    {{ errors.code }}
                  </p>
                  <p class="mt-1 text-xs text-gray-500">
                    * 코드는 영문, 숫자만 입력 가능하며 최대 20자까지 입력할 수 있습니다.
                  </p>
                </div>

                <!-- 카테고리 이름 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    카테고리명 <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="text"
                      v-model="form.name"
                      required
                      maxlength="50"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      :class="{'border-red-500': errors.name}"
                  />
                  <p v-if="errors.name" class="mt-1 text-sm text-red-500">
                    {{ errors.name }}
                  </p>
                </div>

                <!-- 상위 카테고리 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    상위 카테고리
                  </label>
                  <select
                      v-model="form.parentId"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  >
                    <option :value="null">없음 (최상위 카테고리)</option>
                    <option
                        v-for="category in availableParentCategories"
                        :key="category.id"
                        :value="category.id"
                    >
                      {{ category.name }}
                    </option>
                  </select>
                  <p v-if="errors.parentId" class="mt-1 text-sm text-red-500">
                    {{ errors.parentId }}
                  </p>
                </div>

                <!-- 정렬 순서 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    정렬 순서 <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="number"
                      v-model.number="form.orderNum"
                      required
                      min="1"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      :class="{'border-red-500': errors.orderNum}"
                  />
                  <p v-if="errors.orderNum" class="mt-1 text-sm text-red-500">
                    {{ errors.orderNum }}
                  </p>
                  <p class="mt-1 text-xs text-gray-500">
                    * 숫자가 작을수록 먼저 표시됩니다.
                  </p>
                </div>

                <!-- 설명 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    설명
                  </label>
                  <textarea
                      v-model="form.description"
                      rows="3"
                      maxlength="500"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  ></textarea>
                  <p class="mt-1 text-xs text-gray-500">
                    최대 500자까지 입력할 수 있습니다.
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
import { ref, computed, watch } from 'vue';
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue';

const props = defineProps({
  show: {
    type: Boolean,
    required: true
  },
  category: {
    type: Object,
    default: null
  },
  categories: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:show', 'submit']);

// 상태 관리
const form = ref({
  code: '',
  name: '',
  parentId: null,
  orderNum: 1,
  description: ''
});

const errors = ref({});
const isSubmitting = ref(false);

// 계산된 속성
const isEditing = computed(() => !!props.category);

const availableParentCategories = computed(() => {
  // 현재 카테고리를 제외한 가능한 상위 카테고리 목록
  if (!props.category) return props.categories;

  return props.categories.filter(category =>
      category.id !== props.category.id
  );
});

// 카테고리 데이터가 변경될 때 폼 초기화
watch(() => props.category, (newCategory) => {
  if (newCategory) {
    form.value = {
      code: newCategory.code || '',
      name: newCategory.name || '',
      parentId: newCategory.parentId || null,
      orderNum: newCategory.orderNum || 1,
      description: newCategory.description || ''
    };
  } else {
    form.value = {
      code: '',
      name: '',
      parentId: null,
      orderNum: 1,
      description: ''
    };
  }
}, { immediate: true });

// 폼 유효성 검사
const validateForm = () => {
  errors.value = {};

  // 카테고리 코드 유효성 검사
  if (!isEditing.value && !form.value.code) {
    errors.value.code = '카테고리 코드는 필수입니다.';
  } else if (!isEditing.value && !/^[A-Za-z0-9_-]+$/.test(form.value.code)) {
    errors.value.code = '카테고리 코드는 영문, 숫자, 대시(-), 언더스코어(_)만 사용할 수 있습니다.';
  }

  // 카테고리명 유효성 검사
  if (!form.value.name) {
    errors.value.name = '카테고리명은 필수입니다.';
  }

  // 정렬 순서 유효성 검사
  if (!form.value.orderNum || form.value.orderNum < 1) {
    errors.value.orderNum = '정렬 순서는 1 이상이어야 합니다.';
  }

  // 순환 참조 방지 (자기 자신을 부모로 설정하는 경우)
  if (props.category && form.value.parentId === props.category.id) {
    errors.value.parentId = '자기 자신을 상위 카테고리로 설정할 수 없습니다.';
  }

  return Object.keys(errors.value).length === 0;
};

// 폼 제출 처리
const handleSubmit = async () => {
  if (!validateForm()) return;

  try {
    isSubmitting.value = true;

    // 제출할 데이터 구성
    const submitData = {
      name: form.value.name,
      orderNum: form.value.orderNum,
      description: form.value.description || null,
    };

    // 상위 카테고리가 있는 경우에만 추가
    if (form.value.parentId) {
      submitData.parentId = form.value.parentId;
    }

    // 신규 등록 시 코드 추가
    if (!isEditing.value) {
      submitData.code = form.value.code;
    }

    // 부모 컴포넌트에 제출 이벤트 전달
    emit('submit', submitData);
  } catch (error) {
    console.error('Form submission error:', error);
  } finally {
    isSubmitting.value = false;
  }
};

// 모달 닫기
const onClose = () => {
  emit('update:show', false);
  errors.value = {};
};
</script>