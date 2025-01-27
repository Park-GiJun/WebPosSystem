<!-- SalesReportView.vue -->
<template>
  <div class="p-6 space-y-6">
    <!-- 리포트 생성 -->
    <div class="bg-white p-6 rounded-lg shadow">
      <h3 class="text-lg font-semibold mb-4">매출 리포트 생성</h3>
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="text-sm font-medium text-gray-700">리포트 유형</label>
          <select v-model="reportConfig.type" class="mt-1 w-full rounded-md border-gray-300">
            <option value="daily">일간 리포트</option>
            <option value="weekly">주간 리포트</option>
            <option value="monthly">월간 리포트</option>
            <option value="yearly">연간 리포트</option>
          </select>
        </div>
        <div>
          <label class="text-sm font-medium text-gray-700">매장 선택</label>
          <select v-model="reportConfig.store" class="mt-1 w-full rounded-md border-gray-300">
            <option value="all">전체 매장</option>
            <option v-for="store in stores" :key="store.id" :value="store.id">
              {{ store.name }}
            </option>
          </select>
        </div>
        <div>
          <label class="text-sm font-medium text-gray-700">기간 선택</label>
          <input type="date" v-model="reportConfig.date" class="mt-1 w-full rounded-md border-gray-300">
        </div>
        <div class="flex items-end">
          <button @click="generateReport" class="w-full bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
            리포트 생성
          </button>
        </div>
      </div>
    </div>

    <!-- 리포트 목록 -->
    <div class="bg-white rounded-lg shadow">
      <div class="px-6 py-4 border-b border-gray-200">
        <h3 class="text-lg font-semibold">생성된 리포트</h3>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead>
          <tr>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">
              리포트명
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">
              생성일
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">
              매장
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase">
              기간
            </th>
            <th class="px-6 py-3 bg-gray-50 text-center text-xs font-medium text-gray-500 uppercase">
              상태
            </th>
            <th class="px-6 py-3 bg-gray-50"></th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="report in reports" :key="report.id">
            <td class="px-6 py-4 whitespace-nowrap">
              {{ report.name }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ report.createdAt }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              {{ report.store }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              {{ report.period }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-center">
               <span :class="getStatusClass(report.status)" class="px-2 py-1 text-xs rounded-full">
                 {{ report.status }}
               </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm">
              <button @click="downloadReport(report)"
                      class="text-blue-600 hover:text-blue-900 mr-4">
                <Download class="w-5 h-5" />
              </button>
              <button @click="previewReport(report)"
                      class="text-gray-600 hover:text-gray-900 mr-4">
                <Eye class="w-5 h-5" />
              </button>
              <button @click="deleteReport(report)"
                      class="text-red-600 hover:text-red-900">
                <Trash class="w-5 h-5" />
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 리포트 미리보기 모달 -->
    <TransitionRoot as="template" :show="previewModal">
      <Dialog as="div" class="relative z-50">
        <TransitionChild
            enter="ease-out duration-300"
            enter-from="opacity-0"
            enter-to="opacity-100"
            leave="ease-in duration-200"
            leave-from="opacity-100"
            leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
        </TransitionChild>

        <div class="fixed inset-0 z-10 overflow-y-auto">
          <div class="flex min-h-full justify-center p-4 text-center items-center">
            <TransitionChild
                enter="ease-out duration-300"
                enter-from="opacity-0 scale-95"
                enter-to="opacity-100 scale-100"
                leave="ease-in duration-200"
                leave-from="opacity-100 scale-100"
                leave-to="opacity-0 scale-95"
            >
              <DialogPanel class="w-full max-w-4xl transform overflow-hidden rounded-lg bg-white text-left align-middle shadow-xl transition-all">
                <div class="bg-white px-4 pb-4 pt-5 sm:p-6">
                  <div class="flex items-start justify-between">
                    <DialogTitle as="h3" class="text-lg font-semibold leading-6 text-gray-900">
                      리포트 미리보기
                    </DialogTitle>
                    <button @click="previewModal = false" class="text-gray-400 hover:text-gray-500">
                      <file class="h-6 w-6" />
                    </button>
                  </div>
                  <div class="mt-4">
                    <!-- 리포트 미리보기 내용 -->
                    <div v-if="selectedReport" class="space-y-6">
                      <div class="border-b pb-4">
                        <h4 class="text-xl font-bold">{{ selectedReport.name }}</h4>
                        <p class="text-sm text-gray-500">
                          생성일: {{ selectedReport.createdAt }} | 매장: {{ selectedReport.store }}
                        </p>
                      </div>

                      <!-- 매출 요약 -->
                      <div class="grid grid-cols-3 gap-4">
                        <div class="p-4 bg-gray-50 rounded-lg">
                          <h5 class="text-sm text-gray-500">총 매출</h5>
                          <p class="text-2xl font-bold">{{ formatNumber(selectedReport.summary.totalRevenue) }}원</p>
                        </div>
                        <div class="p-4 bg-gray-50 rounded-lg">
                          <h5 class="text-sm text-gray-500">주문 건수</h5>
                          <p class="text-2xl font-bold">{{ formatNumber(selectedReport.summary.totalOrders) }}건</p>
                        </div>
                        <div class="p-4 bg-gray-50 rounded-lg">
                          <h5 class="text-sm text-gray-500">평균 객단가</h5>
                          <p class="text-2xl font-bold">{{ formatNumber(selectedReport.summary.averageOrder) }}원</p>
                        </div>
                      </div>

                      <!-- 매출 차트 -->
                      <div class="h-[300px]">
                        <ReportChart :data="selectedReport.chartData" />
                      </div>

                      <!-- 상세 내역 -->
                      <div>
                        <h5 class="font-semibold mb-3">상세 내역</h5>
                        <div class="space-y-2">
                          <div v-for="(item, idx) in selectedReport.details" :key="idx"
                               class="flex justify-between py-2 border-b">
                            <span>{{ item.label }}</span>
                            <span class="font-medium">{{ item.value }}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Download, Eye, Trash, File } from 'lucide-vue-next'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import ReportChart from '@/components/ReportChart.vue'

// 설정 상태
const reportConfig = ref({
  type: 'monthly',
  store: 'all',
  date: new Date().toISOString().split('T')[0]
})

// 매장 목록
const stores = [
  { id: 1, name: '강남점' },
  { id: 2, name: '홍대점' },
  { id: 3, name: '신촌점' },
  { id: 4, name: '건대점' }
]

// 리포트 목록
const reports = ref([
  {
    id: 1,
    name: '2024년 1월 월간 리포트',
    createdAt: '2024-01-26 15:30',
    store: '전체 매장',
    period: '2024-01-01 ~ 2024-01-31',
    status: '완료',
    summary: {
      totalRevenue: 125680000,
      totalOrders: 8560,
      averageOrder: 14680
    },
    chartData: [
      { date: '01-01', value: 4200000 },
      { date: '01-02', value: 3800000 },
      { date: '01-03', value: 4500000 }
    ],
    details: [
      { label: '신용카드 결제', value: '85,460,000원 (68%)' },
      { label: '현금 결제', value: '25,120,000원 (20%)' },
      { label: '간편결제', value: '15,100,000원 (12%)' }
    ]
  },
  {
    id: 2,
    name: '2023년 12월 월간 리포트',
    createdAt: '2023-12-31 18:00',
    store: '강남점',
    period: '2023-12-01 ~ 2023-12-31',
    status: '완료'
  },
  {
    id: 3,
    name: '2023년 4분기 리포트',
    createdAt: '2024-01-25 10:15',
    store: '전체 매장',
    period: '2023-10-01 ~ 2023-12-31',
    status: '생성중'
  }
])

// 모달 상태
const previewModal = ref(false)
const selectedReport = ref(null)

// 리포트 생성
const generateReport = () => {
  console.log('Generating report with config:', reportConfig.value)
}

// 리포트 다운로드
const downloadReport = (report) => {
  console.log('Downloading report:', report.id)
}

// 리포트 미리보기
const previewReport = (report) => {
  selectedReport.value = report
  previewModal.value = true
}

// 리포트 삭제
const deleteReport = (report) => {
  if (confirm('정말 삭제하시겠습니까?')) {
    console.log('Deleting report:', report.id)
  }
}

// 상태 클래스
const getStatusClass = (status) => {
  return {
    '완료': 'bg-green-100 text-green-800',
    '생성중': 'bg-yellow-100 text-yellow-800',
    '실패': 'bg-red-100 text-red-800'
  }[status]
}

// 숫자 포맷팅
const formatNumber = (value) => {
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}
</script>