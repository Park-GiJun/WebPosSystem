<!-- views/sis/inventory/InventoryListView.vue -->
<template>
  <div class="p-6">
    <div class="mb-6 flex justify-between items-center">
      <h2 class="text-2xl font-bold">재고 관리</h2>
      <div class="flex gap-4">
        <button
            @click="showProductModal = true"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          상품 등록
        </button>
      </div>
    </div>

    <!-- Inventory Summary -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-gray-500 mb-2">총 상품 수</h3>
        <p class="text-3xl font-bold">{{ totalProducts }}개</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-gray-500 mb-2">재고 부족 상품</h3>
        <p class="text-3xl font-bold text-red-600">{{ lowStockProducts }}개</p>
      </div>
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-gray-500 mb-2">총 재고 금액</h3>
        <p class="text-3xl font-bold">₩{{ totalInventoryValue.toLocaleString() }}</p>
      </div>
    </div>

    <!-- Inventory Filter -->
    <div class="bg-white p-6 rounded-lg shadow mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <select v-model="selectedCategory" class="px-4 py-2 border rounded-lg">
          <option value="">전체 카테고리</option>
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
        <select v-model="stockFilter" class="px-4 py-2 border rounded-lg">
          <option value="all">전체 재고</option>
          <option value="low">부족 재고</option>
          <option value="out">품절</option>
        </select>
        <input
            type="text"
            v-model="searchQuery"
            placeholder="상품명 검색"
            class="px-4 py-2 border rounded-lg"
        />
        <button
            @click="searchProducts"
            class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
        >
          검색
        </button>
      </div>
    </div>

    <!-- Inventory Table -->
    <div class="bg-white rounded-lg shadow">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품코드</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품명</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">카테고리</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">판매가</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">현재고</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">적정재고</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상태</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">관리</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="product in products" :key="product.id">
            <td class="px-6 py-4">{{ product.code }}</td>
            <td class="px-6 py-4">{{ product.name }}</td>
            <td class="px-6 py-4">{{ product.category }}</td>
            <td class="px-6 py-4">₩{{ product.price.toLocaleString() }}</td>
            <td class="px-6 py-4">
                <span :class="[
                  product.stock <= product.minStock ? 'text-red-600' : ''
                ]">
                  {{ product.stock }}
                </span>
            </td>
            <td class="px-6 py-4">{{ product.minStock }}</td>
            <td class="px-6 py-4">
                <span :class="[
                  'px-2 py-1 rounded-full text-xs',
                  product.stock === 0 ? 'bg-red-100 text-red-800' :
                  product.stock <= product.minStock ? 'bg-yellow-100 text-yellow-800' :
                  'bg-green-100 text-green-800'
                ]">
                  {{ product.stock === 0 ? '품절' :
                    product.stock <= product.minStock ? '부족' : '정상' }}
                </span>
            </td>
            <td class="px-6 py-4">
              <button
                  @click="editProduct(product)"
                  class="text-blue-600 hover:text-blue-800"
              >
                수정
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Product Modal -->
    <TransitionRoot appear :show="showProductModal" as="template">
      <Dialog as="div" @close="closeProductModal" class="relative z-50">
        <TransitionChild
            enter="duration-300 ease-out"
            enter-from="opacity-0"
            enter-to="opacity-100"
            leave="duration-200 ease-in"
            leave-from="opacity-100"
            leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-black/25" />
        </TransitionChild>

        <div class="fixed inset-0 overflow-y-auto">
          <div class="flex min-h-full items-center justify-center p-4">
            <TransitionChild
                enter="duration-300 ease-out"
                enter-from="opacity-0 scale-95"
                enter-to="opacity-100 scale-100"
                leave="duration-200 ease-in"
                leave-from="opacity-100 scale-100"
                leave-to="opacity-0 scale-95"
            >
              <DialogPanel class="w-full max-w-md bg-white rounded-xl p-6">
                <DialogTitle class="text-lg font-medium mb-4">
                  {{ editingProduct ? '상품 수정' : '상품 등록' }}
                </DialogTitle>

                <form @submit.prevent="saveProduct" class="space-y-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">상품명</label>
                    <input
                        type="text"
                        v-model="productForm.name"
                        required
                        class="w-full px-4 py-2 border rounded-lg"
                    />
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">카테고리</label>
                    <select
                        v-model="productForm.categoryId"
                        required
                        class="w-full px-4 py-2 border rounded-lg"
                    >
                      <option v-for="category in categories" :key="category.id" :value="category.id">
                        {{ category.name }}
                      </option>
                    </select>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">판매가</label>
                    <input
                        type="number"
                        v-model="productForm.price"
                        required
                        min="0"
                        class="w-full px-4 py-2 border rounded-lg"
                    />
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">현재고</label>
                    <input
                        type="number"
                        v-model="productForm.stock"
                        required
                        min="0"
                        class="w-full px-4 py-2 border rounded-lg"
                    />
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">적정재고</label>
                    <input
                        type="number"
                        v-model="productForm.minStock"
                        required
                        min="0"
                        class="w-full px-4 py-2 border rounded-lg"
                    />
                  </div>

                  <div class="flex justify-end gap-4 mt-6">
                    <button
                        type="button"
                        @click="closeProductModal"
                        class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
                    >
                      취소
                    </button>
                    <button
                        type="submit"
                        class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                      저장
                    </button>
                  </div>
                </form>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue'

// State
const totalProducts = ref(152)
const lowStockProducts = ref(8)
const totalInventoryValue = ref(15800000)

const selectedCategory = ref('')
const stockFilter = ref('all')
const searchQuery = ref('')

const showProductModal = ref(false)
const editingProduct = ref(null)

const categories = ref([
  { id: 1, name: '커피' },
  { id: 2, name: '논커피' },
  { id: 3, name: '티' },
  { id: 4, name: '디저트' },
  { id: 5, name: '베이커리' },
])

const products = ref([
  {
    id: 1,
    code: 'P001',
    name: '아메리카노',
    category: '커피',
    price: 4500,
    stock: 120,
    minStock: 100,
  },
  {
    id: 2,
    code: 'P002',
    name: '카페라떼',
    category: '커피',
    price: 5000,
    stock: 80,
    minStock: 100,
  },
  {
    id: 3,
    code: 'P003',
    name: '초코라떼',
    category: '논커피',
    price: 5500,
    stock: 50,
    minStock: 80,
  },
  {
    id: 4,
    code: 'P004',
    name: '얼그레이',
    category: '티',
    price: 4500,
    stock: 0,
    minStock: 50,
  },
])

const productForm = ref({
  name: '',
  categoryId: '',
  price: 0,
  stock: 0,
  minStock: 0,
})

// Methods
const searchProducts = () => {
  // Implement search logic
}

const editProduct = (product) => {
  editingProduct.value = product
  productForm.value = {
    name: product.name,
    categoryId: categories.value.find(c => c.name === product.category)?.id,
    price: product.price,
    stock: product.stock,
    minStock: product.minStock,
  }
  showProductModal.value = true
}

const closeProductModal = () => {
  showProductModal.value = false
  editingProduct.value = null
  productForm.value = {
    name: '',
    categoryId: '',
    price: 0,
    stock: 0,
    minStock: 0,
  }
}

const saveProduct = async () => {
  try {
    if (editingProduct.value) {
      // Update existing product
      await updateProduct({
        id: editingProduct.value.id,
        ...productForm.value
      })
    } else {
      // Create new product
      await createProduct(productForm.value)
    }

    // Refresh product list
    await fetchProducts()
    closeProductModal()
  } catch (error) {
    console.error('Failed to save product:', error)
  }
}

const createProduct = async (product) => {
  // API call to create product
}

const updateProduct = async (product) => {
  // API call to update product
}

const fetchProducts = async () => {
  // API call to fetch products
}
</script>