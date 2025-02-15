// components/product/ProductFilter.vue
<template>
  <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
    <BaseSelect
        v-model="filters.categoryId"
        :options="categoryOptions"
        label="카테고리"
        placeholder="전체"
    />

    <BaseSelect
        v-model="filters.status"
        :options="statusOptions"
        label="상품상태"
        placeholder="전체"
    />

    <BaseInput
        v-model="filters.keyword"
        label="검색어"
        placeholder="상품명 또는 코드"
    />

    <div class="flex items-end">
      <BaseButton
          class="w-full"
          @click="$emit('search', filters)"
      >
        검색
      </BaseButton>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import BaseInput from '../base/BaseInput.vue'
import BaseSelect from '../base/BaseSelect.vue'
import BaseButton from '../base/BaseButton.vue'

const props = defineProps({
  categories: {
    type: Array,
    required: true
  }
})

const filters = ref({
  categoryId: '',
  status: '',
  keyword: ''
})

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

defineEmits(['search'])
</script>