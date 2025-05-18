<!-- src/views/HomeView.vue -->
<template>
	<div class="home-view">
		<!-- 1) 슬라이드 -->
		<ChallengeCarousel :challenges="Cards.slice(0, 10)" />

		<br />
		<hr />
		<!-- 2) 사이트 설명 섹션 -->
		<AboutSection />
		<hr />
		<!-- 3) 랭킹 섹션 -->
		<RankingSection />
	</div>
</template>

<script setup>
import ChallengeCarousel from '@/components/sections/ChallengeCarousel.vue'
import { Cards } from '@/composables/useChallenges'
import AboutSection from '@/components/sections/AboutSection.vue'
import RankingSection from '@/components/sections/RankingSection.vue'
</script>

<style scoped>
.home-view {
	padding: 0 1rem;
}
</style>
