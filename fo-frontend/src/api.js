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
		if (err.response?.status === 401) {
			router.push('/login')
		}
		return Promise.reject(err)
	}
)

export default api
