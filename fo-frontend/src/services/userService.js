import api from '@/api'

// 내 정보 조회
export function getMyInfo() {
	return api.get('/users/me').then((res) => res.data)
}

// 특정 사용자의 특정 챌린지 인증 개수 조회
export const getMyCertificationCount = async (userId, challengeId) => {
	return await api.get(
		`/users/${userId}/challenges/${challengeId}/certifications/count`
	)
}

// 사용자 통계 조회
export const getUserStatistics = async (userId) => {
	try {
		return await api.get(`/users/${userId}/statistics`)
	} catch (error) {
		// 임시로 기본값 반환
		return {
			data: {
				totalChallenges: 0,
				activeChallenges: 0,
				completedChallenges: 0,
				totalCertifications: 0,
			},
		}
	}
}

// 참여내역
export const getMyParticipations = async (userId) => {
	return api.get(`/users/${userId}/participations`).then((res) => res.data)
}

// 닉네임 변경
export function updateNickname(nickname) {
	return api.patch('/users/me/nickname', { nickname }).then((res) => res.data)
}

// 비밀번호 변경
export function changePassword(data) {
	return api.put('/users/me/password', data)
}

// 회원 탈퇴
export function deleteAccount(password) {
	return api.delete('/users/me', { data: { password } })
}
