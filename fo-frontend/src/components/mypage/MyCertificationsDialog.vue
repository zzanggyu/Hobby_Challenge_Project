<!-- src/components/mypage/MyCertificationsDialog.vue -->
<template>
	<v-dialog v-model="show" max-width="900" scrollable>
		<v-card>
			<v-card-title class="text-h6 d-flex align-center">
				<v-icon class="mr-2" color="success">mdi-camera</v-icon>
				{{ challenge?.challengeTitle || '챌린지' }} - 내 인증 목록
				<v-spacer />
				<v-btn icon variant="text" @click="close">
					<v-icon>mdi-close</v-icon>
				</v-btn>
			</v-card-title>

			<v-divider />

			<v-card-text style="max-height: 700px">
				<!-- 로딩 상태 -->
				<div v-if="loading" class="text-center py-4">
					<v-progress-circular indeterminate color="success" />
					<div class="mt-2">인증 목록을 불러오는 중...</div>
				</div>

				<!-- 인증 목록 -->
				<div v-else-if="certifications.length > 0">
					<!-- 요약 정보 -->
					<v-alert variant="tonal" type="success" class="mb-4">
						<div class="d-flex align-center">
							<div>
								<strong>{{ certifications.length }}개</strong>의 인증을
								작성했습니다.
								<span v-if="challenge?.isActive" class="ml-2">
									(진행 중인 챌린지)
								</span>
								<span v-else class="ml-2 text-grey">
									(종료된 챌린지)
								</span>
							</div>
						</div>
					</v-alert>

					<!-- 인증 카드들 -->
					<v-row>
						<v-col
							v-for="cert in certifications"
							:key="cert.certificationId"
							cols="12"
							md="6"
						>
							<v-card elevation="2" class="cert-card">
								<!-- 이미지 -->
								<v-img
									v-if="cert.imageUrl"
									:src="cert.imageUrl"
									height="200"
									cover
									class="cert-image"
								>
									<!-- 날짜 오버레이 -->
									<div class="image-overlay">
										<v-chip size="small" color="white" class="ma-2">
											{{ formatCertDate(cert.createdDate) }}
										</v-chip>
									</div>
								</v-img>

								<v-card-text>
									<!-- 코멘트 -->
									<div class="text-body-2 mb-2 comment-text">
										{{ cert.comment || '' }}
									</div>

									<!-- 메타 정보 -->
									<div
										class="d-flex align-center justify-space-between text-caption text-grey"
									>
										<div class="d-flex align-center">
											<v-icon size="16" class="mr-1"
												>mdi-heart</v-icon
											>
											{{ cert.likeCount || 0 }}

											<v-icon size="16" class="mr-1 ml-3"
												>mdi-comment</v-icon
											>
											{{ cert.commentCount || 0 }}
										</div>

										<div>
											{{ formatCertTime(cert.createdDate) }}
										</div>
									</div>
								</v-card-text>

								<!-- 액션 버튼 -->
								<v-card-actions class="pt-0">
									<v-spacer />
									<v-btn
										size="small"
										variant="text"
										color="primary"
										@click="goToChallengeDetail"
									>
										챌린지로 이동
									</v-btn>
								</v-card-actions>
							</v-card>
						</v-col>
					</v-row>
				</div>

				<!-- 빈 상태 -->
				<div v-else class="text-center py-8">
					<v-icon size="64" color="grey-lighten-1"
						>mdi-camera-outline</v-icon
					>
					<div class="text-h6 mt-4 text-grey">작성한 인증이 없습니다</div>
					<div class="text-body-2 text-grey mt-2">
						이 챌린지에서 아직 인증을 작성하지 않았어요.
					</div>
					<v-btn
						v-if="challenge?.isActive"
						color="primary"
						variant="outlined"
						class="mt-4"
						@click="goToChallengeDetail"
					>
						챌린지에서 인증하기
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
	challenge: {
		type: Object,
		default: null,
	},
	certifications: {
		type: Array,
		default: () => [],
	},
	loading: {
		type: Boolean,
		default: false,
	},
})

// Emits
const emit = defineEmits(['update:modelValue'])

// Computed
const show = computed({
	get: () => props.modelValue,
	set: (value) => emit('update:modelValue', value),
})

// Methods
function close() {
	show.value = false
}

function goToChallengeDetail() {
	if (props.challenge?.challengeId) {
		close()
		router.push(`/challenges/${props.challenge.challengeId}`)
	}
}

function formatCertDate(dateString) {
	const date = new Date(dateString)
	const now = new Date()
	const diffTime = now - date
	const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))

	if (diffDays === 0) return '오늘'
	if (diffDays === 1) return '어제'
	if (diffDays < 7) return `${diffDays}일 전`
	if (diffDays < 30) return `${Math.floor(diffDays / 7)}주 전`

	return date.toLocaleDateString('ko-KR', {
		month: 'short',
		day: 'numeric',
	})
}

function formatCertTime(dateString) {
	const date = new Date(dateString)
	return date.toLocaleString('ko-KR', {
		month: 'short',
		day: 'numeric',
		hour: '2-digit',
		minute: '2-digit',
	})
}
</script>

<style scoped>
.cert-card {
	transition: all 0.3s ease;
}

.cert-card:hover {
	transform: translateY(-1px);
}

.cert-image {
	position: relative;
}

.image-overlay {
	position: absolute;
	top: 0;
	right: 0;
	z-index: 1;
}

.comment-text {
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	text-overflow: ellipsis;
	min-height: 2.4em;
}
</style>
