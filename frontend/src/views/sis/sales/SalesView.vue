<!-- views/SalesView.vue -->
<template>
  <div class="p-6">
    <div class="bg-white p-6 rounded-lg shadow mb-6">
      <div class="flex justify-between mb-4">
        <h3 class="text-lg font-semibold">매출 통계</h3>
        <div class="flex gap-2">
          <button class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
            엑셀 다운로드
          </button>
          <button class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600">
            신규 등록
          </button>
        </div>
      </div>
      <table class="w-full">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">날짜</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">매장</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">매출</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">비고</th>
        </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
        <tr v-for="sale in sales" :key="sale.id">
          <td class="px-6 py-4 whitespace-nowrap">{{ sale.date }}</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ sale.store }}</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ sale.amount.toLocaleString() }}원</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ sale.note }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
const sales = [
  { id: 1, date: '2024-01-25', store: '1호점', amount: 1250000, note: '정상' },
  { id: 2, date: '2024-01-25', store: '2호점', amount: 980000, note: '정상' },
  { id: 3, date: '2024-01-25', store: '3호점', amount: 1580000, note: '정상' }
];
</script>
