import api from '@/api'

// 알림 목록 조회
export function getNotifications(page = 1, size = 20) {
	return api
		.get('/notifications', {
			params: { page, size },
		})
		.then((res) => res.data)
}

// 알림 읽음 처리
export function markAsRead(notificationId) {
	return api.patch(`/notifications/${notificationId}/read`)
}

// 모든 알림 읽음 처리
export function markAllAsRead() {
	return api.patch('/notifications/read-all')
}
