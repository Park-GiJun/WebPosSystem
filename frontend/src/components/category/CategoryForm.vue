// components/category/CategoryForm.vue
<template>
  <form @submit.prevent="handleSubmit" class="space-y-4">
    <BaseSelect
        v-model="form.parentId"
        :options="parentOptions"
        label="상위 카테고리"
        placeholder="없음"
        :error="errors.parentId"
    />

    <BaseInput
        v-model="form.code"
        label="카테고리 코드"
        required
        :error="errors.code"
    />

    <BaseInput
        v-model="form.name"
        label="카테고리명"
        required
        :error="errors.name"
    />

    <BaseInput
        v-model="form.orderNum"
        type="number"
        label="순서"
        :error="errors.orderNum"
    />

    <BaseTextarea
        v-model="form.description"
        label="설명"
        rows="3"
        :error="errors.description"
    />

    <div class="flex justify-end gap-3 mt-6">
      <BaseButton
          type="button"
          variant="secondary"
          @click="$emit('cancel')"
      >
        취소
      </BaseButton>
      <BaseButton
          type="submit"
          :disabled="isSubmitting"
      >
        {{ isSubmitting ? '저장 중...' : '저장' }}
      </BaseButton>
    </div>
  </form>
</template>

<script setup>
import { ref, computed } from 'vue'
import BaseInput from '../base/BaseInput.vue'
import BaseSelect from '../base/BaseSelect.vue'
import BaseTextarea from '../base/BaseTextarea.vue'
import BaseButton from '../base/BaseButton.vue'

const props = defineProps({
  category: Object,
  categories: {
    type: Array,
    required: true
  },
  isSubmitting: Boolean,
  errors: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['submit', 'cancel'])

const form = ref({
  code: '',
  name: '',
  parentId: '',
  orderNum: 0,
  description: ''
})

// Initialize form with category data if editing
if (props.category) {
  form.value = {
    code: props.category.code,
    name: props.category.name,
    parentId: props.category.parent?.id || '',
    orderNum: props.category.orderNum,
    description: props.category.description
  }
}

const parentOptions = computed(() => {
  const availableCategories = props.category
      ? props.categories.filter(cat =>
          cat.id !== props.category.id &&
          !isDescendantOf(cat, props.category)
      )
      : props.categories

  return availableCategories.map(cat => ({
    value: cat.id,
    label: cat.name
  }))
})

const isDescendantOf = (category, ancestor) => {
  if (!category.parent) return false
  if (category.parent.id === ancestor.id) return true
  return isDescendantOf(category.parent, ancestor)
}

const handleSubmit = () => {
  emit('submit', form.value)
}
</script>