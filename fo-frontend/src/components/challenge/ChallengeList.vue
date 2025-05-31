<template>
	<v-container>
		<!-- ① 타이틀 영역 -->
		<!-- ① 타이틀 + 내 관심 버튼 한 줄에 -->
		<v-row class="mb-4" align="center">
			<v-col cols="12" class="d-flex align-center justify-space-between">
				<div class="title d-flex align-center">
					<v-icon class="mr-3" size="36" color="white"
						>mdi-format-list-bulleted</v-icon
					>
					<span class="title-text">챌린지 목록</span>
				</div>
				<v-btn
					color="success"
					size="large"
					class="favorite-btn"
					@click="goToFavoriteChallenge"
				>
					<v-icon left>mdi-star-outline</v-icon>
					내 관심 챌린지
				</v-btn>

				<v-btn
					color="success"
					size="large"
					class="my-challenge-btn"
					@click="goMyChallenge"
				>
					<v-icon left>mdi-trophy</v-icon>
					참여 중인 챌린지
				</v-btn>
			</v-col>
		</v-row>

		<!-- 검색 & 카테고리 필터 바 -->
		<v-row class="mb-4" align="center">
			<v-col cols="12" md="6">
				<v-text-field
					v-model="search"
					label="챌린지 제목 검색"
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
					clearable
				/>
			</v-col>
			<v-spacer />
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
					elevation="4"
					height="310"
					width="100%"
					rounded="xl"
					:tile="false"
					variant="outlined"
					class="d-flex flex-column"
					outlined
					:style="{
						border: '1px solid rgba(165, 243, 212, 0.6)',
						backgroundColor: '#f9fdfc',
					}"
				>
					<!-- 카드 상단: 제목/설명 -->
					<v-card-title class="px-4">
						<div class="d-flex w-100 justify-space-between align-center">
							<span class="text-h6">{{ c.title }}</span>

							<v-icon
								:color="c.isFavorite ? 'red' : 'grey'"
								@click="onToggleFavorite(c)"
								class="mr-2"
								style="cursor: pointer"
							>
								{{ c.isFavorite ? 'mdi-heart' : 'mdi-heart-outline' }}
							</v-icon>
						</div>
					</v-card-title>

					<v-divider color="green lighten-2" />

					<v-card-text class="flex-grow-1">
						{{ c.description }}
					</v-card-text>

					<!-- 카드 하단: 날짜 / 카테고리 / 버튼 -->
					<v-card-subtitle>
						기간: {{ formatDate(c.startDate) }} ~
						{{ formatDate(c.endDate) }}
					</v-card-subtitle>

					<v-card-actions class="mt-auto">
						<v-chip small outlined color="green-accent-4">
							{{ categoryName(c.categoryId) }}
						</v-chip>
						<small class="grey--text text--darken-1"
							>by: {{ c.creatorNickname }}</small
						>
						<v-spacer />

						<!-- 토글 버튼 -->
						<!-- 참여 상태에 따른 버튼 -->
						<template v-if="!c.requested && !c.approved">
							<!-- 아직 요청도 승인도 안 된 경우 -->
							<v-btn
								small
								color="secondary"
								:loading="isJoining && targetId === c.challengeId"
								:disabled="isJoining || (myParts.value?.size || 0) > 0"
								@click="onJoin(c.challengeId)"
							>
								{{
									(myParts.value?.size || 0) > 0
										? '다른 챌린지 참여 중'
										: '참여하기'
								}}
							</v-btn>
						</template>
						<template v-else-if="c.requested">
							<!-- 요청 중인 경우 -->
							<v-btn
								small
								color="error"
								:loading="isJoining && targetId === c.challengeId"
								:disabled="isJoining"
								@click="onCancel(c.challengeId)"
							>
								요청 취소
							</v-btn>
						</template>
						<template v-else-if="c.approved">
							<!-- 승인된 경우 -->
							<v-btn small color="success" disabled> 승인됨 </v-btn>
						</template>
					</v-card-actions>
				</v-card>
			</v-col>
		</v-row>
		<v-row justify="center" class="my-4">
			<v-pagination
				v-model="currentPage"
				:length="totalPages"
				total-visible="10"
				show-first-last-page
				class="my-4"
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
// 내 참여 요청/승인된 챌린지 ID 저장용 Set 선언
const myParts = ref(new Set())
const myPartsMap = ref({})

// 날짜 포맷터
function formatDate(date) {
	return date ? new Date(date).toLocaleDateString() : '-'
}

// 카테고리 ID → 이름 매핑
function categoryName(id) {
	// console.log('categoryName got id:', id)
	// console.log('categories list:', categories.value)
	const cat = categories.value.find((x) => x.categoryId === id)
	return cat ? cat.categoryName : '알 수 없음'
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
	// 챌린지 상세로 이동
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
		// Set 과 Map 동시 채우기
		const set = new Set()
		const map = {}
		list.forEach((p) => {
			if (p.status === 'APPROVED' || p.role === 'OWNER') {
				set.add(p.challengeId)
				map[p.challengeId] = {
					id: p.participationId,
					status: p.status,
					role: p.role,
				}
			}
		})
		myParts.value = set
		myPartsMap.value = map

		console.log('▶ fetchMyParticipations → myParts:', myParts.value)
		console.log('▶ fetchMyParticipations → myPartsMap:', myPartsMap.value)
	} catch (err) {
		handleApiError(err)
	}
}

// 페이징 챌린지 목록 API 호출
async function fetchChallenges() {
	// MyParts 최신화: 서버 참여내역 동기화
	await fetchMyParticipations()
	try {
		// 백엔드에서 { total, items } 형태로 내려줌
		const { totalCount: totalFromAPi, items } = await getChallenges(
			currentPage.value, // 현재 페이지
			pageSize.value, // 페이지 크기기
			search.value, //  검색 키워드
			selectedCategory.value //  카테고리 필터
		)
		totalCount.value = totalFromAPi // 총 챌린지 수
		// 한 번만 map 해서 requested/approved 결정
		// challenge 카드에서 approved: 참여 or 생성
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

// 페이지가 바뀌면 다시 불러오기
watch(currentPage, fetchChallenges)

// 카테고리 가져오기
async function fetchCategories() {
	try {
		categories.value = await getCategories()
	} catch (err) {
		console.error('카테고리 로드 실패', err)
	}
}

// 관심 챌린지 등록 하트 누르기
async function onToggleFavorite(challenge) {
	try {
		const id = challenge.challengeId
		await toggleFavoriteChallenge(id)
		// 2) 전체 챌린지를 다시 불러와서 갱신
		await fetchChallenges()
	} catch (err) {
		handleApiError(err)
	}
}

// 참여 버튼
async function onJoin(challengeId) {
	const userId = authStore.user?.userId
	if (!userId) {
		alert('로그인 후 참여 가능합니다.')
		return router.push({ name: 'login' })
	}

	// 이미 요청/승인 중이면 API 호출 전단에서 막기
	if (myParts.value.has(challengeId)) {
		alert('이미 참여 요청 중인 챌린지가 있거나 참여 중인 챌린지가 있습니다.')
		return
	}

	isJoining.value = true
	targetId.value = challengeId
	try {
		// 1) 실제 서버에 요청
		await joinChallenge(challengeId)
		// 2) 최신 내 참여내역과 챌린지 목록을 통째로 다시 불러와 화면 동기화
		await fetchMyParticipations()
		await fetchChallenges()
		alert('참여 요청이 완료되었습니다!')
	} catch (err) {
		if (axios.isAxiosError(err)) {
			const code = err.response?.data?.errorCode
			if (code === 'PARTICIPATION_LIMIT_EXCEEDED') {
				// 이미 다른 챌린지에 요청 중일 때
				alert(
					'이미 다른 챌린지에 참여 요청/참여 중입니다.\n먼저 기존 요청을 취소하거나 탈퇴해주세요.'
				)
			} else if (err.response?.status === 400) {
				// 그 외 BadRequest
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

// 참여 요청 취소
async function onCancel(challengeId) {
	if (!confirm('참여 요청을 정말 취소하시겠습니까?')) return
	// await fetchMyParticipations()
	const participationId = myPartsMap.value[challengeId]
	if (!participationId) {
		alert('취소할 요청을 찾을 수 없습니다.')
		return
	}
	isJoining.value = true
	targetId.value = challengeId
	try {
		await cancelParticipation(challengeId, participationId)
		await fetchMyParticipations()
		await fetchChallenges() // 다시 목록 갱신
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

// 컴포넌트 마운트 시 초기 로드
onMounted(async () => {
	await authStore.fetchUser()
	await fetchMyParticipations()
	await Promise.all([fetchCategories(), fetchChallenges()])
})

// 검색 + 카테고리 필터 적용
const filteredChallenges = computed(() => {
	return (
		challenges.value
			// 1) 검색/카테고리 필터
			.filter((c) => {
				const matchesText =
					c.title.includes(search.value) ||
					c.description.includes(search.value)
				const matchesCategory =
					!selectedCategory.value ||
					c.categoryId === selectedCategory.value
				return matchesText && matchesCategory
			})
			// 2) 매 렌더링마다 myParts를 보고 requested 재계산
			// 2) 필터링 후에도 요청/승인 상태 그대로 유지
			.map((c) => ({
				...c,
				// (필요하면 다시 동기화)
				requested: c.requested,
				approved: c.approved,
			}))
	)
})

function goToFavoriteChallenge() {
	router.push('/challenges/favorite')
}

// 전체 페이지 수 계산
const totalPages = computed(() => Math.ceil(totalCount.value / pageSize.value))
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

@media (max-width: 600px) {
	.title-text {
		font-size: 1.25rem;
	}
	.favorite-title {
		padding: 0.5rem 1rem;
	}
}
/* 
.favorite-btn {
	background: linear-gradient(135deg, #81c784 0%, #4caf50 100%);
	color: white;
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
} */
</style>
