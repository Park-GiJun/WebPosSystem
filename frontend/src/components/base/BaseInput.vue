// components/base/BaseInput.vue
<template>
  <div>
    <label v-if="label" class="block text-sm font-medium text-gray-700 mb-1">
      {{ label }}
      <span v-if="required" class="text-red-500">*</span>
    </label>
    <input
        :type="type"
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        :class="[
        'w-full px-4 py-2 border rounded-lg',
        error ? 'border-red-500' : 'border-gray-300',
        'focus:ring-2 focus:ring-blue-500 outline-none'
      ]"
        v-bind="$attrs"
    />
    <p v-if="error" class="mt-1 text-sm text-red-500">{{ error }}</p>
  </div>
</template>

<script setup>
defineProps({
  label: String,
  modelValue: [String, Number],
  type: {
    type: String,
    default: 'text'
  },
  required: Boolean,
  error: String
})

defineEmits(['update:modelValue'])
</script>