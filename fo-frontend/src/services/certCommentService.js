import api from '@/api'

// 댓글 목록 조회
export function fetchComments(certificationId) {
	return api
		.get(`/certifications/${certificationId}/comments`)
		.then((res) => res.data)
}

// 댓글 등록
export function addComment(certificationId, content) {
	return api
		.post(`/certifications/${certificationId}/comments`, { content })
		.then((res) => res.data)
}

// 댓글 수정
export function updateComment(certificationId, commentId, content) {
	return api
		.put(`/certifications/${certificationId}/comments/${commentId}`, {
			content,
		})
		.then((res) => res.data)
}

// 댓글 삭제
export function deleteComment(certificationId, commentId) {
	return api
		.delete(`/certifications/${certificationId}/comments/${commentId}`)
		.then((res) => res.data)
}
