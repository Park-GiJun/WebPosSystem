<!-- views/ShipmentView.vue -->
<template>
  <div class="p-6">
    <div class="bg-white p-6 rounded-lg shadow">
      <div class="flex justify-between mb-4">
        <h3 class="text-lg font-semibold">출고 관리</h3>
        <button class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
          출고 등록
        </button>
      </div>
      <table class="w-full">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">번호</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">매장</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">품목</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">수량</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상태</th>
        </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
        <tr v-for="shipment in shipments" :key="shipment.id">
          <td class="px-6 py-4 whitespace-nowrap">{{ shipment.id }}</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ shipment.store }}</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ shipment.item }}</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ shipment.quantity }}</td>
          <td class="px-6 py-4 whitespace-nowrap">
              <span :class="[
                'px-2 py-1 rounded text-sm',
                shipment.status === '완료' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
              ]">
                {{ shipment.status }}
              </span>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
const shipments = [
  { id: 1, store: '1호점', item: '원두(아라비카)', quantity: '10kg', status: '완료' },
  { id: 2, store: '2호점', item: '원두(로부스타)', quantity: '5kg', status: '진행중' },
  { id: 3, store: '3호점', item: '시럽(바닐라)', quantity: '10개', status: '완료' }
];
</script>