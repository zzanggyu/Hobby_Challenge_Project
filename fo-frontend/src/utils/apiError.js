import axios from 'axios'
import router from '@/router'

/**
 * 공통 API 에러 핸들러
 * @param {unknown} err - catch로 받은 에러 객체
 */
export function handleApiError(err) {
	if (axios.isAxiosError(err)) {
		const status = err.response?.status
		const message = err.response?.data?.message

		if ([401, 403].includes(status)) {
			alert('로그인 후 이용 가능합니다.')
			const redirect = router.currentRoute.value.fullPath
			router.push({ name: 'login', query: { redirect } })
		} else {
			alert(message || '요청 중 오류가 발생했습니다.')
		}
	} else {
		console.error('Non-Axios error:', err)
		alert('알 수 없는 오류가 발생했습니다.')
	}
}
