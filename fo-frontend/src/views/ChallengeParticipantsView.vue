<template>
	<v-container class="d-flex justify-center">
		<v-card outlined width="600">
			<v-card-title class="text-h6">참여자 명단</v-card-title>
			<v-divider />

			<v-list density="comfortable" lines="two">
				<!-- 참여자가 없을 때 -->
				<v-alert type="info" border="start" v-if="!participants.length">
					아직 참여자가 없습니다.
				</v-alert>

				<!-- 참여자 목록 -->
				<v-list-item
					v-for="p in participants"
					:key="p.participationId"
					class="py-2"
				>
					<!-- 아이콘 아바타 -->
					<template #prepend>
						<v-avatar size="40" color="grey lighten-3">
							<v-icon size="24" color="grey">mdi-account</v-icon>
						</v-avatar>
					</template>

					<!-- 제목 & 부제목 (기존 v-list-item-content 없이) -->
					<v-list-item-title>
						{{ p.nickname || `User-${p.userId}` }}
					</v-list-item-title>
					<v-list-item-subtitle class="mt-1">
						<v-chip
							size="x-small"
							:color="p.role === 'OWNER' ? 'primary' : 'grey'"
							class="text-white"
						>
							{{ p.role }}
						</v-chip>
					</v-list-item-subtitle>

					<!-- 방출 버튼 -->
					<template #append>
						<v-icon
							size="20"
							color="error"
							v-tooltip="'방출'"
							@click="expel(p.participationId)"
						>
							mdi-close-circle
						</v-icon>
					</template>
				</v-list-item>
			</v-list>

			<!-- 방출 확인 스낵바 -->
			<v-snackbar v-model="snackbar" timeout="2000">
				참여자를 방출했습니다
			</v-snackbar>
		</v-card>
	</v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
	getApprovedParticipants,
	changeStatus,
} from '@/services/challengeService'

const route = useRoute()
const id = Number(route.params.id)
const participants = ref([])
const snackbar = ref(false)

async function load() {
	participants.value = await getApprovedParticipants(id)
}

async function expel(pid) {
	await changeStatus(pid, 'REJECTED')
	snackbar.value = true
	load()
}

onMounted(load)
</script>
