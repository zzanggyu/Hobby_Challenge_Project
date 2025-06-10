<template>
	<!--  슬라이더  -->
	<!-- 슬라이드로 보여줄 챌린지가 1개 이상일 때만 슬라이더 렌더링 -->
	<div v-if="limited.length > 0">
		<v-slide-group v-model="active" mandatory center-active class="pa-6">
			<!-- 왼쪽 화살표 슬롯 -->
			<template #prev>
				<v-btn icon variant="text" @click="prevSlide">
					<v-icon>mdi-chevron-left</v-icon>
				</v-btn>
			</template>
			<!--  오른쪽 화살표 슬롯 -->
			<template #next>
				<v-btn icon variant="text" @click="nextSlide">
					<v-icon>mdi-chevron-right</v-icon>
				</v-btn>
			</template>
			<!-- 슬라이드 카드 반복 출력 -->
			<v-slide-group-item
				v-for="item in limited"
				:key="item.challengeId"
				v-slot="{ isSelected }"
			>
				<v-card
					class="challenge-card mx-3 bg-primary"
					style="color: white; border-radius: 30px"
					:elevation="isSelected ? 6 : 2"
					width="350"
					height="480"
				>
					<!--  썸네일 이미지가 있을 때 이미지 카드 -->
					<template v-if="item.thumbnail">
						<v-responsive aspect-ratio="3/4">
							<v-img :src="item.thumbnail" cover />
						</v-responsive>
					</template>
					<!--  썸네일이 없으면 컬러 배경 카드 -->
					<template v-else>
						<div
							class="color-card d-flex flex-column justify-space-between pa-6 text-white"
							:style="{ background: item.bg || '#7e5bef' }"
						>
							<v-btn
								icon
								size="small"
								class="heart-btn position-absolute"
								style="top: 12px; right: 12px"
								@click.stop="toggleHeart(item)"
							>
								<v-icon :color="item.isFavorite ? 'red' : 'grey'">
									{{
										item.isFavorite
											? 'mdi-heart'
											: 'mdi-heart-outline'
									}}
								</v-icon>
							</v-btn>

							<!-- 상단: 카테고리명 칩 -->
							<div class="d-flex align-center mb-3">
								<v-chip
									size="small"
									color="white"
									text-color="primary"
									class="font-weight-bold elevation-2 px-3 py-1"
									style="border-radius: 12px"
								>
									{{ getCategoryName(item.categoryId) }}
								</v-chip>
							</div>

							<!-- 중간: 제목과 설명 -->
							<div class="flex-grow-1">
								<h2 class="text-h5 font-weight-bold mb-3">
									{{ item.title }}
								</h2>
								<p class="text-body-2 mb-4" style="line-height: 1.4">
									{{ truncateDescription(item.description) }}
								</p>
							</div>

							<!-- 하단: 챌린지 정보들 -->
							<div class="challenge-info">
								<!-- 기간 정보 -->
								<div class="info-row mb-2">
									<v-icon size="16" class="mr-2"
										>mdi-calendar-range</v-icon
									>
									<span class="text-caption">
										{{ formatDate(item.startDate) }} ~
										{{ formatDate(item.endDate) }}
									</span>
								</div>

								<!-- 생성자 정보 -->
								<div class="info-row mb-3">
									<v-icon size="16" class="mr-2">mdi-account</v-icon>
									<span class="text-caption">
										by {{ item.creatorNickname || '익명' }}
									</span>
								</div>

								<!-- 생성일 -->
								<div class="info-row mb-3">
									<v-icon size="16" class="mr-2"
										>mdi-clock-outline</v-icon
									>
									<span class="text-caption">
										{{ formatCreatedDate(item.createdDate) }}
									</span>
								</div>

								<!-- 자세히 보기 버튼 - 이것만 클릭 가능 -->
								<v-btn
									color="white"
									variant="outlined"
									size="small"
									class="detail-button w-100"
									@click="goToDetail(item.challengeId)"
								>
									<v-icon left size="16">mdi-arrow-right</v-icon>
									상세 보기
								</v-btn>
							</div>
						</div>
					</template>
				</v-card>
			</v-slide-group-item>
		</v-slide-group>
		<!--  인디케이터 점 + 자동재생 토글 -->
		<div class="text-center mt-4">
			<v-btn
				v-for="(dot, i) in limited.length"
				:key="i"
				size="8"
				icon
				class="mx-1"
				:color="active === i ? 'primary' : 'grey'"
				@click="active = i"
			/>
			<v-btn icon color="primary" class="ml-4" @click="togglePlay">
				<v-icon>{{ playing ? 'mdi-pause' : 'mdi-play' }}</v-icon>
			</v-btn>
		</div>
	</div>

	<!-- 데이터 없을 때 안내 메시지 (슬라이드 배열이 비었을 때) -->
	<div v-else class="py-8 text-center text-grey">
		<v-icon size="64" color="grey lighten-2">mdi-emoticon-sad</v-icon>
		<p class="text-h6 grey--text mt-4">아직 등록된 인기 챌린지가 없습니다.</p>
	</div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { toggleFavoriteChallenge } from '@/services/challengeService'

const auth = useAuthStore()
const router = useRouter()

const props = defineProps({
	challenges: { type: Array, required: true }, // 슬라이드로 보여줄 챌린지 배열
	maxItems: { type: Number, default: 20 }, // 최대 보여줄 카드 개수
	interval: { type: Number, default: 4000 }, // 자동 슬라이드 간격 4초
	autoPlay: { type: Boolean, default: true }, // 자동재생 여부
	categories: { type: Array, required: true },
})

// 버튼 로딩 상태 관리
const heartLoading = ref(false)
const detailLoading = ref(false)
const targetId = ref(null)

//  추가: 이벤트 emit 정의
const emit = defineEmits(['favorite-updated'])
// 카테고리명 반환 함수 (카테고리 데이터 구조에 맞게 수정)
function getCategoryName(categoryId) {
	const found = props.categories.find((c) => c.categoryId === categoryId)
	return found ? found.categoryName : '기타'
}

// 설명 글자 수 제한 함수 (카드 내 텍스트가 너무 길면 말줄임표로 처리)
function truncateDescription(description) {
	if (!description) return ''
	return description.length > 200
		? description.substring(0, 200) + '...'
		: description
}

// 날짜 포맷팅 함수 (챌린지 리스트와 동일하게)
function formatDate(dateStr) {
	if (!dateStr) return '-'
	const date = new Date(dateStr)
	return date.toLocaleDateString('ko-KR', {
		month: 'short',
		day: 'numeric',
	})
}

// 생성일 포맷팅 함수
function formatCreatedDate(dateStr) {
	if (!dateStr) return '-'
	const date = new Date(dateStr)
	const now = new Date()
	const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24))

	if (diffDays === 0) return '오늘 생성'
	if (diffDays === 1) return '어제 생성'
	if (diffDays < 7) return `${diffDays}일 전 생성`
	if (diffDays < 30) return `${Math.floor(diffDays / 7)}주 전 생성`
	return `${Math.floor(diffDays / 30)}개월 전 생성`
}

// 챌린지 상세 페이지로 이동하는 함수 - 오직 버튼을 통해서만 호출됨
function goToDetail(challengeId) {
	detailLoading.value = true
	targetId.value = challengeId

	// Vue Router를 사용해서 챌린지 상세 페이지로 이동
	router.push({
		name: 'challenge-overview', // 라우터에 정의된 챌린지 상세 페이지 이름
		params: { id: challengeId }, // 챌린지 ID를 파라미터로 전달
	})

	// 로딩 상태 초기화 (페이지 이동 후)
	setTimeout(() => {
		detailLoading.value = false
		targetId.value = null
	}, 500)
}

// 관심 챌린지 토글
async function toggleHeart(item) {
	// 로그인 체크
	if (!auth.isAuthenticated) {
		alert('로그인 후 관심 챌린지 등록이 가능합니다.')
		return router.push('/login')
	}

	heartLoading.value = true
	targetId.value = item.challengeId

	try {
		// API 호출
		const response = await toggleFavoriteChallenge(item.challengeId)

		// 부모 컴포넌트에 데이터 새로고침 요청
		if (
			response &&
			response.data &&
			typeof response.data.isFavorite === 'boolean'
		) {
			// 서버에서 최종 상태를 받아서 적용
			item.isFavorite = response.data.isFavorite
		} else {
			// 서버 응답이 없으면 로컬에서 토글
			item.isFavorite = !item.isFavorite
		}

		// 성공 메시지
		const message = item.isFavorite
			? '내 챌린지에 추가되었습니다!'
			: '내 챌린지에서 제거되었습니다!'
		alert(message)
		emit('favorite-updated', {
			challengeId: item.challengeId,
			isFavorite: item.isFavorite,
		})
	} catch (err) {
		console.error('내내 챌린지 토글 실패:', err)

		// 10개 제한 에러 처리
		if (err.response?.data?.errorCode === 'FAVORITE_LIMIT_EXCEEDED') {
			alert('관심 챌린지는 최대 10개까지만 등록할 수 있습니다.')
		} else {
			alert('오류가 발생했습니다. 다시 시도해주세요.')
		}
	} finally {
		heartLoading.value = false
		targetId.value = null
		// setTimeout(() => {
		// 	emit('refresh-needed')
		// }, 500)
	}
}

// limited: 상위 maxItems개로 잘라서 실제 슬라이드로 보여줌
const limited = computed(() => props.challenges.slice(0, props.maxItems))
const active = ref(0) // 현재 활성 인덱스(슬라이드 위치)
const playing = ref(props.autoPlay) // 자동재생 on/off
let timer = null // setInterval 핸들 저장

//  슬라이드 한 장씩 이동 함수
function nextSlide() {
	active.value = (active.value + 1) % limited.value.length
}
function prevSlide() {
	active.value =
		(active.value - 1 + limited.value.length) % limited.value.length
}

//  자동 재생 컨트롤
function startAuto() {
	stopAuto()
	timer = setInterval(nextSlide, props.interval)
}
function stopAuto() {
	clearInterval(timer)
	timer = null
}
function togglePlay() {
	playing.value ? stopAuto() : startAuto()
	playing.value = !playing.value
}

// 라이프사이클(마운트/언마운트)에서 자동재생 관리
onMounted(() => {
	if (props.autoPlay) startAuto()
})
onBeforeUnmount(stopAuto)

//  (오직 props로 받은 데이터만 화면에 보여주기)
</script>

<style scoped>
.heart-btn {
	background: rgba(0, 0, 0, 0.3) !important;
	backdrop-filter: blur(10px);
	transition: all 0.3s ease;
}
.challenge-card {
	border-radius: 30px;
	overflow: hidden;
	transition: transform 0.2s ease-in-out; /* 호버 효과를 위한 부드러운 전환 */
	/* cursor: pointer; 제거 - 카드 전체가 클릭 가능하지 않음을 명시 */
}

.challenge-card:hover {
	transform: translateY(
		-5px
	); /* 마우스 올렸을 때 카드가 위로 살짝 올라가는 효과 */
}

.color-card {
	height: 100%;
	min-height: 460px;
}

/* 정보 행 스타일링 */
.info-row {
	display: flex;
	align-items: center;
	opacity: 0.9;
}

.info-row .v-icon {
	opacity: 0.8;
}

/* 자세히 보기 버튼 스타일 - 이것만 클릭 가능함을 강조 */
.detail-button {
	background: rgba(255, 255, 255, 0.1) !important;
	border: 1px solid rgba(255, 255, 255, 0.3) !important;
	backdrop-filter: blur(10px);
	transition: all 0.3s ease;
	cursor: pointer; /* 버튼만 클릭 가능함을 명시 */
}

.detail-button:hover {
	background: rgba(255, 255, 255, 0.2) !important;
	transform: translateY(-2px);
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

/* 챌린지 정보 영역 */
.challenge-info {
	background: rgba(0, 0, 0, 0.1);
	border-radius: 12px;
	padding: 12px;
	backdrop-filter: blur(5px);
}

.v-btn--size-8 {
	--v-btn-height: 8px;
	--v-btn-size: 8px;
}

/* 텍스트 가독성 향상 */
.text-h5 {
	text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.text-body-2 {
	text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.2);
}

.text-caption {
	text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.2);
}
</style>
