<!-- views/sis/category/CategoryManagement.vue -->
<template>
  <div class="p-6">
    <!-- 헤더 -->
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold">카테고리 관리</h1>
      <button
          @click="openCategoryModal()"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2"
      >
        <PlusIcon class="w-4 h-4" />
        카테고리 등록
      </button>
    </div>

    <!-- 카테고리 목록 -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <div v-if="isLoading" class="p-6 text-center">
        <div class="flex justify-center">
          <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
        <p class="mt-4 text-gray-600">카테고리 목록을 불러오는 중...</p>
      </div>

      <div v-else-if="categories.length === 0" class="p-6 text-center">
        <p class="text-gray-600">등록된 카테고리가 없습니다.</p>
      </div>

      <table v-else class="w-full">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">코드</th>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">카테고리명</th>
          <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">상위 카테고리</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">정렬 순서</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">하위 카테고리</th>
          <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">관리</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
        <tr v-for="category in categories" :key="category.id">
          <td class="px-6 py-4">{{ category.code }}</td>
          <td class="px-6 py-4">{{ category.name }}</td>
          <td class="px-6 py-4">{{ category.parentName || '-' }}</td>
          <td class="px-6 py-4 text-center">{{ category.orderNum }}</td>
          <td class="px-6 py-4 text-center">
            <button
                v-if="!category.parentId"
                @click="loadSubcategories(category.id)"
                class="text-blue-600 hover:text-blue-800"
            >
              보기
            </button>
            <span v-else>-</span>
          </td>
          <td class="px-6 py-4">
            <div class="flex justify-center gap-2">
              <button
                  @click="editCategory(category)"
                  class="p-1 text-blue-600 hover:bg-blue-50 rounded"
              >
                <PencilIcon class="w-4 h-4" />
              </button>
              <button
                  @click="confirmDeleteCategory(category)"
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
          총 {{ totalCount }}개 카테고리
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

    <!-- 하위 카테고리 목록 -->
    <div v-if="subcategories.length > 0" class="mt-8">
      <h2 class="text-xl font-semibold mb-4">{{ selectedParentName }} 하위 카테고리</h2>
      <div class="bg-white rounded-lg shadow overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">코드</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">카테고리명</th>
            <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">정렬 순서</th>
            <th class="px-6 py-3 text-center text-sm font-medium text-gray-500">관리</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="subcategory in subcategories" :key="subcategory.id">
            <td class="px-6 py-4">{{ subcategory.code }}</td>
            <td class="px-6 py-4">{{ subcategory.name }}</td>
            <td class="px-6 py-4 text-center">{{ subcategory.orderNum }}</td>
            <td class="px-6 py-4">
              <div class="flex justify-center gap-2">
                <button
                    @click="editCategory(subcategory)"
                    class="p-1 text-blue-600 hover:bg-blue-50 rounded"
                >
                  <PencilIcon class="w-4 h-4" />
                </button>
                <button
                    @click="confirmDeleteCategory(subcategory)"
                    class="p-1 text-red-600 hover:bg-red-50 rounded"
                >
                  <TrashIcon class="w-4 h-4" />
                </button>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 카테고리 모달 -->
    <CategoryModal
        v-model:show="showCategoryModal"
        :category="editingCategory"
        :categories="parentCategories"
        @submit="handleCategorySubmit"
    />

    <!-- 삭제 확인 모달 -->
    <BaseModal v-model="showDeleteModal" title="카테고리 삭제">
      <div class="p-4">
        <p>정말 "<span class="font-semibold">{{ categoryToDelete?.name }}</span>" 카테고리를 삭제하시겠습니까?</p>
        <p class="mt-2 text-sm text-gray-500">이 작업은 되돌릴 수 없습니다. 해당 카테고리에 속한 모든 상품도 함께 삭제됩니다.</p>

        <div class="flex justify-end gap-3 mt-6">
          <button
              @click="showDeleteModal = false"
              class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
          >
            취소
          </button>
          <button
              @click="deleteCategory"
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
import { PlusIcon, PencilIcon, TrashIcon } from 'lucide-vue-next';
import CategoryModal from '@/components/category/CategoryModal.vue';
import BaseModal from '@/components/base/BaseModal.vue';
import { useToast } from 'vue-toastification';
import axios from '@/plugins/axios';

const toast = useToast();

// 상태 관리
const isLoading = ref(false);
const categories = ref([]);
const subcategories = ref([]);
const selectedParentId = ref(null);
const selectedParentName = ref('');
const totalCount = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const showCategoryModal = ref(false);
const showDeleteModal = ref(false);
const editingCategory = ref(null);
const categoryToDelete = ref(null);

// 계산된 속성들
const totalPages = computed(() => Math.ceil(totalCount.value / pageSize.value));
const pageNumbers = computed(() => {
  if (totalPages.value <= 5) {
    return Array.from({ length: totalPages.value }, (_, i) => i + 1);
  }

  const current = currentPage.value;
  if (current <= 3) {
    return [1, 2, 3, 4, 5].slice(0, totalPages.value);
  }
  if (current >= totalPages.value - 2) {
    return Array.from({ length: 5 }, (_, i) => totalPages.value - 4 + i).filter(num => num > 0);
  }
  return [current - 2, current - 1, current, current + 1, current + 2];
});

const parentCategories = computed(() => {
  return categories.value.filter(cat => !cat.parentId);
});

// 메서드
const fetchCategories = async () => {
  try {
    isLoading.value = true;

    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    };

    const response = await axios.get('/categories', { params });

    if (response.data && response.data.data) {
      categories.value = response.data.data.content || [];
      totalCount.value = response.data.data.totalElements || 0;
    }
  } catch (error) {
    console.error('Failed to fetch categories:', error);
    toast.error('카테고리 목록을 불러오는데 실패했습니다.');
  } finally {
    isLoading.value = false;
  }
};

const loadSubcategories = async (parentId) => {
  try {
    const parent = categories.value.find(cat => cat.id === parentId);
    if (!parent) return;

    selectedParentId.value = parentId;
    selectedParentName.value = parent.name;

    const response = await axios.get(`/categories/${parentId}/subcategories`);

    if (response.data && response.data.data) {
      subcategories.value = response.data.data || [];
    }
  } catch (error) {
    console.error('Failed to fetch subcategories:', error);
    toast.error('하위 카테고리를 불러오는데 실패했습니다.');
  }
};

const openCategoryModal = () => {
  editingCategory.value = null;
  showCategoryModal.value = true;
};

const editCategory = (category) => {
  editingCategory.value = {...category};
  showCategoryModal.value = true;
};

const confirmDeleteCategory = (category) => {
  categoryToDelete.value = category;
  showDeleteModal.value = true;
};

const deleteCategory = async () => {
  if (!categoryToDelete.value) return;

  try {
    await axios.delete(`/categories/${categoryToDelete.value.id}`);
    toast.success('카테고리가 성공적으로 삭제되었습니다.');
    await fetchCategories();

    // 삭제한 카테고리가 하위 카테고리 목록에 있었다면 해당 목록도 갱신
    if (selectedParentId.value && categoryToDelete.value.parentId === selectedParentId.value) {
      await loadSubcategories(selectedParentId.value);
    }

    showDeleteModal.value = false;
    categoryToDelete.value = null;
  } catch (error) {
    console.error('Failed to delete category:', error);

    if (error.response && error.response.status === 400) {
      toast.error('카테고리에 상품이 존재하여 삭제할 수 없습니다.');
    } else {
      toast.error('카테고리 삭제에 실패했습니다.');
    }
  }
};

const handleCategorySubmit = async (categoryData) => {
  try {
    if (editingCategory.value) {
      // 카테고리 수정
      await axios.put(`/categories/${editingCategory.value.id}`, { data: categoryData });
      toast.success('카테고리가 성공적으로 수정되었습니다.');
    } else {
      // 새 카테고리 등록
      await axios.post('/categories', { data: categoryData });
      toast.success('카테고리가 성공적으로 등록되었습니다.');
    }

    // 데이터 갱신
    await fetchCategories();

    // 선택된 부모 카테고리가 있으면 하위 카테고리 목록도 갱신
    if (selectedParentId.value) {
      await loadSubcategories(selectedParentId.value);
    }

    showCategoryModal.value = false;
  } catch (error) {
    console.error('Failed to save category:', error);

    if (error.response && error.response.data) {
      const errorMessage = error.response.data.message || '카테고리 저장에 실패했습니다.';
      toast.error(errorMessage);
    } else {
      toast.error('카테고리 저장에 실패했습니다.');
    }
  }
};

// 페이지 변경 감지
watch(currentPage, () => {
  fetchCategories();
});

// 초기 데이터 로드
onMounted(() => {
  fetchCategories();
});
</script>