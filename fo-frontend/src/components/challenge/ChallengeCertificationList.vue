<template>
	<v-container>
		<!-- 인증이 없을 떄떄 -->
		<v-row v-if="!logs.length">
			<v-col class="text-center grey--text">아직 인증이 없습니다.</v-col>
		</v-row>
		<!-- 인증 목록록 -->
		<v-row v-else>
			<v-col
				v-for="log in logs"
				:key="log.certificationId"
				cols="12"
				md="6"
				lg="4"
			>
				<v-card
					outlined
					rounded="xl"
					variant="outlined"
					max-width="350"
					min-height="450"
					:style="{
						border: '1px solid rgba(165, 243, 212, 0.6)',
						backgroundColor: '#f9fdfc',
					}"
					@click="openDialog(log.certificationId)"
				>
					<!-- 카드 헤더 날짜+사용자정보 -->
					<v-card-title class="d-flex justify-space-between align-center">
						<div>{{ formatDate(log.createdDate) }}</div>
						<v-spacer />

						<!-- 닉네임 + 별+레벨 -->
						<div class="d-flex align-center">
							<!-- 1) 닉네임 -->
							<div class="font-weight-bold mr-4">
								{{ log.nickname }}
							</div>

							<!-- 2) 별 위에 레벨 -->
							<div class="d-flex flex-column align-center">
								<v-icon size="30" :color="getLevelColor(log.level)">
									mdi-star
								</v-icon>
								<span class="text-caption grey--text">
									Lv. {{ log.level }}
								</span>
							</div>
						</div>
					</v-card-title>
					<!-- 카드 본문: 코멘트 + 사진 -->
					<v-card-text>
						<div class="my-2 body-1">{{ log.comment }}</div>
						<v-img
							v-if="log.imageUrl"
							:src="log.imageUrl"
							height="330"
							class="rounded-lg elevation-1"
							:style="{
								border: '1px solid rgba(165, 243, 212, 0.6)',
								backgroundColor: '#f9fdfc',
							}"
						/>
					</v-card-text>
				</v-card>
			</v-col>
		</v-row>
	</v-container>
	<!-- 인증 상세 모달 -->
	<v-dialog v-model="dialog" max-width="600">
		<CertificationDetailDialog
			v-if="selectedCertId"
			:certificationId="selectedCertId"
			:challengeId="props.challengeId"
			@close="dialog = false"
		/>
	</v-dialog>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getCertifications, deleteCertification } from '@/services/certService'
import { formatDate as _formatDate } from '@/utils/date'
import { useAuthStore } from '@/stores/auth'
import CertificationDetailDialog from './CertificationDetailDialog.vue'

const props = defineProps({
	challengeId: { type: Number, required: true },
	refreshKey: { type: Number, required: true },
	onlyMine: { type: Boolean, default: false },
})

const logs = ref([])

const dialog = ref(false)
const selectedCertId = ref(null)

function openDialog(id) {
	selectedCertId.value = id
	dialog.value = true
}

async function fetchLogs() {
	let data = await getCertifications(props.challengeId)
	if (props.onlyMine) {
		const auth = useAuthStore()
		data = data.filter((c) => c.userId === auth.user.userId)
	}
	logs.value = data
}

// 인증 삭제
async function deleteLog(certId) {
	if (!confirm('정말 이 인증을 삭제하시겠습니까?')) return
	await deleteCertification(props.challengeId, certId)
	await fetchLogs()
}

onMounted(fetchLogs)

// challengeId, refreshKey, onlyMine 중 하나가 바뀌면 다시 불러오기
watch(() => [props.challengeId, props.refreshKey, props.onlyMine], fetchLogs, {
	immediate: true,
})

function formatDate(d) {
	return _formatDate(d)
}

// 레벨별 색 결정 로직
function getLevelColor(level) {
	if (level < 10) return 'grey lighten-1'
	if (level < 20) return 'red'
	if (level < 30) return 'orange'
	if (level < 40) return 'yellow'
	if (level < 50) return 'green'
	if (level < 60) return 'blue'
	if (level < 70) return 'indigo'
	if (level < 80) return 'purple'
	return 'black'
}
</script>
