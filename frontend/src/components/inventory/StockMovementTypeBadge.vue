// components/inventory/StockMovementTypeBadge.vue
<template>
  <span :class="[
    'px-2 py-1 rounded-full text-xs font-medium',
    typeClasses[type]
  ]">
    {{ typeLabels[type] }}
  </span>
</template>

<script setup>
const props = defineProps({
  type: {
    type: String,
    required: true,
    validator: (value) => ['IN', 'OUT'].includes(value)
  }
})

const typeClasses = {
  IN: 'bg-blue-100 text-blue-800',
  OUT: 'bg-red-100 text-red-800'
}

const typeLabels = {
  IN: '입고',
  OUT: '출고'
}
</script>