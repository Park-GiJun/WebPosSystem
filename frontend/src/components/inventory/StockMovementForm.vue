// components/inventory/StockMovementForm.vue
<template>
  <form @submit.prevent="handleSubmit" class="space-y-4">
    <!-- 상품 선택 -->
    <BaseSelect
        v-model="form.productId"
        :options="productOptions"
        label="상품"
        required
        placeholder="상품 선택"
        :error="errors.productId"
    />

    <!-- 수량 -->
    <BaseInput
        v-model="form.quantity"
        type="number"
        label="수량"
        required
        :error="errors.quantity"
    />

    <!-- 담당자 -->
    <BaseInput
        v-model="form.handler"
        label="담당자"
        required
        :error="errors.handler"
    />

    <!-- 비고 -->
    <BaseTextarea
        v-model="form.note"
        label="비고"
        :error="errors.note"
        rows="3"
    />

    <!-- 버튼 -->
    <div class="flex justify-end gap-2">
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
        저장
      </BaseButton>
    </div>
  </form>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import BaseInput from '../base/BaseInput.vue'
import BaseSelect from '../base/BaseSelect.vue'
import BaseTextarea from '../base/BaseTextarea.vue'
import BaseButton from '../base/BaseButton.vue'
import axios from '@/plugins/axios'
import { useToast } from 'vue-toastification'

const props = defineProps({
  type: {
    type: String,
    required: true,
    validator: (value) => ['IN', 'OUT'].includes(value)
  },
  isSubmitting: Boolean
})

const emit = defineEmits(['submit', 'cancel'])
const toast = useToast()

const form = ref({
  productId: '',
  quantity: 0,
  handler: '',
  note: ''
})

const errors = ref({})
const productOptions = ref([])

// 상품 목록 가져오기
const fetchProducts = async () => {
  try {
    const response = await axios.get('/api/products')
    productOptions.value = response.data.data.map(product => ({
      value: product.id,
      label: `${product.code} - ${product.name}`
    }))
  } catch (error) {
    toast.error('상품 목록을 불러오는데 실패했습니다.')
  }
}

const handleSubmit = () => {
  errors.value = {}
  emit('submit', { ...form.value })
}

// 초기 데이터 로드
onMounted(() => {
  fetchProducts()
})
</script>