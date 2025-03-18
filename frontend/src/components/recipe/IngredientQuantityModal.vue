<!-- components/recipe/IngredientQuantityModal.vue -->
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
            <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 shadow-xl">
              <DialogTitle class="text-lg font-bold mb-4">
                {{ ingredient.name }} 재료 정보
              </DialogTitle>

              <div class="space-y-4">
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
                        placeholder="수량 입력"
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
                      <option :value="ingredient.unit">
                        {{ ingredient.unit }}
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

                <div v-if="ingredient.unitPrice" class="text-sm text-gray-600">
                  단가: {{ formatPrice(ingredient.unitPrice) }}
                  <span v-if="amount">
                    | 예상 비용: {{ formatPrice(ingredient.unitPrice * amount) }}
                  </span>
                </div>

                <div class="flex justify-end gap-3 mt-6">
                  <button
                      type="button"
                      @click="onClose"
                      class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200"
                  >
                    취소
                  </button>
                  <button
                      @click="addIngredient"
                      :disabled="!isValidInput"
                      class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
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
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue';

const props = defineProps({
  show: Boolean,
  ingredient: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['close', 'add']);

const amount = ref(1);
const unit = ref(props.ingredient.unit);

// 입력 유효성 검사
const isValidInput = computed(() =>
    amount.value > 0 && unit.value
);

// 가격 포맷팅
const formatPrice = (price) => {
  return price ? `₩${Number(price).toLocaleString()}` : '-';
};

// 재료 추가
const addIngredient = () => {
  if (!isValidInput.value) return;

  emit('add', {
    id: props.ingredient.id,
    name: props.ingredient.name,
    amount: amount.value,
    unit: unit.value
  });

  // onClose();
};

// 모달 닫기
const onClose = () => {
  emit('close');
  amount.value = 1;
  unit.value = props.ingredient.unit;
};
</script>