<!-- SystemSelect.vue -->
<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center">
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8 p-6">
      <button
          @click="navigateToSIS"
          class="bg-white p-8 rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 flex flex-col items-center gap-4 w-72"
      >
        <Store class="text-blue-600 w-12 h-12" />
        <div class="text-center">
          <h2 class="text-xl font-bold mb-2">영업정보시스템</h2>
          <p class="text-gray-600">매출 관리 및 영업 분석</p>
        </div>
      </button>

      <button
          @click="navigateToPOS"
          class="bg-white p-8 rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 flex flex-col items-center gap-4 w-72"
      >
        <ShoppingCart class="text-green-600 w-12 h-12" />
        <div class="text-center">
          <h2 class="text-xl font-bold mb-2">POS 시스템</h2>
          <p class="text-gray-600">판매 및 결제 관리</p>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup>
import { Store, ShoppingCart } from 'lucide-vue-next';
import { useRouter } from 'vue-router';

const router = useRouter();

const navigateToSIS = () => router.push('/sis');
const navigateToPOS = () => router.push('/pos');
</script>
