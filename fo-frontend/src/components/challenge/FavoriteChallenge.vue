<template>
	<v-container>
		<v-row class="mb-4" align="center">
			<v-col cols="12" class="d-flex align-center justify-space-between">
				<!-- 새 헤더 스타일 -->
				<div class="title d-flex align-center">
					<v-icon class="mr-3" size="36" color="white"
						>mdi-star-outline</v-icon
					>
					<span class="title-text">관심 챌린지</span>
				</div>
				<v-btn size="large" class="favorite-btn" @click="goToList">
					<v-icon left>mdi-format-list-bulleted</v-icon>
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
						outlined
						class="d-flex flex-column"
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

						<v-card-actions class="mt-auto px-4">
							<v-chip small outlined color="green-accent-4">
								{{ categoryName(fav.challenge.categoryId) }}
							</v-chip>
							<small class="grey--text text--darken-1"
								>by: {{ fav.challenge.creatorNickname }}</small
							>

							<v-spacer />

							<!--  승인됨 -->
							<template v-if="fav.approved">
								<v-btn small color="success" disabled>승인됨</v-btn>
							</template>
							<!-- 요청 중 -->
							<template v-else-if="fav.requested">
								<v-btn
									small
									color="error"
									:loading="
										isJoining &&
										targetId === fav.challenge.challengeId
									"
									:disabled="isJoining"
									@click="onCancel(fav.challenge.challengeId)"
								>
									요청 취소
								</v-btn>
							</template>
							<!--  아직 요청 안 함 -->
							<template v-else>
								<v-btn
									small
									color="secondary"
									:loading="
										isJoining &&
										targetId === fav.challenge.challengeId
									"
									:disabled="isJoining"
									@click="onJoin(fav.challenge.challengeId)"
								>
									참여하기
								</v-btn>
							</template>
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
} from '@/services/challengeService'
import {
	getMyParticipations,
	joinChallenge,
	cancelParticipation,
} from '@/services/participationService'
import { getCategories } from '@/services/categoryService'
import { useAuthStore } from '@/stores/auth'
import { handleApiError } from '@/utils/apiError'

const authStore = useAuthStore()
const router = useRouter()

const favorites = ref([])
const categories = ref([])

// 내 참여 요청/승인된 챌린지 ID 저장용 Set 선언
const myParts = ref(new Set())
const myPartsMap = ref({})

const isLoadingFavorites = ref(false)
const isToggling = ref(false)
const isJoining = ref(false)
const targetId = ref(null)

function categoryName(id) {
	const cat = categories.value.find((x) => x.categoryId === id)
	return cat ? cat.categoryName : '알 수 없음'
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
		const list = Array.isArray(res)
			? res
			: res.items || res.participations || []
		const set = new Set()
		const map = {}
		list.forEach((p) => {
			if (p.status !== 'REJECTED') {
				set.add(p.challengeId)
				map[p.challengeId] = {
					id: p.participationId,
					status: p.status,
				}
			}
		})
		myParts.value = set
		myPartsMap.value = map
	} catch (err) {
		handleApiError(err)
	}
}

async function fetchFavorites() {
	isLoadingFavorites.value = true
	try {
		// 내 참여 상태 동기화
		await fetchMyParticipations()
		// 즐겨찾기 목록 가져오기
		const data = await getFavoriteChallenges()
		favorites.value = data.map((item) => {
			const cid = item.challenge.challengeId
			const part = myPartsMap.value[cid] || {}
			return {
				...item,
				requested: part.status === 'REQUESTED',
				approved: part.status === 'APPROVED', // ← 여기 추가
			}
		})
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
		// 즐겨찾기 토글 후 최신 목록으로 동기화
		await fetchFavorites()
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

// 참여 취소
async function onCancel(challengeId) {
	if (!confirm('참여 요청을 정말 취소하시겠습니까?')) return
	const participationId = myPartsMap.value[challengeId]
	if (!participationId) {
		alert('취소할 요청을 찾을 수 없습니다.')
		return
	}

	isJoining.value = true
	targetId.value = challengeId
	try {
		await cancelParticipation(challengeId, participationId)
		// 취소 후 다시 내 요청 목록과 favorites 갱신
		await fetchMyParticipations()
		await fetchFavorites()
		alert('참여 요청이 취소되었습니다.')
	} catch (e) {
		handleApiError(e)
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

<style scoped>
.title {
	width: 100%;
	max-width: 1400px;
	margin-bottom: 1rem;
	padding: 0.75rem 1.5rem;
	background: linear-gradient(to right, #66bb6a 0%, #43a047 50%, #2e7d32 100%);
	border-radius: 8px;
	box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
	color: white;
}

.title-text {
	font-size: 1.75rem;
	font-weight: 600;
	text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
}

.favorite-btn {
	background: linear-gradient(135deg, #81c784 0%, #4caf50 100%);
	color: white !important;
	font-weight: 600;
	border-radius: 24px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
	text-transform: none;
	padding: 0.6rem 1.6rem;
	transition: transform 0.2s, box-shadow 0.2s;
}
.favorite-btn:hover {
	transform: translateY(-2px);
	box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}
.favorite-btn .v-icon {
	font-size: 1.2rem;
	margin-right: 0.5rem;
}

@media (max-width: 600px) {
	.title-text {
		font-size: 1.25rem;
	}
	.title {
		padding: 0.5rem 1rem;
	}
}
</style>
