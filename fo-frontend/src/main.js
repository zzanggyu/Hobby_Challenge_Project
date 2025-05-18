import { createApp } from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify' // 플러그인 파일 import
import { createPinia } from 'pinia'
import router from './router'
import { useAuthStore } from '@/stores/auth'
import 'vuetify/styles'
import './style.css'
import '@mdi/font/css/materialdesignicons.css'

createApp(App).use(createPinia()).use(router).use(vuetify).mount('#app')

// 새로고침시 한 번만 실행
const auth = useAuthStore()
auth.fetchUser()
