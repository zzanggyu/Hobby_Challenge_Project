<template>
	<div class="home-view">
		<ChallengeCarousel
			:challenges="popularChallenges"
			:categories="categories"
		/>
		<br />
		<hr />
		<AboutSection />
		<hr />
		<!-- <RankingSection /> -->
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ChallengeCarousel from '@/components/sections/ChallengeCarousel.vue'
import AboutSection from '@/components/sections/AboutSection.vue'
// import RankingSection from '@/components/sections/RankingSection.vue'
import { getPopularChallenges } from '@/services/challengeService'
import { getCategories } from '../services/categoryService'

const popularChallenges = ref([])
const categories = ref([]) // ✅ 카테고리 상태 선언
const loading = ref(false)

onMounted(async () => {
	loading.value = true
	try {
		// ✅ 인기 챌린지 + 카테고리 동시 요청 (병렬로 요청해도 됨)
		;[popularChallenges.value, categories.value] = await Promise.all([
			getPopularChallenges(10),
			getCategories(),
		])
		// console.log('카테고리 데이터:', categories.value)
		// console.log('챌린지 데이터:', popularChallenges.value)
	} catch (e) {
		console.error('데이터 로딩 실패:', e)

		// 기본값 설정
		popularChallenges.value = []
		categories.value = []
	}
	loading.value = false
})
</script>
