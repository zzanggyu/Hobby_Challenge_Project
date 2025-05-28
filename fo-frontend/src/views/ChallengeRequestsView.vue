<template>
	<!-- 가운데 600px 카드 느낌 -->
	<v-container class="d-flex justify-center">
		<v-card outlined width="600">
			<v-card-title class="text-h6">참여 요청 목록</v-card-title>

			<v-divider />

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

async function load() {
	requests.value = await getRequests(id)
}
async function approve(pid) {
	await changeStatus(pid, 'APPROVED')
	load()
}
async function reject(pid) {
	await changeStatus(pid, 'REJECTED')
	load()
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
