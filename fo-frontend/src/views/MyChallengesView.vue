<template>
	<v-container>
		<h2 class="text-h4 mb-4">내 챌린지</h2>
		<v-row dense>
			<v-col
				v-for="p in participations"
				:key="p.participationId"
				cols="12"
				md="6"
				lg="4"
			>
				<v-card outlined height="300">
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
						<v-btn text @click="goDetail(p.challenge.challengeId)"
							>상세 보기</v-btn
						>
					</v-card-actions>
				</v-card>
			</v-col>
		</v-row>
	</v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import {
	getMyParticipations,
	getChallengeById,
} from '@/services/challengeService'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()
const participations = ref([])

async function load() {
	// 1) 참여 내역만 먼저 불러오고
	const raws = await getMyParticipations(auth.user.userId)
	// 2) 각 participation의 challengeId로 실제 challenge 객체를 가져와 붙입니다
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
