<template>
	<v-container>
		<v-row>
			<v-col cols="12">
				<div class="d-flex justify-space-between align-center mb-4">
					<h2 class="text-h4">알림</h2>
					<v-btn
						v-if="notifications.length > 0"
						text
						color="primary"
						@click="markAllAsRead"
					>
						모두 읽음 처리
					</v-btn>
				</div>
			</v-col>
		</v-row>

		<v-row>
			<v-col cols="12">
				<v-card v-if="loading && notifications.length === 0">
					<v-card-text class="text-center py-10">
						<v-progress-circular indeterminate color="primary" />
					</v-card-text>
				</v-card>

				<v-card v-else-if="notifications.length === 0">
					<v-card-text class="text-center py-10 text-grey">
						알림이 없습니다.
					</v-card-text>
				</v-card>

				<v-list v-else three-line>
					<template
						v-for="(notification, index) in notifications"
						:key="notification.id"
					>
						<v-list-item
							@click="handleNotificationClick(notification)"
							:class="{ 'bg-grey-lighten-4': !notification.read }"
						>
							<template v-slot:prepend>
								<v-avatar
									:color="getNotificationColor(notification.type)"
								>
									<v-icon color="white">{{
										getNotificationIcon(notification.type)
									}}</v-icon>
								</v-avatar>
							</template>

							<v-list-item-title class="font-weight-medium">
								{{ notification.title }}
							</v-list-item-title>

							<v-list-item-subtitle>
								{{ notification.message }}
							</v-list-item-subtitle>

							<v-list-item-subtitle>
								<v-chip x-small label class="mt-1">
									{{ formatRelativeTime(notification.createdAt) }}
								</v-chip>
							</v-list-item-subtitle>

							<template v-slot:append>
								<v-btn
									v-if="!notification.read"
									icon
									size="small"
									@click.stop="markAsRead(notification.id)"
								>
									<v-icon size="small">mdi-check</v-icon>
								</v-btn>
							</template>
						</v-list-item>

						<v-divider v-if="index < notifications.length - 1" />
					</template>
				</v-list>

				<!-- 더보기 버튼 -->
				<v-card-actions v-if="hasMore" class="justify-center">
					<v-btn
						variant="text"
						color="primary"
						:loading="loading"
						@click="loadMore"
					>
						더보기
					</v-btn>
				</v-card-actions>
			</v-col>
		</v-row>
	</v-container>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/stores/notification'
import { formatDistanceToNow } from 'date-fns'
import { ko } from 'date-fns/locale'

const router = useRouter()
const notificationStore = useNotificationStore()

const notifications = computed(() => notificationStore.notifications)
const loading = computed(() => notificationStore.loading)
const hasMore = computed(() => notificationStore.hasMore)

// 알림 타입별 아이콘
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

// 상대 시간 포맷팅
function formatRelativeTime(date) {
	return formatDistanceToNow(new Date(date), {
		addSuffix: true,
		locale: ko,
	})
}

// 알림 클릭 처리
async function handleNotificationClick(notification) {
	// 읽음 처리
	if (!notification.read) {
		await notificationStore.markAsRead(notification.id)
	}

	// 관련 페이지로 이동
	if (notification.targetType === 'challenge' && notification.targetId) {
		router.push({
			name: 'challenge-overview',
			params: { id: notification.targetId },
		})
	} else if (
		notification.targetType === 'certification' &&
		notification.targetId
	) {
		// 인증 상세 모달 열기 등
	}
}

// 더보기
async function loadMore() {
	await notificationStore.fetchNotifications(true)
}

// 읽음 처리
async function markAsRead(id) {
	await notificationStore.markAsRead(id)
}

// 모두 읽음 처리
async function markAllAsRead() {
	await notificationStore.markAllAsRead()
}

// 초기 로드
onMounted(() => {
	notificationStore.fetchNotifications()
})
</script>

<style scoped>
.v-list-item {
	cursor: pointer;
	transition: background-color 0.2s;
}

.v-list-item:hover {
	background-color: rgba(0, 0, 0, 0.04);
}
</style>
