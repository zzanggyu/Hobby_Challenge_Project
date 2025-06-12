<template>
	<v-container>
		<!-- 헤더와 컨트롤 영역 -->
		<v-row class="mb-4">
			<v-col cols="12" class="d-flex justify-space-between align-center">
				<h3 class="text-h6">
					{{ onlyMine ? '내 인증 내역' : '전체 인증 내역' }}
				</h3>

				<!-- 컨트롤 영역 -->
				<div class="d-flex align-center gap-3">
					<!-- 정렬 옵션 -->
					<v-select
						v-model="sortBy"
						:items="sortOptions"
						item-title="text"
						item-value="value"
						label="정렬"
						density="compact"
						hide-details
						style="min-width: 150px"
					/>

					<!-- 페이지 크기 선택 -->
				</div>
			</v-col>
		</v-row>

		<!-- 로딩 상태 -->
		<v-row v-if="loading" justify="center" class="my-8">
			<v-progress-circular indeterminate color="primary" size="64" />
		</v-row>

		<!-- 날짜별 그룹화된 인증 목록 -->
		<div v-else-if="groupedLogs.length">
			<div v-for="group in groupedLogs" :key="group.date" class="mb-6">
				<!-- 날짜 헤더 -->
				<div class="date-header mb-3">
					<v-chip color="black" label>
						<v-icon left small>mdi-calendar</v-icon>
						{{ formatDateHeader(group.date) }}
					</v-chip>
					<span class="ml-2 text-caption grey--text">
						({{ group.items.length }}개의 인증)
					</span>
				</div>

				<!-- 해당 날짜의 인증 카드들 -->
				<v-row>
					<v-col
						v-for="log in group.items"
						:key="log.certificationId"
						cols="12"
						md="6"
						lg="4"
					>
						<v-hover v-slot="{ isHovering, props }">
							<v-card
								v-bind="props"
								:elevation="isHovering ? 8 : 2"
								rounded="lg"
								height="450"
								class="cert-card"
								:data-cert-id="log.certificationId"
								:class="{ clickable: canViewDetail }"
								@click="
									canViewDetail
										? openDialog(log.certificationId)
										: showAccessDenied()
								"
							>
								<!-- 이미지 영역 -->
								<div class="image-container">
									<v-img
										v-if="log.imageUrl"
										:src="log.imageUrl"
										:aspect-ratio="4 / 3"
										height="300"
										cover
										class="cert-image"
									>
										<!-- 오버레이는 제거하거나 최소화 -->
										<div class="cert-overlay-minimal">
											<v-chip
												size="x-small"
												color="rgba(0,0,0,0.6)"
												class="ma-2"
											>
												<v-icon size="12" class="mr-1"
													>mdi-camera</v-icon
												>
												인증
											</v-chip>
										</div>
									</v-img>

									<!-- 이미지가 없는 경우 -->
									<div v-else class="no-image-placeholder">
										<v-icon size="48" color="grey"
											>mdi-image-off</v-icon
										>
									</div>
								</div>

								<!-- 카드 내용 -->
								<v-card-text>
									<!-- 시간 표시 -->
									<div class="text-caption grey--text mb-2">
										{{ formatTime(log.createdDate) }}
									</div>

									<!-- 코멘트 -->
									<p class="text-body-2 mb-3 comment-preview">
										{{ log.comment || '' }}
									</p>

									<!-- 좋아요/댓글 수 -->
									<div
										class="d-flex justify-space-between align-center"
									>
										<div>
											<v-chip small outlined class="mr-2">
												<v-icon small left>mdi-heart</v-icon>
												{{ log.likeCount || 0 }}
											</v-chip>
											<v-chip small outlined>
												<v-icon small left>mdi-comment</v-icon>
												{{ log.commentCount || 0 }}
											</v-chip>
										</div>

										<div class="d-flex align-center">
											<!-- 레벨  -->
											<v-chip
												size="x-small"
												:color="getLevelColor(log.level || 1)"
												class="mr-2"
											>
												<v-icon size="10" left>mdi-star</v-icon>
												Lv.{{ log.level || 1 }}
											</v-chip>

											<!-- 닉네임 -->
											<div>
												<v-icon size="16">mdi-account</v-icon>
												{{ log.nickname }}
											</div>
										</div>
									</div>
								</v-card-text>
								<div v-if="!canViewDetail" class="access-overlay">
									<v-icon color="white" size="20">mdi-lock</v-icon>
									<span class="access-text">참여 후 상세보기</span>
								</div>
							</v-card>
						</v-hover>
					</v-col>
				</v-row>

				<v-divider class="mt-4" />
			</div>
		</div>

		<!-- 비어있을 때 -->
		<v-row v-else>
			<v-col class="text-center py-10">
				<v-icon size="64" color="grey lighten-2">mdi-image-off</v-icon>
				<p class="text-h6 grey--text mt-4">
					{{
						onlyMine
							? '아직 인증 내역이 없습니다.'
							: '첫 번째 인증을 등록해보세요!'
					}}
				</p>
			</v-col>
		</v-row>

		<!-- 페이지네이션 -->
		<v-row v-if="totalPages > 0" justify="center" class="mt-6">
			<v-pagination
				v-model="currentPage"
				:length="totalPages"
				:total-visible="7"
				color="black"
				rounded="circle"
				show-first-last-page
			/>
		</v-row>

		<!-- 인증 상세 모달 -->
		<v-dialog v-model="dialog" max-width="600">
			<CertificationDetailDialog
				v-if="selectedCertId"
				:certificationId="selectedCertId"
				:challengeId="props.challengeId"
				@close="dialog = false"
				@deleted="onDeleted"
				@updated="onUpdated"
			/>
		</v-dialog>
	</v-container>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { getCertifications, deleteCertification } from '@/services/certService'
import { useAuthStore } from '@/stores/auth'
import CertificationDetailDialog from './CertificationDetailDialog.vue'
import { format, parseISO, isToday, isYesterday } from 'date-fns'
import { ko } from 'date-fns/locale'

const props = defineProps({
	challengeId: { type: Number, required: true },
	refreshKey: { type: Number, required: true },
	onlyMine: { type: Boolean, default: false },
	canWritePermission: { type: Boolean, default: false },
	autoOpenCertId: { type: Number, default: null }, // 자동 열기할 인증 ID
})

const emit = defineEmits(['cert-modal-opened'])

// 상태 관리
const logs = ref([])
const loading = ref(false)
const dialog = ref(false)
const selectedCertId = ref(null)
const auth = useAuthStore()

// 정렬과 페이징 상태
const sortBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(20)
const totalCount = ref(0)

// 계산된 속성
const totalPages = computed(() => Math.ceil(totalCount.value / pageSize.value))

// 정렬 옵션
const sortOptions = [
	{ text: '최신순', value: 'latest' },
	{ text: '좋아요순', value: 'likes' },
	{ text: '댓글순', value: 'comments' },
]

// 날짜별로 그룹화된 로그 (페이징된 데이터에 대해서만)
const groupedLogs = computed(() => {
	// 정렬 적용 (서버에서 받은 데이터를 클라이언트에서 추가 정렬)
	let sorted = [...logs.value]

	switch (sortBy.value) {
		case 'likes':
			sorted.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
			break
		case 'comments':
			sorted.sort((a, b) => (b.commentCount || 0) - (a.commentCount || 0))
			break
		default: // latest
			sorted.sort(
				(a, b) => new Date(b.createdDate) - new Date(a.createdDate)
			)
	}

	// 날짜별로 그룹화
	const groups = {}
	sorted.forEach((log) => {
		const date = log.certDate || log.createdDate.split('T')[0]
		if (!groups[date]) {
			groups[date] = []
		}
		groups[date].push(log)
	})

	// 배열 형태로 변환하고 날짜 역순 정렬
	return Object.entries(groups)
		.sort((a, b) => b[0].localeCompare(a[0]))
		.map(([date, items]) => ({ date, items }))
})

// 날짜 포맷팅 함수들
function formatDateHeader(dateStr) {
	const date = parseISO(dateStr)
	if (isToday(date)) return '오늘'
	if (isYesterday(date)) return '어제'
	return format(date, 'M월 d일 (EEEE)', { locale: ko })
}

function formatTime(datetime) {
	return format(parseISO(datetime), 'a h:mm', { locale: ko })
}

// 레벨별 색상
function getLevelColor(level) {
	if (level >= 40) return 'black'
	if (level >= 30) return 'purple'
	if (level >= 25) return 'deepblue'
	if (level >= 20) return 'blue'
	if (level >= 15) return 'green'
	if (level >= 10) return 'yellow'
	if (level >= 5) return 'orange'
	if (level >= 2) return 'red'
	return 'grey'
}

//  autoOpenCertId 감시하여 자동 모달 열기
watch(
	() => props.autoOpenCertId,
	(certId) => {
		if (certId) {
			//  데이터가 로드되기를 기다림
			const tryOpenModal = () => {
				if (logs.value.length === 0) {
					setTimeout(tryOpenModal, 200)
					return
				}

				// 해당 인증이 현재 로드된 목록에 있는지 확인
				const targetCert = logs.value.find(
					(log) => log.certificationId === certId
				)

				if (targetCert) {
					// 바로 모달 열기
					openDialog(certId)
					emit('cert-modal-opened') // 부모에게 모달 열림 알림

					// 해당 인증 스크롤 (애니메이션 없이)
					setTimeout(() => {
						scrollToCertification(certId)
					}, 300)
				} else {
					//  전체 페이지에서 검색
					searchAndOpenCertification(certId)
				}
			}

			// 즉시 시도하거나 약간의 지연 후 시도
			if (logs.value.length > 0) {
				tryOpenModal()
			} else {
				setTimeout(tryOpenModal, 300)
			}
		}
	}
)

//  전체 페이지에서 인증 검색
async function searchAndOpenCertification(certId) {
	// 첫 페이지로 이동
	currentPage.value = 1

	// 데이터 로드 대기
	await new Promise((resolve) => setTimeout(resolve, 500))

	const targetCert = logs.value.find((log) => log.certificationId === certId)
	if (targetCert) {
		openDialog(certId)
		emit('cert-modal-opened')
		setTimeout(() => scrollToCertification(certId), 300)
	} else {
		// 모달은 열되 에러 메시지 표시하지 않음 (사용자 경험 고려)
	}
}

// 스크롤 함수
function scrollToCertification(certId) {
	const certElement = document.querySelector(`[data-cert-id="${certId}"]`)
	if (certElement) {
		// 부드러운 스크롤만
		certElement.scrollIntoView({
			behavior: 'smooth',
			block: 'center',
		})
	} else {
	}
}

//  다이얼로그 닫힐 때 새로고침
watch(dialog, (newValue, oldValue) => {
	if (oldValue === true && newValue === false) {
		fetchLogs()
	}
})

//  상세보기 권한 계산
const canViewDetail = computed(() => {
	// 본인 인증만 보는 경우는 항상 허용
	if (props.onlyMine) return true

	// 전체 인증 목록에서는 참여 권한이 있을 때만 상세보기 허용
	return props.canWritePermission
})

//  접근 거부 알림
function showAccessDenied() {
	alert('챌린지에 참여한 후 인증 상세를 볼 수 있습니다.')
}

// 페이징된 데이터 로드 함수
async function fetchLogs() {
	loading.value = true
	try {
		//  페이징 API 호출로 변경
		const result = await getCertifications(
			props.challengeId,
			currentPage.value,
			pageSize.value,
			props.onlyMine
		)

		// PageResponseDTO 구조에 따라 데이터 추출
		logs.value = result.items || []
		totalCount.value = result.totalCount || 0

		console.log(
			`페이지 ${currentPage.value}: ${logs.value.length}개 로드, 총 ${totalCount.value}개`
		)
	} catch (error) {
		console.error('인증 목록 로드 실패:', error)
		logs.value = []
		totalCount.value = 0
	} finally {
		loading.value = false
	}
}

// 다이얼로그
function openDialog(certId) {
	selectedCertId.value = certId
	dialog.value = true
}

// 이벤트 핸들러들
// 삭제 시
function onDeleted(certId) {
	// 삭제 후 현재 페이지를 다시 로드
	fetchLogs()
	dialog.value = false
}
// 수정 시
async function onUpdated(eventData) {
	try {
		await fetchLogs()
	} catch (error) {
		console.error(' 목록 새로고침 실패:', error)
	}
}

onMounted(() => {
	fetchLogs()
})

// props 변경 시 페이지 초기화하고 데이터 재로드
watch(
	() => [props.challengeId, props.refreshKey, props.onlyMine],
	() => {
		currentPage.value = 1 // 페이지 초기화
		fetchLogs()
	},
	{ immediate: true }
)

// 페이지 변경 시 데이터 재로드
watch(currentPage, () => {
	fetchLogs()
})

// 페이지 크기 변경 시 첫 페이지로 돌아가고 데이터 재로드
watch(pageSize, () => {
	currentPage.value = 1
	fetchLogs()
})

// 정렬 변경 시에는 현재 페이지 데이터만 재정렬 (서버 요청 X)
// 만약 서버에서 정렬을 처리하려면 fetchLogs() 호출
</script>

<style scoped>
.date-header {
	display: flex;
	align-items: center;
	padding: 8px 0;
}

.cert-card {
	cursor: pointer;
	transition: all 0.3s;
	height: 100%;
}

.cert-card:hover {
	transform: translateY(-4px);
}

.cert-overlay {
	background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
	padding: 12px;
	border-radius: 0 0 12px 12px;
}

.comment-preview {
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	min-height: 3rem;
}
.image-container {
	position: relative;
	width: 100%;
	height: 300px; /* 고정 높이 */
	overflow: hidden;
}

.cert-image {
	width: 100%;
	height: 100%;
	object-fit: cover; /* 비율 유지하면서 꽉 채움 */
}

/* 컨트롤 영역 간격 */
.gap-3 {
	gap: 12px;
}
</style>
