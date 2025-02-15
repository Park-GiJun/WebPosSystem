<!-- Inventory History Type Badge Component -->
// components/inventory/MovementTypeBadge.vue
<template>
  <span :class="[
    'px-2 py-1 rounded-full text-xs',
    typeClasses[type]
  ]">
    {{ typeLabels[type] }}
  </span>
</template>

<script setup>
const props = defineProps({
  type: {
    type: String,
    required: true
  }
})

const typeClasses = {
  IN: 'bg-green-100 text-green-800',
  OUT: 'bg-blue-100 text-blue-800',
  ADJUST: 'bg-yellow-100 text-yellow-800'
}

const typeLabels = {
  IN: '입고',
  OUT: '출고',
  ADJUST: '조정'
}
</script>
