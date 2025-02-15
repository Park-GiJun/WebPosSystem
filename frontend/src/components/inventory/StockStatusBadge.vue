// components/inventory/StockStatusBadge.vue
<template>
  <span :class="[
    'px-2 py-1 rounded-full text-xs font-medium',
    statusClasses[status]
  ]">
    {{ statusLabels[status] }}
  </span>
</template>

<script setup>
const props = defineProps({
  status: {
    type: String,
    required: true,
    validator: (value) => [
      'NORMAL',     // 정상
      'LOW',        // 부족
      'OUT_OF_STOCK', // 품절
      'EXCESS',     // 과다
      'DISCONTINUED' // 판매중지
    ].includes(value)
  }
})

const statusClasses = {
  NORMAL: 'bg-green-100 text-green-800',      // 정상: 초록색
  LOW: 'bg-yellow-100 text-yellow-800',       // 부족: 노란색
  OUT_OF_STOCK: 'bg-red-100 text-red-800',    // 품절: 빨간색
  EXCESS: 'bg-blue-100 text-blue-800',        // 과다: 파란색
  DISCONTINUED: 'bg-gray-100 text-gray-800'    // 판매중지: 회색
}

const statusLabels = {
  NORMAL: '정상',
  LOW: '부족',
  OUT_OF_STOCK: '품절',
  EXCESS: '과다',
  DISCONTINUED: '판매중지'
}
</script>