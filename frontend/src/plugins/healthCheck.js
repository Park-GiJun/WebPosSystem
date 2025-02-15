// plugins/healthCheck.js
import { healthCheckService } from '@/services/healthCheck'

export default {
    install(app) {
        // Vue 인스턴스에 헬스체크 서비스 주입
        app.config.globalProperties.$healthCheck = healthCheckService

        // 앱 마운트 시 모니터링 시작
        app.mixin({
            mounted() {
                if (this.$root === this) {
                    healthCheckService.startMonitoring()
                }
            },
            unmounted() {
                if (this.$root === this) {
                    healthCheckService.stopMonitoring()
                }
            }
        })
    }
}