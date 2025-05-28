import api from '@/api'

// 챌린지별 인증 내역 조회
export const getCertifications = (challengeId) =>
	api.get(`/challenges/${challengeId}/certifications`).then((res) => res.data)

// 인증 등록 (FormData 로 comment, file 전송)
export const submitCertification = (challengeId, formData) =>
	api
		.post(`/challenges/${challengeId}/certifications`, formData)
		.then((res) => res.data)

// 인증 삭제
export function deleteCertification(challengeId, certificationId) {
	return api.delete(
		`/challenges/${challengeId}/certifications/${certificationId}`
	)
}

// 인증 수정

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
