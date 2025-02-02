// components/pos/PosHeader.vue
<template>
  <header class="flex-none bg-white shadow-sm p-4 flex justify-between items-center">
    <div class="flex items-center gap-4">
      <button @click="$emit('toggleMenu')" class="text-gray-600">
        <Menu class="w-6 h-6" />
      </button>
      <span class="font-bold">매장명: {{ storeName }}</span>
    </div>
    <div class="flex items-center gap-4">
      <span>{{ currentTime }}</span>
      <span>{{ cashierName }}</span>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { Menu } from 'lucide-vue-next'

defineProps({
  storeName: String,
  cashierName: String,
  currentTime: String
})

defineEmits(['toggleMenu'])
</script>