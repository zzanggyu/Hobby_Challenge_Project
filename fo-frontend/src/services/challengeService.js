import api from '@/api' // src/api.js

// 챌린지 생성성
export const createChallenge = (payload) =>
	api.post('/challenges', payload).then((res) => res.data)

// 페이징된 챌린지 가져오기
export const getChallenges = (page = 1, size = 30, search, categoryId) =>
	api
		.get('/challenges', { params: { page, size, search, categoryId } })
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

// 내가 만든/참여한 챌린지 목록 조회 (백엔드에 이런 엔드포인트가 없다면, participation → challengeId 로 매핑해서 가져와야 합니다)
export const getMyChallenges = (userId) =>
	api.get(`/users/${userId}/challenges`).then((res) => res.data)

// 챌린지 상세 가져오기
export const getChallengeById = (id) =>
	api.get(`/challenges/${id}`).then((res) => res.data)

// 상세 조회 (참여 여부/즐겨찾기 여부 포함)
export const getChallengeDetail = (id) =>
	api.get(`/challenges/${id}`).then((res) => res.data)

// 카테고리 전체 목록
export const getCategories = () =>
	api.get('/categories').then((res) => res.data)

// 인기 챌린지 가져오기
export const getPopularChallenges = (size = 12) =>
	api.get('/challenges/popular', { params: { size } }).then((res) => res.data)

// 챌린지 수정
export const updateChallenge = (id, payload) =>
	api.put(`/challenges/${id}`, payload).then((res) => res.data)

// 챌린지 삭제
export const deleteChallenge = (id) =>
	api.delete(`/challenges/${id}`).then((res) => res.data)
