// components/base/BasePagination.vue
<template>
  <div class="flex justify-between items-center px-6 py-4">
    <div class="text-sm text-gray-500">
      총 {{ totalElements }}개
    </div>
    <div class="flex gap-2">
      <button
          v-if="!isFirstPage"
          @click="$emit('update:page', currentPage - 1)"
          class="px-3 py-1 rounded bg-gray-100"
      >
        이전
      </button>

      <button
          v-for="pageNum in displayedPages"
          :key="pageNum"
          @click="$emit('update:page', pageNum)"
          :class="[
          'px-3 py-1 rounded',
          currentPage === pageNum ? 'bg-blue-600 text-white' : 'bg-gray-100'
        ]"
      >
        {{ pageNum }}
      </button>

      <button
          v-if="!isLastPage"
          @click="$emit('update:page', currentPage + 1)"
          class="px-3 py-1 rounded bg-gray-100"
      >
        다음
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  page: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  totalElements: {
    type: Number,
    required: true
  },
  isFirstPage: Boolean,
  isLastPage: Boolean
})

// 표시할 페이지 번호들 계산 (현재 페이지 주변 5개)
const displayedPages = computed(() => {
  const current = props.page
  const total = props.totalPages
  const delta = 2 // 현재 페이지 양쪽으로 보여줄 페이지 수

  const range = []
  for (let i = Math.max(1, current - delta); i <= Math.min(total, current + delta); i++) {
    range.push(i)
  }

  return range
})

defineEmits(['update:page'])
</script>