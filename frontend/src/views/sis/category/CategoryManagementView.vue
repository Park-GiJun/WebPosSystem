<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold">카테고리 관리</h2>
      <BaseButton @click="showCategoryModal = true">
        카테고리 추가
      </BaseButton>
    </div>

    <!-- 카테고리 목록 -->
    <div class="bg-white rounded-lg shadow p-6">
      <CategoryList
          :categories="categories"
          @edit="editCategory"
          @delete="deleteCategory"
      />
    </div>

    <!-- 카테고리 모달 -->
    <BaseModal
        v-model="showCategoryModal"
        :title="editingCategory ? '카테고리 수정' : '카테고리 추가'"
    >
      <CategoryForm
          :category="editingCategory"
          :categories="categories"
          :is-submitting="isSubmitting"
          :errors="errors"
          @submit="handleSubmit"
          @cancel="closeCategoryModal"
      />
    </BaseModal>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useToast } from 'vue-toastification'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseModal from '@/components/base/BaseModal.vue'
import CategoryList from '@/components/category/CategoryList.vue'
import CategoryForm from '@/components/category/CategoryForm.vue'
import axios from '@/plugins/axios'

const toast = useToast()

// State
const categories = ref([])
const showCategoryModal = ref(false)
const editingCategory = ref(null)
const isSubmitting = ref(false)
const errors = ref({})

// Methods
const fetchCategories = async () => {
  try {
    const response = await axios.get('/api/categories')
    categories.value = response.data.data
  } catch (error) {
    toast.error('카테고리 목록을 불러오는데 실패했습니다.')
  }
}

const editCategory = (category) => {
  editingCategory.value = category
  showCategoryModal.value = true
}

const deleteCategory = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return

  try {
    await axios.delete(`/api/categories/${id}`)
    toast.success('카테고리가 삭제되었습니다.')
    fetchCategories()
  } catch (error) {
    toast.error('카테고리 삭제에 실패했습니다.')
  }
}

const handleSubmit = async (formData) => {
  try {
    isSubmitting.value = true
    errors.value = {}

    if (editingCategory.value) {
      await axios.put(`/api/categories/${editingCategory.value.id}`, formData)
      toast.success('카테고리가 수정되었습니다.')
    } else {
      await axios.post('/api/categories', formData)
      toast.success('카테고리가 추가되었습니다.')
    }

    closeCategoryModal()
    fetchCategories()
  } catch (error) {
    if (error.response?.data?.errors) {
      errors.value = error.response.data.errors
    } else {
      toast.error('카테고리 저장에 실패했습니다.')
    }
  } finally {
    isSubmitting.value = false
  }
}

const closeCategoryModal = () => {
  showCategoryModal.value = false
  editingCategory.value = null
  errors.value = {}
}

// Initial fetch
fetchCategories()
</script>