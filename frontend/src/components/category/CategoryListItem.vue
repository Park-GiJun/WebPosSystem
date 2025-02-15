// components/category/CategoryListItem.vue
<template>
  <div class="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
    <div class="flex items-center">
      <span :style="{ marginLeft: `${category.level * 20}px` }">
        {{ category.name }}
      </span>
    </div>
    <div class="flex gap-2">
      <BaseButton
          variant="secondary"
          @click="$emit('edit', category)"
      >
        수정
      </BaseButton>
      <BaseButton
          variant="danger"
          @click="$emit('delete', category.id)"
      >
        삭제
      </BaseButton>
    </div>
  </div>
</template>

<script setup>
import BaseButton from '../base/BaseButton.vue'

defineProps({
  category: {
    type: Object,
    required: true
  }
})

defineEmits(['edit', 'delete'])
</script>