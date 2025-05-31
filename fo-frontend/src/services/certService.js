import api from '@/api'

// 챌린지별 인증 내역 조회
export const getCertifications = (challengeId) =>
	api.get(`/challenges/${challengeId}/certifications`).then((res) => res.data)

// 인증 등록 - FormData로 직접 전송
export function submitCertification(challengeId, formData) {
	return api.post(`/challenges/${challengeId}/certifications`, formData, {
		headers: {
			'Content-Type': 'multipart/form-data',
		},
	})
}

// 인증 수정도 FormData로
export function updateCertification(challengeId, certificationId, formData) {
	return api
		.put(
			`/challenges/${challengeId}/certifications/${certificationId}`,
			formData,
			{
				headers: {
					'Content-Type': 'multipart/form-data',
				},
			}
		)
		.then((res) => res.data)
}

// 인증 삭제
export function deleteCertification(challengeId, certificationId) {
	return api.delete(
		`/challenges/${challengeId}/certifications/${certificationId}`
	)
}

// 인증 상세 가져오기
export function fetchCertDetail(challengeId, certificationId) {
	return api
		.get(`/challenges/${challengeId}/certifications/${certificationId}`)
		.then((r) => r.data)
}

// 좋아요 토글
export function likeCert(challengeId, certificationId) {
	return api
		.post(`/challenges/${challengeId}/certifications/${certificationId}/like`)
		.then((r) => r.data)
}
