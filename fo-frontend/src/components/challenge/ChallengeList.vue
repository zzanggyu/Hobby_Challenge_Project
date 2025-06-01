<template>
	<v-container>
		<!-- ① 타이틀 영역 - 개선된 네비게이션 -->
		<v-row class="mb-4" align="center">
			<v-col cols="12" class="d-flex align-center justify-space-between">
				<div class="title d-flex align-center">
					<v-icon class="mr-3" size="36" color="white"
						>mdi-format-list-bulleted</v-icon
					>
					<span class="title-text">챌린지 목록</span>
				</div>

				<!-- ✅ 새로운 네비게이션 메뉴 -->
				<div class="navigation-section">
					<v-menu offset-y>
						<template v-slot:activator="{ props }">
							<v-btn
								v-bind="props"
								color="white"
								variant="outlined"
								class="nav-menu-btn"
								size="large"
							>
								<v-icon left color="primary">mdi-view-dashboard</v-icon>
								<span class="nav-text">내 챌린지</span>
								<v-icon right color="primary">mdi-chevron-down</v-icon>
							</v-btn>
						</template>

						<v-list class="navigation-menu">
							<v-list-item
								class="nav-item"
								@click="goToFavoriteChallenge"
							>
								<v-list-item-prepend>
									<v-icon color="pink">mdi-heart</v-icon>
								</v-list-item-prepend>
								<v-list-item-title class="nav-item-title">
									관심 챌린지
								</v-list-item-title>
								<v-list-item-subtitle class="nav-item-subtitle">
									저장한 챌린지 보기
								</v-list-item-subtitle>
							</v-list-item>

							<v-divider class="my-1"></v-divider>

							<v-list-item class="nav-item" @click="goMyChallenge">
								<v-list-item-prepend>
									<v-icon color="orange">mdi-trophy</v-icon>
								</v-list-item-prepend>
								<v-list-item-title class="nav-item-title">
									참여 중인 챌린지
								</v-list-item-title>
								<v-list-item-subtitle class="nav-item-subtitle">
									현재 진행중인 챌린지
								</v-list-item-subtitle>
							</v-list-item>
						</v-list>
					</v-menu>
				</div>
			</v-col>
		</v-row>

		<!-- 검색 & 카테고리 필터 바 -->
		<v-row class="mb-6" align="center">
			<v-col cols="12" md="6">
				<v-text-field
					v-model="search"
					label="챌린지 제목 검색"
					prepend-inner-icon="mdi-magnify"
					rounded="lg"
					variant="outlined"
					clearable
					@keyup.enter="fetchChallenges"
				/>
			</v-col>
			<v-col cols="12" md="4">
				<v-select
					v-model="selectedCategory"
					:items="categories"
					item-title="categoryName"
					item-value="categoryId"
					label="카테고리 필터"
					prepend-inner-icon="mdi-filter"
					rounded="lg"
					variant="outlined"
					clearable
				/>
			</v-col>
		</v-row>

		<!-- 챌린지 카드 리스트 -->
		<v-row align="stretch">
			<v-col
				v-for="c in filteredChallenges"
				:key="c.challengeId"
				cols="12"
				md="6"
				lg="4"
				class="d-flex"
			>
				<v-card
					class="challenge-card"
					elevation="6"
					rounded="xl"
					width="100%"
					height="420"
				>
					<!-- 카드 배경과 전체 내용 - 단일 색상 -->
					<div class="card-content" style="background: #7e5bef">
						<!-- 상단: 카테고리와 하트 -->
						<div class="card-header">
							<v-chip
								size="small"
								color="white"
								text-color="primary"
								class="font-weight-bold elevation-2"
								style="border-radius: 12px"
							>
								{{ categoryName(c.categoryId) }}
							</v-chip>

							<v-btn
								icon
								size="small"
								class="heart-btn"
								@click="onToggleFavorite(c)"
							>
								<v-icon
									:color="
										c.isFavorite ? 'red' : 'rgba(255,255,255,0.7)'
									"
									size="24"
								>
									{{
										c.isFavorite ? 'mdi-heart' : 'mdi-heart-outline'
									}}
								</v-icon>
							</v-btn>
						</div>

						<!-- 중간: 제목과 설명 -->
						<div class="card-body">
							<h3 class="card-title">{{ c.title }}</h3>
							<p class="card-description">
								{{ truncateDescription(c.description) }}
							</p>
						</div>

						<!-- 하단: 정보와 버튼들 -->
						<div class="card-footer">
							<!-- 기간 정보 -->
							<div class="info-row mb-2">
								<v-icon
									size="16"
									class="mr-2"
									color="rgba(255,255,255,0.8)"
								>
									mdi-calendar-range
								</v-icon>
								<span class="info-text">
									{{ formatDate(c.startDate) }} ~
									{{ formatDate(c.endDate) }}
								</span>
							</div>

							<!-- 생성자 정보 -->
							<div class="info-row mb-3">
								<v-icon
									size="16"
									class="mr-2"
									color="rgba(255,255,255,0.8)"
								>
									mdi-account
								</v-icon>
								<span class="info-text">
									by {{ c.creatorNickname }}
								</span>
							</div>

							<!-- 액션 버튼들 -->
							<div class="action-buttons">
								<!-- 참여 상태에 따른 버튼 -->
								<template v-if="!c.requested && !c.approved">
									<v-btn
										class="action-button join-btn"
										size="small"
										:loading="isJoining && targetId === c.challengeId"
										:disabled="
											isJoining || (myParts.value?.size || 0) > 0
										"
										@click="onJoin(c.challengeId)"
									>
										<v-icon left size="16">mdi-account-plus</v-icon>
										{{
											(myParts.value?.size || 0) > 0
												? '다른 챌린지 참여 중'
												: '참여하기'
										}}
									</v-btn>
								</template>

								<template v-else-if="c.requested">
									<v-btn
										class="action-button cancel-btn"
										size="small"
										:loading="isJoining && targetId === c.challengeId"
										:disabled="isJoining"
										@click="onCancel(c.challengeId)"
									>
										<v-icon left size="16">mdi-close</v-icon>
										요청 취소
									</v-btn>
								</template>

								<template v-else-if="c.approved">
									<v-btn
										class="action-button approved-btn"
										size="small"
										disabled
									>
										<v-icon left size="16">mdi-check</v-icon>
										승인됨
									</v-btn>
								</template>

								<!-- 상세보기 버튼 -->
								<v-btn
									class="action-button detail-btn"
									size="small"
									@click="goToDetail(c.challengeId)"
								>
									<v-icon left size="16">mdi-arrow-right</v-icon>
									상세보기
								</v-btn>
							</div>
						</div>
					</div>
				</v-card>
			</v-col>
		</v-row>

		<!-- 페이지네이션 -->
		<v-row justify="center" class="my-8">
			<v-pagination
				v-model="currentPage"
				:length="totalPages"
				total-visible="10"
				show-first-last-page
				class="my-4"
				color="primary"
				rounded="circle"
			/>
		</v-row>
	</v-container>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import {
	getChallenges,
	toggleFavoriteChallenge,
} from '@/services/challengeService'
import {
	getMyParticipations,
	joinChallenge,
	cancelParticipation,
} from '@/services/participationService'
import { getCategories } from '@/services/categoryService'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import { handleApiError } from '@/utils/apiError'

const authStore = useAuthStore()
const router = useRouter()

// 참여 상태
const isJoining = ref(false)
const targetId = ref(null)

// 페이징 상태
const currentPage = ref(1)
const pageSize = ref(30)
const totalCount = ref(0)

// 챌린지 목록을 저장할 반응형 변수
const challenges = ref([])
const categories = ref([]) // 카테고리 목록

// 검색어 와 선택된 카테고리 를 위한 반응형 변수
const search = ref('')
const selectedCategory = ref(null)

// 내 참여내역을 id → participationId 매핑해서 저장
const myParts = ref(new Set())
const myPartsMap = ref({})

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

// 카테고리 ID → 이름 매핑
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

async function goMyChallenge() {
	// 참여 중인 챌린지 조회
	const parts = await getMyParticipations(authStore.user.userId)
	const myApproved = parts.find(
		(p) => p.status === 'APPROVED' || p.role === 'OWNER'
	)
	if (!myApproved) {
		alert('참여중인 챌린지가 없습니다.')
		return
	}
	router.push({
		name: 'challenge-overview',
		params: { id: myApproved.challengeId },
	})
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
			set.add(p.challengeId)
			map[p.challengeId] = {
				id: p.participationId,
				status: p.status,
				role: p.role,
			}
		})
		myParts.value = set
		myPartsMap.value = map
	} catch (err) {
		handleApiError(err)
	}
}

// 페이징 챌린지 목록 API 호출
async function fetchChallenges() {
	await fetchMyParticipations()
	try {
		const { totalCount: totalFromAPi, items } = await getChallenges(
			currentPage.value,
			pageSize.value,
			search.value,
			selectedCategory.value
		)
		totalCount.value = totalFromAPi
		challenges.value = items.map((c) => {
			const participation = myPartsMap.value[c.challengeId] || {}
			return {
				...c,
				isFavorite: c.isFavorite,
				requested: participation.status === 'REQUESTED',
				approved:
					participation.status === 'APPROVED' ||
					participation.role === 'OWNER',
			}
		})
	} catch (err) {
		if (axios.isAxiosError(err) && [401, 403].includes(err.response.status)) {
			alert('로그인해야 이용할 수 있습니다.')
			router.push({
				name: 'login',
				query: { redirect: router.currentRoute.value.fullPath },
			})
		} else {
			handleApiError(err)
		}
	}
}

watch(currentPage, fetchChallenges)

async function fetchCategories() {
	try {
		categories.value = await getCategories()
	} catch (err) {
		console.error('카테고리 로드 실패', err)
	}
}

async function onToggleFavorite(challenge) {
	try {
		const id = challenge.challengeId
		await toggleFavoriteChallenge(id)
		await fetchChallenges()
	} catch (err) {
		handleApiError(err)
	}
}

async function onJoin(challengeId) {
	const userId = authStore.user?.userId
	if (!userId) {
		alert('로그인 후 참여 가능합니다.')
		return router.push({ name: 'login' })
	}

	if (myParts.value.has(challengeId)) {
		alert('이미 참여 요청 중인 챌린지가 있거나 참여 중인 챌린지가 있습니다.')
		return
	}

	isJoining.value = true
	targetId.value = challengeId
	try {
		await joinChallenge(challengeId)
		await fetchMyParticipations()
		await fetchChallenges()
		alert('참여 요청이 완료되었습니다!')
	} catch (err) {
		if (axios.isAxiosError(err)) {
			const code = err.response?.data?.errorCode
			if (code === 'PARTICIPATION_LIMIT_EXCEEDED') {
				alert(
					'이미 다른 챌린지에 참여 요청/참여 중입니다.\n먼저 기존 요청을 취소하거나 탈퇴해주세요.'
				)
			} else if (err.response?.status === 400) {
				alert(err.response.data.message || '참여 요청에 실패했습니다.')
			} else {
				handleApiError(err)
			}
		} else {
			console.error(err)
			alert('알 수 없는 오류가 발생했습니다.')
		}
	} finally {
		isJoining.value = false
		targetId.value = null
	}
}

async function onCancel(challengeId) {
	if (!confirm('참여 요청을 정말 취소하시겠습니까?')) return
	const participationObj = myPartsMap.value[challengeId]
	const participationId = participationObj?.id
	if (!participationId) {
		alert('취소할 요청을 찾을 수 없습니다.')
		return
	}
	isJoining.value = true
	targetId.value = challengeId
	try {
		await cancelParticipation(challengeId, participationId)
		await fetchMyParticipations()
		await fetchChallenges()
		alert('요청이 취소되었습니다.')
	} catch (err) {
		if (axios.isAxiosError(err)) {
			const code = err.response?.status
			if (code === 404) {
				alert('요청을 찾을 수 없습니다.')
				return
			}
			if (code === 403) {
				alert('취소 권한이 없습니다.')
				return
			}
		}
		handleApiError(err)
	} finally {
		isJoining.value = false
		targetId.value = null
	}
}

onMounted(async () => {
	await authStore.fetchUser()
	await fetchMyParticipations()
	await Promise.all([fetchCategories(), fetchChallenges()])
})

const filteredChallenges = computed(() => {
	return challenges.value
		.filter((c) => {
			const matchesText =
				c.title.includes(search.value) ||
				c.description.includes(search.value)
			const matchesCategory =
				!selectedCategory.value || c.categoryId === selectedCategory.value
			return matchesText && matchesCategory
		})
		.map((c) => ({
			...c,
			requested: c.requested,
			approved: c.approved,
		}))
})

function goToFavoriteChallenge() {
	router.push('/challenges/favorite')
}

const totalPages = computed(() => Math.ceil(totalCount.value / pageSize.value))
</script>

<style scoped>
/* 타이틀 스타일 */
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

/* ✅ 새로운 네비게이션 스타일 */
.navigation-section {
	z-index: 10;
}

.nav-menu-btn {
	background: rgba(255, 255, 255, 0.95) !important;
	backdrop-filter: blur(10px);
	border: 2px solid rgba(255, 255, 255, 0.3) !important;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
	transition: all 0.3s ease;
	text-transform: none !important;
	font-weight: 600;
}

.nav-menu-btn:hover {
	background: rgba(255, 255, 255, 1) !important;
	transform: translateY(-2px);
	box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.nav-text {
	color: #333 !important;
	margin: 0 8px;
}

.navigation-menu {
	border-radius: 12px;
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
	padding: 8px 0;
	min-width: 280px;
}

.nav-item {
	padding: 16px 20px;
	transition: background-color 0.2s ease;
	border-radius: 8px;
	margin: 0 8px;
}

.nav-item:hover {
	background-color: rgba(0, 0, 0, 0.04);
}

.nav-item-title {
	font-weight: 600;
	font-size: 1rem;
	margin-bottom: 2px;
}

.nav-item-subtitle {
	font-size: 0.85rem;
	color: rgba(0, 0, 0, 0.6);
}

/* 챌린지 카드 스타일 */
.challenge-card {
	transition: transform 0.3s ease, box-shadow 0.3s ease;
	overflow: hidden;
}

.challenge-card:hover {
	transform: translateY(-8px);
	box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15) !important;
}

.card-content {
	height: 100%;
	padding: 24px;
	display: flex;
	flex-direction: column;
	color: white;
	position: relative;
}

/* 카드 헤더 */
.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20px;
}

.heart-btn {
	background: rgba(255, 255, 255, 0.1) !important;
	backdrop-filter: blur(10px);
}

.heart-btn:hover {
	background: rgba(255, 255, 255, 0.2) !important;
}

/* 카드 본문 */
.card-body {
	flex-grow: 1;
	margin-bottom: 20px;
}

.card-title {
	font-size: 1.4rem;
	font-weight: 700;
	margin-bottom: 12px;
	text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
	line-height: 1.3;
}

.card-description {
	font-size: 0.95rem;
	line-height: 1.5;
	opacity: 0.9;
	text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.2);
}

/* 정보 행 */
.info-row {
	display: flex;
	align-items: center;
	margin-bottom: 8px;
}

.info-text {
	font-size: 0.85rem;
	opacity: 0.9;
	text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.2);
}

/* 액션 버튼들 */
.action-buttons {
	display: flex;
	flex-direction: column;
	gap: 8px;
}

.action-button {
	width: 100%;
	text-transform: none !important;
	font-weight: 600;
	border-radius: 12px;
	transition: all 0.3s ease;
}

.join-btn {
	background: rgba(76, 175, 80, 0.9) !important;
	color: white !important;
}

.join-btn:hover {
	background: rgba(76, 175, 80, 1) !important;
	transform: translateY(-2px);
}

.cancel-btn {
	background: rgba(244, 67, 54, 0.9) !important;
	color: white !important;
}

.cancel-btn:hover {
	background: rgba(244, 67, 54, 1) !important;
	transform: translateY(-2px);
}

.approved-btn {
	background: rgba(76, 175, 80, 0.7) !important;
	color: white !important;
}

.detail-btn {
	background: rgba(255, 255, 255, 0.15) !important;
	color: white !important;
	border: 1px solid rgba(255, 255, 255, 0.3);
	backdrop-filter: blur(10px);
}

.detail-btn:hover {
	background: rgba(255, 255, 255, 0.25) !important;
	transform: translateY(-2px);
}

/* 반응형 */
@media (max-width: 600px) {
	.title-text {
		font-size: 1.25rem;
	}

	.card-content {
		padding: 20px;
	}

	.card-title {
		font-size: 1.2rem;
	}

	.nav-menu-btn {
		font-size: 0.9rem;
		padding: 8px 16px;
	}

	.navigation-menu {
		min-width: 250px;
	}
}
</style>
