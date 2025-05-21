import api from '@/api'

// 카테고리 전체 목록
export const getCategories = () =>
	api.get(`/categories`).then((res) => res.data)

export const getCategory = (categoryId) =>
	api.get(`/categories/${categoryId}`).then((res) => res.data)
