import api from '@/api'

// 챌린지별 인증 내역 조회
export const getCertifications = (challengeId) =>
	api.get(`/challenges/${challengeId}/certifications`).then((res) => res.data)

// 인증 등록 (이미지 파일 직접 보내지 않고, imageKey+comment만 JSON으로!)
export function submitCertification(challengeId, payload) {
	// payload: { imageKey, comment }
	return api.post(`/challenges/${challengeId}/certifications`, payload)
}

// 서버에서 S3 업로드용 프리사인드 URL 발급 받기
export function getPresignedUrl(challengeId, filename, contentType) {
	return api
		.get(`/challenges/${challengeId}/certifications/presign`, {
			params: { filename, contentType },
		})
		.then((res) => res.data)
}

// 인증 삭제
export function deleteCertification(challengeId, certificationId) {
	return api.delete(
		`/challenges/${challengeId}/certifications/${certificationId}`
	)
}

// 인증 수정
// 인증 본문문 수정
export function updateCertification(challengeId, certificationId, comment) {
	return api
		.put(`/challenges/${challengeId}/certifications/${certificationId}`, {
			comment,
		})
		.then((res) => res.data)
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
