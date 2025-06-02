<template>
	<v-container>
		<!-- 헤더 -->
		<v-row class="mb-6" align="center">
			<v-col cols="12" class="d-flex align-center justify-space-between">
				<div class="d-flex align-center">
					<v-avatar size="48" color="pink-lighten-4" class="mr-4">
						<v-icon color="pink" size="28">mdi-heart</v-icon>
					</v-avatar>
					<div>
						<h1 class="text-h4 font-weight-bold mb-1">내 챌린지</h1>
						<p class="text-body-2 text-grey ma-0">
							관심 챌린지와 참여 요청중인 챌린지
						</p>
					</div>
				</div>

				<v-btn
					color="primary"
					variant="outlined"
					size="large"
					rounded="lg"
					@click="goToList"
				>
					<v-icon left>mdi-format-list-bulleted</v-icon>
					전체 챌린지
				</v-btn>
			</v-col>
		</v-row>

		<!-- 챌린지 현황 요약 카드 -->
		<!-- 챌린지 현황 요약 카드 -->
		<v-row class="mb-4">
			<v-col cols="12" md="6">
				<v-card color="pink-lighten-5" elevation="1">
					<v-card-text class="text-center">
						<v-icon size="32" color="pink" class="mb-2">mdi-heart</v-icon>
						<div class="text-h6 font-weight-bold">
							{{ favoriteCount }}
						</div>
						<div class="text-caption">관심 챌린지</div>
					</v-card-text>
				</v-card>
			</v-col>
			<v-col cols="12" md="6">
				<v-card color="orange-lighten-5" elevation="1">
					<v-card-text class="text-center">
						<v-icon size="32" color="orange" class="mb-2"
							>mdi-clock-outline</v-icon
						>
						<div class="text-h6 font-weight-bold">
							{{ requestedCount }}
						</div>
						<div class="text-caption">요청 중</div>
					</v-card-text>
				</v-card>
			</v-col>
		</v-row>

		<!-- 로딩 -->
		<v-row v-if="isLoadingFavorites" justify="center" class="my-12">
			<v-progress-circular indeterminate color="primary" size="64" />
		</v-row>

		<!-- 빈 상태 -->
		<v-row v-else-if="favorites.length === 0" justify="center" class="my-12">
			<v-col cols="12" md="6" class="text-center">
				<v-icon size="80" color="grey-lighten-2" class="mb-4">
					mdi-heart-outline
				</v-icon>
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

		<!-- 챌린지 카드 -->
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
					<!-- 카드 헤더 -->
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

							<!-- 챌린지 타입 표시 (관심 vs 요청중) -->
							<v-chip
								v-if="isRequestedOnly(fav)"
								size="x-small"
								color="orange"
								variant="flat"
								class="ml-1"
							>
								요청중
							</v-chip>
						</div>

						<!--  하트 버튼 (관심 챌린지만 해당) -->
						<v-btn
							v-if="!isRequestedOnly(fav)"
							icon
							size="small"
							@click.stop="onToggleFavorite(fav.challenge.challengeId)"
						>
							<v-icon color="red">mdi-heart</v-icon>
						</v-btn>
					</v-card-title>

					<!-- 카드 내용 -->
					<v-card-text class="flex-grow-1 pt-1">
						<h3 class="text-h6 mb-3">{{ fav.challenge.title }}</h3>
						<p class="text-body-2 text-grey-darken-1 mb-4">
							{{ truncateDescription(fav.challenge.description) }}
						</p>
					</v-card-text>

					<!-- 정보 -->
					<div class="px-4 pb-2">
						<!-- 기간 정보 -->
						<div class="d-flex align-center mb-2">
							<v-icon size="16" class="mr-2">mdi-calendar-range</v-icon>
							<span class="text-caption">
								{{ formatDate(fav.challenge.startDate) }} ~
								{{ formatDate(fav.challenge.endDate) }}
							</span>
						</div>
						<!-- 생성자 정보 -->
						<div class="d-flex align-center mb-2">
							<v-icon size="16" class="mr-2">mdi-account</v-icon>
							<span class="text-caption">
								{{ fav.challenge.creatorNickname }}
							</span>
						</div>
						<!-- 등록/요청 날짜 -->
						<div class="d-flex align-center mb-3">
							<v-icon size="16" class="mr-2">
								{{
									isRequestedOnly(fav)
										? 'mdi-clock-outline'
										: 'mdi-heart-outline'
								}}
							</v-icon>
							<span class="text-caption">
								{{ isRequestedOnly(fav) ? '요청일' : '관심 등록일' }}:
								{{ formatDate(fav.createdDate) }}
							</span>
						</div>
					</div>

					<!-- 카드 액션 -->
					<v-card-actions class="pt-0">
						<!-- 참여 상태 버튼 -->
						<template v-if="fav.approved">
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

						<template v-else-if="fav.requested">
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

		<!-- 새로고침 버튼 -->
		<v-row justify="center" class="mt-6">
			<v-btn
				variant="outlined"
				color="primary"
				:loading="isLoadingFavorites"
				@click="fetchFavorites"
			>
				<v-icon left>mdi-refresh</v-icon>
				새로고침
			</v-btn>
		</v-row>
	</v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
	getFavoriteChallenges, // 🔥 백엔드에서 getFavoritesAndRequestedChallenges 사용
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

// 📊 현황 요약 계산
const favoriteCount = computed(() => {
	return favorites.value.filter((fav) => !isRequestedOnly(fav)).length
})

const requestedCount = computed(() => {
	return favorites.value.filter((fav) => fav.requested && !fav.approved).length
})

const approvedCount = computed(() => {
	return favorites.value.filter((fav) => fav.approved).length
})

// 🔍 요청중인 챌린지인지 판별하는 함수
function isRequestedOnly(fav) {
	// 관심 챌린지가 아니면서 요청중인 경우 (= 순수 요청중인 챌린지)
	return fav.requested && !fav.challenge.isFavorite
}

// 설명 글자 수 제한
function truncateDescription(description) {
	if (!description) return ''
	return description.length > 150
		? description.substring(0, 150) + '...'
		: description
}

// 날짜 포맷터
function formatDate(date) {
	if (!date) return '-'
	return new Date(date).toLocaleDateString('ko-KR', {
		year: 'numeric',
		month: 'short',
		day: 'numeric',
	})
}

// 카테고리명 반환
function categoryName(id) {
	const cat = categories.value.find((x) => x.categoryId === id)
	return cat ? cat.categoryName : '기타'
}

// 상세 페이지로 이동
function goToDetail(challengeId) {
	router.push({
		name: 'challenge-overview',
		params: { id: challengeId },
	})
}

// 챌린지 목록으로 이동
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
				map[p.challengeId] = {
					id: p.participationId,
					status: p.status,
				}
			}
		})
		myPartsMap.value = map
	} catch (err) {
		handleApiError(err)
	}
}

// 🔥 관심 챌린지 + 요청중인 챌린지 목록 가져오기 (통합)
async function fetchFavorites() {
	isLoadingFavorites.value = true
	try {
		await fetchMyParticipations()

		// ✅ 백엔드의 새로운 통합 API 사용
		const data = await getFavoriteChallenges() // 실제로는 getFavoritesAndRequestedChallenges를 호출

		favorites.value = data.map((item) => {
			const cid = item.challenge.challengeId
			const part = myPartsMap.value[cid] || {}
			return {
				...item,
				requested: part.status === 'REQUESTED',
				approved: part.status === 'APPROVED',
			}
		})

		console.log('📋 로딩된 챌린지 목록:', favorites.value.length)
		console.log('❤️ 관심 챌린지:', favoriteCount.value)
		console.log('⏳ 요청중:', requestedCount.value)
		console.log('✅ 참여중:', approvedCount.value)
	} catch (err) {
		console.error('관심 챌린지 로딩 실패:', err)
		handleApiError(err)
	} finally {
		isLoadingFavorites.value = false
	}
}

// 관심 챌린지 토글(취소) - 관심 챌린지만 해당
async function onToggleFavorite(challengeId) {
	try {
		await toggleFavoriteChallenge(challengeId)
		await fetchFavorites() // 새로고침하여 최신 상태 반영
	} catch (err) {
		handleApiError(err)
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
		await fetchFavorites() // 새로고침하여 요청 상태 반영
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
		await fetchFavorites() // 새로고침하여 최신 상태 반영
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
/* Vuetify 기본 스타일만 사용하므로 추가 CSS 최소화 */
.challenge-card {
	cursor: default; /* 일반 커서 강제 적용 */
	transition: transform 0.2s ease, box-shadow 0.3s ease;
}

.challenge-card:hover {
	transform: translateY(-4px);
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12) !important;
}

/* 요약 카드 호버 효과 */
.v-card:hover {
	transform: translateY(-2px);
	transition: transform 0.2s ease;
}

/* 텍스트 말줄임 처리 개선 */
.text-h6 {
	display: -webkit-box;

	-webkit-box-orient: vertical;
	overflow: hidden;
	line-height: 1.3;
}
</style>
