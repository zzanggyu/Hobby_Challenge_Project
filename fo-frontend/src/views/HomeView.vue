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
import { ref, onMounted, computed, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import ChallengeCarousel from '@/components/sections/ChallengeCarousel.vue'
import AboutSection from '@/components/sections/AboutSection.vue'
import RankingSection from '@/components/sections/RankingSection.vue'
import {
	getPopularChallenges,
	getFavoriteChallenges,
} from '@/services/challengeService'
import { getCategories } from '../services/categoryService'

const auth = useAuthStore()
const popularChallenges = ref([])
const categories = ref([]) // 카테고리 상태 선언
const loading = ref(false)

// 관심 챌린지 상태 업데이트 핸들러
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

// 로그인 상태 변화 감지 추가
watch(isLoggedIn, async (newVal, oldVal) => {
	if (newVal !== oldVal) {
		console.log('로그인 상태 변경 감지:', newVal)
		await loadPopularChallengesWithFavorites()
	}
})

// 새로운 함수 추가
async function loadPopularChallengesWithFavorites() {
	loading.value = true
	try {
		//  인기 챌린지 가져오기
		const challenges = await getPopularChallenges(10)

		//  로그인 상태인 경우에만 좋아요 상태 보정
		if (auth.isAuthenticated && auth.user?.userId) {
			try {
				// 내가 좋아요한 챌린지 목록 가져오기
				const favoriteData = await getFavoriteChallenges()

				// 좋아요한 챌린지 ID 목록 추출
				const favoriteIds = favoriteData.map((fav) =>
					fav.challenge ? fav.challenge.challengeId : fav.challengeId
				)

				// 3. 인기 챌린지별로 좋아요 상태 보정
				popularChallenges.value = challenges.map((challenge) => {
					const correctedIsFavorite = favoriteIds.includes(
						challenge.challengeId
					)

					return {
						...challenge,
						isFavorite: correctedIsFavorite,
					}
				})
			} catch (favoriteError) {
				// 실패시 백엔드 값 그대로 사용
				popularChallenges.value = challenges.map((challenge) => ({
					...challenge,
					isFavorite: false,
				}))
			}
		} else {
			// 비로그인 상태

			popularChallenges.value = challenges.map((challenge) => ({
				...challenge,
				isFavorite: false,
			}))
		}
	} catch (e) {
		popularChallenges.value = []
	} finally {
		loading.value = false
	}
}

onMounted(async () => {
	loading.value = true
	try {
		// 사용자 정보 먼저 확인
		await auth.fetchUser()

		// 카테고리와 인기 챌린지(+좋아요 상태) 동시 로딩
		const [categoriesData] = await Promise.all([
			getCategories(),
			loadPopularChallengesWithFavorites(),
		])

		categories.value = categoriesData
	} catch (e) {
		popularChallenges.value = []
		categories.value = []
	} finally {
		loading.value = false
	}
})
</script>
