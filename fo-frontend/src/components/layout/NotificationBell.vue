<template>
	<v-menu v-model="open" location="bottom end" offset-y max-width="380">
		<template #activator="{ props }">
			<v-btn icon v-bind="props" @click="onOpenBell">
				<v-badge :content="unreadCount" color="red" v-if="unreadCount > 0">
					<v-icon>mdi-bell-outline</v-icon>
				</v-badge>
				<v-icon v-else>mdi-bell-outline</v-icon>
			</v-btn>
		</template>

		<v-card style="max-height: 450px" class="pa-0">
			<!-- 헤더 -->
			<v-card-title
				class="d-flex justify-space-between align-center py-3 px-4"
			>
				<span class="text-h6">🔔 알림</span>
				<v-btn text size="small" color="primary" @click="goToAll">
					모두 보기
				</v-btn>
			</v-card-title>

			<v-divider />

			<!-- 로딩 상태 -->
			<div v-if="loading" class="text-center py-6">
				<v-progress-circular indeterminate color="primary" size="30" />
				<p class="mt-2 text-caption">알림을 불러오는 중...</p>
			</div>

			<!-- 알림 리스트 -->
			<v-list
				v-else-if="recentNotifications.length"
				style="max-height: 350px; overflow-y: auto"
				class="py-0"
			>
				<v-list-item
					v-for="n in recentNotifications"
					:key="n.id"
					@click="handleClick(n)"
					:class="{ 'bg-grey-lighten-4': !n.read }"
					density="compact"
					class="py-2"
				>
					<template #prepend>
						<v-avatar size="36" :color="getNotificationColor(n.type)">
							<v-icon size="18" color="white">
								{{ getNotificationIcon(n.type) }}
							</v-icon>
						</v-avatar>
					</template>

					<div class="notification-content">
						<v-list-item-title
							class="text-body-2 font-weight-medium mb-1"
						>
							{{ n.title }}
							<v-chip
								v-if="!n.read"
								x-small
								color="primary"
								class="ml-1"
							>
								NEW
							</v-chip>
						</v-list-item-title>
						<v-list-item-subtitle class="text-caption mb-1">
							{{ n.message }}
						</v-list-item-subtitle>
						<v-list-item-subtitle class="text-caption text-grey">
							{{ formatRelativeTime(n.createdAt) }}
						</v-list-item-subtitle>
					</div>
				</v-list-item>
			</v-list>

			<!-- 알림이 없을 때 -->
			<div v-else class="text-center py-8">
				<v-icon size="48" color="grey lighten-2">mdi-bell-off</v-icon>
				<p class="text-body-2 grey--text mt-2">새로운 알림이 없습니다</p>
			</div>
		</v-card>
	</v-menu>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/stores/notification'
import { formatDistanceToNow } from 'date-fns'
import { ko } from 'date-fns/locale'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const router = useRouter()
const store = useNotificationStore()
const open = ref(false)

const recentNotifications = computed(() => store.recentNotifications)
const unreadCount = computed(() => store.unreadCount)
const loading = computed(() => store.loading)

watch(
	() => authStore.isAuthenticated,
	(isAuthenticated) => {
		if (!isAuthenticated) {
			store.clearNotifications()
			stopNotificationPolling()
		} else {
			store.fetchNotifications()
			startNotificationPolling()
		}
	}
)

// 주기적 알림 확인을 위한 인터벌
let notificationInterval = null

// 알림 벨 클릭 시 최신 알림 로드
async function onOpenBell() {
	if (!authStore.isAuthenticated) return
	if (!open.value) {
		await store.refreshNotifications()
	}
}

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
		CHALLENGE_STARTING_SOON: 'mdi-clock-start', // 시작 예정
		CHALLENGE_STARTED: 'mdi-play-circle', // 시작됨
		CHALLENGE_ENDING_SOON: 'mdi-clock-end', // 종료 예정
		CHALLENGE_ENDED: 'mdi-flag-checkered', // 종료됨
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
		// 챌린지 생명주기 색상들
		CHALLENGE_STARTING_SOON: 'amber', // 노란색 (주의)
		CHALLENGE_STARTED: 'green', // 초록색 (시작)
		CHALLENGE_ENDING_SOON: 'deep-orange', // 주황색 (경고)
		CHALLENGE_ENDED: 'indigo', // 남색 (완료)
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

	console.log('🔔 알림 클릭:', notification) // 디버깅용

	try {
		// 1. 챌린지 관련 알림들
		if (notification.targetType === 'challenge' && notification.targetId) {
			router.push({
				name: 'challenge-overview',
				params: { id: notification.targetId },
			})
			return
		}

		// 2. 인증 관련 알림들 (NEW_CERT, NEW_COMMENT, NEW_LIKE)
		if (
			notification.targetType === 'certification' &&
			notification.targetId
		) {
			const challengeId =
				notification.certChallengeId || notification.challengeId

			if (challengeId) {
				// 🆕 쿼리 파라미터로 모달 열기 신호
				router.push({
					name: 'challenge-overview',
					params: { id: challengeId },
					hash: '#certifications',
					query: {
						cert: notification.targetId, // 간단하게 cert로 축약
						open: '1', // 모달 열기 신호
					},
				})
			}
			return
		}

		// 3. 타입별 기본 처리 (targetType이 없거나 누락된 경우)
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
						query: notification.certId
							? {
									certId: notification.certId,
									openCert: 'true',
							  }
							: {},
					})
				} else {
					console.warn(
						'인증 관련 알림이지만 챌린지 ID가 없음:',
						notification
					)
					router.push({ name: 'mypage', query: { tab: 'certifications' } })
				}
				break

			case 'SYSTEM_NOTICE':
				// 시스템 공지사항
				router.push({ name: 'home' })
				break

			default:
				console.warn(
					'🚨 처리되지 않은 알림 타입:',
					notification.type,
					notification
				)
				router.push({ name: 'my-notifications' })
				break
		}
	} catch (error) {
		console.error('알림 클릭 처리 중 오류:', error)
		alert('페이지 이동 중 오류가 발생했습니다.')
		router.push({ name: 'my-notifications' })
	}
}

// 전체 보기로 이동
function goToAll() {
	open.value = false
	router.push({ name: 'my-notifications' })
}

// 주기적으로 알림 확인 (30초마다)
function startNotificationPolling() {
	notificationInterval = setInterval(() => {
		store.checkNewNotifications()
	}, 30000) // 30초마다
}

// 주기적 확인 중지
function stopNotificationPolling() {
	if (notificationInterval) {
		clearInterval(notificationInterval)
		notificationInterval = null
	}
}

// 컴포넌트 마운트 시
onMounted(() => {
	if (authStore.isAuthenticated) {
		store.fetchNotifications()
		startNotificationPolling()
	}
})

// 컴포넌트 언마운트 시
onUnmounted(() => {
	stopNotificationPolling()
})
</script>

<style scoped>
.notification-content {
	flex: 1;
	min-width: 0; /* 텍스트 오버플로우 방지 */
}

.v-list-item {
	cursor: pointer;
	transition: background-color 0.2s;
	min-height: 72px;
}

.v-list-item:hover {
	background-color: rgba(0, 0, 0, 0.04);
}
</style>
