<!-- views/InventoryView.vue -->
<template>
  <div class="p-6">
    <div class="bg-white p-6 rounded-lg shadow">
      <div class="flex justify-between mb-4">
        <h3 class="text-lg font-semibold">재고 관리</h3>
        <div class="flex gap-2">
          <button class="px-4 py-2 bg-yellow-500 text-white rounded hover:bg-yellow-600">
            발주 요청
          </button>
          <button class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
            재고 등록
          </button>
        </div>
      </div>
      <table class="w-full">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상품명</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">카테고리</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">현재 재고</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">적정 재고</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상태</th>
        </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
        <tr v-for="item in inventory" :key="item.id">
          <td class="px-6 py-4 whitespace-nowrap">{{ item.name }}</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ item.category }}</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ item.current }}</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ item.optimal }}</td>
          <td class="px-6 py-4 whitespace-nowrap">
              <span :class="[
                'px-2 py-1 rounded text-sm',
                getStatusClass(item.status)
              ]">
                {{ item.status }}
              </span>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>