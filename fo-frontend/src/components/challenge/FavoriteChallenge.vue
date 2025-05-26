<template>
	<v-container>
		<v-row class="mb-4" align="center">
			<v-col cols="12" class="d-flex align-center justify-space-between">
				<h2 class="text-h5 mb-0">관심 챌린지</h2>
				<!-- 챌린지 목록으로 돌아가기 버튼 -->
				<v-btn
					color="secondary"
					variant="tonal"
					size="large"
					@click="goToList"
					class="ml-auto"
				>
					<v-icon left>mdi-arrow-left</v-icon>
					챌린지 목록으로
				</v-btn>
			</v-col>
		</v-row>

		<!-- 로딩 스피너 -->
		<v-row v-if="isLoadingFavorites" justify="center">
			<v-progress-circular indeterminate />
		</v-row>

		<v-row align="stretch">
			<template v-for="fav in favorites" :key="fav.challengeId">
				<v-col v-if="fav.challenge" cols="12" md="6" lg="4" class="d-flex">
					<v-card
						elevation="4"
						height="310"
						width="100%"
						rounded="xl"
						:style="{
							border: '1px solid rgba(165, 243, 212, 0.6)',
							backgroundColor: '#f9fdfc',
						}"
					>
						<v-card-title class="px-4">
							<div
								class="d-flex w-100 justify-space-between align-center"
							>
								<span class="text-h6">{{ fav.challenge.title }}</span>
								<v-icon
									color="red"
									@click="onToggleFavorite(fav.challenge.challengeId)"
									style="cursor: pointer"
									>mdi-heart</v-icon
								>
							</div>
						</v-card-title>

						<v-divider color="green lighten-2" />

						<v-card-text class="flex-grow-1">
							{{ fav.challenge.description }}
						</v-card-text>

						<v-card-subtitle>
							기간: {{ formatDate(fav.challenge.startDate) }} ~
							{{ formatDate(fav.challenge.endDate) }}
						</v-card-subtitle>

						<v-card-actions class="mt-auto">
							<v-chip small outlined color="green-accent-4">
								{{ categoryName(fav.challenge.categoryId) }}
							</v-chip>
							<small class="grey--text text--darken-1">
								by: {{ fav.challenge.creatorNickname }}
							</small>
							<v-spacer />
							<v-btn
								small
								:color="fav.requested ? 'black' : 'secondary'"
								:loading="
									isJoining && targetId === fav.challenge.challengeId
								"
								:disabled="fav.requested || isJoining"
								@click="onJoin(fav.challenge.challengeId)"
							>
								{{ fav.requested ? '요청중' : '참여하기' }}
							</v-btn>
						</v-card-actions>
					</v-card>
				</v-col>
			</template>
		</v-row>
	</v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import {
	getFavoriteChallenges,
	toggleFavoriteChallenge,
	joinChallenge,
	getMyParticipations,
} from '@/services/challengeService'
import { getCategories } from '@/services/categoryService'
import { useAuthStore } from '@/stores/auth'
import { handleApiError } from '@/utils/apiError'

const authStore = useAuthStore()
const router = useRouter()

const favorites = ref([])
const categories = ref([])

// 내 참여 요청/승인된 챌린지 ID 저장용 Set 선언
const myParts = ref(new Set())

const isLoadingFavorites = ref(false)
const isToggling = ref(false)
const isJoining = ref(false)
const targetId = ref(null)

function categoryName(id) {
	const cat = categories.value.find((x) => x.categoryId === id)
	return cat ? cat.name : '알 수 없음'
}

// 날짜 포맷
function formatDate(d) {
	return d ? new Date(d).toLocaleDateString() : '-'
}

// 챌린지 목록으로 돌아가기기
function goToList() {
	router.push({ name: 'challenge-list' })
}

// 내 참여내역 불러와 myParts Set을 채우기
async function fetchMyParticipations() {
	const userId = authStore.user?.userId
	if (!userId) return
	try {
		const res = await getMyParticipations(userId)
		console.log('fetchMyParticipations response:', res)
		const list = Array.isArray(res)
			? res
			: res.items || res.participations || []
		myParts.value = new Set(
			list.filter((p) => p.status !== 'REJECTED').map((p) => p.challengeId)
		)
	} catch (err) {
		handleApiError(err)
	}
}

// 내 관심 챌린지 목록 가져오기
async function fetchFavorites() {
	isLoadingFavorites.value = true
	try {
		const data = await getFavoriteChallenges()
		favorites.value = data.map((item) => ({
			...item,
			requested: myParts.value.has(item.challenge.challengeId),
		}))
	} catch (err) {
		handleApiError(err)
	} finally {
		isLoadingFavorites.value = false
	}
}

// 관심 챌린지 토글(취소)
async function onToggleFavorite(challengeId) {
	isToggling.value = true
	try {
		await toggleFavoriteChallenge(challengeId)
		favorites.value = favorites.value.filter(
			(f) => f.challenge.challengeId !== challengeId
		)
	} catch (err) {
		handleApiError(err)
	} finally {
		isToggling.value = false
	}
}

// 참여하기 버튼
async function onJoin(challengeId) {
	const userId = authStore.user?.userId
	if (!userId) {
		alert('로그인 후 참여 가능합니다.')
		return router.push({ name: 'login' })
	}

	isJoining.value = true
	targetId.value = challengeId
	try {
		await joinChallenge(userId, challengeId)
		alert('참여 요청이 완료되었습니다!')
		const fav = favorites.value.find(
			(x) => x.challenge.challengeId === challengeId
		)
		if (fav) fav.requested = true
	} catch (err) {
		handleApiError(err)
	} finally {
		isJoining.value = false
		targetId.value = null
	}
}

// 카테고리 이름 매핑
async function loadCategories() {
	try {
		categories.value = await getCategories()
	} catch (e) {
		console.error('카테고리 로드 실패', e)
	}
}

// 마운트 시 초기 로드
onMounted(async () => {
	await authStore.fetchUser()
	await fetchMyParticipations() // 수정: 먼저 내 참여내역 로드
	await Promise.all([fetchFavorites(), loadCategories()])
})
</script>

<style scoped></style>
