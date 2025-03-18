<!-- views/sis/product/ProductManagementView.vue -->
<template>
  <div class="p-6">
    <!-- 헤더 영역 -->
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold">상품 관리</h2>
      <div class="flex gap-2">
        <button
            @click="showCategoryModal = true"
            class="px-4 py-2 border rounded-lg hover:bg-gray-50"
        >
          카테고리 관리
        </button>
        <button
            @click="openProductModal()"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2"
        >
          <PlusIcon class="w-4 h-4" />
          상품 등록
        </button>
      </div>
    </div>

    <!-- 검색 필터 영역 -->
    <div class="bg-white p-6 rounded-lg shadow mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">카테고리</label>
          <select
              v-model="filters.categoryId"
              class="w-full px-4 py-2 border rounded-lg"
          >
            <option value="">전체</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">상품 유형</label>
          <select
              v-model="filters.productType"
              class="w-full px-4 py-2 border rounded-lg"
          >
            <option value="">전체</option>
            <option v-for="type in productTypes" :key="type.value" :value="type.value">
              {{ type.label }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">상태</label>
          <select
              v-model="filters.status"
              class="w-full px-4 py-2 border rounded-lg"
          >
            <option value="">전체</option>
            <option value="ON_SALE">판매중</option>
            <option value="OUT_OF_STOCK">품절</option>
            <option value="DISCONTINUED">판매중지</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">검색어</label>
          <div class="relative">
            <input
                type="text"
                v-model="filters.keyword"
                placeholder="상품명 또는 코드"
                class="w-full px-4 py-2 border rounded-lg pr-10"
            />
            <SearchIcon class="w-5 h-5 text-gray-400 absolute right-3 top-2.5" />
          </div>
        </div>
      </div>
      <div class="flex justify-end mt-4">
        <button
            @click="fetchProducts"
            class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
        >
          검색
        </button>
      </div>
    </div>

    <!-- 상품 목록 테이블 -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <div v-if="isLoading" class="p-6 text-center">
        <div class="flex justify-center">
          <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
        <p class="mt-4 text-gray-600">상품 목록을 불러오는 중...</p>
      </div>

      <div v-else-if="products.length === 0" class="p-6 text-center">
        <p class="text-gray-600">등록된 상품이 없습니다.</p>
      </div>

      <table v-else class="w-full">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품코드</th>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상품명</th>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">카테고리</th>
          <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">판매가</th>
          <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">원가</th>
          <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">재고</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">유형</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">상태</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">관리</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
        <tr v-for="product in products" :key="product.id">
          <td class="px-6 py-4">{{ product.code }}</td>
          <td class="px-6 py-4">{{ product.name }}</td>
          <td class="px-6 py-4">{{ product.category.name }}</td>
          <td class="px-6 py-4 text-right">{{ formatPrice(product.price) }}</td>
          <td class="px-6 py-4 text-right">{{ formatPrice(product.costPrice) }}</td>
          <td class="px-6 py-4 text-right" :class="getStockClass(product)">
            {{ product.stock }}
          </td>
          <td class="px-6 py-4 text-center">
              <span class="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs">
                {{ getProductTypeLabel(product.productType) }}
              </span>
          </td>
          <td class="px-6 py-4 text-center">
              <span :class="getStatusBadgeClass(product.status)">
                {{ getStatusLabel(product.status) }}
              </span>
          </td>
          <td class="px-6 py-4">
            <div class="flex justify-center gap-2">
              <button
                  @click="openProductModal(product)"
                  class="p-1 text-blue-600 hover:bg-blue-50 rounded"
              >
                <PencilIcon class="w-4 h-4" />
              </button>
              <button
                  @click="openStockModal(product)"
                  class="p-1 text-green-600 hover:bg-green-50 rounded"
              >
                <PackageIcon class="w-4 h-4" />
              </button>
              <button
                  @click="confirmDelete(product)"
                  class="p-1 text-red-600 hover:bg-red-50 rounded"
              >
                <TrashIcon class="w-4 h-4" />
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- 페이지네이션 -->
      <div class="flex justify-between items-center px-6 py-4 border-t">
        <div class="text-sm text-gray-500">
          총 {{ totalCount }}개 상품
        </div>
        <div class="flex gap-2">
          <button
              v-for="pageNum in pageNumbers"
              :key="pageNum"
              @click="currentPage = pageNum"
              :class="[
              'px-3 py-1 rounded',
              currentPage === pageNum ? 'bg-blue-600 text-white' : 'bg-gray-100'
            ]"
          >
            {{ pageNum }}
          </button>
        </div>
      </div>
    </div>

    <!-- 상품 모달 -->
    <ProductModal
        v-if="showProductModal"
        :product="selectedProduct"
        :categories="categories"
        :show="showProductModal"
        @close="showProductModal = false"
        @submit="handleProductSubmit"
    />

    <!-- 카테고리 모달 -->
    <CategoryModal
        v-if="showCategoryModal"
        :show="showCategoryModal"
        :categories="categories"
        @close="showCategoryModal = false"
        @refresh="fetchCategories"
    />

    <!-- 재고 조정 모달 -->
    <StockModal
        v-if="showStockModal"
        :product="selectedProduct"
        :show="showStockModal"
        @close="showStockModal = false"
        @update-stock="handleStockUpdate"
    />

    <!-- 삭제 확인 모달 -->
    <ConfirmModal
        v-if="showDeleteModal"
        :show="showDeleteModal"
        :title="`상품 삭제`"
        :message="`'${selectedProduct?.name}' 상품을 삭제하시겠습니까?`"
        @close="showDeleteModal = false"
        @confirm="deleteProduct"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { PlusIcon, SearchIcon, PencilIcon, TrashIcon, PackageIcon } from 'lucide-vue-next';
import { useToast } from 'vue-toastification';
import axios from '@/plugins/axios';

import ProductModal from '@/components/product/ProductModal.vue';
import CategoryModal from '@/components/category/CategoryModal.vue';
import StockModal from '@/components/product/StockModal.vue';
import ConfirmModal from '@/components/common/ConfirmModal.vue';

const toast = useToast();

// 상태 변수들
const isLoading = ref(false);
const products = ref([]);
const categories = ref([]);
const totalCount = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const showProductModal = ref(false);
const showCategoryModal = ref(false);
const showStockModal = ref(false);
const showDeleteModal = ref(false);
const selectedProduct = ref(null);

// 필터 상태
const filters = ref({
  categoryId: '',
  status: '',
  productType: '',
  keyword: ''
});

// 상품 유형 목록
const productTypes = [
  { value: 'PRODUCT', label: '일반 상품' },
  { value: 'RAW_MATERIAL', label: '원재료' },
  { value: 'RECIPE_PRODUCT', label: '레시피 상품' },
  { value: 'SET_PRODUCT', label: '세트 상품' }
];

// 계산된 속성들
const totalPages = computed(() => Math.ceil(totalCount.value / pageSize.value));
const pageNumbers = computed(() => {
  if (totalPages.value <= 5) {
    return Array.from({ length: totalPages.value }, (_, i) => i + 1);
  }

  const current = currentPage.value;
  if (current <= 3) {
    return [1, 2, 3, 4, 5];
  }
  if (current >= totalPages.value - 2) {
    return Array.from({ length: 5 }, (_, i) => totalPages.value - 4 + i);
  }
  return [current - 2, current - 1, current, current + 1, current + 2];
});

// 메서드들
const fetchProducts = async () => {
  try {
    isLoading.value = true;

    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      ...filters.value
    };

    const response = await axios.get('/products', { params });
    if (response.data && response.data.data) {
      products.value = response.data.data.content || [];
      totalCount.value = response.data.data.totalElements || 0;
    }
  } catch (error) {
    console.error('Failed to fetch products:', error);
    toast.error('상품 목록을 불러오는데 실패했습니다.');
  } finally {
    isLoading.value = false;
  }
};

const fetchCategories = async () => {
  try {
    const response = await axios.get('/categories');
    if (response.data && response.data.data) {
      categories.value = response.data.data.content || [];
    }
  } catch (error) {
    console.error('Failed to fetch categories:', error);
    toast.error('카테고리 목록을 불러오는데 실패했습니다.');
  }
};

const openProductModal = (product = null) => {
  selectedProduct.value = product;
  showProductModal.value = true;
};

const openStockModal = (product) => {
  selectedProduct.value = product;
  showStockModal.value = true;
};

const confirmDelete = (product) => {
  selectedProduct.value = product;
  showDeleteModal.value = true;
};

const deleteProduct = async () => {
  if (!selectedProduct.value) return;

  try {
    await axios.delete(`/products/${selectedProduct.value.id}`);
    toast.success('상품이 성공적으로 삭제되었습니다.');
    fetchProducts();
    showDeleteModal.value = false;
  } catch (error) {
    console.error('Failed to delete product:', error);
    toast.error('상품 삭제에 실패했습니다.');
  }
};

const handleProductSubmit = async (productData) => {
  try {
    if (selectedProduct.value) {
      // 기존 상품 수정
      await axios.put(`/products/${selectedProduct.value.id}`, { data: productData });
      toast.success('상품 정보가 수정되었습니다.');
    } else {
      // 신규 상품 등록
      await axios.post('/products', { data: productData });
      toast.success('상품이 등록되었습니다.');
    }
    showProductModal.value = false;
    fetchProducts();
  } catch (error) {
    console.error('Failed to save product:', error);
    toast.error('상품 저장에 실패했습니다.');
  }
};

const handleStockUpdate = async (data) => {
  try {
    await axios.patch(`/products/${selectedProduct.value.id}/stock`, { data });
    toast.success('재고가 성공적으로 업데이트되었습니다.');
    showStockModal.value = false;
    fetchProducts();
  } catch (error) {
    console.error('Failed to update stock:', error);
    toast.error('재고 업데이트에 실패했습니다.');
  }
};

// 유틸리티 함수들
const formatPrice = (price) => {
  return `₩${Number(price).toLocaleString()}`;
};

const getStatusLabel = (status) => {
  const statusMap = {
    'ON_SALE': '판매중',
    'OUT_OF_STOCK': '품절',
    'DISCONTINUED': '판매중지'
  };
  return statusMap[status] || status;
};

const getProductTypeLabel = (type) => {
  const typeMap = {
    'PRODUCT': '일반 상품',
    'RAW_MATERIAL': '원재료',
    'RECIPE_PRODUCT': '레시피 상품',
    'SET_PRODUCT': '세트 상품',
    'GOODS': '상품'
  };
  return typeMap[type] || type;
};

const getStatusBadgeClass = (status) => {
  const classes = {
    'ON_SALE': 'bg-green-100 text-green-800',
    'OUT_OF_STOCK': 'bg-red-100 text-red-800',
    'DISCONTINUED': 'bg-gray-100 text-gray-800'
  };
  return `px-2 py-1 rounded-full text-xs ${classes[status] || 'bg-gray-100 text-gray-800'}`;
};

const getStockClass = (product) => {
  if (product.stock === 0) return 'text-red-600 font-bold';
  if (product.stock <= product.minStock) return 'text-yellow-600 font-bold';
  return '';
};

// 감시자
watch(currentPage, () => {
  fetchProducts();
});

// 생명주기 훅
onMounted(() => {
  fetchCategories();
  fetchProducts();
});
</script>