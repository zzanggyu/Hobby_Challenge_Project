<template>
	<!-- 로고 + 이름 -->
	<v-app-bar app color="white" height="78" elevation="4">
		<v-toolbar-title>
			<v-btn
				variant="text"
				height="78"
				class="d-flex align-center"
				@click="goHome"
			>
				<img src="@/assets/logo.png" alt="로고" height="78" class="mr-3" />
				<span class="hobby-title">함께하는 취미 챌린지!!</span>
			</v-btn>
		</v-toolbar-title>

		<v-spacer></v-spacer>
		<!-- 메뉴 버튼튼 -->
		<!-- <v-btn text @click="goHome">홈</v-btn> -->

		<!-- 챌린지 생성 페이지로 이동 -->
		<v-btn text @click="goChallengeCreate">챌린지 생성</v-btn>
		<!-- 챌린지 목록으로 이동 -->
		<v-btn text @click="goChallengeList">챌린지 목록</v-btn>
		<v-btn text @click="goMyPage">마이페이지</v-btn>
		<!-- <v-btn text @click="goNotifications">알림</v-btn> -->
		<notification-bell class="mr-2" />
		<v-btn v-if="!auth.isAuthenticated" text to="/login"> 로그인 </v-btn>
		<v-btn v-else text @click="onLogout"> 로그아웃 </v-btn>
		<v-btn v-if="!auth.isAuthenticated" text @click="goToSignup"
			>회원가입</v-btn
		>
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
</style>
