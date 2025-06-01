<template>
	<v-container>
		<!-- 상단 헤더 카드 -->
		<v-card class="header-card mb-8">
			<div class="header-content">
				<div>
					<h1 class="header-title">{{ detail.title }}</h1>
					<div class="header-subtitle">
						기간: {{ formatDate(detail.startDate) }} ~
						{{ formatDate(detail.endDate) }}
					</div>
				</div>
				<div v-if="isOwner" class="header-actions">
					<v-btn outlined color="white" class="mr-2" @click="onEdit">
						<v-icon left size="18">mdi-pencil</v-icon> 수정
					</v-btn>
					<v-btn outlined color="white" @click="onDelete">
						<v-icon left size="18">mdi-delete</v-icon> 삭제
					</v-btn>
				</div>
			</div>
		</v-card>

		<!-- 탭 헤더 -->
		<v-tabs
			v-model="tab"
			color="primary"
			center-active
			align-tabs="center"
			slider-color="primary"
			class="mb-4"
			@update:modelValue="onTabChange"
		>
			<v-tab v-if="canWrite" value="0">인증등록</v-tab>
			<v-tab value="1">전체 인증내역</v-tab>
			<v-tab value="2">참여자</v-tab>
			<v-tab v-if="isOwner" value="3">요청</v-tab>
			<v-tab value="4">내 인증내역</v-tab>
		</v-tabs>

		<div class="mb-4" v-if="myStatus === 'APPROVED' && !isOwner">
			<v-btn color="error" @click="onLeave" :loading="leaving">
				챌린지 탈퇴하기
			</v-btn>
		</div>

		<!--  탭 내용  -->
		<!-- 탭 콘텐츠 - 조건부 렌더링 -->
		<div class="mt-4">
			<!-- 0: 인증등록 -->
			<ChallengeCertificationForm
				v-if="tab === '0' && canWrite"
				@submitted="onSubmitted"
			/>
			<v-alert type="info" v-if="!joined">
				참여 요청이 승인될 때까지 인증내역을 볼 수 없습니다.
			</v-alert>
			<!-- 1: 전체 인증내역 보기 -->
			<ChallengeCertificationList
				v-if="tab === '1'"
				:challengeId="id"
				:refreshKey="refreshKey"
			/>
			<!-- 참여자 보기 -->
			<ChallengeParticipantsView
				v-if="tab === '2'"
				:challengeId="id"
				:refreshKey="refreshKey"
			/>
			<!-- 참여 요청자 보기 -->
			<ChallengeRequestsView v-if="tab === '3' && isOwner" />
			<!-- 내 인증 내역만 -->
			<ChallengeCertificationList
				v-if="tab === '4'"
				:challengeId="id"
				:refreshKey="refreshKey"
				only-mine
			/>
		</div>
	</v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
	deleteChallenge,
	getChallengeDetail,
} from '@/services/challengeService'
import { useAuthStore } from '@/stores/auth'
import ChallengeParticipantsView from './ChallengeParticipantsView.vue'
import ChallengeRequestsView from './ChallengeRequestsView.vue'
import ChallengeCertificationForm from '../components/challenge/ChallengeCertificationForm.vue'
import ChallengeCertificationList from '@/components/challenge/ChallengeCertificationList.vue'

// 참여 상태 확인·취소 API
import {
	getMyParticipations,
	cancelParticipation,
} from '@/services/participationService'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const id = Number(route.params.id)

// const challenge = ref({})
const detail = ref({})
const tab = ref('1')
// 이 키를 바꿔 주면 CertificationView 가 re-fetch 합니다
const refreshKey = ref(0)

// 탈퇴 상태 관리
const myStatus = ref('NONE') // NONE, REQUESTED, APPROVED
const myParticipationId = ref(null)
const leaving = ref(false)

// 날짜 포맷
function formatDate(d) {
	return d ? new Date(d).toLocaleDateString() : '-'
}

// 챌린지 상세 로드
async function loadDetail() {
	detail.value = await getChallengeDetail(id)
}

// 내 참여 상태 로드
async function loadMyStatus() {
	const userId = auth.user?.userId
	if (!userId) {
		myStatus.value = 'NONE'
		return
	}
	const list = await getMyParticipations(userId)
	const me = list.find((p) => p.challengeId === id && p.status !== 'REJECTED')
	if (me) {
		myStatus.value = me.status
		myParticipationId.value = me.participationId
	} else {
		myStatus.value = 'NONE'
		myParticipationId.value = null
	}
}

// 데이터 로드
onMounted(loadDetail)

// 권한
const isOwner = computed(() => auth.user.userId === detail.value.createdBy)
// 챌린지 참여 상태
const joined = computed(() => detail.value.joined)
// 챌린지 기간 내인지
const inPeriod = computed(() => {
	if (!detail.value.startDate || !detail.value.endDate) return false
	const now = new Date()
	const start = new Date(detail.value.startDate)
	const end = new Date(detail.value.endDate)
	return now >= start && now <= end
})

// 최종 인증 등록 가능 여부
const canWrite = computed(
	() => (isOwner.value || joined.value) && inPeriod.value
)

// 챌린지 수정
function onEdit() {
	router.push({ name: 'challenge-edit', params: { id } })
}
// 챌린지 삭제
// 챌린지 삭제 경고 메시지 강화
async function onDelete() {
	const confirmed = confirm(
		'⚠️ 경고: 챌린지를 삭제하면 완전히 제거되며 복구할 수 없습니다.\n' +
			'모든 참여자 데이터와 인증 내역도 함께 삭제됩니다.\n\n' +
			'정말 삭제하시겠습니까?'
	)
	if (!confirmed) return

	await deleteChallenge(id)
	router.push({ name: 'challenge-list' })
}

// 탭 권한한
function onTabChange(newTab) {
	if (newTab === '0' && !canWrite.value) {
		alert('승인된 참여자만 인증을 등록할 수 있습니다.')
		return
	}
	if (newTab === '1' && !joined.value) {
		alert('승인된 참여자만 전체 인증내역을 볼 수 있습니다.')
		return
	}
	tab.value = newTab
}

// 폼에서 제출이 완료되면 호출됩니다
function onSubmitted() {
	// 인증내역 탭으로 전환
	tab.value = '1'
	// CertificationView 가 다시 데이터를 가져오도록 키를 변경
	refreshKey.value++
	// 권한(joined) 업데이트
	loadDetail()
	loadMyStatus()
}

// 챌린지 탈퇴하기
async function onLeave() {
	if (!confirm('정말 챌린지에서 탈퇴하시겠습니까?')) return
	leaving.value = true
	try {
		await cancelParticipation(id, myParticipationId.value)
		alert('챌린지에서 탈퇴되었습니다.')
		// 다시 로딩
		await Promise.all([loadDetail(), loadMyStatus()])
		tab.value = '1'
		refreshKey.value++
	} catch (e) {
		console.error(e)
		alert('탈퇴 중 오류가 발생했습니다.')
	} finally {
		leaving.value = false
		router.push({ name: 'challenge-list' })
	}
}

// 컴포넌트 마운트 시 초기 로드
onMounted(async () => {
	await Promise.all([loadDetail(), loadMyStatus()])
})
</script>

<style scoped>
.header-card {
	background: linear-gradient(
		135deg,
		#2e7d32 0%,
		/* 진한 녹색 */ #81c784 50%,
		/* 중간 연두 */ #66bb6a 100%
	);
	color: white;
	border-radius: 12px;
	box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
	padding: 1.5rem;
}

.header-content {
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	justify-content: space-between;
}

@media (min-width: 600px) {
	.header-content {
		flex-direction: row;
		align-items: center;
	}
}

.header-title {
	font-size: 2rem;
	font-weight: 700;
	margin: 0;
	text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
}

.header-subtitle {
	font-size: 1rem;
	opacity: 0.85;
	margin-top: 0.25rem;
}

@media (min-width: 600px) {
	.header-subtitle {
		margin-top: 0.5rem;
	}
}

.header-actions .v-btn {
	text-transform: none;
	font-weight: 500;
}
</style>
