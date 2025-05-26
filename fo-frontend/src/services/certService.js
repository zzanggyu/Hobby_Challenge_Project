import api from '@/api'

// 챌린지별 인증 내역 조회
export const getCertifications = (challengeId) =>
	api.get(`/challenges/${challengeId}/certifications`).then((res) => res.data)

// 인증 등록 (FormData 로 comment, file 전송)
// export const submitCertification = (challengeId, formData) =>
// 	api
// 		.post(`/challenges/${challengeId}/certifications`, formData)
// 		.then((res) => res.data)

export const submitCertification = (challengeId, formData) =>
	api
		.post(`/challenges/${challengeId}/certifications`, formData, {
			headers: { 'Content-Type': 'multipart/form-data' },
		})
		.then((res) => res.data) // 에러는 catch 안 하고 상위로 던짐
