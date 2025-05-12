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
		<v-btn text @click="goChallenges">챌린지</v-btn>
		<v-btn text @click="goMyPage">마이페이지</v-btn>
		<!-- <v-btn text @click="goNotifications">알림</v-btn> -->
		<notification-bell class="mr-2" />
		<v-btn text @click="goToLogin">로그인</v-btn>
		<v-btn text @click="goToSignup">회원가입</v-btn>
	</v-app-bar>
</template>

<script setup>
import NotificationBell from '@/components/layout/NotificationBell.vue'
import { useRouter } from 'vue-router'
// router 인스턴스 생성
const router = useRouter()

// 페이지 이동 함수
function goHome() {
	router.push('/')
}
function goChallenges() {
	router.push('/challenges')
}
function goMyPage() {
	router.push('/mypage')
}
function goToLogin() {
	router.push('/login')
}
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
