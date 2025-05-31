// stores/notification.js 수정
import { defineStore } from 'pinia'
import api from '@/api'

export const useNotificationStore = defineStore('notification', {
	state: () => ({
		notifications: [],
		unreadCount: 0,
		loading: false,
		hasMore: true,
		page: 1,
		pageSize: 20,
	}),

	getters: {
		// 최신 5개만 가져오기
		recentNotifications: (state) => state.notifications.slice(0, 5),
	},

	actions: {
		// 알림 목록 가져오기
		async fetchNotifications(loadMore = false) {
			if (this.loading || (!loadMore && this.notifications.length > 0))
				return

			this.loading = true
			try {
				const { data } = await api.get('/notifications', {
					params: {
						page: loadMore ? this.page + 1 : 1,
						size: this.pageSize,
					},
				})

				if (loadMore) {
					this.notifications.push(...data.items)
					this.page++
				} else {
					this.notifications = data.items
					this.page = 1
				}

				this.hasMore = data.items.length === this.pageSize
				this.unreadCount = data.unreadCount
			} catch (error) {
				console.error('알림 로드 실패:', error)
			} finally {
				this.loading = false
			}
		},

		// 알림 읽음 처리
		async markAsRead(id) {
			try {
				await api.patch(`/notifications/${id}/read`)
				const notification = this.notifications.find((n) => n.id === id)
				if (notification) {
					notification.read = true
					this.unreadCount = Math.max(0, this.unreadCount - 1)
				}
			} catch (error) {
				console.error('알림 읽음 처리 실패:', error)
			}
		},

		// 모든 알림 읽음 처리
		async markAllAsRead() {
			try {
				await api.patch('/notifications/read-all')
				this.notifications.forEach((n) => (n.read = true))
				this.unreadCount = 0
			} catch (error) {
				console.error('전체 읽음 처리 실패:', error)
			}
		},
	},
})
