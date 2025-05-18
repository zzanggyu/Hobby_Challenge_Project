import api from '@/api'

// 회원가입
export function signup(dto) {
	// POST /auth/signup
	return api.post('/auth/signup', dto)
}

// 이메일 인증 코드 전송
export function sendCode(email) {
	return api.post('/auth/sendCode', null, { params: { email } })
}

// 인증 코드 확인
export function verifyCode(email, code) {
	return api.post('/auth/verifyCode', null, { params: { email, code } })
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
	return api.get('/auth/me')
}
