// components/pos/ProductGrid.vue
<template>
  <div class="flex-1 p-4 flex flex-col">
    <div class="flex-none mb-4">
      <div class="flex gap-2 overflow-x-auto">
        <button
            @click="selectedCategoryId = null"
            :class="[
              'px-6 py-3 rounded-lg whitespace-nowrap font-medium',
              selectedCategoryId === null ? 'bg-blue-600 text-white' : 'bg-white hover:bg-gray-50'
            ]"
        >
          전체
        </button>
        <button
            v-for="cat in categories"
            :key="cat.id"
            @click="selectCategory(cat.id)"
            :class="[
              'px-6 py-3 rounded-lg whitespace-nowrap font-medium',
              selectedCategoryId === cat.id ? 'bg-blue-600 text-white' : 'bg-white hover:bg-gray-50'
            ]"
        >
          {{ cat.name }}
        </button>
      </div>
    </div>

    <div v-if="isLoading" class="flex-1 flex items-center justify-center">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>

    <div v-else-if="filteredProducts.length === 0" class="flex-1 flex items-center justify-center">
      <p class="text-gray-500">상품이 없습니다.</p>
    </div>

    <div v-else class="grid grid-cols-4 gap-4 overflow-auto flex-1">
      <button
          v-for="product in filteredProducts"
          :key="product.id"
          @click="addToOrder(product)"
          class="bg-white h-32 p-4 rounded-lg shadow hover:shadow-md transition-shadow flex flex-col justify-center items-center"
      >
        <h3 class="font-medium text-center">{{ product.name }}</h3>
        <p class="text-blue-600 mt-2">₩{{ product.price.toLocaleString() }}</p>
        <p v-if="product.stock <= 0 && product.productType !== 'RECIPE_PRODUCT'" class="text-red-500 text-xs mt-1">품절</p>
      </button>
    </div>

    <!-- 페이지네이션 -->
    <div v-if="totalPages > 1" class="mt-4 flex justify-center">
      <div class="flex gap-2">
        <button
            v-for="page in paginationRange"
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
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import { posService } from '@/services/posService'

const props = defineProps({
  defaultCategories: {
    type: Array,
    default: () => []
  },
  defaultProducts: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['addItem'])
const toast = useToast()

const isLoading = ref(false)
const categories = ref(props.defaultCategories)
const products = ref(props.defaultProducts)
const selectedCategoryId = ref(null)
const currentPage = ref(1)
const pageSize = ref(12)
const totalPages = ref(1)
const totalItems = ref(0)

const paginationRange = computed(() => {
  const range = [];
  const maxPages = 5;
  const start = Math.max(1, currentPage.value - Math.floor(maxPages / 2));
  const end = Math.min(totalPages.value, start + maxPages - 1);

  for (let i = start; i <= end; i++) {
    range.push(i);
  }

  return range;
});

const filteredProducts = computed(() => {
  if (selectedCategoryId.value === null) {
    return products.value;
  }
  return products.value.filter(product => {
    return product.category && product.category.id === selectedCategoryId.value;
  });
});

// 카테고리 선택 처리
const selectCategory = async (categoryId) => {
  selectedCategoryId.value = categoryId;
  currentPage.value = 1;
  await fetchProductsByCategory();
};

// API 호출 함수들
const fetchCategories = async () => {
  try {
    isLoading.value = true;
    const response = await posService.getCategories();
    categories.value = response.content || [];
  } catch (error) {
    toast.error('카테고리를 불러오는데 실패했습니다.');
  } finally {
    isLoading.value = false;
  }
};

const fetchProducts = async () => {
  try {
    isLoading.value = true;
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      status: 'ON_SALE',
      sort: 'price,asc',
    };

    const response = await posService.getProducts(params);

    products.value = (response.content || []).filter(product => product.productType !== 'RAW_MATERIAL');

    totalItems.value = products.value.length; // 필터링된 개수
    totalPages.value = Math.ceil(totalItems.value / pageSize.value);
  } catch (error) {
    toast.error('상품을 불러오는데 실패했습니다.');
  } finally {
    isLoading.value = false;
  }
};


const fetchProductsByCategory = async () => {
  try {
    isLoading.value = true;

    if (selectedCategoryId.value === null) {
      await fetchProducts();
      return;
    }

    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      status: 'ON_SALE'
    };

    const response = await posService.getProductsByCategory(selectedCategoryId.value, params);
    products.value = response.content || [];
    totalItems.value = response.totalElements || 0;
    totalPages.value = response.totalPages || 1;
  } catch (error) {
    toast.error('상품을 불러오는데 실패했습니다.');
  } finally {
    isLoading.value = false;
  }
};

const addToOrder = (product) => {
  if (product.stock <= 0 && product.productType !== 'RECIPE_PRODUCT') {
    toast.error(`${product.name}은(는) 품절입니다.`);
    return;
  }

  emit('addItem', {
    id: product.id,
    name: product.name,
    price: product.price,
    quantity: 1
  });

  toast.success(`${product.name} 추가됨`);
};

watch(currentPage, () => {
  if (selectedCategoryId.value === null) {
    fetchProducts();
  } else {
    fetchProductsByCategory();
  }
});

onMounted(async () => {
  if (props.defaultCategories.length === 0) {
    await fetchCategories();
  }

  if (props.defaultProducts.length === 0) {
    await fetchProducts();
  }
});
</script>