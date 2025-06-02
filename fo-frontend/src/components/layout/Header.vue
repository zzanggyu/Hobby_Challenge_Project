<template>
	<!-- 로고 + 이름 -->
	<v-app-bar app color="white" height="80" elevation="4">
		<v-toolbar-title>
			<!-- 🔥 요소들을 분리해서 배치 -->
			<div class="d-flex align-center">
				<!-- 1. 로고만 있는 버튼 -->
				<v-btn
					variant="text"
					height="80"
					width="80"
					class="pa-0 logo-btn"
					@click="goHome"
				>
					<img src="@/assets/logo.png" alt="로고" height="70" />
				</v-btn>

				<!-- 2. 환영 메시지 (버튼 밖으로 분리) -->
				<div
					v-if="auth.isAuthenticated"
					class="welcome-message-simple ml-3 mr-4"
				>
					<span class="welcome-text-simple">
						{{ auth.user?.nickname }}님 환영합니다!
					</span>
				</div>

				<!-- 3. 타이틀 텍스트 (버튼 밖으로 분리) -->
				<!-- <span
					class="hobby-title"
					:class="{ 'ml-3': !auth.isAuthenticated }"
				>
					함께하는 취미 챌린지!!
				</span> -->
			</div>
		</v-toolbar-title>

		<v-spacer></v-spacer>
		<div class="menu-bar">
			<v-btn text @click="goChallengeCreate" class="font-weight-bold">
				챌린지 생성
			</v-btn>
			<v-divider vertical class="mx-2" />
			<v-btn text @click="goChallengeList" class="font-weight-bold"
				>챌린지 목록</v-btn
			>
			<v-btn text @click="goMyPage" class="font-weight-bold">
				내 정보
			</v-btn>
			<notification-bell class="mr-2" />
			<v-divider vertical class="mx-2" />
			<v-btn
				v-if="!auth.isAuthenticated"
				text
				to="/login"
				class="font-weight-bold"
				>로그인</v-btn
			>
			<v-btn v-else text @click="onLogout" class="font-weight-bold"
				>로그아웃</v-btn
			>
			<v-btn
				v-if="!auth.isAuthenticated"
				text
				@click="goToSignup"
				class="font-weight-bold"
				>회원가입</v-btn
			>
		</div>
	</v-app-bar>
</template>

<script setup>
import NotificationBell from '@/components/layout/NotificationBell.vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { logout as apiLogout } from '@/services/authService'

// router 인스턴스 생성
const router = useRouter()
const auth = useAuthStore()

// 페이지 이동 함수
function goHome() {
	router.push('/')
}

// 챌린지 목록 페이지로 이동
function goChallengeList() {
	router.push('/challenges')
}

// 챌린지 생성 페이지로 이동
function goChallengeCreate() {
	router.push('/challenges/new')
}

// 마이페이지로 이동
function goMyPage() {
	router.push('/mypage')
}

// 로그아웃
async function onLogout() {
	try {
		// 서버 세션/쿠키를 무효화
		await apiLogout()
	} catch (e) {
		console.warn('로그아웃 API 호출 중 에러', e)
	}
	// 클라이언트 쪽 auth store 초기화
	auth.$patch({ isAuthenticated: false, user: null })
	// 홈 또는 로그인 페이지로 이동
	router.push('/')
}

// 회원가입 페이지로 이동
function goToSignup() {
	router.push('/signup')
}

// function scrollTo(id) {
//   document.getElementById(id)?.scrollIntoView({ behavior: "smooth" });
// }
</script>

<style scoped>
/* 필요하면 헤더 고정 시 투명 → 흰색 전환 애니메이션 등 추가 */
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
</style>
