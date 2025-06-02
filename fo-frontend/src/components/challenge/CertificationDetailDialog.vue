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
			<v-img :src="cert.imageUrl" height="250" class="mb-4" />

			<!-- 수정 모드일 때 -->
			<div v-if="editing">
				<v-text-field
					v-model="editedComment"
					label="코멘트 수정"
					outlined
					dense
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
					<v-btn icon small class="ml-1" @click="startEditComment(c)">
						<v-icon>mdi-pencil</v-icon>
					</v-btn>
					<v-btn
						icon
						small
						class="ml-1"
						@click="removeComment(c.commentId)"
					>
						<v-icon>mdi-delete</v-icon>
					</v-btn>
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
				:rules="[(v) => !v || v.length <= 100 || '100자 이내로 입력하세요']"
				dense
			>
				<template v-slot:append-inner>
					<span class="text-caption" :class="commentLengthColor">
						{{ newComment.length }}/100
					</span>
				</template>
			</v-textarea>
			<v-btn small color="primary" @click="postComment">등록</v-btn>
		</v-card-text>
	</v-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import {
	fetchCertDetail,
	deleteCertification,
	likeCert,
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

// 댓글 수정 상태
const editingCommentId = ref(null)
const editedContent = ref('')

// 인증 수정 모드 상태
const editing = ref(false)
const editedComment = ref('')

const emit = defineEmits(['close', 'deleted'])

// 데이터 로딩
async function load() {
	cert.value = await fetchCertDetail(props.challengeId, props.certificationId)
	comments.value = await fetchComments(props.certificationId)
	liked.value = cert.value.likedByMe
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

// 댓글 수정
function startEditComment(c) {
	editingCommentId.value = c.commentId
	editedContent.value = c.content
}
async function saveEditedComment() {
	if (!editedContent.value) return
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

// 인증 편집
function onEdit() {
	editing.value = true
	editedComment.value = cert.value.comment
}
function cancelEdit() {
	editing.value = false
}
async function saveEdit() {
	if (!editedComment.value) return
	await updateComment(props.certificationId, editedComment.value)
	cert.value.comment = editedComment.value
	editing.value = false
}
</script>
