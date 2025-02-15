// components/base/BaseSelect.vue
<template>
  <div>
    <label v-if="label" class="block text-sm font-medium text-gray-700 mb-1">
      {{ label }}
      <span v-if="required" class="text-red-500">*</span>
    </label>
    <select
        :value="modelValue"
        @change="$emit('update:modelValue', $event.target.value)"
        :class="[
        'w-full px-4 py-2 border rounded-lg',
        error ? 'border-red-500' : 'border-gray-300',
        'focus:ring-2 focus:ring-blue-500 outline-none'
      ]"
        v-bind="$attrs"
    >
      <option v-if="placeholder" value="">{{ placeholder }}</option>
      <option
          v-for="option in options"
          :key="option.value"
          :value="option.value"
      >
        {{ option.label }}
      </option>
    </select>
    <p v-if="error" class="mt-1 text-sm text-red-500">{{ error }}</p>
  </div>
</template>

<script setup>
defineProps({
  label: String,
  modelValue: [String, Number],
  placeholder: String,
  options: {
    type: Array,
    default: () => []
  },
  required: Boolean,
  error: String
})

defineEmits(['update:modelValue'])
</script>