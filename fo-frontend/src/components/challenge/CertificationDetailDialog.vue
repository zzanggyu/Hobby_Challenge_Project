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
			<div v-for="c in comments" :key="c.commentId" class="mb-2">
				<strong>{{ c.nickname }}:</strong>
				<template v-if="editingCommentId === c.commentId">
					<v-text-field v-model="editedContent" dense />
					<v-btn small color="primary" @click="saveEditedComment"
						>저장</v-btn
					>
				</template>
				<template v-else>
					<span>{{ c.content }}</span>
					<template v-if="c.nickname === auth.user.nickname">
						<v-btn icon small @click="startEditComment(c)">
							<v-icon>mdi-pencil</v-icon>
						</v-btn>
						<v-btn icon small @click="removeComment(c.commentId)">
							<v-icon>mdi-delete</v-icon>
						</v-btn>
					</template>
				</template>
			</div>

			<!-- 4) 댓글 입력 -->
			<v-text-field
				v-model="newComment"
				label="댓글 남기기"
				clearable
				dense
			/>
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
	$emit('deleted', props.certificationId)
	$emit('close')
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
