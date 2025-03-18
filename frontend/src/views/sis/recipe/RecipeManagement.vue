<!-- views/sis/recipe/RecipeManagement.vue -->
<template>
  <div class="p-6">
    <!-- 헤더 -->
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold">레시피 관리</h1>
      <button
          @click="openRecipeModal"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2"
      >
        <PlusIcon class="w-4 h-4" />
        레시피 등록
      </button>
    </div>

    <!-- 검색 필터 -->
    <div class="bg-white p-6 rounded-lg shadow mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            카테고리
          </label>
          <select
              v-model="filters.categoryId"
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
              v-model="filters.status"
              class="w-full px-4 py-2 border rounded-lg"
          >
            <option value="">전체</option>
            <option value="ACTIVE">활성</option>
            <option value="INACTIVE">비활성</option>
          </select>
        </div>
        <div class="col-span-2">
          <label class="block text-sm font-medium text-gray-700 mb-1">
            검색어
          </label>
          <div class="relative">
            <input
                type="text"
                v-model="filters.keyword"
                placeholder="레시피명 입력"
                class="w-full px-4 py-2 border rounded-lg pr-10"
            />
            <SearchIcon class="w-5 h-5 text-gray-400 absolute right-3 top-2.5" />
          </div>
        </div>
      </div>
      <div class="mt-4 flex justify-end">
        <button
            @click="fetchRecipes"
            class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
        >
          검색
        </button>
      </div>
    </div>

    <!-- 레시피 목록 -->
    <div class="bg-white rounded-lg shadow">
      <div v-if="isLoading" class="p-6 text-center">
        <div class="flex justify-center">
          <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
        <p class="mt-4 text-gray-600">레시피 목록을 불러오는 중...</p>
      </div>

      <div v-else-if="recipes.length === 0" class="p-6 text-center">
        <p class="text-gray-600">등록된 레시피가 없습니다.</p>
      </div>

      <table v-else class="w-full">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">레시피명</th>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">카테고리</th>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">재료</th>
          <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">가격</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">조리시간</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">상태</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">관리</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
        <tr v-for="recipe in recipes" :key="recipe.id">
          <td class="px-6 py-4">{{ recipe.name }}</td>
          <td class="px-6 py-4">{{ recipe.category.name }}</td>
          <td class="px-6 py-4">
            <div class="flex flex-wrap gap-1">
              <span
                  v-for="(component, index) in recipe.recipeComponents"
                  :key="index"
                  class="inline-block px-2 py-1 bg-gray-100 rounded-full text-xs"
              >
                {{ component.ingredient.name }} ({{ component.quantity }}{{ component.unit }})
              </span>
              <span
                  v-if="recipe.recipeComponents && recipe.recipeComponents.length > 3"
                  class="inline-block px-2 py-1 text-xs text-gray-500"
              >
                외 {{ recipe.recipeComponents.length - 3 }}개
              </span>
            </div>
          </td>
          <td class="px-6 py-4 text-right">{{ formatPrice(recipe.price) }}</td>
          <td class="px-6 py-4 text-center">{{ recipe.cookingTime || '-' }} 분</td>
          <td class="px-6 py-4 text-center">
            <span
                :class="[
                'px-2 py-1 rounded-full text-xs font-medium',
                recipe.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
              ]"
            >
              {{ recipe.status === 'ACTIVE' ? '활성' : '비활성' }}
            </span>
          </td>
          <td class="px-6 py-4">
            <div class="flex justify-center gap-2">
              <button
                  @click="editRecipe(recipe)"
                  class="p-1 text-blue-600 hover:bg-blue-50 rounded"
              >
                <PencilIcon class="w-4 h-4" />
              </button>
              <button
                  @click="confirmDeleteRecipe(recipe)"
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
          총 {{ totalCount }}개 레시피
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

    <!-- 레시피 모달 -->
    <RecipeModal
        v-if="showRecipeModal"
        :show="showRecipeModal"
        :recipe="selectedRecipe"
        :categories="categories"
        @close="closeRecipeModal"
        @submit="handleRecipeSubmit"
    />

    <!-- 삭제 확인 모달 -->
    <BaseModal
        v-model="showDeleteModal"
        title="레시피 삭제"
    >
      <div class="p-4">
        <p>
          정말 "<span class="font-semibold">{{ recipeToDelete?.name }}</span>" 레시피를
          삭제하시겠습니까?
        </p>
        <p class="mt-2 text-sm text-gray-500">
          이 작업은 되돌릴 수 없습니다.
        </p>

        <div class="flex justify-end gap-3 mt-6">
          <button
              @click="showDeleteModal = false"
              class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
          >
            취소
          </button>
          <button
              @click="deleteRecipe"
              class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
          >
            삭제
          </button>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import {
  PlusIcon,
  PencilIcon,
  TrashIcon,
  SearchIcon
} from 'lucide-vue-next';
import { useToast } from 'vue-toastification';
import axios from '@/plugins/axios';

import RecipeModal from '@/components/recipe/RecipeModal.vue';
import BaseModal from '@/components/base/BaseModal.vue';

const toast = useToast();

// 상태 관리
const isLoading = ref(false);
const recipes = ref([]);
const categories = ref([]);
const totalCount = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 모달 상태
const showRecipeModal = ref(false);
const showDeleteModal = ref(false);
const selectedRecipe = ref(null);
const recipeToDelete = ref(null);

// 필터 상태
const filters = ref({
  categoryId: '',
  status: '',
  keyword: ''
});

// 페이지네이션 계산
const pageNumbers = computed(() => {
  const totalPages = Math.ceil(totalCount.value / pageSize.value);

  if (totalPages <= 5) {
    return Array.from({ length: totalPages }, (_, i) => i + 1);
  }

  const current = currentPage.value;
  if (current <= 3) {
    return [1, 2, 3, 4, 5];
  }
  if (current >= totalPages - 2) {
    return Array.from({ length: 5 }, (_, i) => totalPages - 4 + i);
  }

  return [current - 2, current - 1, current, current + 1, current + 2];
});

// 메서드들
const fetchCategories = async () => {
  try {
    const response = await axios.get('/categories');
    categories.value = response.data.data.content || [];
  } catch (error) {
    console.error('카테고리 불러오기 실패:', error);
    toast.error('카테고리 목록을 불러오는데 실패했습니다.');
  }
};

const fetchRecipes = async () => {
  try {
    isLoading.value = true;

    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      productType: 'RECIPE_PRODUCT',
      ...filters.value
    };

    const response = await axios.get('/products', { params });

    if (response.data && response.data.data) {
      recipes.value = response.data.data.content || [];
      totalCount.value = response.data.data.totalElements || 0;
    }
  } catch (error) {
    console.error('레시피 불러오기 실패:', error);
    toast.error('레시피 목록을 불러오는데 실패했습니다.');
  } finally {
    isLoading.value = false;
  }
};

const openRecipeModal = () => {
  selectedRecipe.value = null;
  showRecipeModal.value = true;
};

const editRecipe = (recipe) => {
  selectedRecipe.value = recipe;
  showRecipeModal.value = true;
};

const closeRecipeModal = () => {
  showRecipeModal.value = false;
  selectedRecipe.value = null;
};

const handleRecipeSubmit = () => {
  fetchRecipes();
};

const confirmDeleteRecipe = (recipe) => {
  recipeToDelete.value = recipe;
  showDeleteModal.value = true;
};

const deleteRecipe = async () => {
  if (!recipeToDelete.value) return;

  try {
    await axios.delete(`/products/${recipeToDelete.value.id}`);
    toast.success('레시피가 삭제되었습니다.');
    fetchRecipes();
    showDeleteModal.value = false;
  } catch (error) {
    console.error('레시피 삭제 실패:', error);
    toast.error('레시피 삭제에 실패했습니다.');
  }
};

const formatPrice = (price) => {
  return price ? `₩${Number(price).toLocaleString()}` : '-';
};

// 페이지 변경 감지
watch(currentPage, () => {
  fetchRecipes();
});

// 초기 로드
onMounted(() => {
  fetchCategories();
  fetchRecipes();
});
</script>