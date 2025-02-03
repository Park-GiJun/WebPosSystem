<!-- LoginView.vue -->
<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <!-- 상단 버튼 영역 -->
    <div class="absolute top-4 right-4 flex gap-4">
      <button
          @click="checkHealth"
          class="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg flex items-center gap-2"
      >
        <ActivitySquare class="w-4 h-4" />
        서버 상태 확인
      </button>

      <router-link
          to="/meeting"
          class="px-4 py-2 bg-blue-600 text-white hover:bg-blue-700 rounded-lg"
      >
        미팅 페이지
      </router-link>
    </div>

    <!-- 헬스체크 결과 표시 -->
    <div v-if="healthStatus" class="absolute top-20 right-4 w-80">
      <div
          :class="[
          'p-4 rounded-lg',
          healthStatus.status === 'success'
            ? 'bg-green-100 text-green-800'
            : 'bg-red-100 text-red-800'
        ]"
      >
        {{ healthStatus.message }}
      </div>
    </div>

    <!-- 로그인 폼 -->
    <div class="flex-1 flex items-center justify-center">
      <div class="bg-white p-8 rounded-xl shadow-lg w-96">
        <h1 class="text-2xl font-bold mb-6 text-center">로그인</h1>

        <!-- 에러 메시지 -->
        <div v-if="errorMessage" class="mb-4 p-4 bg-red-100 text-red-800 rounded-lg">
          {{ errorMessage }}
        </div>

        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              아이디
            </label>
            <input
                type="text"
                v-model="form.username"
                class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              비밀번호
            </label>
            <input
                type="password"
                v-model="form.password"
                class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                required
            />
          </div>

          <button
              type="submit"
              class="w-full py-2 px-4 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
              :disabled="isLoading"
          >
            {{ isLoading ? '로그인 중...' : '로그인' }}
          </button>
        </form>
      </div>
      <div class="mt-6 pt-4 border-t text-sm text-gray-500">
        <p class="text-center">관리자 계정</p>
        <div class="flex justify-center gap-4 mt-1">
          <span>ID: admin</span>
          <span>PW: admin</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ActivitySquare } from 'lucide-vue-next'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import axios from '@/plugins/axios'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const healthStatus = ref(null)
const errorMessage = ref('')
const isLoading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

// URL query parameter에서 에러 메시지 확인
onMounted(() => {
  if (route.query.error) {
    errorMessage.value = route.query.error
    // 에러 메시지를 표시한 후 URL에서 제거
    router.replace({ query: {} })
  }
})

const checkHealth = async () => {
  try {
    const response = await axios.get('/health')

    if (response.status === 200) {
      healthStatus.value = {
        status: 'success',
        message: '백엔드 서버가 정상적으로 동작중입니다.'
      }
    } else {
      healthStatus.value = {
        status: 'error',
        message: '백엔드 서버 연결에 실패했습니다.'
      }
    }
  } catch (error) {
    healthStatus.value = {
      status: 'error',
      message: '백엔드 서버에 연결할 수 없습니다.'
    }
  }

  setTimeout(() => {
    healthStatus.value = null
  }, 3000)
}

const handleSubmit = async () => {
  try {
    isLoading.value = true
    errorMessage.value = ''

    await authStore.login(form.username, form.password)
    await router.push('/system')
  } catch (error) {
    console.error('Login failed:', error)
    errorMessage.value = error.response?.data?.message || '로그인에 실패했습니다.'
  } finally {
    isLoading.value = false
  }
}
</script>