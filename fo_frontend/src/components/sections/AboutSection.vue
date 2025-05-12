<!-- src/components/sections/AboutSection.vue -->
<template>
	<v-container class="py-16">
		<!-- 1. 소개 헤더 -->
		<div class="text-center mb-10">
			<h2 class="text-h3 font-weight-bold mb-4">
				취미를 기록하고, 함께 성장하는 공간
			</h2>
			<p
				class="text-body-1 mx-auto"
				style="max-width: 600px; line-height: 1.6"
			>
				HobbyChallenge는 여러분의 취미 여정을 쉽고 즐겁게 만들어 줍니다.
				목표를 설정하고, 매일 인증하며, 친구들과 응원 메시지를 주고받으세요.
				나만의 성장 스토리가 쌓일수록 더 큰 동기부여가 찾아옵니다!
			</p>
		</div>

		<!-- 2. 기능 카드 4종 -->
		<v-row justify="center" align="stretch" dense>
			<v-col cols="12" sm="6" md="3" v-for="f in features" :key="f.title">
				<v-card class="pa-8 text-center" elevation="3">
					<v-icon size="48" class="mb-4">{{ f.icon }}</v-icon>
					<h3 class="text-h6 font-weight-bold mb-2">{{ f.title }}</h3>
					<p class="text-body-2" style="line-height: 1.5">{{ f.desc }}</p>
				</v-card>
			</v-col>
		</v-row>
	</v-container>
</template>

<script setup>
const features = [
	{
		icon: 'mdi-target',
		title: '개인 맞춤 목표',
		desc: '나에게 딱 맞는 챌린지를 골라 시작할 수 있어요.',
	},
	{
		icon: 'mdi-calendar-check',
		title: '데일리 인증',
		desc: '사진·메모로 매일의 성취를 간편하게 기록하세요.',
	},
	{
		icon: 'mdi-account-group',
		title: '커뮤니티 응원',
		desc: '친구들과 함께 참여하고, 서로의 성장을 응원해요.',
	},
	{
		icon: 'mdi-chart-line',
		title: '내 성장 통계',
		desc: '주간·월간 통계로 내 취미 여정을 한눈에 확인합니다.',
	},
]
</script>

<style scoped>
.home-view + .v-container {
	background: #fafafa; /* 섹션 배경색 추가로 분리감 주기 */
}
</style>
