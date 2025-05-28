import api from '@/api'

// 챌린지 참여 요청청
export const joinChallenge = (challengeId) =>
	api.post(`/challenges/${challengeId}/participations`).then((res) => res.data)

// 승인된 참여자
export const getApprovedParticipants = (challengeId) =>
	api.get(`/challenges/${challengeId}/participants`).then((res) => res.data)

// 참여 요청 목록
export const getRequests = (challengeId) =>
	api.get(`/challenges/${challengeId}/participations`).then((res) => res.data)

// 참여 상태 변경 (승인/거절)
export const changeStatus = (participationId, status) =>
	api
		.patch(`/participations/${participationId}/status`, null, {
			params: { status },
		})
		.then((res) => res.data)

// 내 참여내역 조회
export const getMyParticipations = (userId) =>
	api.get(`/users/${userId}/participations`).then((res) => res.data)

// 요청 취소/탈퇴
export const cancelParticipation = (challengeId, participationId) =>
	api
		.delete(`/challenges/${challengeId}/participations/${participationId}`)
		.then((res) => res.data)
