<!-- SisLayout.vue -->
<template>
  <div class="flex h-screen bg-gray-100">
    <!-- Sidebar -->
    <div :class="['bg-white shadow-lg transition-all duration-300', isSidebarOpen ? 'w-64' : 'w-20']">
      <div class="flex items-center justify-between p-4 border-b">
        <h1 v-if="isSidebarOpen" @click="selectMenu" class="text-xl font-bold">SIS 시스템</h1>
        <button @click="toggleSidebar" class="p-2 rounded hover:bg-gray-100">
          <ChevronLeft v-if="isSidebarOpen" class="w-5 h-5" />
          <ChevronRight v-else class="w-5 h-5" />
        </button>
      </div>

      <nav class="mt-4">
        <div v-for="item in menuItems" :key="item.name" class="relative">
          <!-- Main Menu Button -->
          <button
              @click="toggleSubmenu(item)"
              :class="[
             'w-full flex items-center p-4 hover:bg-gray-100',
             isActiveMenu(item) ? 'bg-blue-50 text-blue-600' : '',
           ]"
          >
            <component :is="item.icon" class="w-5 h-5" />
            <span v-if="isSidebarOpen" class="ml-4">{{ item.name }}</span>
            <ChevronDown
                v-if="isSidebarOpen && item.submenu"
                :class="[
               'w-4 h-4 ml-auto transition-transform',
               openSubmenus.includes(item.name) ? 'transform rotate-180' : ''
             ]"
            />
          </button>

          <!-- Submenu -->
          <div
              v-if="item.submenu && openSubmenus.includes(item.name) && isSidebarOpen"
              class="bg-gray-50"
          >
            <router-link
                v-for="submenu in item.submenu"
                :key="submenu.path"
                :to="submenu.path"
                class="flex items-center pl-12 pr-4 py-2 text-sm hover:bg-gray-100"
                :class="isCurrentRoute(submenu.path) ? 'text-blue-600' : 'text-gray-600'"
            >
              {{ submenu.name }}
            </router-link>
          </div>
        </div>
      </nav>
    </div>

    <!-- Main Content -->
    <div class="flex-1 overflow-hidden flex flex-col">
      <header class="bg-white shadow-sm">
        <div class="flex justify-between items-center px-6 py-4">
          <h2 class="text-2xl font-semibold">{{ currentPageTitle }}</h2>
          <div class="flex items-center gap-4">
            <span>{{ username }}</span>
            <button @click="logout" class="flex items-center gap-2 text-gray-600 hover:text-gray-900">
              <LogOut class="w-5 h-5" />
              로그아웃
            </button>
          </div>
        </div>
      </header>

      <main class="flex-1 overflow-auto">
        <router-view></router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  ChevronLeft, ChevronRight, ChevronDown,
  BarChart3, Users, Package, Boxes, Settings,
  Database, LogOut, CreditCard, FileText, PersonStandingIcon
} from 'lucide-vue-next'
import {HomeIcon} from "@heroicons/vue/24/outline";

const router = useRouter()
const route = useRoute()

const isSidebarOpen = ref(true)
const username = ref('사용자')
const openSubmenus = ref([])

const menuItems = [
  {
    name: 'Home',
    icon: HomeIcon,
    path: '/sis/dashboard',
  },
  {
    name: '매출관리',
    icon: BarChart3,
    path: '/sis/sales',
    submenu: [
      { name: '매출 현황', path: '/sis/sales/status' },
      { name: '매출 분석', path: '/sis/sales/analysis' },
      { name: '매출 리포트', path: '/sis/sales/report' }
    ]
  },
  {
    name: '직원관리',
    icon: Users,
    path: '/sis/employees',
    submenu: [
      { name: '직원 목록', path: '/sis/employees/list' },
      { name: '근태 관리', path: '/sis/employees/attendance' }
    ]
  },
  {
    name: '결제관리',
    icon: CreditCard,
    path: '/sis/payments',
    submenu: [
      { name: '결제 내역', path: '/sis/payments/history' },
      { name: '결제 통계', path: '/sis/payments/stats' }
    ]
  },
  {
    name: '재고관리',
    icon: Boxes,
    path: '/sis/inventory',
    submenu: [
      { name: '재고 현황', path: '/sis/inventory/status' },
      { name: '입/출고 관리', path: '/sis/inventory/movement' },
      { name: '재고 관리', path: '/sis/inventory/list' },
      { name: '재고 이력', path: '/sis/inventory/history' },
    ]
  },
  {
    name: '상품관리',
    icon: Boxes,
    path: '/sis/product',
    submenu: [
      {name: '상품 관리', path:'/sis/product/management'} ,
    ]
  },
  {
    name: '고객관리',
    icon: PersonStandingIcon,
    path: '/sis/customers',
    submenu: [
      { name: '고객 목록', path: '/sis/customers/list' },
    ]
  },
  {
    name: '시스템관리',
    icon: Database,
    path: '/sis/system'
  },
  {
    name: '설정',
    icon: Settings,
    path: '/sis/settings'
  }
]

const currentPageTitle = computed(() => {
  const currentPath = route.path
  for (const item of menuItems) {
    if (item.submenu) {
      const submenu = item.submenu.find(sub => currentPath === sub.path)
      if (submenu) return submenu.name
    }
    if (currentPath === item.path) return item.name
  }
  return ''
})

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
  if (!isSidebarOpen.value) {
    openSubmenus.value = []
  }
}

const toggleSubmenu = (item) => {
  if (!item.submenu) {
    router.push(item.path)
    return
  }

  const index = openSubmenus.value.indexOf(item.name)
  if (index === -1) {
    openSubmenus.value.push(item.name)
  } else {
    openSubmenus.value.splice(index, 1)
  }
}

const isCurrentRoute = (path) => route.path === path

const isActiveMenu = (item) => {
  if (item.submenu) {
    return item.submenu.some(sub => isCurrentRoute(sub.path))
  }
  return isCurrentRoute(item.path)
}

const logout = () => {
  router.push('/')
}

const selectMenu = () => {
  router.push('/system')
}
</script>