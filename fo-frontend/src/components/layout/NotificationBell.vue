<!-- NotificationBell.vue 수정 -->
<template>
	<v-menu v-model="open" location="bottom end" offset-y max-width="360">
		<template #activator="{ props }">
			<v-btn icon v-bind="props">
				<v-badge :content="unread" color="red" v-if="unread">
					<v-icon>mdi-bell-outline</v-icon>
				</v-badge>
				<v-icon v-else>mdi-bell-outline</v-icon>
			</v-btn>
		</template>

		<v-list style="max-height: 400px" class="py-0">
			<v-list-subheader class="d-flex justify-space-between align-center">
				<v-btn text size="small" color="primary" @click="goToAll">
					알림 모두 보기
				</v-btn>
			</v-list-subheader>

			<v-divider />

			<v-list-item
				v-for="n in recentNotifications"
				:key="n.id"
				@click="handleClick(n)"
				:class="{ 'bg-grey-lighten-4': !n.read }"
				density="compact"
			>
				<template #prepend>
					<v-icon size="small" :color="getNotificationColor(n.type)">
						{{ getNotificationIcon(n.type) }}
					</v-icon>
				</template>

				<v-list-item-title class="text-body-2">
					{{ n.title }}
				</v-list-item-title>
				<v-list-item-subtitle class="text-caption">
					{{ formatRelativeTime(n.createdAt) }}
				</v-list-item-subtitle>
			</v-list-item>

			<v-list-item v-if="!recentNotifications.length" class="text-center">
				<v-list-item-title class="text-grey">
					알림이 없습니다
				</v-list-item-title>
			</v-list-item>
		</v-list>
	</v-menu>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/stores/notification'
import { formatDistanceToNow } from 'date-fns'
import { ko } from 'date-fns/locale'

const router = useRouter()
const store = useNotificationStore()
const open = ref(false)

const recentNotifications = computed(() => store.recentNotifications)
const unread = computed(() => store.unreadCount)

// 알림 타입별 아이콘 (동일한 함수)
function getNotificationIcon(type) {
	const icons = {
		challenge_request: 'mdi-account-plus',
		challenge_approved: 'mdi-check-circle',
		challenge_rejected: 'mdi-close-circle',
		certification_like: 'mdi-heart',
		certification_comment: 'mdi-comment',
		challenge_reminder: 'mdi-bell',
		challenge_complete: 'mdi-trophy',
	}
	return icons[type] || 'mdi-bell'
}

// 알림 타입별 색상
function getNotificationColor(type) {
	const colors = {
		challenge_request: 'blue',
		challenge_approved: 'green',
		challenge_rejected: 'red',
		certification_like: 'pink',
		certification_comment: 'purple',
		challenge_reminder: 'orange',
		challenge_complete: 'yellow',
	}
	return colors[type] || 'grey'
}

// 상대 시간 포맷
function formatRelativeTime(date) {
	return formatDistanceToNow(new Date(date), {
		addSuffix: true,
		locale: ko,
	})
}

// 알림 클릭 처리
async function handleClick(notification) {
	open.value = false

	// 읽음 처리
	if (!notification.read) {
		await store.markAsRead(notification.id)
	}

	// 관련 페이지로 이동
	if (notification.targetType === 'challenge' && notification.targetId) {
		router.push({
			name: 'challenge-overview',
			params: { id: notification.targetId },
		})
	}
}

// 전체 보기로 이동
function goToAll() {
	open.value = false
	router.push({ name: 'my-notifications' })
}

// 마운트 시 알림 로드
onMounted(() => {
	store.fetchNotifications()
})
</script>
