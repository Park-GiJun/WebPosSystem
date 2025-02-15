<!-- views/product/ProductManagement.vue -->
<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold">상품 관리</h1>
      <div class="flex gap-2">
        <button
            @click="showCategoryModal = true"
            class="px-4 py-2 border rounded-lg hover:bg-gray-50"
        >
          카테고리 관리
        </button>
        <button
            @click="showProductModal = true"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2"
        >
          <PlusIcon class="w-4 h-4" />
          상품 등록
        </button>
      </div>
    </div>

    <!-- Search Form -->
    <div class="bg-white p-6 rounded-lg shadow mb-6">
      <div class="grid grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            카테고리
          </label>
          <select
              v-model="searchParams.categoryId"
              class="w-full px-4 py-2 border rounded-lg"
          >
            <option value="">전체</option>
            <option
                v-for="category in categories"
                :key="category.id"
                :value="category.id"
            >
              {{ category.name }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            상태
          </label>
          <select
              v-model="searchParams.status"
              class="w-full px-4 py-2 border rounded-lg"
          >
            <option value="">전체</option>
            <option value="ON_SALE">판매중</option>
            <option value="OUT_OF_STOCK">품절</option>
            <option value="DISCONTINUED">판매중지</option>
          </select>
        </div>
        <div class="col-span-2">
          <label class="block text-sm font-medium text-gray-700 mb-1">
            검색어
          </label>
          <div class="relative">
            <input
                type="text"
                v-model="searchParams.keyword"
                placeholder="상품명 또는 상품코드 입력"
                class="w-full px-4 py-2 border rounded-lg pr-10"
            />
            <SearchIcon class="w-5 h-5 text-gray-400 absolute right-3 top-2.5" />
          </div>
        </div>
      </div>
      <div class="mt-4 flex justify-end">
        <button
            @click="fetchProducts"
            class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
        >
          검색
        </button>
      </div>
    </div>

    <!-- Product List -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="w-full">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품코드</th>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">카테고리</th>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품명</th>
          <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">판매가</th>
          <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">원가</th>
          <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">재고</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">단위</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">상태</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">관리</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
        <tr v-for="product in products" :key="product.id">
          <td class="px-6 py-4">{{ product.code }}</td>
          <td class="px-6 py-4">{{ product.category.name }}</td>
          <td class="px-6 py-4">{{ product.name }}</td>
          <td class="px-6 py-4 text-right">{{ formatPrice(product.price) }}</td>
          <td class="px-6 py-4 text-right">{{ formatPrice(product.costPrice) }}</td>
          <td class="px-6 py-4 text-right">{{ product.unit }}</td>
          <td class="px-6 py-4 text-right">{{ product.stock }}</td>
          <td class="px-6 py-4">
              <span
                  :class="[
                  'px-2 py-1 rounded-full text-xs font-medium',
                  statusClasses[product.status]
                ]"
              >
                {{ statusLabels[product.status] }}
              </span>
          </td>
          <td class="px-6 py-4">
            <div class="flex justify-center gap-2">
              <button
                  @click="editProduct(product)"
                  class="p-1 text-blue-600 hover:bg-blue-50 rounded"
              >
                <PenSquareIcon class="w-4 h-4" />
              </button>
              <button
                  @click="deleteProduct(product.id)"
                  class="p-1 text-red-600 hover:bg-red-50 rounded"
              >
                <Trash2Icon class="w-4 h-4" />
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- Pagination -->
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

    <CategoryModal
        v-model:show="showCategoryModal"
        :categories="categories"
        :editing-category="selectedCategory"
        @refresh="fetchCategories"
    />

    <ProductModal
        v-model:show="showProductModal"
        :categories="categories"
        :editing-product="selectedProduct"
        @refresh="fetchProducts"
    />
  </div>
</template>

<script setup>
import {ref, computed, onMounted, watch} from 'vue'
import { PlusIcon, SearchIcon, PenSquareIcon, Trash2Icon } from 'lucide-vue-next'
import CategoryModal from '@/components/category/CategoryModal.vue'
import ProductModal from '@/components/product/ProductModal.vue'
import axios from '@/plugins/axios'
import { useToast } from 'vue-toastification'

const toast = useToast()

// State
const products = ref([])
const categories = ref([])
const totalCount = ref(0)
const currentPage = ref(1)
const searchParams = ref({
  keyword: '',
  categoryId: '',
  status: ''
})

const showCategoryModal = ref(false)
const showProductModal = ref(false)
const selectedCategory = ref(null)
const selectedProduct = ref(null)

// Computed
const totalPages = computed(() => Math.ceil(totalCount.value / 20))

const statusClasses = {
  ON_SALE: 'bg-green-100 text-green-800',
  OUT_OF_STOCK: 'bg-red-100 text-red-800',
  DISCONTINUED: 'bg-gray-100 text-gray-800'
}

const statusLabels = {
  ON_SALE: '판매중',
  OUT_OF_STOCK: '품절',
  DISCONTINUED: '판매중지'
}

// Methods
const fetchProducts = async () => {
  try {
    const params = {
      page: currentPage.value - 1,
      size: 20,
      ...searchParams.value
    }

    const response = await axios.get('/products', { params })
    products.value = response.data.data.content
    totalCount.value = response.data.data.totalElements
  } catch (error) {
    toast.error('상품 목록을 불러오는데 실패했습니다')
  }
}

const fetchCategories = async () => {
  try {
    const response = await axios.get('/categories')
    categories.value = response.data.data.content
  } catch (error) {
    toast.error('카테고리 목록을 불러오는데 실패했습니다')
  }
}

const editProduct = (product) => {
  selectedProduct.value = product
  showProductModal.value = true
}

const deleteProduct = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return

  try {
    await axios.delete(`/products/${id}`)
    toast.success('상품이 삭제되었습니다')
    await fetchProducts()
  } catch (error) {
    toast.error('상품 삭제에 실패했습니다')
  }
}

const formatPrice = (price) => {
  return `₩${price.toLocaleString()}`
}

// Watch
watch(currentPage, () => {
  fetchProducts()
})

// Initial fetch
onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>