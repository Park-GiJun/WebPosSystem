<!-- EmployeeListView.vue -->
<template>
  <div class="p-6 space-y-6">
    <!-- 직원 검색/필터 -->
    <div class="bg-white p-4 rounded-lg shadow flex items-center justify-between">
      <div class="flex gap-4 flex-1">
        <input
            type="text"
            v-model="search"
            placeholder="이름 또는 직원번호 검색"
            class="w-64 rounded-md border-gray-300"
        >
        <select v-model="filters.department" class="rounded-md border-gray-300">
          <option value="">전체 부서</option>
          <option v-for="dept in departments" :key="dept">{{ dept }}</option>
        </select>
        <select v-model="filters.position" class="rounded-md border-gray-300">
          <option value="">전체 직급</option>
          <option v-for="pos in positions" :key="pos">{{ pos }}</option>
        </select>
      </div>
      <button class="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
        직원 등록
      </button>
    </div>

    <!-- 직원 목록 테이블 -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead>
        <tr>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            직원번호
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            이름
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            부서
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            직급
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            입사일
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            상태
          </th>
          <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
            관리
          </th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
        <tr v-for="employee in employees" :key="employee.id">
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
            {{ employee.employeeId }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex items-center">
              <div class="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center">
                {{ employee.name[0] }}
              </div>
              <div class="ml-4">
                <div class="text-sm font-medium text-gray-900">{{ employee.name }}</div>
                <div class="text-sm text-gray-500">{{ employee.email }}</div>
              </div>
            </div>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{ employee.department }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{ employee.position }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{ employee.joinDate }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
             <span :class="getStatusClass(employee.status)" class="px-2 py-1 text-xs rounded-full">
               {{ employee.status }}
             </span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
            <button class="text-blue-600 hover:text-blue-900 mr-3">상세</button>
            <button class="text-red-600 hover:text-red-900">삭제</button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const search = ref('')
const filters = ref({
  department: '',
  position: ''
})

const departments = ['영업부', '마케팅부', 'IT부서', '인사부']
const positions = ['사원', '대리', '과장', '차장', '부장']

const employees = ref([
  {
    id: 1,
    employeeId: 'EMP001',
    name: '홍길동',
    email: 'hong@example.com',
    department: '영업부',
    position: '과장',
    joinDate: '2020-03-15',
    status: '재직'
  },
  {
    id: 2,
    employeeId: 'EMP002',
    name: '김영희',
    email: 'kim@example.com',
    department: 'IT부서',
    position: '대리',
    joinDate: '2021-06-01',
    status: '재직'
  },
  {
    id: 3,
    employeeId: 'EMP003',
    name: '이철수',
    email: 'lee@example.com',
    department: '마케팅부',
    position: '사원',
    joinDate: '2022-01-10',
    status: '휴직'
  }
])

const getStatusClass = (status) => {
  return {
    '재직': 'bg-green-100 text-green-800',
    '휴직': 'bg-yellow-100 text-yellow-800',
    '퇴사': 'bg-red-100 text-red-800'
  }[status]
}
</script>