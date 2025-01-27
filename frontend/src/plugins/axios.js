import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

// Axios 기본 설정
axios.defaults.baseURL = import.meta.env.VITE_API_BASE_URL

// Request Interceptor
axios.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// Response Interceptor
axios.interceptors.response.use(
    (response) => {
        // 토큰 리프레시 로직
        const newToken = response.headers['new-token']
        if (newToken) {
            const authStore = useAuthStore()
            authStore.token = newToken
            localStorage.setItem('token', newToken)
        }
        return response
    },
    (error) => {
        if (error.response?.status === 401) {
            const authStore = useAuthStore()
            authStore.logout()
            router.push('/login')
        }
        return Promise.reject(error)
    }
)