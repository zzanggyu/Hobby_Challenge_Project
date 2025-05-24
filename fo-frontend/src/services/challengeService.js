import api from '@/api' // src/api.js

export const createChallenge = (payload) =>
	api.post('/challenges', payload).then((res) => res.data)

export const getChallenges = (page = 1, size = 30, search, categoryId) =>
	api
		.get('/challenges', { params: { page, size, search, categoryId } })
		.then((res) => res.data)

export const joinChallenge = (userId, challengeId) =>
	api
		.post(`/challenges/${challengeId}/participations`, { userId })
		.then((res) => res.data)

// 챌린지 하트 누르면 관심 챌린지에 저장 또 누르면 제거
export async function toggleFavoriteChallenge(challengeId) {
	await api.post(`/challenges/favorite/${challengeId}`)
}

// 내가 저장한 관심 챌린지 리스트 조회
export async function getFavoriteChallenges() {
	const { data } = await api.get('/challenges/favorite')
	return data
}

// “내 참여내역” 조회 추가
export const getMyParticipations = (userId) =>
	api.get(`/users/${userId}/participations`).then((res) => res.data)
