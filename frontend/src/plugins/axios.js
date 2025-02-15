// plugins/axios.js
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const instance = axios.create({
    // baseURL: "http://localhost:9832/api"
    baseURL: "https://gijun.net/api"
})

// 에러 코드별 한글 메시지 매핑
const ERROR_MESSAGES = {
    // Auth Errors
    'AUTH_001': '아이디 또는 비밀번호가 일치하지 않습니다.',
    'AUTH_002': '사용자를 찾을 수 없습니다.',
    'AUTH_003': '토큰이 만료되었습니다.',
    'AUTH_004': '유효하지 않은 토큰입니다.',
    'AUTH_005': '이미 로그인된 사용자입니다.',

    // User Errors
    'USER_001': '이미 사용중인 아이디입니다.',
    'USER_002': '이미 사용중인 이메일입니다.',
    'USER_003': '잘못된 비밀번호입니다.',

    // Common Errors
    'COMMON_001': '잘못된 요청입니다.',
    'COMMON_002': '서버 내부 오류가 발생했습니다.',
    'COMMON_003': '접근이 거부되었습니다.',

    // HTTP Status Codes
    400: '잘못된 요청입니다.',
    401: '인증이 필요합니다.',
    403: '접근 권한이 없습니다.',
    404: '요청한 리소스를 찾을 수 없습니다.',
    500: '서버 오류가 발생했습니다.'
}

// Request Interceptor
instance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

// Response Interceptor
instance.interceptors.response.use(
    (response) => {
        const newToken = response.headers['new-token']
        if (newToken) {
            const authStore = useAuthStore()
            authStore.token = newToken
            localStorage.setItem('token', newToken)
        }
        return response
    },
    async (error) => {
        const authStore = useAuthStore()
        const status = error.response?.status
        const errorData = error.response?.data
        const errorCode = errorData?.errorCode

        // 로그인 요청 시 에러 처리
        if (error.config.url.includes('/auth/login')) {
            const displayMessage = ERROR_MESSAGES[errorCode] || errorData?.message || ERROR_MESSAGES[status] || '로그인에 실패했습니다.'

            await router.push({
                path: '/login',
                query: {error: displayMessage}
            })
            return Promise.reject(error)
        }

        // 일반적인 에러 처리
        switch (errorCode) {
            // Auth 관련 에러
            case 'AUTH_003':
            case 'AUTH_004':
                authStore.logout()
                await router.push({
                    path: '/login',
                    query: {error: ERROR_MESSAGES[errorCode]}
                })
                break
            case 'AUTH_005':
                await router.push({
                    path: '/login',
                    query: {error: ERROR_MESSAGES[errorCode]}
                })
                break

            // 권한 관련 에러
            case 'COMMON_003':
                await router.push('/error/403')
                break

            // 서버 에러
            case 'COMMON_002':
                await router.push('/error/500')
                break

            default:
                // HTTP 상태 코드 기반 처리
                switch (status) {
                    case 401:
                        authStore.logout()
                        await router.push({
                            path: '/login',
                            query: {error: '세션이 만료되었습니다. 다시 로그인해주세요.'}
                        })
                        break
                    case 403:
                        await router.push('/error/403')
                        break
                    case 404:
                        await router.push('/error/404')
                        break
                    case 500:
                        await router.push('/error/500')
                        break
                    default:
                        console.error('Unhandled error:', error)
                        await router.push({
                            path: '/error/500',
                            query: {
                                error: ERROR_MESSAGES[errorCode] || ERROR_MESSAGES[status] || '알 수 없는 오류가 발생했습니다.'
                            }
                        })
                }
        }

        return Promise.reject(error)
    }
)

export default instance