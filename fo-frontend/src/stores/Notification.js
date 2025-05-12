// src/stores/notification.js
import { defineStore } from 'pinia'

export const useNotificationStore = defineStore('notification', {
	state: () => ({
		notifications: [
			{
				id: 1,
				title: '새 댓글이 달렸어요!',
				date: '05-10 10:21',
				read: false,
			},
			{
				id: 2,
				title: '챌린지 인증 기한 D-1',
				date: '05-09 09:00',
				read: true,
			},
		],
	}),
	getters: {
		// 읽지 않은 알림 개수
		unreadCount: (state) => state.notifications.filter((n) => !n.read).length,
	},
	actions: {
		// 개별 알림 읽음 처리
		markAsRead(id) {
			const n = this.notifications.find((item) => item.id === id)
			if (n) n.read = true
		},
		// (옵션) API에서 알림 불러오기
		async fetchNotifications() {
			// const { data } = await api.get('/notifications')
			// this.notifications = data
		},
	},
})
