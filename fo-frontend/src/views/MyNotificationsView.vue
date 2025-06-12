<!-- src/views/MyNotificationsView.vue -->
<template>
	<v-container>
		<v-row>
			<v-col cols="12">
				<div class="d-flex justify-space-between align-center mb-4">
					<div class="d-flex align-center">
						<v-icon size="32" color="orange" class="mr-3">
							mdi-bell-ring
						</v-icon>
						<h2 class="text-h4">전체 알림</h2>
					</div>

					<v-btn
						v-if="notifications.length > 0 && unreadCount > 0"
						text
						color="primary"
						@click="markAllAsRead"
						:loading="markingAll"
					>
						모두 읽음 처리 ({{ unreadCount }})
					</v-btn>
				</div>
			</v-col>
		</v-row>

		<v-row>
			<v-col cols="12">
				<!-- 로딩 상태 -->
				<v-card v-if="loading && notifications.length === 0">
					<v-card-text class="text-center py-10">
						<v-progress-circular indeterminate color="primary" />
						<p class="mt-4">알림을 불러오는 중...</p>
					</v-card-text>
				</v-card>

				<!-- 알림이 없을 때 -->
				<v-card v-else-if="notifications.length === 0">
					<v-card-text class="text-center py-10">
						<v-icon size="64" color="grey lighten-2">mdi-bell-off</v-icon>
						<p class="text-h6 grey--text mt-4">
							아직 받은 알림이 없습니다.
						</p>
						<p class="text-body-2 grey--text">
							챌린지에 참여하거나 활동하면 알림을 받을 수 있어요!
						</p>
					</v-card-text>
				</v-card>

				<!-- 알림 리스트 -->
				<v-list v-else three-line width="800" class="mx-auto">
					<template
						v-for="(notification, index) in notifications"
						:key="notification.id"
					>
						<v-list-item
							@click="handleNotificationClick(notification)"
							:class="{ 'bg-grey-lighten-4': !notification.read }"
							class="notification-item"
						>
							<template v-slot:prepend>
								<v-avatar
									:color="getNotificationColor(notification.type)"
									size="40"
								>
									<v-icon color="white">{{
										getNotificationIcon(notification.type)
									}}</v-icon>
								</v-avatar>
							</template>

							<v-list-item-title class="font-weight-medium">
								{{ notification.title }}
								<v-chip
									v-if="!notification.read"
									x-small
									color="primary"
									class="ml-2"
								>
									NEW
								</v-chip>
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
								<div class="d-flex flex-column align-center">
									<v-btn
										v-if="!notification.read"
										icon
										size="small"
										@click.stop="markAsRead(notification.id)"
									>
										<v-icon size="small" color="primary"
											>mdi-check</v-icon
										>
									</v-btn>
									<v-icon v-else size="small" color="success"
										>mdi-check-circle</v-icon
									>
								</div>
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
import { onMounted, computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/stores/notification'
import { formatDistanceToNow } from 'date-fns'
import { ko } from 'date-fns/locale'

const router = useRouter()
const notificationStore = useNotificationStore()

const notifications = computed(() => notificationStore.notifications)
const loading = computed(() => notificationStore.loading)
const hasMore = computed(() => notificationStore.hasMore)
const unreadCount = computed(() => notificationStore.unreadCount)

const markingAll = ref(false)

// 알림 타입별 아이콘
function getNotificationIcon(type) {
	const icons = {
		CHALLENGE_REQUEST: 'mdi-account-plus',
		CHALLENGE_REQUEST_APPROVED: 'mdi-check-circle',
		CHALLENGE_REQUEST_REJECTED: 'mdi-close-circle',
		NEW_CERT: 'mdi-camera',
		NEW_COMMENT: 'mdi-comment',
		NEW_LIKE: 'mdi-heart',
		SYSTEM_NOTICE: 'mdi-bell',
		CHALLENGE_STARTING_SOON: 'mdi-clock-start',
		CHALLENGE_STARTED: 'mdi-play-circle',
		CHALLENGE_ENDING_SOON: 'mdi-clock-end',
		CHALLENGE_ENDED: 'mdi-flag-checkered',
	}
	return icons[type] || 'mdi-bell'
}

// 알림 타입별 색상
function getNotificationColor(type) {
	const colors = {
		CHALLENGE_REQUEST: 'blue',
		CHALLENGE_REQUEST_APPROVED: 'green',
		CHALLENGE_REQUEST_REJECTED: 'red',
		NEW_CERT: 'purple',
		NEW_COMMENT: 'orange',
		NEW_LIKE: 'pink',
		SYSTEM_NOTICE: 'grey',
		CHALLENGE_STARTING_SOON: 'amber',
		CHALLENGE_STARTED: 'green',
		CHALLENGE_ENDING_SOON: 'deep-orange',
		CHALLENGE_ENDED: 'indigo',
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

	try {
		//  챌린지 관련 알림들
		if (notification.targetType === 'challenge' && notification.targetId) {
			router.push({
				name: 'challenge-overview',
				params: { id: notification.targetId },
			})
			return
		}

		//  인증 관련 알림들 (NEW_CERT, NEW_COMMENT, NEW_LIKE)
		if (
			notification.targetType === 'certification' &&
			notification.targetId
		) {
			const challengeId =
				notification.certChallengeId || notification.challengeId

			if (challengeId) {
				//  쿼리 파라미터로 모달 열기 신호
				router.push({
					name: 'challenge-overview',
					params: { id: challengeId },
					hash: '#certifications',
					query: {
						cert: notification.targetId,
						open: '1',
						// 타임스탬프 추가로 같은 알림 재클릭 시에도 감지
						t: Date.now(),
					},
				})
			} else {
				router.push({
					name: 'mypage',
					query: { tab: 'certifications' },
				})
			}
			return
		}

		//  타입별 처리
		switch (notification.type) {
			case 'CHALLENGE_REQUEST':
			case 'CHALLENGE_REQUEST_APPROVED':
			case 'CHALLENGE_REQUEST_REJECTED':
			case 'CHALLENGE_STARTING_SOON':
			case 'CHALLENGE_STARTED':
			case 'CHALLENGE_ENDING_SOON':
			case 'CHALLENGE_ENDED':
				// 챌린지 관련 - certChallengeId 또는 challengeId 사용
				const relatedChallengeId =
					notification.certChallengeId ||
					notification.challengeId ||
					notification.targetId
				if (relatedChallengeId) {
					router.push({
						name: 'challenge-overview',
						params: { id: relatedChallengeId },
					})
				} else {
					router.push({ name: 'challenge-list' })
				}
				break

			case 'NEW_CERT':
			case 'NEW_COMMENT':
			case 'NEW_LIKE':
				// 인증 관련 - certChallengeId 사용
				const certChallengeId =
					notification.certChallengeId || notification.challengeId
				if (certChallengeId) {
					router.push({
						name: 'challenge-overview',
						params: { id: certChallengeId },
						hash: '#certifications',
						query: {
							cert: notification.targetId || notification.certId,
							open: '1',
							t: Date.now(),
						},
					})
				} else {
					console.warn(
						'인증 관련 알림이지만 챌린지 ID가 없음:',
						notification
					)
					router.push({ name: 'mypage', query: { tab: 'certifications' } })
				}
				break

			default:
				// 기본적으로 홈으로 이동
				router.push({ name: 'my-notifications' })
				break
		}
	} catch (error) {
		console.error('알림 클릭 처리 중 오류:', error)
		alert('페이지 이동 중 오류가 발생했습니다.')
	}
}

// 더보기
async function loadMore() {
	await notificationStore.fetchNotifications(true)
}

// 개별 읽음 처리
async function markAsRead(id) {
	await notificationStore.markAsRead(id)
}

// 전체 읽음 처리
async function markAllAsRead() {
	markingAll.value = true
	try {
		await notificationStore.markAllAsRead()
	} finally {
		markingAll.value = false
	}
}

// 초기 로드
onMounted(() => {
	notificationStore.refreshNotifications()
})
</script>

<style scoped>
.notification-item {
	cursor: pointer;
	transition: background-color 0.2s;
	min-height: 80px;
}

.notification-item:hover {
	background-color: rgba(0, 0, 0, 0.04);
}
</style>
