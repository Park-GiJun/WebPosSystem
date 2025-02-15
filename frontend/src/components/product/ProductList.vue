<template>
  <div class="overflow-x-auto">
    <table class="w-full">
      <thead class="bg-gray-50">
      <tr>
        <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품코드</th>
        <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">카테고리</th>
        <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품명</th>
        <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">판매가</th>
        <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">재고</th>
        <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">상태</th>
        <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">관리</th>
      </tr>
      </thead>
      <tbody class="divide-y divide-gray-200">
      <tr v-for="product in products" :key="product.id">
        <td class="px-6 py-4">{{ product.code }}</td>
        <td class="px-6 py-4">{{ product.category.name }}</td>
        <td class="px-6 py-4">{{ product.name }}</td>
        <td class="px-6 py-4 text-right">₩{{ product.price.toLocaleString() }}</td>
        <td class="px-6 py-4 text-right">{{ product.stock }}</td>
        <td class="px-6 py-4">
        </td>
        <td class="px-6 py-4">
          <div class="flex justify-center gap-2">
            <BaseButton
                variant="secondary"
                @click="$emit('edit', product)"
            >
              수정
            </BaseButton>
            <BaseButton
                variant="danger"
                @click="$emit('delete', product.id)"
            >
              삭제
            </BaseButton>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import BaseButton from '../base/BaseButton.vue'

defineProps({
  products: {
    type: Array,
    required: true
  }
})

defineEmits(['edit', 'delete'])
</script>
