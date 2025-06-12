<template>
	<!-- 가운데 600px 카드 느낌 -->
	<v-container class="d-flex justify-center">
		<v-card outlined width="600">
			<v-card-title class="text-h6">참여 요청 목록</v-card-title>

			<v-divider />

			<v-alert
				v-if="error"
				type="error"
				dense
				class="ma-4"
				closable
				@click:close="error = ''"
			>
				{{ error }}
			</v-alert>

			<!-- 요청 리스트 -->
			<v-list density="compact" lines="two">
				<!-- 요청 항목 -->
				<v-list-item
					v-for="r in requests"
					:key="r.participationId"
					class="py-2"
				>
					<!-- 왼쪽: 닉네임 + 날짜 -->
					<v-list-item-title>{{
						r.nickname || `USER #${r.userId}`
					}}</v-list-item-title>
					<v-list-item-subtitle class="text-caption grey--text">
						{{ formatDate(r.requestDate) }}
					</v-list-item-subtitle>

					<!-- 오른쪽 버튼 -->
					<template #append>
						<v-btn
							size="small"
							color="primary"
							class="mr-1"
							@click="approve(r.participationId)"
						>
							승인
						</v-btn>
						<v-btn
							size="small"
							color="error"
							@click="reject(r.participationId)"
						>
							거절
						</v-btn>
					</template>
				</v-list-item>

				<!-- 비어 있을 때 -->
				<v-list-item v-if="requests.length === 0">
					<v-list-item-title>대기 중인 요청이 없습니다.</v-list-item-title>
				</v-list-item>
			</v-list>
		</v-card>
	</v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getRequests, changeStatus } from '@/services/participationService'
const route = useRoute()
const id = Number(route.params.id)
const requests = ref([])

const error = ref('')
const processing = ref(null)

async function load() {
	try {
		requests.value = await getRequests(id)
	} catch (err) {
		error.value = '요청 목록을 불러오는데 실패했습니다.'
	}
}

// 참여 승인
async function approve(pid) {
	processing.value = pid
	error.value = ''
	try {
		await changeStatus(pid, 'APPROVED')
		requests.value = requests.value.filter((r) => r.participationId !== pid)
	} catch (err) {
		//  에러 처리
		const errorMessage = err.response?.data?.message

		if (errorMessage?.includes('취소')) {
			error.value = '참여 요청이 이미 취소되었습니다. 목록을 새로고침합니다.'
			setTimeout(() => load(), 2000) // 2초 후 자동 새로고침
		} else if (errorMessage?.includes('이미')) {
			error.value = '이미 처리된 요청입니다.'
			setTimeout(() => load(), 2000)
		} else {
			error.value = errorMessage || '승인 처리 중 오류가 발생했습니다.'
		}
	} finally {
		processing.value = null
	}
}

// 참여 거절
async function reject(pid) {
	processing.value = pid
	error.value = ''

	try {
		await changeStatus(pid, 'REJECTED')

		// 성공 시 해당 항목 제거
		requests.value = requests.value.filter((r) => r.participationId !== pid)
	} catch (err) {
		// 에러 처리
		const errorMessage = err.response?.data?.message

		if (errorMessage?.includes('취소')) {
			error.value = '참여 요청이 이미 취소되었습니다. 목록을 새로고침합니다.'
			setTimeout(() => load(), 2000)
		} else if (errorMessage?.includes('이미')) {
			error.value = '이미 처리된 요청입니다.'
			setTimeout(() => load(), 2000)
		} else {
			error.value = errorMessage || '거절 처리 중 오류가 발생했습니다.'
		}
	} finally {
		processing.value = null
	}
}

function formatDate(iso) {
	const d = new Date(iso)
	return d.toLocaleString('ko-KR', {
		year: 'numeric',
		month: '2-digit',
		day: '2-digit',
		hour: '2-digit',
		minute: '2-digit',
	})
}

onMounted(load)
</script>
