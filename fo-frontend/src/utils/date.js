/**
 * 날짜 문자열(ISO 등)을 로컬 날짜 표기로 변환합니다.
 * @param {string|Date} d
 * @returns {string}
 */
export function formatDate(d) {
	if (!d) return '-'
	const date = d instanceof Date ? d : new Date(d)
	return date.toLocaleDateString()
}
