import api from '@/api'

// 내 정보 조회
export function getMyInfo() {
	return api.get('/users/me').then((res) => res.data)
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
