<!-- components/recipe/IngredientsModal.vue -->
<template>
  <TransitionRoot appear :show="show" as="template">
    <Dialog as="div" @close="$emit('close')" class="relative z-50">
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
            <DialogPanel class="w-full max-w-xl transform overflow-hidden rounded-2xl bg-white p-6 shadow-xl">
              <div class="flex justify-between items-center mb-4">
                <DialogTitle class="text-lg font-bold">재료 선택</DialogTitle>
                <button
                    type="button"
                    @click="$emit('close')"
                    class="text-gray-400 hover:text-gray-500"
                >
                  <XIcon class="w-6 h-6" />
                </button>
              </div>

              <!-- 검색 필터 -->
              <div class="mb-4">
                <div class="relative">
                  <input
                      type="text"
                      v-model="searchKeyword"
                      placeholder="재료명 검색..."
                      class="w-full px-4 py-2 pr-10 border rounded-lg"
                  />
                  <SearchIcon class="w-5 h-5 text-gray-400 absolute right-3 top-2.5" />
                </div>
              </div>

              <!-- 로딩 상태 -->
              <div v-if="isLoading" class="p-4 text-center">
                <div class="flex justify-center">
                  <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
                </div>
                <p class="mt-2 text-gray-600">재료 목록을 불러오는 중...</p>
              </div>

              <!-- 재료 목록 -->
              <div v-else-if="filteredIngredients.length === 0" class="p-4 text-center text-gray-500">
                일치하는 재료가 없습니다.
              </div>

              <div v-else class="h-72 overflow-y-auto border rounded-lg">
                <table class="w-full">
                  <thead class="bg-gray-50 sticky top-0">
                  <tr>
                    <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">재료명</th>
                    <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">기본 단위</th>
                    <th class="px-4 py-3 text-right text-sm font-medium text-gray-500">단가</th>
                    <th class="px-4 py-3 text-center text-sm font-medium text-gray-500">선택</th>
                  </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-200">
                  <tr v-for="ingredient in filteredIngredients" :key="ingredient.id">
                    <td class="px-4 py-3">{{ ingredient.name }}</td>
                    <td class="px-4 py-3">{{ ingredient.unit }}</td>
                    <td class="px-4 py-3 text-right">
                      {{ formatPrice(ingredient.unitPrice) }}
                    </td>
                    <td class="px-4 py-3 text-center">
                      <button
                          @click="$emit('select', ingredient)"
                          class="px-3 py-1 text-sm bg-blue-100 text-blue-600 rounded-full hover:bg-blue-200"
                      >
                        선택
                      </button>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue';
import { XIcon, SearchIcon } from 'lucide-vue-next';
import axios from '@/plugins/axios';
import { useToast } from 'vue-toastification';

const props = defineProps({
  show: Boolean
});

const emit = defineEmits(['close', 'select']);
const toast = useToast();

const searchKeyword = ref('');
const isLoading = ref(false);
const ingredients = ref([]);

// 필터링된 재료 목록
const filteredIngredients = computed(() => {
  const keyword = searchKeyword.value.toLowerCase().trim();
  return ingredients.value.filter(ing =>
      ing.name.toLowerCase().includes(keyword)
  );
});

// 재료 목록 불러오기
const fetchIngredients = async () => {
  try {
    isLoading.value = true;
    const response = await axios.get('/products/raw-materials');
    ingredients.value = response.data.data || [];
  } catch (error) {
    console.error('Failed to fetch ingredients:', error);
    toast.error('재료 목록을 불러오는데 실패했습니다.');
  } finally {
    isLoading.value = false;
  }
};

// 가격 포맷팅
const formatPrice = (price) => {
  return price ? `₩${Number(price).toLocaleString()}` : '-';
};

// 모달 오픈 시 재료 목록 로드
watch(() => props.show, (show) => {
  if (show) {
    fetchIngredients();
  }
});

// 초기 로드
onMounted(() => {
  if (props.show) {
    fetchIngredients();
  }
});
</script>