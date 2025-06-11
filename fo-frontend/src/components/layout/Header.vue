<template>
	<!-- 로고 + 이름 -->
	<v-app-bar app color="white" height="80" elevation="4">
		<v-toolbar-title>
			<div class="d-flex align-center">
				<!--  로고만 있는 버튼 -->
				<v-btn
					variant="text"
					height="80"
					width="80"
					class="pa-0 logo-btn"
					@click="goHome"
				>
					<img src="@/assets/logo.png" alt="로고" height="85" width="90" />
				</v-btn>

				<!--  환영 메시지 -->
				<div
					v-if="auth.isAuthenticated"
					class="welcome-message-simple ml-3 mr-4"
				>
					<span class="welcome-text-simple">
						{{ auth.user?.nickname }}님 환영합니다!
					</span>
				</div>
			</div>
		</v-toolbar-title>

		<v-spacer />
		<!-- 메뉴 바 -->
		<div class="menu-bar">
			<!--  챌린지 드롭다운 메뉴 -->
			<v-menu offset-y>
				<template v-slot:activator="{ props }">
					<v-btn v-bind="props" text class="font-weight-bold dropdown-btn">
						챌린지
						<v-icon right small>mdi-chevron-down</v-icon>
					</v-btn>
				</template>

				<v-list class="challenge-dropdown">
					<!-- 챌린지 생성 -->
					<v-list-item @click="goChallengeCreate" class="dropdown-item">
						<template v-slot:prepend>
							<v-icon color="primary">mdi-plus-circle</v-icon>
						</template>
						<v-list-item-title>챌린지 생성</v-list-item-title>
						<v-list-item-subtitle
							>새로운 챌린지 만들기</v-list-item-subtitle
						>
					</v-list-item>

					<v-divider class="my-1"></v-divider>

					<!-- 챌린지 목록 -->
					<v-list-item @click="goChallengeList" class="dropdown-item">
						<template v-slot:prepend>
							<v-icon color="blue">mdi-format-list-bulleted</v-icon>
						</template>
						<v-list-item-title>전체 챌린지</v-list-item-title>
						<v-list-item-subtitle
							>모든 챌린지 둘러보기</v-list-item-subtitle
						>
					</v-list-item>

					<v-divider class="my-1"></v-divider>

					<!-- 관심 챌린지 -->
					<v-list-item @click="goFavoriteChallenge" class="dropdown-item">
						<template v-slot:prepend>
							<v-icon color="pink">mdi-heart</v-icon>
						</template>
						<v-list-item-title>내 챌린지</v-list-item-title>
						<v-list-item-subtitle
							>관심 & 참여 (요청) 중인 챌린지</v-list-item-subtitle
						>
					</v-list-item>

					<v-divider class="my-1"></v-divider>

					<!-- 진행 중인 챌린지 (있을 때만 표시) -->
					<v-list-item
						v-if="hasActiveChallenge"
						@click="goActiveChallenge"
						class="dropdown-item active-challenge"
					>
						<template v-slot:prepend>
							<v-icon color="green">mdi-trophy</v-icon>
						</template>
						<v-list-item-title>진행 중인 챌린지</v-list-item-title>
						<v-list-item-subtitle
							>인증하러 바로 가기</v-list-item-subtitle
						>
					</v-list-item>
				</v-list>
			</v-menu>

			<v-divider vertical class="mx-2" />

			<!-- 내 정보 -->
			<v-btn text @click="goMyPage" class="font-weight-bold">
				내 정보
			</v-btn>

			<v-divider vertical class="mx-2" />

			<!-- 알림 벨 -->
			<notification-bell class="mr-2" />

			<v-divider vertical class="mx-2" />

			<!-- 로그인/로그아웃 -->
			<v-btn
				v-if="!auth.isAuthenticated"
				text
				to="/login"
				class="font-weight-bold"
			>
				로그인
			</v-btn>
			<v-btn v-else text @click="onLogout" class="font-weight-bold">
				로그아웃
			</v-btn>

			<v-divider vertical class="mx-2" />

			<!-- 회원가입 (로그인 안 됐을 때만) -->
			<v-btn
				v-if="!auth.isAuthenticated"
				text
				@click="goToSignup"
				class="font-weight-bold"
			>
				회원가입
			</v-btn>

			<v-divider vertical class="mx-2" />
		</div>
	</v-app-bar>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import NotificationBell from '@/components/layout/NotificationBell.vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { logout as apiLogout } from '@/services/authService'
import { getMyParticipations } from '@/services/participationService'

// router 인스턴스 생성
const router = useRouter()
const auth = useAuthStore()

//  활성 챌린지 상태 관리
const activeChallenge = ref(null)

//  현재 진행중인 챌린지가 있는지 확인
const hasActiveChallenge = computed(() => {
	return activeChallenge.value !== null
})

//  내 참여중인 챌린지 확인
async function checkActiveChallenge() {
	if (!auth.isAuthenticated || !auth.user?.userId) {
		activeChallenge.value = null
		return
	}
	try {
		const participations = await getMyParticipations(auth.user.userId)
		// APPROVED 상태이거나 OWNER 역할인 챌린지 찾기
		const active = participations.find(
			(p) => p.status === 'APPROVED' || p.role === 'OWNER'
		)
		activeChallenge.value = active || null
	} catch (error) {
		console.error('활성 챌린지 확인 실패:', error)
		activeChallenge.value = null
	}
}

watch(
	() => auth.isAuthenticated,
	async (newVal, oldVal) => {
		console.log('Header - 로그인 상태 변경:', oldVal, '→', newVal)

		if (newVal) {
			// 로그인 시: 활성 챌린지 확인
			await checkActiveChallenge()
		} else {
			// 로그아웃 시: 활성 챌린지 초기화
			activeChallenge.value = null
		}
	},
	{ immediate: false }
)

// 페이지 이동 함수들
function goHome() {
	router.push('/')
}

function goChallengeList() {
	router.push('/challenges')
}

function goChallengeCreate() {
	router.push('/challenges/new')
}

function goFavoriteChallenge() {
	router.push('/challenges/favorite')
}

//  진행중인 챌린지로 이동
function goActiveChallenge() {
	if (activeChallenge.value) {
		router.push({
			name: 'challenge-overview',
			params: { id: activeChallenge.value.challengeId },
		})
	}
}

function goMyPage() {
	router.push('/mypage')
}

// 로그아웃
async function onLogout() {
	try {
		await apiLogout()
	} catch (e) {
		console.warn('로그아웃 API 호출 중 에러', e)
	}
	// 클라이언트 쪽 auth store 초기화
	auth.$patch({ isAuthenticated: false, user: null })

	activeChallenge.value = null //  활성 챌린지도 초기화
	router.push('/')
}

function goToSignup() {
	router.push('/signup')
}

//  컴포넌트 마운트 시 활성 챌린지 확인
onMounted(() => {
	if (auth.isAuthenticated) {
		checkActiveChallenge()
	}
})

//  로그인 상태 변화 감지해서 활성 챌린지 업데이트
// (만약 필요하다면 watch로 auth.isAuthenticated 감시)
</script>

<style scoped>
/* 기존 스타일 유지 */
.hobby-title {
	font-family: 'Nanum Pen Script', cursive;
	font-size: 30px;
	color: #000000;
}

.welcome-message-simple {
	padding: 6px 12px;
	background-color: #f3e5f5;
	border-radius: 16px;
	border: 1px solid #e1bee7;
}

.welcome-text-simple {
	font-size: 14px;
	font-weight: 500;
	color: #7b1fa2;
}

/*  드롭다운 메뉴 스타일 */
.dropdown-btn {
	transition: all 0.2s ease;
}

.dropdown-btn:hover {
	background-color: rgba(0, 0, 0, 0.04);
	transform: translateY(-1px);
}

.challenge-dropdown {
	border-radius: 12px;
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
	padding: 8px 0;
	min-width: 280px;
	max-width: 320px;
}

.dropdown-item {
	padding: 12px 16px;
	transition: background-color 0.2s ease;
	border-radius: 8px;
	margin: 0 8px;
	cursor: pointer;
}

.dropdown-item:hover {
	background-color: rgba(0, 0, 0, 0.04);
	transform: translateX(2px);
}

/* 진행중인 챌린지  */
.active-challenge {
	background: linear-gradient(
		135deg,
		rgba(76, 175, 80, 0.1) 0%,
		rgba(76, 175, 80, 0.05) 100%
	);
	border: 1px solid rgba(76, 175, 80, 0.3);
}

.active-challenge:hover {
	background: linear-gradient(
		135deg,
		rgba(76, 175, 80, 0.15) 0%,
		rgba(76, 175, 80, 0.1) 100%
	);
}

/* 드롭다운 애니메이션 */
.v-list-item {
	transition: all 0.3s ease;
}

.v-list-item-title {
	font-weight: 600;
	font-size: 0.95rem;
}

.v-list-item-subtitle {
	font-size: 0.8rem;
	opacity: 0.7;
	margin-top: 2px;
}
</style>
