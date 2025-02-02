import { defineStore } from 'pinia'
import axios from '@/plugins/axios.js'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null,
        token: null,
        isAuthenticated: false
    }),

    actions: {
        async login(username, password) {
            try {
                const response = await axios.post('/auth/login', { username, password })
                const { token } = response.data.data

                this.token = token
                this.isAuthenticated = true

                localStorage.setItem('token', token)
                localStorage.setItem('username', username)

                // 사용자 정보 가져오기
                await this.fetchUserInfo()

                return true
            } catch (error) {
                throw error
            }
        },

        async validateSession() {
            try {
                const response = await axios.get('/user/me')
                this.user = response.data.data
                this.isAuthenticated = true
                return true
            } catch (error) {
                this.logout()
                throw error
            }
        },

        async fetchUserInfo() {
            try {
                const response = await axios.get('/user/me')
                this.user = response.data.data
            } catch (error) {
                throw error
            }
        },

        logout() {
            this.user = null
            this.token = null
            this.isAuthenticated = false
            localStorage.removeItem('token')
            localStorage.removeItem('username')
        }
    }
})