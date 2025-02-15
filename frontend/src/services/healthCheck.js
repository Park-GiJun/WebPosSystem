// services/healthCheck.js
import apiClient from '@/plugins/axios'

class HealthCheckService {
    constructor() {
        this.intervalId = null
        this.checkInterval = 600000
    }

    async sendSlackNotification(status, message) {
        try {
            const now = new Date();
            const formattedTime = now.toLocaleString('ko-KR', {
                timeZone: 'Asia/Seoul',
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                hour12: false
            });

            const payload = {
                status,
                message,
                timestamp: formattedTime
            }

            await apiClient.post('/notifications/slack', payload)
        } catch (error) {
            console.error('Failed to send Slack notification:', error)
        }
    }

    async checkHealth() {
        try {
            const response = await apiClient.get('/health')

            if (response.status === 200) {
                await this.sendSlackNotification('success', '백엔드 서버가 정상적으로 동작중입니다.')
                return {
                    status: 'success',
                    message: '백엔드 서버가 정상적으로 동작중입니다.'
                }
            } else {
                await this.sendSlackNotification('error', '백엔드 서버 연결에 실패했습니다.')
                return {
                    status: 'error',
                    message: '백엔드 서버 연결에 실패했습니다.'
                }
            }
        } catch (error) {
            await this.sendSlackNotification('error', '백엔드 서버에 연결할 수 없습니다.')
            return {
                status: 'error',
                message: '백엔드 서버에 연결할 수 없습니다.'
            }
        }
    }

    startMonitoring() {
        if (this.intervalId) return

        // 초기 체크
        this.checkHealth()

        // 주기적 체크 시작
        this.intervalId = setInterval(() => {
            this.checkHealth()
        }, this.checkInterval)
    }

    stopMonitoring() {
        if (this.intervalId) {
            clearInterval(this.intervalId)
            this.intervalId = null
        }
    }
}

export const healthCheckService = new HealthCheckService()