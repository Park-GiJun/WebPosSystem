<!-- views/EmployeeView.vue -->
<template>
  <div class="p-6">
    <div class="bg-white p-6 rounded-lg shadow">
      <div class="flex justify-between mb-4">
        <h3 class="text-lg font-semibold">직원 관리</h3>
        <button class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
          직원 등록
        </button>
      </div>
      <table class="w-full">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">이름</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">직급</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">매장</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">연락처</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상태</th>
        </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
        <tr v-for="employee in employees" :key="employee.id">
          <td class="px-6 py-4 whitespace-nowrap">{{ employee.name }}</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ employee.position }}</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ employee.store }}</td>
          <td class="px-6 py-4 whitespace-nowrap">{{ employee.phone }}</td>
          <td class="px-6 py-4 whitespace-nowrap">
              <span :class="[
                'px-2 py-1 rounded text-sm',
                employee.status === '근무중' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
              ]">
                {{ employee.status }}
              </span>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
const employees = [
  { id: 1, name: '김직원', position: '매니저', store: '1호점', phone: '010-1234-5678', status: '근무중' },
  { id: 2, name: '이직원', position: '바리스타', store: '2호점', phone: '010-2345-6789', status: '근무중' },
  { id: 3, name: '박직원', position: '바리스타', store: '3호점', phone: '010-3456-7890', status: '퇴근' }
];
</script>
