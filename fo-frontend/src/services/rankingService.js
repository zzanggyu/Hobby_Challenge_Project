import api from '@/api'

// 사용자별 인증 수 랭킹 조회
export function fetchUserRankings(limit = 10) {
	return api
		.get('/rankings/users', {
			params: { limit },
		})
		.then((res) => res.data)
}
