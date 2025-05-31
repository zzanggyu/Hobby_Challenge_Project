<!-- ChallengeCertificationList.vue 레이아웃 개선 -->
<template>
	<v-container>
		<!-- 정렬 옵션 추가 -->
		<v-row class="mb-4">
			<v-col cols="12" class="d-flex justify-space-between align-center">
				<h3 class="text-h6">
					{{ onlyMine ? '내 인증 내역' : '전체 인증 내역' }}
				</h3>
				<v-select
					v-model="sortBy"
					:items="sortOptions"
					item-title="text"
					item-value="value"
					label="정렬"
					dense
					hide-details
					style="max-width: 200px"
				/>
			</v-col>
		</v-row>

		<!-- 인증 목록 - 개선된 레이아웃 -->
		<v-row v-if="sortedLogs.length">
			<v-col
				v-for="log in sortedLogs"
				:key="log.certificationId"
				cols="12"
				md="6"
				lg="4"
			>
				<v-hover v-slot="{ isHovering, props }">
					<v-card
						v-bind="props"
						:elevation="isHovering ? 8 : 2"
						rounded="xl"
						class="cert-card"
						@click="openDialog(log.certificationId)"
					>
						<!-- 이미지 영역 -->
						<v-img
							v-if="log.imageUrl"
							:src="log.imageUrl"
							height="250"
							cover
							class="align-end"
						>
							<v-card-title class="white--text">
								<div class="cert-overlay">
									<v-avatar size="32" class="mr-2">
										<v-img
											v-if="log.userImageUrl"
											:src="log.userImageUrl"
										/>
										<v-icon v-else>mdi-account</v-icon>
									</v-avatar>
									<span>{{ log.nickname }}</span>
								</div>
							</v-card-title>
						</v-img>

						<!-- 이미지 없을 때 -->
						<div v-else class="no-image-placeholder">
							<v-icon size="64" color="grey lighten-2"
								>mdi-image-off</v-icon
							>
						</div>

						<!-- 카드 내용 -->
						<v-card-text>
							<!-- 날짜와 레벨 -->
							<div
								class="d-flex justify-space-between align-center mb-2"
							>
								<span class="text-caption">{{
									formatDate(log.createdDate)
								}}</span>
								<v-chip
									x-small
									:color="getLevelColor(log.level)"
									text-color="white"
								>
									Lv.{{ log.level }}
								</v-chip>
							</div>

							<!-- 코멘트 -->
							<p class="text-body-2 mb-3 comment-preview">
								{{ log.comment }}
							</p>

							<!-- 좋아요/댓글 수 -->
							<div class="d-flex justify-space-between align-center">
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

								<!-- 본인 인증일 때만 수정/삭제 버튼 -->
								<div v-if="log.userId === auth.user.userId">
									<v-btn icon x-small @click.stop="editCert(log)">
										<v-icon small>mdi-pencil</v-icon>
									</v-btn>
									<v-btn
										icon
										x-small
										@click.stop="deleteCert(log.certificationId)"
									>
										<v-icon small>mdi-delete</v-icon>
									</v-btn>
								</div>
							</div>
						</v-card-text>
					</v-card>
				</v-hover>
			</v-col>
		</v-row>

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

		<!-- 인증 상세 모달 -->
		<v-dialog v-model="dialog" max-width="600">
			<CertificationDetailDialog
				v-if="selectedCertId"
				:certificationId="selectedCertId"
				:challengeId="props.challengeId"
				@close="dialog = false"
				@deleted="onDeleted"
			/>
		</v-dialog>
	</v-container>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
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
const sortBy = ref('latest')
const dialog = ref(false)
const selectedCertId = ref(null)
const auth = useAuthStore()

// 정렬 옵션
const sortOptions = [
	{ text: '최신순', value: 'latest' },
	{ text: '좋아요순', value: 'likes' },
	{ text: '댓글순', value: 'comments' },
]

// 정렬된 로그
const sortedLogs = computed(() => {
	const sorted = [...logs.value]

	switch (sortBy.value) {
		case 'likes':
			return sorted.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
		case 'comments':
			return sorted.sort(
				(a, b) => (b.commentCount || 0) - (a.commentCount || 0)
			)
		default: // latest
			return sorted.sort(
				(a, b) => new Date(b.createdDate) - new Date(a.createdDate)
			)
	}
})

// ... 기존 함수들 ...

// 인증 삭제 후 처리
function onDeleted(certId) {
	logs.value = logs.value.filter((log) => log.certificationId !== certId)
	dialog.value = false
}

// 인증 수정 (구현 예정)
function editCert(cert) {
	// TODO: 수정 모달 열기
	console.log('Edit cert:', cert)
}

// 인증 삭제
async function deleteCert(certId) {
	if (!confirm('정말 이 인증을 삭제하시겠습니까?')) return

	try {
		await deleteCertification(props.challengeId, certId)
		await fetchLogs()
	} catch (error) {
		console.error('인증 삭제 실패:', error)
		alert('인증 삭제에 실패했습니다.')
	}
}
</script>

<style scoped>
.cert-card {
	cursor: pointer;
	transition: all 0.3s;
}

.cert-card:hover {
	transform: translateY(-4px);
}

.cert-overlay {
	background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
	padding: 12px;
	border-radius: 0 0 12px 12px;
}

.no-image-placeholder {
	height: 250px;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #f5f5f5;
}

.comment-preview {
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 3;
	-webkit-box-orient: vertical;
	min-height: 60px;
}
</style>
