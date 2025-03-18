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
              <DialogTitle class="text-lg font-medium mb-4">
                {{ isEditing ? '상품 수정' : '상품 등록' }}
              </DialogTitle>

              <form @submit.prevent="handleSubmit" class="space-y-4">
                <!-- 상품 코드 (신규 등록 시만 표시) -->
                <div v-if="!isEditing">
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

                <!-- 상품 유형 (신규 등록 시만 표시) -->
                <div v-if="!isEditing">
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    상품 유형
                    <span class="text-red-500">*</span>
                  </label>
                  <select
                      v-model="form.productType"
                      required
                      class="w-full px-4 py-2 border rounded-lg"
                      :class="{'border-red-500': errors.productType}"
                  >
                    <option value="">유형 선택</option>
                    <option v-for="type in productTypes" :key="type.value" :value="type.value">
                      {{ type.label }}
                    </option>
                  </select>
                  <p v-if="errors.productType" class="mt-1 text-sm text-red-500">
                    {{ errors.productType }}
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
                    <option v-for="category in categories" :key="category.id" :value="category.id">
                      {{ category.name }}
                    </option>
                  </select>
                  <p v-if="errors.categoryId" class="mt-1 text-sm text-red-500">
                    {{ errors.categoryId }}
                  </p>
                </div>

                <!-- 상품 설명 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    상품 설명
                  </label>
                  <textarea
                      v-model="form.description"
                      rows="3"
                      class="w-full px-4 py-2 border rounded-lg"
                      :class="{'border-red-500': errors.description}"
                  ></textarea>
                  <p v-if="errors.description" class="mt-1 text-sm text-red-500">
                    {{ errors.description }}
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
                    <p v-if="errors.stock" class="mt-1 text-sm text-red-500">
                      {{ errors.stock }}
                    </p>
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

                <!-- 단위 -->
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
                  <p v-if="errors.unit" class="mt-1 text-sm text-red-500">
                    {{ errors.unit }}
                  </p>
                </div>

                <!-- 상태 (수정 시에만 표시) -->
                <div v-if="isEditing">
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    상태
                  </label>
                  <select
                      v-model="form.status"
                      class="w-full px-4 py-2 border rounded-lg"
                  >
                    <option value="ON_SALE">판매중</option>
                    <option value="OUT_OF_STOCK">품절</option>
                    <option value="DISCONTINUED">판매중지</option>
                  </select>
                </div>

                <!-- 바코드 (옵션) -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    바코드
                  </label>
                  <input
                      type="text"
                      v-model="form.barcode"
                      maxlength="30"
                      class="w-full px-4 py-2 border rounded-lg"
                  />
                </div>

                <!-- 이미지 URL (옵션) -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    이미지 URL
                  </label>
                  <input
                      type="text"
                      v-model="form.imageUrl"
                      class="w-full px-4 py-2 border rounded-lg"
                  />
                </div>

                <!-- 과세 여부 (신규 등록 시에만 표시) -->
                <div v-if="!isEditing">
                  <label class="flex items-center">
                    <input
                        type="checkbox"
                        v-model="form.isTaxable"
                        class="rounded text-blue-600"
                    />
                    <span class="ml-2 text-sm text-gray-700">과세 상품</span>
                  </label>
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
import axios from '@/plugins/axios';
import { useToast } from 'vue-toastification';

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
});

const emit = defineEmits(['update:show', 'refresh']);
const toast = useToast();

const isEditing = computed(() => !!props.editingProduct);
const isSubmitting = ref(false);
const errors = ref({});

// 상품 유형 옵션
const productTypes = [
  { value: 'PRODUCT', label: '제품' },
  { value: 'RAW_MATERIAL', label: '원재료' },
  { value: 'GOODS', label: '상품' },
  { value: 'SET_PRODUCT', label: '세트상품' }
];

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
];

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
  unit: 'EA',
  productType: 'PRODUCT',
  status: 'ON_SALE',
  barcode: '',
  imageUrl: '',
  isTaxable: true
});

// 폼 초기화
watch(() => props.editingProduct, (product) => {
  if (product) {
    form.value = {
      name: product.name,
      description: product.description || '',
      categoryId: product.category?.id || '',
      price: Number(product.price),
      costPrice: Number(product.costPrice),
      minStock: Number(product.minStock),
      maxStock: product.maxStock ? Number(product.maxStock) : null,
      unit: product.unit || 'EA',
      status: product.status || 'ON_SALE',
      barcode: product.barcode || '',
      imageUrl: product.imageUrl || ''
    };
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
      unit: 'EA',
      productType: 'PRODUCT',
      status: 'ON_SALE',
      barcode: '',
      imageUrl: '',
      isTaxable: true
    };
  }
}, { immediate: true });

// 유효성 검사
const validateForm = () => {
  errors.value = {};

  if (!isEditing.value && !form.value.code) {
    errors.value.code = '상품코드는 필수입니다';
  }

  if (!form.value.name) {
    errors.value.name = '상품명은 필수입니다';
  }

  if (!form.value.categoryId) {
    errors.value.categoryId = '카테고리는 필수입니다';
  }

  if (!isEditing.value && !form.value.productType) {
    errors.value.productType = '상품 유형은 필수입니다';
  }

  if (form.value.price < 0) {
    errors.value.price = '판매가는 0 이상이어야 합니다';
  }

  if (form.value.costPrice < 0) {
    errors.value.costPrice = '원가는 0 이상이어야 합니다';
  }

  if (!isEditing.value && form.value.stock < 0) {
    errors.value.stock = '재고는 0 이상이어야 합니다';
  }

  if (form.value.minStock < 0) {
    errors.value.minStock = '최소재고는 0 이상이어야 합니다';
  }

  if (form.value.maxStock && form.value.maxStock < form.value.minStock) {
    errors.value.maxStock = '최대재고는 최소재고보다 커야 합니다';
  }

  if (!form.value.unit) {
    errors.value.unit = '단위는 필수입니다';
  }

  return Object.keys(errors.value).length === 0;
};

// 폼 제출
const handleSubmit = async () => {
  try {
    if (!validateForm()) return;

    isSubmitting.value = true;

    if (isEditing.value) {
      // 기존 상품 수정
      const data = {
        name: form.value.name,
        description: form.value.description,
        categoryId: Number(form.value.categoryId),
        price: Number(form.value.price),
        costPrice: Number(form.value.costPrice),
        minStock: Number(form.value.minStock),
        maxStock: form.value.maxStock ? Number(form.value.maxStock) : null,
        unit: form.value.unit,
        status: form.value.status,
        barcode: form.value.barcode,
        imageUrl: form.value.imageUrl
      };

      await axios.put(`/products/${props.editingProduct.id}`, { data });
      toast.success('상품이 수정되었습니다');
    } else {
      // 신규 상품 등록
      const data = {
        code: form.value.code,
        name: form.value.name,
        description: form.value.description,
        categoryId: Number(form.value.categoryId),
        price: Number(form.value.price),
        costPrice: Number(form.value.costPrice),
        stock: Number(form.value.stock),
        minStock: Number(form.value.minStock),
        maxStock: form.value.maxStock ? Number(form.value.maxStock) : null,
        unit: form.value.unit,
        productType: form.value.productType,
        isTaxable: form.value.isTaxable,
        barcode: form.value.barcode,
        imageUrl: form.value.imageUrl
      };

      await axios.post('/products', { data });
      toast.success('상품이 등록되었습니다');
    }

    emit('refresh');
    onClose();
  } catch (error) {
    console.error('Failed to save product:', error);

    if (error.response?.data?.errors) {
      errors.value = error.response.data.errors;
    } else {
      toast.error('상품 저장에 실패했습니다');
    }
  } finally {
    isSubmitting.value = false;
  }
};

const onClose = () => {
  emit('update:show', false);
  errors.value = {};
};
</script>