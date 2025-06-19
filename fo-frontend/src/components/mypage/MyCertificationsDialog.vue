<template>
	<v-dialog v-model="show" max-width="1000" scrollable>
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
					<v-row v-if="certifications.length > 0">
						<v-col
							v-for="cert in certifications"
							:key="cert.certificationId"
							cols="12"
							md="6"
						>
							<v-card
								elevation="2"
								class="cert-card"
								:class="{
									'cert-card-expanded':
										selectedCertId === cert.certificationId,
								}"
							>
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

								<!-- 확장된 상세 정보 영역 (새로 추가) -->
								<v-expand-transition>
									<div
										v-if="selectedCertId === cert.certificationId"
										class="cert-detail-expanded"
										@click.stop
									>
										<v-divider />

										<v-card-text class="pt-3">
											<!-- 고정 높이를 가진 스크롤 컨테이너 -->
											<div
												class="comments-scroll-container"
												:class="{
													'loading-state': loadingComments,
												}"
											>
												<!-- 로딩 상태 - 고정 높이 유지 -->
												<div
													v-if="loadingComments"
													class="loading-content"
												>
													<div class="text-center py-8">
														<v-progress-circular
															indeterminate
															size="32"
															color="primary"
														/>
														<div class="text-body-2 mt-3">
															댓글을 불러오는 중...
														</div>
													</div>
												</div>

												<!-- 로딩 완료 후 내용 -->
												<div v-else>
													<!-- 댓글 목록 영역 -->
													<div
														v-if="
															cert.detailedComments &&
															cert.detailedComments.length > 0
														"
														class="comments-archive"
													>
														<h5
															class="text-subtitle-2 mb-3 text-grey-darken-1"
														>
															<v-icon left size="16"
																>mdi-message-text</v-icon
															>
															받았던 댓글 ({{
																cert.detailedComments.length
															}}개)
														</h5>

														<div
															v-for="comment in cert.detailedComments"
															:key="comment.commentId"
															class="comment-archive-item mb-3 pa-3 bg-grey-lighten-5 rounded"
														>
															<!-- 기존 댓글 표시 구조 그대로 -->
															<div
																class="d-flex align-items-start"
															>
																<v-avatar
																	size="24"
																	class="mr-3"
																>
																	<v-icon
																		size="16"
																		color="grey-darken-1"
																		>mdi-account-circle</v-icon
																	>
																</v-avatar>
																<div class="flex-grow-1">
																	<div
																		class="d-flex align-items-center mb-1"
																	>
																		<span
																			class="text-subtitle-2 font-weight-medium text-grey-darken-2"
																		>
																			{{ comment.nickname }}
																		</span>
																		<span
																			class="text-caption text-grey ml-2"
																		>
																			{{
																				formatCommentTime(
																					comment.createdDate ||
																						comment.createdAt
																				)
																			}}
																		</span>
																	</div>
																	<div
																		class="text-body-2 comment-content"
																	>
																		{{ comment.content }}
																	</div>
																</div>
															</div>
														</div>
													</div>

													<!-- 댓글이 없을 때의 메시지 -->
													<div
														v-else-if="
															cert.detailsLoaded &&
															(!cert.detailedComments ||
																cert.detailedComments.length ===
																	0)
														"
														class="no-comments-message text-center py-8"
													>
														<v-icon
															size="32"
															color="grey-lighten-2"
															>mdi-comment-outline</v-icon
														>
														<div
															class="text-body-2 text-grey mt-2"
														>
															아직 받은 댓글이 없습니다
														</div>
													</div>
												</div>
											</div>
										</v-card-text>
									</div>
								</v-expand-transition>

								<!-- 액션 버튼 -->
								<v-card-actions class="pt-0">
									<v-spacer />
									<v-btn
										size="small"
										variant="text"
										color="primary"
										@click="openCertificationDetail(cert)"
									>
										<v-icon left size="16">
											{{
												selectedCertId === cert.certificationId
													? 'mdi-chevron-up'
													: 'mdi-chevron-down'
											}}
										</v-icon>
										{{
											selectedCertId === cert.certificationId
												? '접기'
												: '댓글보기'
										}}
									</v-btn>
									<!-- 종료된 챌린지가 아닌 경우에만 챌린지 이동 버튼 표시 -->
									<v-btn
										v-if="challenge?.isActive"
										size="small"
										variant="text"
										color="black"
										@click.stop="goToChallengeDetail"
									>
										<v-icon left size="16">mdi-external-link</v-icon>
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
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchComments } from '@/services/certCommentService'
import { fetchCertDetail } from '@/services/certService'

const router = useRouter()
// 인증 상세 기능을 위한 상태 변수들
const selectedCertId = ref(null) // 현재 확장된 인증 ID
const newCommentText = ref('') // 새 댓글 입력 텍스트
const loadingComments = ref(false) // 댓글 로딩 상태

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

// 새로운 함수: 인증 상세 정보 토글
async function openCertificationDetail(certification) {
	// 이미 선택된 인증을 다시 클릭하면 닫기
	if (selectedCertId.value === certification.certificationId) {
		selectedCertId.value = null
		return
	}

	// 즉시 확장 영역 표시 (데이터 로딩과 독립적으로)
	selectedCertId.value = certification.certificationId

	// 해당 인증이 이미 로드되었는지 확인
	const cert = props.certifications.find(
		(c) => c.certificationId === certification.certificationId
	)

	// 이미 로드된 데이터가 있으면 재사용, 없으면 로드
	if (!cert?.detailsLoaded) {
		await loadCertificationDetails(certification.certificationId)
	}
}

// 인증의 상세 정보 로드 (댓글, 좋아요 등)
async function loadCertificationDetails(certificationId) {
	loadingComments.value = true

	try {
		const commentsData = await fetchComments(certificationId)

		const cert = props.certifications.find(
			(c) => c.certificationId === certificationId
		)

		if (cert) {
			cert.detailedComments = commentsData || []
			cert.detailsLoaded = true
			console.log('댓글 로드 성공:', commentsData.length, '개')
		}
	} catch (error) {
		console.error('인증 댓글 로드 실패:', error)

		const cert = props.certifications.find(
			(c) => c.certificationId === certificationId
		)
		if (cert) {
			cert.detailedComments = []
			cert.detailsLoaded = true
		}
	} finally {
		loadingComments.value = false
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

function formatCommentTime(dateString) {
	if (!dateString) return ''

	const date = new Date(dateString)
	const now = new Date()
	const diffMinutes = Math.floor((now - date) / (1000 * 60))

	if (diffMinutes < 1) return '방금 전'
	if (diffMinutes < 60) return `${diffMinutes}분 전`
	if (diffMinutes < 1440) return `${Math.floor(diffMinutes / 60)}시간 전`

	return date.toLocaleDateString('ko-KR', {
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

/* 확장된 카드 스타일링 */
.cert-card-expanded {
	border: 2px solid #1976d2 !important;
	box-shadow: 0 4px 12px rgba(25, 118, 210, 0.2) !important;
}

/* 상세 정보 확장 영역 */
.cert-detail-expanded {
	background-color: #f8f9fa;
	border-top: 1px solid #e9ecef;
}

/* 댓글 입력 영역 스타일링 */
.comment-input-section {
	background-color: white;
	padding: 16px;
	border-radius: 8px;
	border: 1px solid #e0e0e0;
}

/* 댓글 스크롤 컨테이너 - 핵심 개선사항 */
.comments-scroll-container {
	height: 280px; /* max-height 대신 고정 height 사용 */
	overflow-y: auto;
	overflow-x: hidden;
	scroll-behavior: smooth;
	position: relative; /* 내부 컨텐츠 절대 위치 지정을 위해 */

	/* 부드러운 전환 효과 */
	transition: opacity 0.2s ease;

	scrollbar-width: thin;
	scrollbar-color: #bdbdbd #f5f5f5;
}
/* 로딩 상태일 때 스타일 */
.comments-scroll-container.loading-state {
	display: flex;
	align-items: center;
	justify-content: center;
	min-height: 200px; /* 로딩 시에는 조금 더 높게 */
}

/* 로딩 컨텐츠 중앙 정렬 */
.loading-content,
.comments-archive {
	min-height: 280px; /* 컨테이너와 동일한 높이 */
	display: flex;
	flex-direction: column;
}

/* 로딩 컨텐츠는 중앙 정렬 */
.loading-content {
	align-items: center;
	justify-content: center;
}

/* 댓글 목록은 상단 정렬 */
.comments-archive {
	align-items: stretch;
	justify-content: flex-start;
	padding: 16px;
}

/* 확장 영역 자체도 고정 크기 */
.cert-detail-expanded {
	background-color: #f8f9fa;
	border-top: 1px solid #e9ecef;
	height: 350px; /* 고정 높이로 애니메이션 안정화 */
	overflow: hidden;
}

/* 댓글이 없을 때 메시지도 중앙 정렬 */
.no-comments-message {
	height: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}
/* 확장된 카드 높이 안정화 */
.cert-detail-expanded {
	background-color: #f8f9fa;
	border-top: 1px solid #e9ecef;
	/* max-height 제거 - 내부 스크롤 컨테이너로 높이 제어 */
	overflow: hidden;
}

/* 웹킷 브라우저용 스크롤바 커스터마이징 */
.comments-scroll-container::-webkit-scrollbar {
	width: 6px;
}

.comments-scroll-container::-webkit-scrollbar-track {
	background: #f5f5f5;
	border-radius: 3px;
}

.comments-scroll-container::-webkit-scrollbar-thumb {
	background: #bdbdbd;
	border-radius: 3px;
	transition: background 0.2s ease;
}

.comments-scroll-container::-webkit-scrollbar-thumb:hover {
	background: #9e9e9e;
}

/* 확장된 카드 자체의 최대 높이도 제한 */
.cert-detail-expanded {
	background-color: #f8f9fa;
	border-top: 1px solid #e9ecef;
	max-height: 420px; /* 전체 확장 영역의 최대 높이 설정 */
	overflow: hidden; /* 전체 영역에서는 스크롤 숨김 */
}

/* 댓글 아이템의 스타일링 개선 */
.comment-archive-item {
	transition: background-color 0.2s ease;
	border-left: 3px solid #e3f2fd; /* 왼쪽에 얇은 구분선 추가 */
}

.comment-archive-item:hover {
	background-color: #e8f5e8 !important; /* 호버 시 색상 변경 */
}

/* 댓글 내용의 가독성 개선 */
.comment-content {
	line-height: 1.5; /* 줄 간격 늘려서 읽기 편하게 */
	word-break: break-word; /* 긴 단어가 있을 때 적절히 줄바꿈 */
}

/* 기존 스타일들... */
.cert-card {
	transition: all 0.3s ease;
}

.cert-card:hover {
	transform: translateY(-1px);
}

.cert-card-expanded {
	border: 2px solid #1976d2 !important;
	box-shadow: 0 4px 12px rgba(25, 118, 210, 0.2) !important;
}
</style>
