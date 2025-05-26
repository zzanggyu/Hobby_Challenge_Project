<template>
	<v-container class="pa-6" fluid>
		<v-card class="mx-auto" max-width="800" elevation="4">
			<h2 class="text-h6 font-weight-bold mb-6">챌린지 수정</h2>

			<v-form ref="form" v-model="valid" @submit.prevent="onSubmit">
				<!-- 제목 -->
				<v-text-field
					v-model="model.title"
					:rules="rules.title"
					label="제목"
					required
					clearable
					class="mb-4"
				/>

				<!-- 설명 -->
				<v-textarea
					v-model="model.description"
					:rules="rules.description"
					label="설명"
					rows="4"
					clearable
					auto-grow
					counter
					class="mb-4"
				/>

				<!-- 기간 -->
				<div class="d-flex flex-column flex-sm-row gap-4 mb-4">
					<v-date-input
						v-model="model.startDate"
						:rules="rules.date"
						label="시작일"
						prepend-icon="mdi-calendar-start"
						format="yyyy-MM-dd"
						required
					/>
					<v-date-input
						v-model="model.endDate"
						:rules="rules.date"
						label="종료일"
						prepend-icon="mdi-calendar-end"
						format="yyyy-MM-dd"
						required
					/>
				</div>

				<!-- 카테고리 -->
				<v-select
					v-model="model.categoryId"
					:items="categories"
					item-title="categoryName"
					item-value="categoryId"
					label="카테고리"
					:rules="rules.category"
					clearable
					placeholder="카테고리를 선택하세요"
					required
					class="mb-6"
				/>

				<!-- 액션 버튼 -->
				<div class="d-flex justify-end mt-6">
					<v-btn variant="outlined" class="mr-2" @click="router.back()"
						>취소</v-btn
					>
					<v-btn :loading="loading" color="primary" type="submit"
						>저장</v-btn
					>
				</div>
			</v-form>
		</v-card>
	</v-container>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
	getChallengeById,
	updateChallenge,
	getCategories,
} from '@/services/challengeService'

const route = useRoute()
const router = useRouter()
const id = Number(route.params.id)

// 원본 + 양방향 바인딩용 모델
const model = reactive({
	title: '',
	description: '',
	startDate: '',
	endDate: '',
	categoryId: null,
})

// 카테고리 목록
const categories = ref([])

// 유효성 및 로딩 상태
const valid = ref(false)
const loading = ref(false)
const form = ref(null)

// 검증 rules (Vuetify)
const rules = {
	title: [
		(v) => !!v || '제목은 필수입니다.',
		(v) => v?.length >= 2 || '2글자 이상',
	],
	description: [
		(v) => !!v || '설명은 필수입니다.',
		(v) => v?.length >= 10 || '10글자 이상',
	],
	date: [(v) => !!v || '필수'],
	category: [(v) => !!v || '카테고리 선택'],
}

// 초기 데이터 로딩
onMounted(async () => {
	try {
		const [detail, cats] = await Promise.all([
			getChallengeById(id),
			getCategories(),
		])

		categories.value = cats
		// 그런 뒤 model에 id를 넣는다
		Object.assign(model, {
			title: detail.title,
			description: detail.description,
			startDate: detail.startDate?.slice(0, 10),
			endDate: detail.endDate?.slice(0, 10),
			categoryId: detail.categoryId, // ← id만 보관(숫자 3)
		})
	} catch (e) {
		alert('챌린지 정보를 가져오지 못했습니다.')
		router.back()
	}
})

// 제출
async function onSubmit() {
	const { valid } = await form.value.validate() // 구조 분해
	if (!valid) return
	loading.value = true
	try {
		await updateChallenge(id, { ...model })
		alert('수정이 완료되었습니다.')
		router.push({ name: 'challenge-overview', params: { id } })
	} catch (e) {
		console.error(e)
		alert('저장에 실패했습니다.')
	} finally {
		loading.value = false
	}
}
</script>

<style scoped>
.gap-4 {
	gap: 1rem;
}
</style>
