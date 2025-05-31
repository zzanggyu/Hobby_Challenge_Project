import api from '@/api'

// 회원가입
export function signup(dto) {
	// POST /auth/signup
	return api.post('/auth/signup', dto)
}

// 로그인
export function login({ loginId, password }) {
	return api.post('/auth/login', { loginId, password })
}

// 로그아웃
export function logout() {
	return api.post('/auth/logout')
}

// 내 정보 조회
export function me() {
	return api.get('/auth/me').then((res) => res.data)
}

/* 회원가입용 이메일 인증 */
// 코드 발송
export function sendSignupCode(email) {
	return api.post('/auth/signup/send-code', null, { params: { email } })
}
// 코드 검증
export function verifySignupCode(email, code) {
	return api.post('/auth/signup/verify-code', null, {
		params: { email, code },
	})
}

/* 아이디 찾기 */
export function findId(username, email) {
	return api.post('/auth/find-id', { username, email })
}

// 리프레시 토큰
export function refresh() {
	return api.post('/auth/refresh')
}

/* 비밀번호 재설정용 이메일 인증 */
// 1) 코드 발송
export function sendPasswordResetCode(loginId, email) {
	return api.post('/auth/password/send-code', { loginId, email })
}
// 2) 코드 검증
export function verifyPasswordCode(email, code) {
	return api.post('/auth/password/verify-code', { email, code })
}
// 3) 새 비밀번호 저장
export function resetPassword(loginId, newPassword) {
	return api.post('/auth/password/reset', { loginId, newPassword })
}
