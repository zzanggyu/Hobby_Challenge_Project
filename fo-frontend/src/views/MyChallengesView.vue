<template>
	<v-container>
		<div class="title d-flex align-center">
			<v-icon class="mr-3" size="36" color="white">mdi-trophy</v-icon>
			<span class="title-text">내 챌린지</span>
		</div>
		<v-row align="stretch" justify="center" class="fill-height">
			<v-col
				v-for="p in participations"
				:key="p.participationId"
				cols="12"
				md="6"
				lg="4"
				class="d-flex"
			>
				<v-card
					elevation="4"
					height="500"
					width="100%"
					rounded="xl"
					:tile="false"
					variant="outlined"
					class="d-flex flex-column"
					outlined
					:style="{
						border: '1px solid rgba(230, 30, 30, 0.3)',
						backgroundColor: '#f9fdfc',
					}"
				>
					<v-card-title class="justify-space-between">
						{{ p.challenge.title }}
						<v-chip small color="green" v-if="p.status === 'APPROVED'"
							>참여중</v-chip
						>
						<v-chip small color="orange" v-else>요청중</v-chip>
					</v-card-title>
					<v-card-text>
						{{ p.challenge.description }}
					</v-card-text>
					<v-card-actions>
						<v-btn
							variant="tonal"
							color="primary"
							elevation="2"
							rounded="pill"
							size="small"
							@click="goDetail(p.challenge.challengeId)"
						>
							<v-icon left size="18">mdi-open-in-new</v-icon>
							챌린지 자세히 보기
						</v-btn>
					</v-card-actions>
				</v-card>
			</v-col>
		</v-row>
	</v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getChallengeById } from '@/services/challengeService'
import { getMyParticipations } from '@/services/participationService'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()
const participations = ref([])

async function load() {
	//  참여 내역만 먼저 불러오고
	const raws = await getMyParticipations(auth.user.userId)
	//  각 participation의 challengeId로 실제 challenge 객체를 가져와 붙입니다
	participations.value = await Promise.all(
		raws.map(async (p) => {
			const challenge = await getChallengeById(p.challengeId)
			return { ...p, challenge }
		})
	)
}
function goDetail(id) {
	router.push({ name: 'challenge-overview', params: { id } })
}

onMounted(load)
</script>

<style scoped>
.title {
	width: 100%;
	max-width: 1800px;
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
	.title {
		padding: 0.5rem 1rem;
	}
}
</style>
<style scoped>
.fill-height {
	min-height: 60vh; /* 뷰포트 높이의 60% 만큼 빈 공간 확보 */
}
</style>
