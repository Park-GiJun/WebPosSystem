import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'
import App from './App.vue'
import router from './router'
import './style.css'
import './plugins/axios'
import HealthCheckPlugin from './plugins/healthCheck'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(HealthCheckPlugin)
app.use(Toast, {
    position: "bottom-left",
    timeout: 2000,
    closeOnClick: true,
    pauseOnFocusLoss: true,
    pauseOnHover: true,
    draggable: true,
    draggablePercent: 0.6,
    showCloseButtonOnHover: false,
    hideProgressBar: true,
    closeButton: "button",
    icon: true,
    rtl: false
})
app.mount('#app')
