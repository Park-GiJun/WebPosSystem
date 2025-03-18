<template>
  <TransitionRoot appear :show="show" as="template">
    <Dialog as="div" @close="onClose" class="relative z-50">
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
                    @click="onClose"
                    class="text-gray-400 hover:text-gray-500"
                >
                  <XIcon class="h-6 w-6" />
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

              <!-- 재료 목록 -->
              <div class="h-96 overflow-y-auto border rounded-lg">
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
                          @click="selectIngredient(ingredient)"
                          class="px-3 py-1 text-sm bg-blue-100 text-blue-600 rounded-full hover:bg-blue-200"
                      >
                        선택
                      </button>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>

              <!-- 선택된 재료 입력 -->
              <div v-if="selectedIngredient" class="mt-4 p-4 bg-gray-50 rounded-lg">
                <h3 class="font-medium mb-3">재료 정보 입력</h3>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      수량
                    </label>
                    <input
                        type="number"
                        v-model.number="amount"
                        min="0.1"
                        step="0.1"
                        class="w-full px-4 py-2 border rounded-lg"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                      단위
                    </label>
                    <select
                        v-model="unit"
                        class="w-full px-4 py-2 border rounded-lg"
                    >
                      <option :value="selectedIngredient.unit">
                        {{ selectedIngredient.unit }}
                      </option>
                      <!-- 추가 단위 변환 옵션들 -->
                    </select>
                  </div>
                </div>
                <div class="flex justify-end mt-4">
                  <button
                      @click="addIngredient"
                      class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                  >
                    추가
                  </button>
                </div>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Dialog, DialogPanel, DialogTitle, TransitionRoot, TransitionChild } from '@headlessui/vue';
import { XIcon, SearchIcon } from 'lucide-vue-next';

const props = defineProps({
  show: Boolean,
  ingredients: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['close', 'select']);

const searchKeyword = ref('');
const selectedIngredient = ref(null);
const amount = ref(1);
const unit = ref('');

// 검색 필터 적용
const filteredIngredients = computed(() => {
  if (!searchKeyword.value) return props.ingredients;
  const keyword = searchKeyword.value.toLowerCase();
  return props.ingredients.filter(ing =>
      ing.name.toLowerCase().includes(keyword)
  );
});

// 재료 선택
const selectIngredient = (ingredient) => {
  selectedIngredient.value = ingredient;
  unit.value = ingredient.unit;
};

// 재료 추가 후 `close` 호출하지 않음
const addIngredient = () => {
  if (!selectedIngredient.value || amount.value <= 0) return;

  emit('select', {
    id: selectedIngredient.value.id,
    name: selectedIngredient.value.name,
    amount: amount.value,
    unit: unit.value,
    unitPrice: selectedIngredient.value.unitPrice
  });

  resetForm(); // 모달은 닫지 않고 폼만 초기화
};

// 폼 초기화
const resetForm = () => {
  selectedIngredient.value = null;
  amount.value = 1;
  unit.value = '';
};

// 모달 닫기 (부모가 직접 닫도록 수정)
const onClose = () => {
  resetForm();
  emit('close');
};

// 가격 포맷팅
const formatPrice = (price) => {
  return `₩${price.toLocaleString()}`;
};
</script>
