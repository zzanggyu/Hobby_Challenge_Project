<template>
	<v-dialog v-model="show" max-width="1000" scrollable>
		<v-card>
			<v-card-title class="text-h6 d-flex align-center">
				<v-icon class="mr-2" color="primary">mdi-trophy-variant</v-icon>
				내 참여 챌린지 목록
				<v-spacer />
				<v-btn icon variant="text" @click="close">
					<v-icon>mdi-close</v-icon>
				</v-btn>
			</v-card-title>

			<v-divider />

			<v-card-text style="max-height: 600px">
				<!-- 로딩 상태 -->
				<div v-if="loading" class="text-center py-4">
					<v-progress-circular indeterminate color="primary" />
					<div class="mt-2">챌린지 목록을 불러오는 중...</div>
				</div>

				<!-- 챌린지 목록 -->
				<div v-else-if="challenges.length > 0">
					<!-- 요약 정보 -->
					<v-alert variant="tonal" type="info" class="mb-4" color="black">
						<div class="d-flex align-center">
							<div>
								총 <strong>{{ challenges.length }}개</strong> 챌린지에
								참여했으며,
								<strong>{{ totalCertifications }}개</strong>의 인증을
								작성했습니다.
							</div>
						</div>
					</v-alert>

					<!-- 챌린지 카드들 -->
					<v-row>
						<v-col
							v-for="challenge in challenges"
							:key="challenge.challengeId"
							cols="12"
							md="6"
						>
							<v-card
								class="challenge-card"
								:class="{ 'active-challenge': challenge.isActive }"
								@click="selectChallenge(challenge)"
								hover
								elevation="2"
							>
								<v-card-title class="text-subtitle-1">
									{{ challenge.challengeTitle }}

									<v-spacer />
									<v-chip
										:color="challenge.isActive ? 'success' : 'grey'"
										size="small"
										variant="flat"
									>
										{{ challenge.isActive ? '진행중' : '종료' }}
									</v-chip>
								</v-card-title>

								<v-card-text>
									<div class="d-flex align-center mb-2">
										<v-icon class="mr-2" size="18">mdi-camera</v-icon>
										<span class="text-body-2">
											내 인증:
											<strong
												>{{
													challenge.certificationCount
												}}개</strong
											>
										</span>
									</div>

									<div class="d-flex align-center mb-2">
										<v-icon class="mr-2" size="18"
											>mdi-account-star</v-icon
										>
										<span class="text-body-2">
											역할:
											<v-chip
												:color="
													challenge.role === 'OWNER'
														? 'primary'
														: 'secondary'
												"
												size="x-small"
											>
												{{
													challenge.role === 'OWNER'
														? '생성자'
														: '참여자'
												}}
											</v-chip>
										</span>
									</div>

									<div
										v-if="challenge.endDate"
										class="d-flex align-center"
									>
										<v-icon class="mr-2" size="18"
											>mdi-calendar-end</v-icon
										>
										<span class="text-caption text-grey">
											종료일: {{ formatEndDate(challenge.endDate) }}
										</span>
									</div>
								</v-card-text>

								<v-card-actions class="pt-0">
									<v-spacer />
									<v-btn
										size="small"
										variant="text"
										color="primary"
										append-icon="mdi-arrow-right"
									>
										인증 목록 보기
									</v-btn>
								</v-card-actions>
							</v-card>
						</v-col>
					</v-row>
				</div>

				<!-- 빈 상태 -->
				<div v-else class="text-center py-8">
					<v-icon size="64" color="grey-lighten-1"
						>mdi-trophy-outline</v-icon
					>
					<div class="text-h6 mt-4 text-grey">
						참여한 챌린지가 없습니다
					</div>
					<div class="text-body-2 text-grey mt-2">
						새로운 챌린지에 참여해보세요!
					</div>
					<v-btn
						color="primary"
						variant="outlined"
						class="mt-4"
						@click="goToChallengeList"
					>
						챌린지 둘러보기
					</v-btn>
				</div>
			</v-card-text>
		</v-card>
	</v-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Props
const props = defineProps({
	modelValue: {
		type: Boolean,
		default: false,
	},
	challenges: {
		type: Array,
		default: () => [],
	},
	loading: {
		type: Boolean,
		default: false,
	},
})

// Emits
const emit = defineEmits(['update:modelValue', 'challenge-selected'])

// Computed
const show = computed({
	get: () => props.modelValue,
	set: (value) => emit('update:modelValue', value),
})

const totalCertifications = computed(() =>
	props.challenges.reduce((sum, c) => sum + c.certificationCount, 0)
)

// Methods
function close() {
	show.value = false
}

function selectChallenge(challenge) {
	emit('challenge-selected', challenge)
}

function goToChallengeList() {
	close()
	router.push('/challenges')
}

function formatEndDate(dateString) {
	if (!dateString) return '미정'

	const date = new Date(dateString)
	const now = new Date()
	const diffTime = date - now
	const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

	if (diffDays < 0) {
		return `${Math.abs(diffDays)}일 전 종료`
	} else if (diffDays === 0) {
		return '오늘 종료'
	} else if (diffDays <= 7) {
		return `${diffDays}일 후 종료`
	} else {
		return date.toLocaleDateString('ko-KR', {
			year: 'numeric',
			month: 'long',
			day: 'numeric',
		})
	}
}
</script>

<style scoped>
.challenge-card {
	transition: all 0.3s ease;
	cursor: pointer;
	border-left: 4px solid transparent;
}

.challenge-card:hover {
	transform: translateY(-2px);
	border-left-color: #1976d2;
}

.challenge-card.active-challenge {
	border-left-color: #4caf50;
}
</style>
