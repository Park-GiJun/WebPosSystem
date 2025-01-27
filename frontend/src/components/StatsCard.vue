<!-- StatsCard.vue -->
<template>
  <div class="bg-white p-4 rounded-lg shadow">
    <h4 class="text-sm text-gray-500">{{ title }}</h4>
    <p class="mt-2 text-2xl font-bold">{{ value }}</p>
    <div class="mt-2 flex items-center text-sm">
     <span :class="growthClass">
       {{ growth > 0 ? '+' : '' }}{{ growth }}%
     </span>
      <span class="ml-2 text-gray-500">전월 대비</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: String,
    required: true
  },
  growth: {
    type: Number,
    required: true
  }
})

const growthClass = computed(() => {
  return props.growth > 0 ? 'text-green-500' : 'text-red-500'
})
</script>