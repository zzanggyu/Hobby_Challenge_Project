import axios from 'axios'
import router from './router'

const api = axios.create({
	baseURL: 'http://localhost:8080/api', // 백엔드 기본 URL
	withCredentials: true, // 쿠키 기반 인증 쓸 때
})

// 응답 시: 401 발생하면 로그인 페이지로
api.interceptors.response.use(
	(res) => res,
	(err) => {
		const status = err.response?.status
		if (status === 401 || status === 403) {
			// 사용자에게 알림 메시지
			alert('로그인해야 이용할 수 있습니다.')

			// 로그인 페이지로 이동, 원래 가려던 경로를 쿼리로 남기기
			const redirect = router.currentRoute.value.fullPath
			router.push({ name: 'login', query: { redirect } })
		}
		return Promise.reject(err)
	}
)

export default api
