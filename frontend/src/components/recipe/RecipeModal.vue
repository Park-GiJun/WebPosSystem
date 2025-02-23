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
                        @click="addIngredient"
                        class="px-3 py-1 text-sm bg-gray-100 rounded hover:bg-gray-200"
                    >
                      + 재료 추가
                    </button>
                  </div>

                  <div class="space-y-2">
                    <div
                        v-for="(ingredient, index) in form.ingredients"
                        :key="index"
                        class="flex gap-2 items-start"
                    >
                      <input
                          type="text"
                          v-model="ingredient.name"
                          placeholder="재료명"
                          required
                          class="flex-1 px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      />
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
</template>

<script setup>
import { ref, watch } from 'vue';
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue';
import { XIcon } from 'lucide-vue-next';

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

// Form state
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

watch(() => props.recipe, (recipe) => {
  if (recipe) {
    form.value = {
      name: recipe.name,
      categoryId: recipe.categoryId,
      ingredients: [...recipe.ingredients],
      price: recipe.price,
      cookingTime: recipe.cookingTime,
      instructions: recipe.instructions,
      status: recipe.status
    };
  } else {
    form.value = {
      name: '',
      categoryId: '',
      ingredients: [{ name: '', amount: 0, unit: 'g' }],
      price: 0,
      cookingTime: 0,
      instructions: '',
      status: 'ACTIVE'
    };
  }
}, { immediate: true });

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

  const hasEmptyIngredient = form.value.ingredients.some(
      ing => !ing.name || ing.amount <= 0
  );

  if (hasEmptyIngredient) {
    errors.value.ingredients = '모든 재료의 정보를 입력해주세요.';
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

const addIngredient = () => {
  form.value.ingredients.push({ name: '', amount: 0, unit: 'g' });
};

const removeIngredient = (index) => {
  if (form.value.ingredients.length > 1) {
    form.value.ingredients.splice(index, 1);
  }
};

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
        ...ing,
        amount: Number(ing.amount)
      }))
    };

    emit('submit', data);
  } catch (error) {
    console.error('Form submission error:', error);
  } finally {
    isSubmitting.value = false;
  }
};

const onClose = () => {
  emit('close');
};
</script>