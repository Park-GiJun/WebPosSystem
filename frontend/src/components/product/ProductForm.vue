// components/product/ProductForm.vue
<template>
  <form @submit.prevent="handleSubmit" class="space-y-4">
    <div class="grid grid-cols-2 gap-4">
      <BaseSelect
          v-model="form.categoryId"
          :options="categoryOptions"
          label="카테고리"
          required
          :error="errors.categoryId"
      />

      <BaseInput
          v-model="form.code"
          label="상품코드"
          required
          :error="errors.code"
      />
    </div>

    <BaseInput
        v-model="form.name"
        label="상품명"
        required
        :error="errors.name"
    />

    <BaseTextarea
        v-model="form.description"
        label="상품 설명"
        rows="3"
        :error="errors.description"
    />

    <div class="grid grid-cols-2 gap-4">
      <BaseInput
          v-model="form.price"
          type="number"
          label="판매가"
          required
          :error="errors.price"
      />

      <BaseInput
          v-model="form.costPrice"
          type="number"
          label="원가"
          required
          :error="errors.costPrice"
      />
    </div>

    <div class="grid grid-cols-3 gap-4">
      <BaseInput
          v-model="form.stock"
          type="number"
          label="현재 재고"
          required
          :error="errors.stock"
      />

      <BaseInput
          v-model="form.minStock"
          type="number"
          label="최소 재고"
          required
          :error="errors.minStock"
      />

      <BaseInput
          v-model="form.maxStock"
          type="number"
          label="최대 재고"
          :error="errors.maxStock"
      />
    </div>

    <div class="grid grid-cols-2 gap-4">
      <BaseSelect
          v-model="form.status"
          :options="statusOptions"
          label="상품상태"
          required
          :error="errors.status"
      />

      <BaseSelect
          v-model="form.isTaxable"
          :options="taxableOptions"
          label="과세여부"
          required
          :error="errors.isTaxable"
      />
    </div>

    <div class="grid grid-cols-2 gap-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          판매 시작일
        </label>
        <input
            type="datetime-local"
            v-model="form.saleStartDate"
            class="w-full px-4 py-2 border rounded-lg border-gray-300"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          판매 종료일
        </label>
        <input
            type="datetime-local"
            v-model="form.saleEndDate"
            class="w-full px-4 py-2 border rounded-lg border-gray-300"
        />
      </div>
    </div>

    <div class="flex justify-end gap-3 mt-6">
      <BaseButton
          type="button"
          variant="secondary"
          @click="$emit('cancel')"
      >
        취소
      </BaseButton>
      <BaseButton
          type="submit"
          :disabled="isSubmitting"
      >
        {{ isSubmitting ? '저장 중...' : '저장' }}
      </BaseButton>
    </div>
  </form>
</template>

<script setup>
import { ref, computed } from 'vue'
import BaseInput from '../base/BaseInput.vue'
import BaseSelect from '../base/BaseSelect.vue'
import BaseTextarea from '../base/BaseTextarea.vue'
import BaseButton from '../base/BaseButton.vue'

const props = defineProps({
  product: Object,
  categories: {
    type: Array,
    required: true
  },
  isSubmitting: Boolean,
  errors: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['submit', 'cancel'])

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
  status: 'ON_SALE',
  isTaxable: true,
  taxRate: 0.1,
  saleStartDate: null,
  saleEndDate: null
})

// Initialize form with product data if editing
if (props.product) {
  form.value = {
    code: props.product.code,
    name: props.product.name,
    description: props.product.description,
    categoryId: props.product.category.id,
    price: props.product.price,
    costPrice: props.product.costPrice,
    stock: props.product.stock,
    minStock: props.product.minStock,
    maxStock: props.product.maxStock,
    status: props.product.status,
    isTaxable: props.product.isTaxable,
    taxRate: props.product.taxRate,
    saleStartDate: props.product.saleStartDate,
    saleEndDate: props.product.saleEndDate
  }
}

const categoryOptions = computed(() =>
    props.categories.map(cat => ({
      value: cat.id,
      label: cat.name
    }))
)

const statusOptions = [
  { value: 'ON_SALE', label: '판매중' },
  { value: 'OUT_OF_STOCK', label: '품절' },
  { value: 'DISCONTINUED', label: '판매중지' },
  { value: 'UPCOMING', label: '판매예정' }
]

const taxableOptions = [
  { value: true, label: '과세' },
  { value: false, label: '비과세' }
]

const handleSubmit = () => {
  emit('submit', form.value)
}
</script>