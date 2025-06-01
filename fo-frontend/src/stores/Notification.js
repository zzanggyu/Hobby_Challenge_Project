// src/stores/notification.js (기존 파일 교체)
import { defineStore } from 'pinia'
import {
	getNotifications,
	markAsRead as apiMarkAsRead,
	markAllAsRead as apiMarkAllAsRead,
} from '@/services/notificationService'

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
		// 최신 5개만 가져오기 (벨 드롭다운용)
		recentNotifications: (state) => state.notifications.slice(0, 5),
	},

	actions: {
		// 알림 목록 가져오기
		async fetchNotifications(loadMore = false) {
			if (this.loading || (!loadMore && this.notifications.length > 0))
				return

			this.loading = true
			try {
				const data = await getNotifications(
					loadMore ? this.page + 1 : 1,
					this.pageSize
				)

				if (loadMore) {
					this.notifications.push(...data.items)
					this.page++
				} else {
					this.notifications = data.items
					this.page = 1
				}

				this.hasMore = data.items.length === this.pageSize
				this.unreadCount = data.unreadCount || 0
			} catch (error) {
				console.error('알림 로드 실패:', error)
			} finally {
				this.loading = false
			}
		},

		// 알림 읽음 처리
		async markAsRead(id) {
			try {
				await apiMarkAsRead(id)
				const notification = this.notifications.find((n) => n.id === id)
				if (notification && !notification.read) {
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
				await apiMarkAllAsRead()
				this.notifications.forEach((n) => (n.read = true))
				this.unreadCount = 0
			} catch (error) {
				console.error('전체 읽음 처리 실패:', error)
			}
		},

		// 주기적으로 알림 업데이트 (새로고침)
		async refreshNotifications() {
			this.notifications = []
			await this.fetchNotifications()
		},

		// 새 알림이 있는지 확인 (주기적 호출용)
		async checkNewNotifications() {
			try {
				const data = await getNotifications(1, 1)
				this.unreadCount = data.unreadCount || 0
			} catch (error) {
				console.error('알림 확인 실패:', error)
			}
		},
	},
})
