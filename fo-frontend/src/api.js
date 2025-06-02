import axios from 'axios'
import router from './router'
import { logout } from '@/services/authService'

const api = axios.create({
	baseURL: 'http://localhost:8081/api', // 백엔드 기본 URL
	withCredentials: true, // 쿠키 기반 인증 쓸 때
})

// 응답 시: 401 발생하면 로그인 페이지로
api.interceptors.response.use(
	(res) => res,
	async (err) => {
		const { response, config } = err
		const data = response?.data

		// 401 Unauthorized일 때, 다시 시도 플래그가 없으면
		if (response?.status === 401 && !config._retry) {
			// 엑세스 토큰 만료
			config._retry = true
			try {
				// 1. 리프레시 토큰으로 새 액세스 토큰 발급
				await api.post('/auth/refresh')
				// 2. 원래 요청을 재시도
				return api(config)
			} catch (refreshErr) {
				// 리프레시도 실패하면 조용히 처리 (에러 로그 제거)
				console.log('Refresh failed, redirecting to login') // 단순 로그로 변경
				// logout() // 일시적으로 주석 처리
				const redirect = router.currentRoute.value.fullPath
				// router.push({ name: 'login', query: { redirect } }) // 일시적으로 주석 처리
			}
		}
		return Promise.reject(err)
	}
)

export default api
