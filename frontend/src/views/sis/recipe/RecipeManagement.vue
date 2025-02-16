<!-- views/sis/recipe/RecipeManagement.vue -->
<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold">레시피 관리</h1>
      <div class="flex gap-2">
        <button
            @click="showRecipeModal = true"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2"
        >
          <PlusIcon class="w-4 h-4" />
          레시피 등록
        </button>
      </div>
    </div>

    <!-- Search Form -->
    <div class="bg-white p-6 rounded-lg shadow mb-6">
      <div class="flex gap-4">
        <input
            type="text"
            v-model="searchKeyword"
            placeholder="레시피명 또는 코드 검색"
            class="flex-1 px-4 py-2 border rounded-lg"
            @keyup.enter="handleSearch"
        />
        <button
            @click="handleSearch"
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
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">코드</th>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">레시피명</th>
          <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">판매가</th>
          <th class="px-6 py-3 text-right text-sm font-medium text-gray-500">원가</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">상태</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">관리</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
        <tr v-for="recipe in recipes" :key="recipe.id">
          <td class="px-6 py-4">{{ recipe.code }}</td>
          <td class="px-6 py-4">{{ recipe.name }}</td>
          <td class="px-6 py-4 text-right">{{ formatPrice(recipe.price) }}</td>
          <td class="px-6 py-4 text-right">{{ formatPrice(recipe.costPrice) }}</td>
          <td class="px-6 py-4">
              <span
                  :class="[
                  'px-2 py-1 rounded-full text-xs font-medium',
                  statusClasses[recipe.status]
                ]"
              >
                {{ statusLabels[recipe.status] }}
              </span>
          </td>
          <td class="px-6 py-4">
            <div class="flex justify-center gap-2">
              <button
                  @click="editRecipe(recipe)"
                  class="p-1 text-blue-600 hover:bg-blue-50 rounded"
              >
                <PenSquareIcon class="w-4 h-4" />
              </button>
              <button
                  @click="showIngredients(recipe)"
                  class="p-1 text-green-600 hover:bg-green-50 rounded"
              >
                <ListIcon class="w-4 h-4" />
              </button>
              <button
                  @click="deleteRecipe(recipe.id)"
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
          총 {{ totalRecords }}개 레시피
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

    <!-- Recipe Modal -->
    <RecipeModal
        v-model:show="showRecipeModal"
        :editing-recipe="editingRecipe"
        @refresh="fetchRecipes"
    />

    <!-- Ingredients Modal -->
    <IngredientsModal
        v-model:show="showIngredientsModal"
        :recipe="selectedRecipe"
        @refresh="fetchRecipes"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { PlusIcon, PenSquareIcon, ListIcon, Trash2Icon } from 'lucide-vue-next'
import { useToast } from 'vue-toastification'
import RecipeModal from '@/components/recipe/RecipeModal.vue'
import IngredientsModal from '@/components/recipe/IngredientsModal.vue'
import axios from '@/plugins/axios'

const toast = useToast()

// State
const recipes = ref([])
const totalRecords = ref(0)
const currentPage = ref(1)
const searchKeyword = ref('')
const showRecipeModal = ref(false)
const showIngredientsModal = ref(false)
const editingRecipe = ref(null)
const selectedRecipe = ref(null)

// Status styling
const statusClasses = {
  'ON_SALE': 'bg-green-100 text-green-800',
  'OUT_OF_STOCK': 'bg-red-100 text-red-800',
  'DISCONTINUED': 'bg-gray-100 text-gray-800'
}

const statusLabels = {
  'ON_SALE': '판매중',
  'OUT_OF_STOCK': '품절',
  'DISCONTINUED': '판매중지'
}

// Methods
const formatPrice = (price) => {
  return `₩${price.toLocaleString()}`
}

const fetchRecipes = async () => {
  try {
    const params = {
      page: currentPage.value - 1,
      size: 10
    }

    if (searchKeyword.value) {
      const response = await axios.get(`/recipes/search?keyword=${searchKeyword.value}`)
      recipes.value = response.data.data
      totalRecords.value = response.data.data.length
    } else {
      const response = await axios.get('/recipes', { params })
      recipes.value = response.data.data.content
      totalRecords.value = response.data.data.totalElements
    }
  } catch (error) {
    toast.error('레시피 목록을 불러오는데 실패했습니다')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchRecipes()
}

const editRecipe = (recipe) => {
  editingRecipe.value = recipe
  showRecipeModal.value = true
}

const showIngredients = (recipe) => {
  selectedRecipe.value = recipe
  showIngredientsModal.value = true
}

const deleteRecipe = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return

  try {
    await axios.delete(`/recipes/${id}`)
    toast.success('레시피가 삭제되었습니다')
    fetchRecipes()
  } catch (error) {
    toast.error('레시피 삭제에 실패했습니다')
  }
}

// Watch
watch(currentPage, () => {
  fetchRecipes()
})

// Initial fetch
onMounted(() => {
  fetchRecipes()
})
</script>