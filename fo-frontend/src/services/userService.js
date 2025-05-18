import api from '@/api'

// 내 프로필 조회
export function fetchMyProfile() {
	return api.get('/user/me')
}

// 특정 사용자 조회 (ID)
export function fetchUserById(userId) {
	return api.get(`/user/${userId}`)
}

// 로그인 아이디 중복 검사
export function checkLoginId(loginId) {
	return api.get(`/user/login/${loginId}`)
}
