// components/pos/ProductGrid.vue
<template>
  <div class="flex-1 p-4 flex flex-col">
    <!-- Categories -->
    <div class="flex-none mb-4">
      <div class="flex gap-2 overflow-x-auto">
        <button
            v-for="cat in categories"
            :key="cat.id"
            @click="selectedCategory = cat.id"
            :class="[
            'px-6 py-3 rounded-lg whitespace-nowrap font-medium',
            selectedCategory === cat.id ? 'bg-blue-600 text-white' : 'bg-white hover:bg-gray-50'
          ]"
        >
          {{ cat.name }}
        </button>
      </div>
    </div>

    <!-- Products Grid -->
    <div class="grid grid-cols-4 gap-4 overflow-auto flex-1">
      <button
          v-for="product in filteredProducts"
          :key="product.id"
          @click="addToOrder(product)"
          class="bg-white h-32 p-4 rounded-lg shadow hover:shadow-md transition-shadow flex flex-col justify-center items-center"
      >
        <h3 class="font-medium text-center">{{ product.name }}</h3>
        <p class="text-blue-600 mt-2">₩{{ product.price.toLocaleString() }}</p>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useToast } from 'vue-toastification'

const props = defineProps({
  categories: {
    type: Array,
    required: true
  },
  products: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['addItem'])
const toast = useToast()

const selectedCategory = ref(props.categories[0].id)

const filteredProducts = computed(() => {
  return props.products.filter(product => product.categoryId === selectedCategory.value)
})

const addToOrder = (product) => {
  emit('addItem', product)
  toast.success(`${product.name} 추가됨`)
}
</script>