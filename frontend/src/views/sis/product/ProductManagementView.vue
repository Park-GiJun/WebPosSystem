// views/ProductManagementView.vue (continued)
<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold">상품 관리</h2>
      <BaseButton @click="showProductModal = true">
        상품 등록
      </BaseButton>
    </div>

    <!-- 검색 필터 -->
    <div class="bg-white rounded-lg shadow p-6 mb-6">
      <ProductFilter
          :categories="categories"
          @search="handleSearch"
      />
    </div>

    <!-- 상품 목록 -->
    <div class="bg-white rounded-lg shadow">
      <ProductList
          :products="products"
          @edit="editProduct"
          @delete="deleteProduct"
      />

      <!-- 페이지네이션 -->
      <div class="flex justify-between items-center px-6 py-4 border-t">
        <div class="text-sm text-gray-500">
          총 {{ totalCount }}개 상품
        </div>
        <div class="flex gap-2">
          <button
              v-for="page in totalPages"
              :key="page"
              @click="currentPage = page"
              :class="[
              'px-3 py-1 rounded',
              currentPage === page ? 'bg-blue-600 text-white' : 'bg-gray-100'
            ]"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>

    <!-- 상품 모달 -->
    <BaseModal
        v-model="showProductModal"
        :title="editingProduct ? '상품 수정' : '상품 등록'"
    >
      <ProductForm
          :product="editingProduct"
          :categories="categories"
          :is-submitting="isSubmitting"
          :errors="errors"
          @submit="handleSubmit"
          @cancel="closeProductModal"
      />
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useToast } from 'vue-toastification'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseModal from '@/components/base/BaseModal.vue'
import ProductFilter from '@/components/product/ProductFilter.vue'
import ProductList from '@/components/product/ProductList.vue'
import ProductForm from '@/components/product/ProductForm.vue'
import axios from '@/plugins/axios'

const toast = useToast()

// State
const categories = ref([])
const products = ref([])
const totalCount = ref(0)
const currentPage = ref(1)
const filters = ref({})
const showProductModal = ref(false)
const editingProduct = ref(null)
const isSubmitting = ref(false)
const errors = ref({})

// Computed
const totalPages = computed(() => Math.ceil(totalCount.value / 10))

// Methods
const fetchCategories = async () => {
  try {
    const response = await axios.get('/api/categories')
    categories.value = response.data.data
  } catch (error) {
    toast.error('카테고리 목록을 불러오는데 실패했습니다.')
  }
}

const fetchProducts = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: 10,
      ...filters.value
    }

    const response = await axios.get('/api/products', { params })
    products.value = response.data.data.content
    totalCount.value = response.data.data.totalElements
  } catch (error) {
    toast.error('상품 목록을 불러오는데 실패했습니다.')
  }
}

const handleSearch = (searchFilters) => {
  filters.value = searchFilters
  currentPage.value = 1
  fetchProducts()
}

const editProduct = (product) => {
  editingProduct.value = product
  showProductModal.value = true
}

const deleteProduct = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return

  try {
    await axios.delete(`/api/products/${id}`)
    toast.success('상품이 삭제되었습니다.')
    fetchProducts()
  } catch (error) {
    toast.error('상품 삭제에 실패했습니다.')
  }
}

const handleSubmit = async (formData) => {
  try {
    isSubmitting.value = true
    errors.value = {}

    const productData = {
      ...formData,
      price: Number(formData.price),
      costPrice: Number(formData.costPrice),
      stock: Number(formData.stock),
      minStock: Number(formData.minStock),
      maxStock: formData.maxStock ? Number(formData.maxStock) : null
    }

    if (editingProduct.value) {
      await axios.put(`/api/products/${editingProduct.value.id}`, productData)
      toast.success('상품이 수정되었습니다.')
    } else {
      await axios.post('/api/products', productData)
      toast.success('상품이 등록되었습니다.')
    }

    closeProductModal()
    fetchProducts()
  } catch (error) {
    if (error.response?.data?.errors) {
      errors.value = error.response.data.errors
    } else {
      toast.error('상품 저장에 실패했습니다.')
    }
  } finally {
    isSubmitting.value = false
  }
}

const closeProductModal = () => {
  showProductModal.value = false
  editingProduct.value = null
  errors.value = {}
}

// Watch for page changes
watch(currentPage, () => {
  fetchProducts()
})

// Initial fetch
fetchCategories()
fetchProducts()
</script>