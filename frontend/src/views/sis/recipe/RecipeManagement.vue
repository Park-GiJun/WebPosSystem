<!-- Recipe.vue -->
<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold">레시피 관리</h1>
      <button
          @click="showRecipeModal = true"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2"
      >
        <PlusIcon class="w-4 h-4" />
        레시피 등록
      </button>
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
                v-model="searchParams.keyword"
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

    <!-- Recipe List -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="w-full">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">레시피명</th>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">재료</th>
          <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">가격</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">상태</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">관리</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
        <tr v-for="recipe in recipes" :key="recipe.id">
          <td class="px-6 py-4">{{ recipe.name }}</td>
          <td class="px-6 py-4">
              <span v-for="(ingredient, index) in recipe.ingredients" :key="index" class="inline-block px-2 py-1 bg-gray-100 rounded-full text-sm mr-1 mb-1">
                {{ ingredient.name }} ({{ ingredient.amount }}{{ ingredient.unit }})
              </span>
          </td>
          <td class="px-6 py-4 text-right">{{ formatPrice(recipe.price) }}</td>
          <td class="px-6 py-4">
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
                  @click="deleteRecipe(recipe.id)"
                  class="p-1 text-red-600 hover:bg-red-50 rounded"
              >
                <TrashIcon class="w-4 h-4" />
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- Pagination -->
      <div class="flex justify-between items-center px-6 py-4 border-t">
        <div class="text-sm text-gray-500">
          총 {{ totalCount }}개 레시피
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
  </div>

  <!-- Recipe Modal -->
  <RecipeModal
      v-if="showRecipeModal"
      :show="showRecipeModal"
      :recipe="editingRecipe"
      :categories="categories"
      @close="closeRecipeModal"
      @submit="handleRecipeSubmit"
  />
</template>

<script setup>
import {ref, computed, onMounted, watch} from 'vue';
import axios from "@/plugins/axios.js";
import { PlusIcon, PencilIcon, TrashIcon, SearchIcon } from 'lucide-vue-next';
import RecipeModal from '@/components/recipe/RecipeModal.vue';
import { useToast } from 'vue-toastification';

const toast = useToast();

// State
const recipes = ref([]);
const categories = ref([]);
const totalCount = ref(0);
const currentPage = ref(1);
const searchParams = ref({
  categoryId: '',
  status: '',
  keyword: ''
});
const showRecipeModal = ref(false);
const editingRecipe = ref(null);

// Computed
const totalPages = computed(() => Math.ceil(totalCount.value / 10));

// Methods
const fetchRecipes = async () => {
  try {
    const response = await axios.get('/recipes', {
      params: {
        page: currentPage.value,
        size: 10,
        ...searchParams.value
      }
    });
    recipes.value = response.data.content;
    totalCount.value = response.data.totalElements;
  } catch (error) {
    toast.error('레시피 목록을 불러오는데 실패했습니다.');
  }
};

const fetchCategories = async () => {
  try {
    const response = await axios.get('/categories');
    categories.value = response.data;
  } catch (error) {
    toast.error('카테고리 목록을 불러오는데 실패했습니다.');
  }
};

const editRecipe = (recipe) => {
  editingRecipe.value = recipe;
  showRecipeModal.value = true;
};

const deleteRecipe = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return;

  try {
    await axios.delete(`/recipes/${id}`);
    toast.success('레시피가 삭제되었습니다.');
    await fetchRecipes();
  } catch (error) {
    toast.error('레시피 삭제에 실패했습니다.');
  }
};

const handleRecipeSubmit = async (data) => {
  try {
    if (editingRecipe.value) {
      await axios.put(`/recipes/${editingRecipe.value.id}`, data);
      toast.success('레시피가 수정되었습니다.');
    } else {
      await axios.post('/recipes', data);
      toast.success('레시피가 등록되었습니다.');
    }
    closeRecipeModal();
    await fetchRecipes();
  } catch (error) {
    toast.error(error.response?.data?.message || '레시피 저장에 실패했습니다.');
  }
};

const closeRecipeModal = () => {
  showRecipeModal.value = false;
  editingRecipe.value = null;
};

const formatPrice = (price) => {
  return `₩${price.toLocaleString()}`;
};

// Watch
watch(currentPage, () => {
  fetchRecipes();
});

// Initial fetch
onMounted(() => {
  fetchCategories();
  fetchRecipes();
});
</script>