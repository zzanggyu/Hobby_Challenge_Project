import api from '@/api'

export function fetchRankings(period) {
	return api
		.get('/rankings', { params: { type: period, metric: 'count' } })
		.then((res) => res.data)
}
