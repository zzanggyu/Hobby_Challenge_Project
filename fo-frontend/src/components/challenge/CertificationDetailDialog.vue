<template>
	<v-card>
		<v-toolbar flat>
			<v-btn icon @click="$emit('close')">
				<v-icon>mdi-close</v-icon>
			</v-btn>
			<v-toolbar-title>인증 상세</v-toolbar-title>

			<!-- 삭제·수정 버튼 (본인 인증만) -->
			<v-spacer />
			<template v-if="cert.userId === auth.user.userId">
				<v-btn icon @click="onEdit">
					<v-icon color="primary">mdi-pencil</v-icon>
				</v-btn>
				<v-btn icon @click="onDelete">
					<v-icon color="error">mdi-delete</v-icon>
				</v-btn>
			</template>
		</v-toolbar>

		<v-card-text>
			<!-- 1) 이미지 & 코멘트 -->
			<div class="image-container mb-4">
				<v-img
					:src="cert.imageUrl"
					max-height="600"
					contain
					class="rounded-lg cert-detail-image clickable-image"
					@click="showFullImage = true"
				>
					<!-- 확대 힌트 -->
					<div class="zoom-hint">
						<v-icon color="white" size="16">mdi-magnify-plus</v-icon>
					</div>
				</v-img>

				<!-- 전체화면 이미지 모달 -->
				<v-dialog v-model="showFullImage" max-width="95vw">
					<v-card>
						<v-toolbar flat>
							<v-spacer />
							<v-btn icon @click="showFullImage = false">
								<v-icon>mdi-close</v-icon>
							</v-btn>
						</v-toolbar>
						<v-img :src="cert.imageUrl" max-height="90vh" contain />
					</v-card>
				</v-dialog>
			</div>

			<!-- 수정 모드일 때 -->
			<div v-if="editing">
				<!-- 🆕 수정 모드 이미지 미리보기 -->
				<div v-if="editing" class="mb-4">
					<h4 class="text-subtitle-1 mb-3">
						{{
							editedImagePreview ? '새 이미지 미리보기' : '현재 이미지'
						}}
					</h4>

					<div class="preview-container position-relative">
						<v-img
							:src="editedImagePreview || cert.imageUrl"
							max-height="500"
							contain
							class="rounded-lg preview-image"
						>
							<!-- 상태 표시 칩 -->
							<v-chip
								:color="editedImagePreview ? 'success' : 'orange'"
								size="small"
								class="status-chip"
							>
								<v-icon left size="16">
									{{
										editedImagePreview
											? 'mdi-check-circle'
											: 'mdi-image'
									}}
								</v-icon>
								{{ editedImagePreview ? '새 이미지' : '기존 이미지' }}
							</v-chip>

							<!-- 로딩 표시 -->
							<template v-slot:placeholder>
								<div
									class="d-flex align-center justify-center fill-height"
								>
									<v-progress-circular
										indeterminate
										color="grey-lighten-5"
									/>
								</div>
							</template>
						</v-img>
					</div>
				</div>

				<!--  파일 업로드 영역 (간소화) -->
				<div class="mb-4">
					<v-file-input
						:key="`file-input-${editing}`"
						:model-value="editedImageFile"
						@update:model-value="onImageChange"
						label="새 이미지 선택 (선택사항)"
						accept="image/*"
						prepend-icon="mdi-camera-plus"
						show-size
						clearable
						variant="outlined"
						density="compact"
						:disabled="imageProcessing"
						:rules="[
							(files) =>
								!files ||
								files.size <= 5 * 1024 * 1024 ||
								'파일 크기는 5MB 이하여야 합니다.',
							(files) =>
								!files ||
								files.type?.startsWith('image/') ||
								'이미지 파일만 업로드 가능합니다.',
						]"
						hint="JPG, PNG, GIF → 자동으로 800x600 JPEG로 최적화됩니다 (10MB까지 업로드 가능)"
						persistent-hint
					>
						<template v-slot:prepend>
							<v-icon
								:color="
									editedImageFile
										? 'success'
										: imageProcessing
										? 'warning'
										: 'grey'
								"
							>
								{{
									imageProcessing
										? 'mdi-cog-sync'
										: editedImageFile
										? 'mdi-check-circle'
										: 'mdi-camera-plus'
								}}
							</v-icon>
						</template>
					</v-file-input>
				</div>

				<!-- 코멘트 수정 -->
				<v-textarea
					v-model="editedComment"
					label="코멘트 수정"
					outlined
					rows="3"
					counter="500"
					maxlength="500"
					class="mb-3"
				/>
				<v-btn small color="primary" @click="saveEdit">저장</v-btn>
				<v-btn small @click="cancelEdit">취소</v-btn>
			</div>
			<div v-else>
				<p>{{ cert.comment }}</p>
			</div>

			<!-- 2) 좋아요 -->
			<v-btn icon @click="toggleLike">
				<v-icon :color="liked ? 'red' : 'grey'">mdi-heart</v-icon>
			</v-btn>
			<span>{{ cert.likeCount }} likes</span>

			<v-divider class="my-4" />

			<!-- 3) 댓글 리스트 -->
			<div
				v-for="c in comments"
				:key="c.commentId"
				class="mb-2 d-flex align-center justify-space-between"
			>
				<!-- 왼쪽: 닉네임/본문 -->
				<div>
					<strong class="mr-2">{{ c.nickname }}:</strong>
					<template v-if="editingCommentId === c.commentId">
						<v-textarea
							v-model="editedContent"
							dense
							rows="4"
							auto-grow
							max-rows="5"
							:counter="100"
							maxlength="100"
							style="min-width: 500px"
						/>
						<v-btn
							small
							color="primary"
							class="ml-1"
							@click="saveEditedComment"
						>
							저장
						</v-btn>
					</template>
					<template v-else>
						<span>{{ c.content }}</span>
					</template>
				</div>

				<!-- 오른쪽: 수정/삭제 아이콘 (본인 댓글일 때만) -->
				<div
					v-if="c.nickname === auth.user.nickname"
					class="d-flex align-center"
				>
					<v-menu>
						<template v-slot:activator="{ props }">
							<v-btn icon variant="text" size="small" v-bind="props">
								<v-icon size="18">mdi-dots-vertical</v-icon>
							</v-btn>
						</template>

						<v-list density="compact" min-width="100">
							<v-list-item @click="startEditComment(c)">
								<template v-slot:prepend>
									<v-icon size="18">mdi-pencil</v-icon>
								</template>
								<v-list-item-title>수정</v-list-item-title>
							</v-list-item>

							<v-list-item @click="removeComment(c.commentId)">
								<template v-slot:prepend>
									<v-icon size="18" color="error">mdi-delete</v-icon>
								</template>
								<v-list-item-title>삭제</v-list-item-title>
							</v-list-item>
						</v-list>
					</v-menu>
				</div>
			</div>

			<!-- 4) 댓글 입력 -->

			<v-textarea
				v-model="newComment"
				label="댓글 남기기"
				placeholder="따뜻한 응원 메시지를 남겨보세요!"
				variant="outlined"
				rows="4"
				auto-grow
				max-rows="5"
				:counter="100"
				maxlength="100"
				@keyup.enter="postComment"
				:rules="[(v) => !v || v.length <= 100 || '100자 이내로 입력하세요']"
				dense
			>
				<template v-slot:append-inner>
					<span class="text-caption"> {{ newComment.length }}/100 </span>
				</template>
			</v-textarea>
			<v-btn small color="primary" @click="postComment">등록</v-btn>
		</v-card-text>
	</v-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { resizeImage, getImageInfo, formatFileSize } from '@/utils/imageUtils'
import {
	fetchCertDetail,
	deleteCertification,
	likeCert,
	updateCertification,
} from '@/services/certService'
import {
	fetchComments,
	addComment,
	updateComment,
	deleteComment,
} from '@/services/certCommentService'

const props = defineProps({
	certificationId: { type: Number, required: true },
	challengeId: { type: Number, required: true },
})

const auth = useAuthStore()
const cert = ref({})
const comments = ref([])
const newComment = ref('')
const liked = ref(false)

// 이미지 리사이징
const compressionInfo = ref(null)
const imageProcessing = ref(false)

// 댓글 수정 상태
const editingCommentId = ref(null)
const editedContent = ref('')

// 인증 수정 상태
const editing = ref(false)
const editedComment = ref('')
const editedImageFile = ref(null)
const editedImagePreview = ref(null)
const savingEdit = ref(false)
// 이미지 확대 상태
const showFullImage = ref(false)

const emit = defineEmits(['close', 'deleted'])

// 데이터 로딩
async function load() {
	try {
		cert.value = await fetchCertDetail(
			props.challengeId,
			props.certificationId
		)
		comments.value = await fetchComments(props.certificationId)
		liked.value = cert.value.likedByMe || false
	} catch (error) {
		//  접근 권한 에러 처리
		if (error.response?.data?.errorCode === 'CERTIFICATION_ACCESS_DENIED') {
			alert('챌린지에 참여한 후 인증 상세를 볼 수 있습니다.')
			emit('close') // 모달 닫기
			return
		}
		console.error('인증 상세 로드 실패:', error)
	}
}

onMounted(load)

// 좋아요
async function toggleLike() {
	try {
		const { liked: result } = await likeCert(
			props.challengeId,
			props.certificationId
		)
		liked.value = result
		cert.value.likeCount += result ? 1 : -1
	} catch (e) {
		alert('좋아요 처리 중 오류가 발생했습니다.')
	}
}

// 댓글 등록
async function postComment() {
	if (!newComment.value) return
	await addComment(props.certificationId, newComment.value)
	newComment.value = ''
	comments.value = await fetchComments(props.certificationId)
}

// 댓글 수정 시작
function startEditComment(c) {
	console.log('선택된 댓글:', c)
	editingCommentId.value = c.commentId
	editedContent.value = c.content
}

// 댓글 수정
async function saveEditedComment() {
	if (!editedContent.value) return

	console.log('댓글 수정 데이터 확인:', {
		commentId: editingCommentId.value,
		type: typeof editingCommentId.value,
	})

	await updateComment(
		props.certificationId,
		editingCommentId.value,
		editedContent.value
	)
	editingCommentId.value = null
	comments.value = await fetchComments(props.certificationId)
}

// 댓글 삭제
async function removeComment(commentId) {
	if (!confirm('댓글을 삭제하시겠습니까?')) return
	await deleteComment(props.certificationId, commentId)
	comments.value = await fetchComments(props.certificationId)
}

// 인증 삭제
async function onDelete() {
	if (!confirm('정말 삭제하시겠습니까?')) return
	await deleteCertification(props.challengeId, props.certificationId)
	emit('deleted', props.certificationId)
	emit('close')
}

// 개선된 onImageChange 함수

async function onImageChange(files) {
	console.log('onImageChange 호출됨:', files, typeof files)

	// null/undefined 체크
	if (files === null || files === undefined) {
		console.log(' files가 null/undefined - 상태 초기화')
		resetImageState()
		return
	}

	let selectedFile = null

	// 전달된 데이터 타입에 따라 처리
	if (files instanceof File) {
		selectedFile = files
	} else if (files instanceof FileList) {
		selectedFile = files.length > 0 ? files[0] : null
	} else if (Array.isArray(files)) {
		selectedFile = files.length > 0 ? files[0] : null
	} else {
		console.log(' 알 수 없는 형태:', typeof files, files)
		resetImageState()
		return
	}

	// 선택된 파일 검증
	if (!selectedFile || !(selectedFile instanceof File)) {
		console.log(' 유효하지 않은 파일')
		resetImageState()
		return
	}

	// 기본 검증
	if (selectedFile.size > 5 * 1024 * 1024) {
		// 10MB로 증가 (리사이징 전이므로)
		alert('파일 크기는 5MB 이하여야 합니다.')
		resetImageState()
		return
	}

	if (!selectedFile.type.startsWith('image/')) {
		alert('이미지 파일만 업로드 가능합니다.')
		resetImageState()
		return
	}

	try {
		imageProcessing.value = true

		//  원본 이미지 정보 확인
		console.log(' 원본 이미지 분석 중...')
		const originalInfo = await getImageInfo(selectedFile)

		console.log(' 원본 정보:', originalInfo)

		// 이미지 리사이징 수행
		console.log(' 이미지 리사이징 시작...')
		const resizedFile = await resizeImage(selectedFile, {
			maxWidth: 800, // 챌린지 인증에 적합한 크기
			maxHeight: 600,
			quality: 0.85, // 85% 품질 (Instagram 수준)
			format: 'jpeg', // 호환성을 위해 JPEG 통일
		})

		// 압축 정보 계산
		const compressionRatio = (
			((selectedFile.size - resizedFile.size) / selectedFile.size) *
			100
		).toFixed(1)
		compressionInfo.value = {
			original: formatFileSize(selectedFile.size),
			compressed: formatFileSize(resizedFile.size),
			ratio: compressionRatio,
			dimensions: `${originalInfo.width}x${originalInfo.height}`,
		}

		//  상태 업데이트
		editedImageFile.value = resizedFile

		//  미리보기 생성
		const reader = new FileReader()
		reader.onload = (e) => {
			editedImagePreview.value = e.target.result
			console.log(' 미리보기 생성 완료')
		}
		reader.onerror = (e) => {
			console.error(' 파일 읽기 실패:', e)
			alert('파일을 읽는 중 오류가 발생했습니다.')
			resetImageState()
		}
		reader.readAsDataURL(resizedFile)
	} catch (error) {
		console.error(' 이미지 처리 실패:', error)
		alert(`이미지 처리 중 오류가 발생했습니다: ${error.message}`)
		resetImageState()
	} finally {
		imageProcessing.value = false
	}
}

// 상태 초기화 헬퍼 함수
function resetImageState() {
	editedImageFile.value = null
	editedImagePreview.value = null
	compressionInfo.value = null
}

//  인증 수정 시작
function onEdit() {
	editing.value = true
	editedComment.value = cert.value.comment
	editedImageFile.value = null
	editedImagePreview.value = null
}

// 인증 수정 취소
function cancelEdit() {
	editing.value = false
	editedComment.value = ''
	resetImageState()
}

// 인증 게시글 편집
// 인증 수정 저장 - 이미지 + 코멘트
async function saveEdit() {
	if (!editedComment.value.trim()) {
		alert('코멘트를 입력해주세요.')
		return
	}

	savingEdit.value = true

	try {
		console.log('=== 인증 수정 시작 ===')
		console.log('Props:', {
			challengeId: props.challengeId,
			certificationId: props.certificationId,
		})
		console.log('수정 내용:', {
			comment: editedComment.value,
			hasNewImage: !!editedImageFile.value,
		})

		const formData = new FormData()
		formData.append('comment', editedComment.value)

		// FormData 생성
		if (editedImageFile.value) {
			formData.append('image', editedImageFile.value)
		}

		// FormData 내용 확인
		console.log('FormData 내용:')
		for (let [key, value] of formData.entries()) {
			console.log(`${key}:`, value)
		}

		// API 호출
		const updatedCert = await updateCertification(
			props.challengeId,
			props.certificationId,
			formData
		)

		// 성공 시 상태 업데이트
		cert.value = updatedCert
		editing.value = false
		editedImageFile.value = null
		editedImagePreview.value = null

		console.log('=== 인증 수정 완료 ===')
		console.log('업데이트된 인증:', updatedCert)

		alert('인증이 성공적으로 수정되었습니다!')
	} catch (error) {
		console.error('=== 인증 수정 실패 ===')
		console.error('에러:', error)
		console.error('응답:', error.response?.data)

		const errorMessage =
			error.response?.data?.message || '인증 수정에 실패했습니다.'
		alert(errorMessage)
	} finally {
		savingEdit.value = false
	}
}
</script>

<style scoped>
.cert-detail-image {
	width: 100%;
	background-color: #f8f9fa; /* 연한 회색 배경 */
	border: 1px solid #e9ecef;
}

.image-container {
	display: flex;
	justify-content: center;
	align-items: center;
	width: 100%;
	border-radius: 12px;
	overflow: hidden;
}

.preview-image {
	background-color: #f8f9fa;
	border: 1px solid #e9ecef;
}

/* 반응형 처리 */
@media (max-width: 600px) {
	.cert-detail-image {
		max-height: 400px;
	}

	.preview-image {
		max-height: 350px;
	}
}
.gap-2 {
	gap: 8px;
}

/* 미리보기 이미지 스타일 */
.v-img {
	border-radius: 8px;
}

/* 수정 모드 구분을 위한 배경 */
.editing-mode {
	background-color: #f5f5f5;
	padding: 16px;
	border-radius: 8px;
	border: 2px dashed #2196f3;
}
.clickable-image {
	cursor: pointer;
	transition: transform 0.2s ease;
	position: relative;
}

.clickable-image:hover {
	transform: scale(1.02);
}

.zoom-hint {
	position: absolute;
	top: 8px;
	right: 8px;
	background: rgba(0, 0, 0, 0.6);
	border-radius: 50%;
	padding: 4px;
	opacity: 0.7;
}

.clickable-image:hover .zoom-hint {
	opacity: 1;
}
</style>
