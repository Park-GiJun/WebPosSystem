<template>
  <div :class="['p-6 rounded-xl shadow-sm transition-all duration-200 hover:shadow-md', bgColor]">
    <div class="flex items-center gap-3 mb-4">
      <component :is="icon" class="w-6 h-6" :class="iconColor" />
      <h3 class="font-semibold text-gray-800">{{ title }}</h3>
    </div>
    <slot></slot>
  </div>
</template>

<script setup>
defineProps({
  icon: {
    type: String,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  bgColor: {
    type: String,
    default: 'bg-red-50'
  },
  iconColor: {
    type: String,
    default: 'text-red-500'
  }
});
</script>