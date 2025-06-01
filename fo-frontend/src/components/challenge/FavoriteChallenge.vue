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
						<h1 class="text-h4 font-weight-bold mb-1">관심 챌린지</h1>
						<p class="text-body-2 text-grey ma-0">
							저장한 챌린지와 요청중인 챌린지
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
					마음에 드는 챌린지에 하트를 눌러 저장해보세요!
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
					class="d-flex flex-column"
					height="350"
				>
					<!-- 카드 헤더 -->
					<v-card-title class="d-flex justify-space-between align-center">
						<v-chip
							size="small"
							color="primary"
							variant="outlined"
							class="mr-2"
						>
							{{ categoryName(fav.challenge.categoryId) }}
						</v-chip>

						<v-btn
							icon
							size="small"
							@click.stop="onToggleFavorite(fav.challenge.challengeId)"
						>
							<v-icon color="red">mdi-heart</v-icon>
						</v-btn>
					</v-card-title>

					<!-- 카드 내용 -->
					<v-card-text class="flex-grow-1">
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
						<div class="d-flex align-center mb-3">
							<v-icon size="16" class="mr-2">mdi-account</v-icon>
							<span class="text-caption">
								{{ fav.challenge.creatorNickname }}
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
								승인됨
							</v-btn>
						</template>

						<template v-else-if="fav.requested">
							<v-btn
								color="error"
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
import { ref, onMounted } from 'vue'
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

// 설명 글자 수 제한
function truncateDescription(description) {
	if (!description) return ''
	return description.length > 200
		? description.substring(0, 200) + '...'
		: description
}

// 날짜 포맷터
function formatDate(date) {
	if (!date) return '-'
	return new Date(date).toLocaleDateString('ko-KR', {
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

// 관심 챌린지 목록 가져오기
async function fetchFavorites() {
	isLoadingFavorites.value = true
	try {
		await fetchMyParticipations()
		const data = await getFavoriteChallenges()
		favorites.value = data.map((item) => {
			const cid = item.challenge.challengeId
			const part = myPartsMap.value[cid] || {}
			return {
				...item,
				requested: part.status === 'REQUESTED',
				approved: part.status === 'APPROVED',
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
	try {
		await toggleFavoriteChallenge(challengeId)
		await fetchFavorites()
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
/* Vuetify 기본 스타일만 사용하므로 추가 CSS 최소화 */
.v-card {
	cursor: default; /* 일반 커서 강제 적용 */
}
.v-card:hover {
	transform: translateY(-2px);
	transition: transform 0.2s ease;
}
</style>
