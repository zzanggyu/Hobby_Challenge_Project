// src/stores/auth.js
import { defineStore } from 'pinia'
import {
	login as apiLogin,
	logout as apiLogout,
	me as apiMe,
} from '@/services/authService'

export const useAuthStore = defineStore('auth', {
	state: () => ({
		isAuthenticated: false,
	}),
	actions: {
		// 로그인
		async login(credentials) {
			await apiLogin(credentials)
			this.isAuthenticated = true
		},
		// 로그아웃
		async logout() {
			await apiLogout()
			this.isAuthenticated = false
		},
		// 새로고침 해도 로그인 유지
		async fetchUser() {
			try {
				await apiMe()
				this.isAuthenticated = true
			} catch {
				this.isAuthenticated = false
			}
		},
	},
})
