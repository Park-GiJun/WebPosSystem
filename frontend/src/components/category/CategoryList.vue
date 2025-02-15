// components/category/CategoryList.vue
<template>
  <div class="space-y-4">
    <template v-for="category in categories" :key="category.id">
      <CategoryListItem
          :category="category"
          @edit="$emit('edit', category)"
          @delete="$emit('delete', category.id)"
      />
    </template>
  </div>
</template>

<script setup>
defineProps({
  categories: {
    type: Array,
    required: true
  }
})

defineEmits(['edit', 'delete'])
</script>