// components/base/BaseButton.vue
<template>
  <button
      :type="type"
      :class="[
      'px-4 py-2 rounded-lg transition-colors',
      'disabled:opacity-50 disabled:cursor-not-allowed',
      variantClasses[variant]
    ]"
      v-bind="$attrs"
  >
    <slot />
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: {
    type: String,
    default: 'button'
  },
  variant: {
    type: String,
    default: 'primary'
  }
})

const variantClasses = computed(() => ({
  primary: 'bg-blue-600 text-white hover:bg-blue-700',
  secondary: 'bg-gray-100 text-gray-700 hover:bg-gray-200',
  danger: 'bg-red-100 text-red-600 hover:bg-red-200'
}))
</script>