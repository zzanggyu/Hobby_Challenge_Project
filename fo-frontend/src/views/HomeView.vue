<template>
	<div class="home-view">
		<ChallengeCarousel
			:challenges="popularChallenges"
			:categories="categories"
			@favorite-updated="handleFavoriteUpdate"
		/>
		<br />
		<v-divider></v-divider>
		<AboutSection />
		<v-divider></v-divider>
		<RankingSection />
	</div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import ChallengeCarousel from '@/components/sections/ChallengeCarousel.vue'
import AboutSection from '@/components/sections/AboutSection.vue'
import RankingSection from '@/components/sections/RankingSection.vue'
import { getPopularChallenges } from '@/services/challengeService'
import { getCategories } from '../services/categoryService'

const auth = useAuthStore()
const popularChallenges = ref([])
const categories = ref([]) // 카테고리 상태 선언
const loading = ref(false)

// 관심 챌린지 상태 업데이트 핸들러 (새로 추가)
const handleFavoriteUpdate = ({ challengeId, isFavorite }) => {
	console.log('부모에서 받은 하트 업데이트:', challengeId, isFavorite)

	// 해당 챌린지의 isFavorite 상태만 업데이트
	const challenge = popularChallenges.value.find(
		(c) => c.challengeId === challengeId
	)
	if (challenge) {
		challenge.isFavorite = isFavorite
		console.log(
			'부모 데이터 업데이트 완료:',
			challenge.title,
			'- isFavorite:',
			isFavorite
		)
	}
}

//  로그인 상태 변화 감지하여 데이터 새로고침
const isLoggedIn = computed(() => auth.isAuthenticated)

// 데이터 새로고침 함수 추가
async function refreshData() {
	try {
		loading.value = true
		popularChallenges.value = await getPopularChallenges(10)
	} catch (e) {
		console.error('데이터 새로고침 실패:', e)
		popularChallenges.value = []
	} finally {
		loading.value = false
	}
}

onMounted(async () => {
	loading.value = true
	try {
		// 인기 챌린지 + 카테고리
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
