import { createApp } from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify' // 플러그인 파일 import
import { createPinia } from 'pinia'
import router from './router'
import 'vuetify/styles'
import './style.css'
import '@mdi/font/css/materialdesignicons.css'

createApp(App).use(createPinia()).use(router).use(vuetify).mount('#app')
