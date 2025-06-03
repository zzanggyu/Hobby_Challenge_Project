<template>
	<v-container>
		<!-- 헤더 -->
		<v-sheet
			color="pink-lighten-5"
			class="pa-6 mb-6"
			rounded="xl"
			elevation="2"
		>
			<v-row align="center">
				<v-col cols="12" class="d-flex align-center justify-space-between">
					<div class="d-flex align-center">
						<v-avatar size="48" color="pink-lighten-4" class="mr-4">
							<v-icon color="pink" size="28">mdi-heart</v-icon>
						</v-avatar>
						<div>
							<div color="red">
								<h1 class="text-h4 font-weight-bold mb-1">내 챌린지</h1>
							</div>
							<!-- <p class="text-body-2 text-grey ma-0">
							관심 챌린지 ({{ favoriteOnlyCount }}/10), 참여중 ({{
								participatingCount
							}}), 요청중 ({{ requestingCount }})
						</p> -->
						</div>
					</div>

					<!-- <v-btn
					color="primary"
					variant="outlined"
					size="large"
					rounded="lg"
					@click="goToList"
				>
					<v-icon left>mdi-format-list-bulleted</v-icon>
					전체 챌린지
				</v-btn> -->
				</v-col>
			</v-row>
		</v-sheet>

		<!--  현황 요약 카드 개선 -->
		<v-row class="mb-4">
			<v-col cols="12" md="4">
				<v-card color="pink-lighten-5" elevation="1">
					<v-card-text class="text-center">
						<v-icon size="32" color="pink" class="mb-2">mdi-heart</v-icon>
						<div class="text-h6 font-weight-bold">
							{{ favoriteOnlyCount }}/10
						</div>
						<div class="text-caption">관심 챌린지</div>
					</v-card-text>
				</v-card>
			</v-col>
			<v-col cols="12" md="4">
				<v-card color="green-lighten-5" elevation="1">
					<v-card-text class="text-center">
						<v-icon size="32" color="green" class="mb-2"
							>mdi-check-circle</v-icon
						>
						<div class="text-h6 font-weight-bold">
							{{ participatingCount }}
						</div>
						<div class="text-caption">참여중</div>
					</v-card-text>
				</v-card>
			</v-col>
			<v-col cols="12" md="4">
				<v-card color="orange-lighten-5" elevation="1">
					<v-card-text class="text-center">
						<v-icon size="32" color="orange" class="mb-2"
							>mdi-clock-outline</v-icon
						>
						<div class="text-h6 font-weight-bold">
							{{ requestingCount }}
						</div>
						<div class="text-caption">요청중</div>
					</v-card-text>
				</v-card>
			</v-col>
		</v-row>

		<!-- 로딩/빈 상태는 기존과 동일 -->
		<v-row v-if="isLoadingFavorites" justify="center" class="my-12">
			<v-progress-circular indeterminate color="primary" size="64" />
		</v-row>

		<v-row v-else-if="favorites.length === 0" justify="center" class="my-12">
			<v-col cols="12" md="6" class="text-center">
				<v-icon size="80" color="grey-lighten-2" class="mb-4"
					>mdi-heart-outline</v-icon
				>
				<h2 class="text-h5 mb-4">아직 관심 챌린지가 없어요</h2>
				<p class="text-body-1 text-grey mb-6">
					마음에 드는 챌린지에 하트를 눌러 저장하거나<br />
					새로운 챌린지에 참여 요청을 해보세요!
				</p>
				<v-btn color="primary" size="large" @click="goToList">
					<v-icon left>mdi-format-list-bulleted</v-icon>
					챌린지 둘러보기
				</v-btn>
			</v-col>
		</v-row>

		<!-- 챌린지 카드 - 상태별 구분  -->
		<v-row v-else>
			<v-col
				v-for="fav in favorites"
				:key="fav.challengeId"
				cols="12"
				md="6"
				lg="4"
			>
				<v-card
					v-if="fav.challenge"
					elevation="2"
					class="d-flex flex-column challenge-card"
					height="380"
				>
					<!-- 카드 헤더 개선 -->
					<v-card-title
						class="d-flex justify-space-between align-center pb-2"
					>
						<div class="d-flex align-center">
							<v-chip
								size="small"
								color="primary"
								variant="outlined"
								class="mr-2"
							>
								{{ categoryName(fav.challenge.categoryId) }}
							</v-chip>

							<!-- 상태별 칩 표시  -->
							<v-chip
								v-if="fav.participating"
								size="x-small"
								color="success"
								variant="flat"
								class="ml-1"
							>
								<v-icon left size="12">mdi-check-circle</v-icon>
								참여중
							</v-chip>

							<v-chip
								v-else-if="fav.requesting"
								size="x-small"
								color="orange"
								variant="flat"
								class="ml-1"
							>
								<v-icon left size="12">mdi-clock-outline</v-icon>
								요청중
							</v-chip>

							<v-chip
								v-else
								size="x-small"
								color="pink"
								variant="flat"
								class="ml-1"
							>
								<v-icon left size="12">mdi-heart</v-icon>
								관심
							</v-chip>
						</div>

						<!-- 하트 버튼 (관심 챌린지만 해당) -->
						<v-btn
							v-if="!fav.participating && !fav.requesting"
							icon
							size="small"
							@click.stop="onToggleFavorite(fav.challenge.challengeId)"
						>
							<v-icon color="red">mdi-heart</v-icon>
						</v-btn>
					</v-card-title>

					<!-- 카드 내용은 기존과 동일 -->
					<v-card-text class="flex-grow-1 pt-1">
						<h3 class="text-h6 mb-3">{{ fav.challenge.title }}</h3>
						<p class="text-body-2 text-grey-darken-1 mb-4">
							{{ truncateDescription(fav.challenge.description) }}
						</p>
					</v-card-text>

					<!-- 정보 섹션 -->
					<div class="px-4 pb-2">
						<div class="d-flex align-center mb-2">
							<v-icon size="16" class="mr-2">mdi-calendar-range</v-icon>
							<span class="text-caption">
								{{ formatDate(fav.challenge.startDate) }} ~
								{{ formatDate(fav.challenge.endDate) }}
							</span>
						</div>
						<div class="d-flex align-center mb-2">
							<v-icon size="16" class="mr-2">mdi-account</v-icon>
							<span class="text-caption">{{
								fav.challenge.creatorNickname
							}}</span>
						</div>
						<div class="d-flex align-center mb-3">
							<!-- 상태별 아이콘 표시 -->
							<v-icon size="16" class="mr-2">
								{{
									fav.participating
										? 'mdi-check-circle'
										: fav.requesting
										? 'mdi-clock-outline'
										: 'mdi-heart-outline'
								}}
							</v-icon>
							<span class="text-caption">
								{{
									fav.participating
										? '참여일'
										: fav.requesting
										? '요청일'
										: '관심 등록일'
								}}:
								{{ formatDate(fav.createdDate) }}
							</span>
						</div>
					</div>

					<!-- 카드 액션 - 기존과 동일하지만 상태별 버튼 로직 개선 -->
					<v-card-actions class="pt-0">
						<template v-if="fav.participating">
							<v-btn
								color="success"
								variant="tonal"
								size="small"
								disabled
								@click.stop
							>
								<v-icon left size="16">mdi-check</v-icon>
								참여 중
							</v-btn>
						</template>

						<template v-else-if="fav.requesting">
							<v-btn
								color="warning"
								variant="tonal"
								size="small"
								:loading="
									isJoining && targetId === fav.challenge.challengeId
								"
								@click.stop="onCancel(fav.challenge.challengeId)"
							>
								<v-icon left size="16">mdi-close</v-icon>
								요청 취소
							</v-btn>
						</template>

						<template v-else>
							<v-btn
								color="primary"
								variant="tonal"
								size="small"
								:loading="
									isJoining && targetId === fav.challenge.challengeId
								"
								@click.stop="onJoin(fav.challenge.challengeId)"
							>
								<v-icon left size="16">mdi-account-plus</v-icon>
								참여하기
							</v-btn>
						</template>

						<v-spacer />

						<v-btn
							variant="text"
							size="small"
							@click.stop="goToDetail(fav.challenge.challengeId)"
						>
							상세보기
							<v-icon right size="16">mdi-arrow-right</v-icon>
						</v-btn>
					</v-card-actions>
				</v-card>
			</v-col>
		</v-row>
	</v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
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

// 상태 관리
const favorites = ref([])
const categories = ref([])
const myPartsMap = ref({})
const isLoadingFavorites = ref(false)
const isJoining = ref(false)
const targetId = ref(null)

// 상태별 개수 계산 개선
const favoriteOnlyCount = computed(() => {
	return favorites.value.filter(
		(fav) => !fav.participating && !fav.requesting // 순수 관심 챌린지만
	).length
})

const participatingCount = computed(() => {
	return favorites.value.filter((fav) => fav.participating).length
})

const requestingCount = computed(() => {
	return favorites.value.filter((fav) => fav.requesting).length
})

// 기존 함수들은 그대로 유지...
function truncateDescription(description) {
	if (!description) return ''
	return description.length > 150
		? description.substring(0, 150) + '...'
		: description
}

function formatDate(date) {
	if (!date) return '-'
	return new Date(date).toLocaleDateString('ko-KR', {
		year: 'numeric',
		month: 'short',
		day: 'numeric',
	})
}

function categoryName(id) {
	const cat = categories.value.find((x) => x.categoryId === id)
	return cat ? cat.categoryName : '기타'
}

function goToDetail(challengeId) {
	router.push({ name: 'challenge-overview', params: { id: challengeId } })
}

function goToList() {
	router.push({ name: 'challenge-list' })
}

// 내 참여내역 불러오기
async function fetchMyParticipations() {
	const userId = authStore.user?.userId
	if (!userId) return
	try {
		const res = await getMyParticipations(userId)
		const list = Array.isArray(res)
			? res
			: res.items || res.participations || []
		const map = {}
		list.forEach((p) => {
			if (p.status !== 'REJECTED') {
				map[p.challengeId] = { id: p.participationId, status: p.status }
			}
		})
		myPartsMap.value = map
	} catch (err) {
		handleApiError(err)
	}
}

//  관심 챌린지 + 참여중 챌린지 목록 가져오기 - 백엔드에서 상태 정보 포함
async function fetchFavorites() {
	isLoadingFavorites.value = true
	try {
		await fetchMyParticipations()

		// 백엔드의 새로운 통합 API 사용 (상태 정보 포함)
		const data = await getFavoriteChallenges()

		favorites.value = data.map((item) => {
			const cid = item.challenge.challengeId
			const part = myPartsMap.value[cid] || {}
			return {
				...item,
				//  백엔드에서 설정한 상태 정보를 우선 사용
				participating: item.participating || part.status === 'APPROVED',
				requesting: item.requesting || part.status === 'REQUESTED',
			}
		})
	} catch (err) {
		console.error('관심 챌린지 로딩 실패:', err)
		handleApiError(err)
	} finally {
		isLoadingFavorites.value = false
	}
}

//  관심 챌린지 토글 - 10개 제한
async function onToggleFavorite(challengeId) {
	try {
		await toggleFavoriteChallenge(challengeId)
		await fetchFavorites()
	} catch (err) {
		// 10개 제한 에러 처리
		if (err.response?.data?.errorCode === 'FAVORITE_LIMIT_EXCEEDED') {
			alert('관심 챌린지는 최대 10개까지만 등록할 수 있습니다.')
		} else {
			handleApiError(err)
		}
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
		await joinChallenge(challengeId)
		alert('참여 요청이 완료되었습니다!')
		await fetchFavorites()
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

	const participationId = myPartsMap.value[challengeId]?.id
	if (!participationId) {
		alert('취소할 요청을 찾을 수 없습니다.')
		return
	}

	isJoining.value = true
	targetId.value = challengeId
	try {
		await cancelParticipation(challengeId, participationId)
		await fetchFavorites()
		alert('참여 요청이 취소되었습니다.')
	} catch (e) {
		handleApiError(e)
	} finally {
		isJoining.value = false
		targetId.value = null
	}
}

// 카테고리 목록 가져오기
async function loadCategories() {
	try {
		categories.value = await getCategories()
	} catch (e) {
		console.error('카테고리 로드 실패', e)
	}
}

// 컴포넌트 마운트 시 초기 로드
onMounted(async () => {
	await authStore.fetchUser()
	await Promise.all([fetchFavorites(), loadCategories()])
})
</script>

<style scoped>
/* 기존 스타일 + 추가 */
.challenge-card {
	cursor: default;
	transition: transform 0.2s ease, box-shadow 0.3s ease;
}

.challenge-card:hover {
	transform: translateY(-4px);
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12) !important;
}

/* 상태별 칩 애니메이션 */
.v-chip {
	transition: all 0.3s ease;
}

.v-chip:hover {
	transform: scale(1.05);
}
</style>
