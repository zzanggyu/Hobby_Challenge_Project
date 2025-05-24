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
		user: null, // 로그인 유저 정보 저장용
	}),
	actions: {
		// 로그인
		async login(credentials) {
			// 로그인 api
			try {
				await apiLogin(credentials)
				// 로그인 성공 후 내 정보 가져오기
				const me = await apiMe()
				this.user = me
				this.isAuthenticated = true
			} catch (err) {
				this.user = null
				this.isAuthenticated = false
				// 알림 띄우거나 에러 리턴
				throw err
			}
		},
		// 로그아웃
		async logout() {
			await apiLogout()
			this.isAuthenticated = false
			this.user = null
		},
		// 새로고침 해도 로그인 유지
		async fetchUser() {
			try {
				const me = await apiMe()
				this.user = me
				this.isAuthenticated = true
			} catch {
				this.isAuthenticated = false
				this.user = null
			}
		},
	},
})
