<template>
	<v-container>
		<!-- 챌린지 기본 정보 -->
		<!-- <v-row class="mb-4">
			<v-col cols="12">
				<h2 class="text-h5 mb-2">{{ challenge.title }} — 인증</h2>
				<div>
					기간: {{ formatDate(challenge.startDate) }} ~
					{{ formatDate(challenge.endDate) }}
				</div>
			</v-col>
		</v-row> -->

		<!-- 오늘 인증 미제출 시 폼 -->
		<v-row v-if="canSubmit" class="mb-6">
			<v-col cols="12" md="8">
				<v-card outlined>
					<v-card-title>오늘의 인증 등록</v-card-title>
					<v-card-text>
						<v-textarea
							v-model="newLog.comment"
							label="한 줄 메시지"
							rows="2"
							clearable
						/>
						<v-file-input
							v-model="newLog.file"
							label="사진 업로드 (선택)"
							accept="image/*"
						/>
					</v-card-text>
					<v-card-actions>
						<v-spacer />
						<v-btn
							color="primary"
							:loading="isSubmitting"
							@click="submitLog"
							>인증하기</v-btn
						>
					</v-card-actions>
				</v-card>
			</v-col>
		</v-row>

		<!-- 인증 기록 리스트 -->
		<v-row>
			<v-col
				v-for="log in certLogs"
				:key="log.certificationId"
				cols="12"
				md="6"
				lg="4"
			>
				<v-card outlined>
					<v-card-title>{{ formatDate(log.createdDate) }}</v-card-title>
					<v-card-text>
						<div>{{ log.comment }}</div>
						<v-img
							v-if="log.imageUrl"
							:src="log.imageUrl"
							height="200"
							class="mt-2"
						/>
					</v-card-text>
				</v-card>
			</v-col>
			<v-col
				v-if="certLogs.length === 0"
				cols="12"
				class="text-center grey--text"
				>아직 인증 기록이 없습니다.</v-col
			>
		</v-row>
	</v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getChallengeById } from '@/services/challengeService'
import { getCertifications, submitCertification } from '@/services/certService'
import { formatDate as _formatDate } from '@/utils/date'

const route = useRoute()
const router = useRouter()
const challengeId = Number(route.params.id)

const challenge = ref({})
const certLogs = ref([])
const newLog = ref({ comment: '', file: null })
const isSubmitting = ref(false)

function formatDate(d) {
	return _formatDate(d)
}

const todayKey = computed(() => {
	const today = new Date().toISOString().slice(0, 10)
	return certLogs.value.some((log) => log.createdDate.startsWith(today))
})

const canSubmit = computed(() => {
	const now = new Date(),
		start = new Date(challenge.value.startDate),
		end = new Date(challenge.value.endDate)
	return now >= start && now <= end && !todayKey.value
})

async function fetchData() {
	try {
		challenge.value = await getChallengeById(challengeId)
		certLogs.value = await getCertifications(challengeId)
	} catch (err) {
		// 403(권한 없음)이면 내 챌린지 목록으로 강제 이동
		if (err.response?.status === 403) {
			alert('인증은 승인된 참여자만 가능합니다.')
			router.push({ name: 'MyChallenges' })
		} else {
			console.error(err)
		}
	}
}

async function submitLog() {
	if (!newLog.value.comment) {
		return alert('메시지를 입력하세요.')
	}

	isSubmitting.value = true

	try {
		const form = new FormData()
		form.append('comment', newLog.value.comment)
		if (newLog.value.file) form.append('file', newLog.value.file)

		await submitCertification(challengeId, form) //  API 호출
		await fetchData() // 방금 올린 것까지 다시 가져오기
		newLog.value = { comment: '', file: null } // 폼 리셋
	} catch (err) {
		const res = err.response?.data

		// 하루 1회 초과 등록 중복 에러
		if (res?.errorCode === '400041') {
			alert(res.message || '오늘은 이미 인증을 올렸습니다.')
			return Promise.resolve()
		}

		// 입력 검증(Bean Validation) 에러
		if (res?.detail) {
			// 10자 이상 입력하세요
			const msg = res.detail
				.replace(/[{}]/g, '')
				.split(', ')
				.map((s) => s.split('=').pop())
				.join('\n')
			return alert(msg)
		}

		// 기타
		console.error(err)
		alert(res?.message || '인증 등록 중 오류')
	} finally {
		isSubmitting.value = false
		return Promise.reject(err)
	}
}

onMounted(fetchData)
</script>
