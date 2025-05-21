// src/main.js
import { createApp } from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import { createPinia } from 'pinia'
import router from './router'
import { useAuthStore } from '@/stores/auth'
import 'vuetify/styles'
import './style.css'
import '@mdi/font/css/materialdesignicons.css'

async function initApp() {
	// 1) Pinia 생성 및 설치
	const pinia = createPinia()
	const app = createApp(App)
	app.use(pinia)

	// 2) 스토어에서 fetchUser 호출 → 로그인 상태 복원
	const auth = useAuthStore()
	await auth.fetchUser() // <-- 여길 마운트 전에 반드시 await

	// 3) 라우터·Vuetify 설치
	app.use(router)
	app.use(vuetify)

	// 4) 마운트
	app.mount('#app')
}

initApp()
