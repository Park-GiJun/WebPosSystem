<!-- components/recipe/RecipeModal.vue -->
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
              <DialogTitle class="text-lg font-bold mb-4">
                {{ recipe ? '레시피 수정' : '레시피 등록' }}
              </DialogTitle>

              <form @submit.prevent="handleSubmit" class="space-y-4">
                <!-- 기본 정보 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    레시피명
                    <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="text"
                      v-model="form.name"
                      required
                      maxlength="100"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      :class="{'border-red-500': errors.name}"
                  />
                  <p v-if="errors.name" class="mt-1 text-sm text-red-500">
                    {{ errors.name }}
                  </p>
                </div>

                <!-- 카테고리 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    카테고리
                    <span class="text-red-500">*</span>
                  </label>
                  <select
                      v-model="form.categoryId"
                      required
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      :class="{'border-red-500': errors.categoryId}"
                  >
                    <option value="">카테고리 선택</option>
                    <option
                        v-for="category in categories"
                        :key="category.id"
                        :value="category.id"
                    >
                      {{ category.name }}
                    </option>
                  </select>
                  <p v-if="errors.categoryId" class="mt-1 text-sm text-red-500">
                    {{ errors.categoryId }}
                  </p>
                </div>

                <!-- 재료 목록 -->
                <div>
                  <div class="flex justify-between items-center mb-2">
                    <label class="block text-sm font-medium text-gray-700">
                      재료
                      <span class="text-red-500">*</span>
                    </label>
                    <button
                        type="button"
                        @click="showIngredientModal = true"
                        class="px-3 py-1 text-sm bg-gray-100 rounded hover:bg-gray-200"
                    >
                      + 재료 추가
                    </button>
                  </div>

                  <div v-if="form.ingredients.length === 0" class="p-4 bg-gray-50 rounded-lg text-center text-gray-500">
                    재료를 추가해주세요
                  </div>

                  <div v-else class="space-y-2">
                    <div
                        v-for="(ingredient, index) in form.ingredients"
                        :key="index"
                        class="flex gap-2 items-start p-3 bg-gray-50 rounded-lg"
                    >
                      <div class="flex-1">
                        <p class="font-medium">{{ ingredient.name }}</p>
                        <div class="flex gap-2 mt-1">
                          <input
                              type="number"
                              v-model.number="ingredient.amount"
                              placeholder="수량"
                              required
                              min="0"
                              step="0.1"
                              class="w-24 px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                          />
                          <select
                              v-model="ingredient.unit"
                              required
                              class="w-24 px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                          >
                            <option value="g">g</option>
                            <option value="ml">ml</option>
                            <option value="개">개</option>
                            <option value="컵">컵</option>
                            <option value="oz">oz</option>
                            <option value="tbsp">tbsp</option>
                            <option value="tsp">tsp</option>
                          </select>
                        </div>
                      </div>
                      <button
                          type="button"
                          @click="removeIngredient(index)"
                          class="p-2 text-red-600 hover:bg-red-50 rounded"
                      >
                        <XIcon class="w-5 h-5" />
                      </button>
                    </div>
                  </div>
                  <p v-if="errors.ingredients" class="mt-1 text-sm text-red-500">
                    {{ errors.ingredients }}
                  </p>
                </div>

                <!-- 가격 정보 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    판매가
                    <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="number"
                      v-model.number="form.price"
                      required
                      min="0"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      :class="{'border-red-500': errors.price}"
                  />
                  <p v-if="errors.price" class="mt-1 text-sm text-red-500">
                    {{ errors.price }}
                  </p>
                </div>

                <!-- 조리 시간 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    조리 시간 (분)
                    <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="number"
                      v-model.number="form.cookingTime"
                      required
                      min="1"
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      :class="{'border-red-500': errors.cookingTime}"
                  />
                  <p v-if="errors.cookingTime" class="mt-1 text-sm text-red-500">
                    {{ errors.cookingTime }}
                  </p>
                </div>

                <!-- 조리 방법 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    조리 방법
                    <span class="text-red-500">*</span>
                  </label>
                  <textarea
                      v-model="form.instructions"
                      rows="4"
                      required
                      class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      :class="{'border-red-500': errors.instructions}"
                  ></textarea>
                  <p v-if="errors.instructions" class="mt-1 text-sm text-red-500">
                    {{ errors.instructions }}
                  </p>
                </div>

                <!-- 상태 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    상태
                  </label>
                  <div class="flex gap-4">
                    <label class="flex items-center">
                      <input
                          type="radio"
                          v-model="form.status"
                          value="ACTIVE"
                          class="mr-2"
                      />
                      <span>활성</span>
                    </label>
                    <label class="flex items-center">
                      <input
                          type="radio"
                          v-model="form.status"
                          value="INACTIVE"
                          class="mr-2"
                      />
                      <span>비활성</span>
                    </label>
                  </div>
                </div>

                <!-- 버튼 영역 -->
                <div class="flex justify-end gap-3 mt-6">
                  <button
                      type="button"
                      @click="onClose"
                      class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200"
                  >
                    취소
                  </button>
                  <button
                      type="submit"
                      :disabled="isSubmitting"
                      class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:opacity-50"
                  >
                    {{ isSubmitting ? '저장 중...' : '저장' }}
                  </button>
                </div>
              </form>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>

  <!-- Ingredient Selection Modal -->
  <TransitionRoot appear :show="showIngredientModal" as="template">
    <Dialog as="div" @close="showIngredientModal = false" class="relative z-50">
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
                    @click="showIngredientModal = false"
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
                      v-model="ingredientSearchKeyword"
                      placeholder="재료명 검색..."
                      class="w-full px-4 py-2 pr-10 border rounded-lg"
                  />
                  <SearchIcon class="w-5 h-5 text-gray-400 absolute right-3 top-2.5" />
                </div>
              </div>

              <div v-if="isLoadingIngredients" class="p-4 text-center">
                <div class="flex justify-center">
                  <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
                </div>
                <p class="mt-2 text-gray-600">재료 목록을 불러오는 중...</p>
              </div>

              <!-- 재료 목록 -->
              <div v-else class="h-72 overflow-y-auto border rounded-lg">
                <table class="w-full">
                  <thead class="bg-gray-50 sticky top-0">
                  <tr>
                    <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">재료명</th>
                    <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">기본 단위</th>
                    <th class="px-4 py-3 text-center text-sm font-medium text-gray-500">선택</th>
                  </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-200">
                  <tr v-for="ingredient in filteredIngredients" :key="ingredient.id">
                    <td class="px-4 py-3">{{ ingredient.name }}</td>
                    <td class="px-4 py-3">{{ ingredient.unit }}</td>
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
                        v-model.number="ingredientAmount"
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
                        v-model="ingredientUnit"
                        class="w-full px-4 py-2 border rounded-lg"
                    >
                      <option :value="selectedIngredient.unit">
                        {{ selectedIngredient.unit }}
                      </option>
                      <option value="g">g</option>
                      <option value="ml">ml</option>
                      <option value="개">개</option>
                      <option value="컵">컵</option>
                      <option value="oz">oz</option>
                      <option value="tbsp">tbsp</option>
                      <option value="tsp">tsp</option>
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
  show: Boolean,
  recipe: {
    type: Object,
    default: null
  },
  categories: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['close', 'submit']);
const toast = useToast();

// 폼 상태
const form = ref({
  name: '',
  categoryId: '',
  ingredients: [],
  price: 0,
  cookingTime: 0,
  instructions: '',
  status: 'ACTIVE'
});

const errors = ref({});
const isSubmitting = ref(false);

// 재료 선택 모달 상태
const showIngredientModal = ref(false);
const isLoadingIngredients = ref(false);
const ingredientList = ref([]);
const ingredientSearchKeyword = ref('');
const selectedIngredient = ref(null);
const ingredientAmount = ref(1);
const ingredientUnit = ref('');

// 재료 목록 필터링
const filteredIngredients = computed(() => {
  if (!ingredientSearchKeyword.value) return ingredientList.value;

  const keyword = ingredientSearchKeyword.value.toLowerCase();
  return ingredientList.value.filter(ing =>
      ing.name.toLowerCase().includes(keyword)
  );
});

// 레시피 폼 초기화
watch(() => props.recipe, (recipe) => {
  if (recipe) {
    form.value = {
      name: recipe.name,
      categoryId: recipe.categoryId,
      ingredients: [...recipe.ingredients],
      price: recipe.price,
      cookingTime: recipe.cookingTime,
      instructions: recipe.instructions,
      status: recipe.status || 'ACTIVE'
    };
  } else {
    form.value = {
      name: '',
      categoryId: '',
      ingredients: [],
      price: 0,
      cookingTime: 0,
      instructions: '',
      status: 'ACTIVE'
    };
  }
}, { immediate: true });

// 재료 관련 메서드
const fetchIngredients = async () => {
  try {
    isLoadingIngredients.value = true;
    // API 호출하여 원재료(RAW_MATERIAL) 목록 가져오기
    const response = await axios.get('/products/raw-materials');
    ingredientList.value = response.data.data || [];
  } catch (error) {
    toast.error('재료 목록을 불러오는데 실패했습니다.');
    console.error('Error fetching ingredients:', error);
  } finally {
    isLoadingIngredients.value = false;
  }
};

const selectIngredient = (ingredient) => {
  selectedIngredient.value = ingredient;
  ingredientUnit.value = ingredient.unit || 'g';
  ingredientAmount.value = 1;
};

const addIngredient = () => {
  if (!selectedIngredient.value || ingredientAmount.value <= 0) {
    toast.error('유효한 수량을 입력해주세요.');
    return;
  }

  // 이미 추가된 재료인지 확인
  const existingIndex = form.value.ingredients.findIndex(
      item => item.id === selectedIngredient.value.id
  );

  if (existingIndex !== -1) {
    // 이미 있는 재료면 수량 업데이트
    form.value.ingredients[existingIndex].amount = ingredientAmount.value;
    form.value.ingredients[existingIndex].unit = ingredientUnit.value;
    toast.info(`${selectedIngredient.value.name} 수량이 업데이트되었습니다.`);
  } else {
    // 새 재료 추가
    form.value.ingredients.push({
      id: selectedIngredient.value.id,
      name: selectedIngredient.value.name,
      amount: ingredientAmount.value,
      unit: ingredientUnit.value
    });
    toast.success(`${selectedIngredient.value.name}이(가) 추가되었습니다.`);
  }

  // 재료 선택 상태 초기화
  selectedIngredient.value = null;
  showIngredientModal.value = false;
};

const removeIngredient = (index) => {
  const ingredientName = form.value.ingredients[index].name;
  form.value.ingredients.splice(index, 1);
  toast.info(`${ingredientName}이(가) 제거되었습니다.`);
};

// 폼 유효성 검사
const validateForm = () => {
  errors.value = {};

  if (!form.value.name) {
    errors.value.name = '레시피명은 필수입니다.';
  }

  if (!form.value.categoryId) {
    errors.value.categoryId = '카테고리는 필수입니다.';
  }

  if (!form.value.ingredients.length) {
    errors.value.ingredients = '최소 1개 이상의 재료가 필요합니다.';
  }

  if (form.value.price < 0) {
    errors.value.price = '가격은 0 이상이어야 합니다.';
  }

  if (form.value.cookingTime <= 0) {
    errors.value.cookingTime = '조리 시간은 1분 이상이어야 합니다.';
  }

  if (!form.value.instructions) {
    errors.value.instructions = '조리 방법은 필수입니다.';
  }

  return Object.keys(errors.value).length === 0;
};

// 폼 제출
const handleSubmit = async () => {
  if (!validateForm()) return;

  try {
    isSubmitting.value = true;

    const data = {
      ...form.value,
      categoryId: Number(form.value.categoryId),
      price: Number(form.value.price),
      cookingTime: Number(form.value.cookingTime),
      ingredients: form.value.ingredients.map(ing => ({
        ingredientId: ing.id,
        quantity: Number(ing.amount),
        unit: ing.unit
      }))
    };

    emit('submit', data);
  } catch (error) {
    console.error('Form submission error:', error);
    toast.error('레시피 저장에 실패했습니다.');
  } finally {
    isSubmitting.value = false;
  }
};

const onClose = () => {
  emit('close');
};

// 모달이 열릴 때 재료 목록 로드
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