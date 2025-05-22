import api from '@/api' // src/api.js

export const createChallenge = (payload) =>
	api.post('/challenges', payload).then((res) => res.data)

export const getChallenges = () =>
	api.get('/challenges').then((res) => res.data)

export const joinChallenge = (challengeId) =>
	api.post(`/challenges/${challengeId}/join`).then((res) => res.data)

// 챌린지 하트 누르면 관심 챌린지에 저장 또 누르면 제거
export async function toggleFavoriteChallenge(challengeId) {
	await api.post(`/challenges/favorite/${challengeId}`)
}

// 내가 저장한 관심 챌린지 리스트 조회
export async function getFavoriteChallenges() {
	const { data } = await api.get('/challenges/favorite')
	return data
}
