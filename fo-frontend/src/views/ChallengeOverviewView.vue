<template>
	<v-container>
		<!-- 상단 헤더 카드 -->
		<v-card
			color="primary"
			elevation="2"
			rounded="xl"
			class="pa-6 mb-8 text-white"
		>
			<div class="d-flex flex-column flex-sm-row justify-space-between">
				<div>
					<h2 class="text-h6 text-sm-h5 font-weight-bold mb-1">
						{{ challenge.title }}
					</h2>
					<div class="text-body-2 text-sm-body-1 opacity-75">
						기간 : {{ formatDate(challenge.startDate) }} &nbsp;–&nbsp;{{
							formatDate(challenge.endDate)
						}}
					</div>
				</div>

				<div v-if="isOwner" class="d-flex align-center mt-4 mt-sm-0">
					<v-btn
						density="comfortable"
						variant="outlined"
						color="white"
						class="mr-2"
						@click="onEdit"
					>
						<v-icon icon="mdi-pencil" size="18" class="mr-1" /> 수정
					</v-btn>

					<v-btn
						density="comfortable"
						variant="outlined"
						color="white"
						@click="onDelete"
					>
						<v-icon icon="mdi-delete-outline" size="18" class="mr-1" />
						삭제
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
		>
			<v-tab v-if="canWrite" value="0">인증등록</v-tab>
			<v-tab value="1">인증내역</v-tab>
			<v-tab v-if="isOwner" value="2">참여자</v-tab>
			<v-tab v-if="isOwner" value="3">요청</v-tab>
		</v-tabs>

		<!--  탭 내용  -->
		<!-- 탭 콘텐츠 - 조건부 렌더링 -->
		<div class="mt-4">
			<ChallengeCertificationForm v-if="tab === '0' && canWrite" />
			<ChallengeCertificationView v-if="tab === '1'" />
			<ChallengeParticipantsView v-if="tab === '2' && isOwner" />
			<ChallengeRequestsView v-if="tab === '3' && isOwner" />
		</div>
	</v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
	getChallengeById,
	deleteChallenge,
	getChallengeDetail,
} from '@/services/challengeService'
import { useAuthStore } from '@/stores/auth'
import ChallengeCertificationView from './ChallengeCertificationView.vue'
import ChallengeParticipantsView from './ChallengeParticipantsView.vue'
import ChallengeRequestsView from './ChallengeRequestsView.vue'
import ChallengeCertificationForm from './ChallengeCertificationForm.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const id = Number(route.params.id)

const challenge = ref({})
const detail = ref({})
const tab = ref('1')

async function load() {
	detail.value = await getChallengeDetail(id)
	challenge.value = detail.value
}

// 데이터 로드드
onMounted(load)

// 권한
const isOwner = computed(() => auth.user.userId === detail.value.createdBy)
const canWrite = computed(() => isOwner.value || detail.value.joined === true) // 인증등록 가능

// 수정
function onEdit() {
	router.push({ name: 'challenge-edit', params: { id } })
}

// 삭제
async function onDelete() {
	if (confirm('정말 삭제하시겠습니까?')) {
		await deleteChallenge(id)
		router.push({ name: 'challenge-list' })
	}
}

function formatDate(d) {
	return d ? new Date(d).toLocaleDateString() : '-'
}
</script>
