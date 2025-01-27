<!-- views/sis/customers/CustomerListView.vue -->
<template>
  <div class="p-6">
    <div class="mb-6 flex justify-between items-center">
      <h2 class="text-2xl font-bold">고객 조회</h2>
      <button
          @click="showRegisterModal = true"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
      >
        고객 등록
      </button>
    </div>

    <!-- 검색 필터 -->
    <div class="bg-white p-6 rounded-lg shadow mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <input
            type="text"
            v-model="searchName"
            placeholder="고객명 검색"
            class="px-4 py-2 border rounded-lg"
        />
        <input
            type="text"
            v-model="searchPhone"
            placeholder="전화번호 검색"
            class="px-4 py-2 border rounded-lg"
        />
        <select v-model="membershipLevel" class="px-4 py-2 border rounded-lg">
          <option value="">전체 등급</option>
          <option value="BRONZE">브론즈</option>
          <option value="SILVER">실버</option>
          <option value="GOLD">골드</option>
          <option value="PLATINUM">플래티넘</option>
        </select>
        <button
            @click="searchCustomers"
            class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
        >
          검색
        </button>
      </div>
    </div>

    <!-- 고객 목록 테이블 -->
    <div class="bg-white rounded-lg shadow">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">고객명</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">전화번호</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">회원등급</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">보유 포인트</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">최근 방문일</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">누적 구매액</th>
            <th class="px-6 py-3 text-left text-sm font-medium text-gray-500">관리</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="customer in customers" :key="customer.id">
            <td class="px-6 py-4">{{ customer.name }}</td>
            <td class="px-6 py-4">{{ customer.phone }}</td>
            <td class="px-6 py-4">
                <span :class="[
                  'px-2 py-1 rounded-full text-xs',
                  customer.level === 'PLATINUM' ? 'bg-purple-100 text-purple-800' :
                  customer.level === 'GOLD' ? 'bg-yellow-100 text-yellow-800' :
                  customer.level === 'SILVER' ? 'bg-gray-100 text-gray-800' :
                  'bg-orange-100 text-orange-800'
                ]">
                  {{ customer.level }}
                </span>
            </td>
            <td class="px-6 py-4">{{ customer.points.toLocaleString() }} P</td>
            <td class="px-6 py-4">{{ formatDate(customer.lastVisit) }}</td>
            <td class="px-6 py-4">₩{{ customer.totalPurchase.toLocaleString() }}</td>
            <td class="px-6 py-4">
              <button
                  @click="editCustomer(customer)"
                  class="text-blue-600 hover:text-blue-800"
              >
                수정
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <div class="flex justify-between items-center px-6 py-4 border-t">
        <div class="text-sm text-gray-500">
          총 {{ totalCustomers }}명
        </div>
        <div class="flex gap-2">
          <button
              v-for="page in totalPages"
              :key="page"
              @click="currentPage = page"
              :class="[
              'px-3 py-1 rounded',
              currentPage === page ? 'bg-blue-600 text-white' : 'bg-gray-100'
            ]"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>

    <!-- 고객 등록/수정 모달 -->
    <CustomerRegisterModal
        v-if="showRegisterModal"
        :customer="selectedCustomer"
        @close="closeRegisterModal"
        @save="saveCustomer"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CustomerRegisterModal from '@/components/modals/CustomerRegisterModal.vue'

// State
const searchName = ref('')
const searchPhone = ref('')
const membershipLevel = ref('')
const currentPage = ref(1)
const showRegisterModal = ref(false)
const selectedCustomer = ref(null)

// Sample Data
const customers = ref([
  {
    id: 1,
    name: '김철수',
    phone: '010-1234-5678',
    level: 'GOLD',
    points: 12500,
    lastVisit: '2024-01-25T14:30:00',
    totalPurchase: 1250000
  },
  {
    id: 2,
    name: '이영희',
    phone: '010-9876-5432',
    level: 'SILVER',
    points: 5800,
    lastVisit: '2024-01-24T11:20:00',
    totalPurchase: 580000
  },
  // ... 더 많은 고객 데이터
])

const totalCustomers = ref(42)
const totalPages = ref(5)

// Methods
const searchCustomers = async () => {
  try {
    // API 호출
    const response = await fetch('/api/customers', {
      method: 'POST',
      body: JSON.stringify({
        name: searchName.value,
        phone: searchPhone.value,
        level: membershipLevel.value,
        page: currentPage.value
      })
    })
    const data = await response.json()
    // 데이터 업데이트
  } catch (error) {
    console.error('Failed to fetch customers:', error)
  }
}

const editCustomer = (customer) => {
  selectedCustomer.value = customer
  showRegisterModal.value = true
}

const closeRegisterModal = () => {
  showRegisterModal.value = false
  selectedCustomer.value = null
}

const saveCustomer = async (customerData) => {
  try {
    // API 호출
    const response = await fetch('/api/customers', {
      method: selectedCustomer.value ? 'PUT' : 'POST',
      body: JSON.stringify(customerData)
    })
    const data = await response.json()
    // 성공 시 목록 새로고침
    await searchCustomers()
    closeRegisterModal()
  } catch (error) {
    console.error('Failed to save customer:', error)
  }
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString()
}
</script>