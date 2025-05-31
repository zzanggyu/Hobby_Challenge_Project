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
					clearable
					h
					min-rows="10"
					max-rows="10"
					rows="10"
					counter="500"
					class="mb-4"
				/>

				<!-- 기간 -->
				<div class="d-flex flex-column flex-sm-row gap-4 mb-4">
					<v-menu
						v-model="startMenu"
						:close-on-content-click="false"
						transition="scale-transition"
						offset-y
					>
						<template #activator="{ props }">
							<v-text-field
								v-model="startDateFormatted"
								:rules="rules.date"
								label="시작일"
								readonly
								v-bind="props"
								outlined
								dense
								required
							/>
						</template>
						<v-date-picker
							v-model="model.startDate"
							:min="today"
							@update:model-value="startMenu = false"
						/>
					</v-menu>
					<v-menu
						v-model="endMenu"
						:close-on-content-click="false"
						transition="scale-transition"
						offset-y
					>
						<template #activator="{ props }">
							<v-text-field
								v-model="endDateFormatted"
								:rules="rules.date"
								label="종료일 (최소 일주일 뒤)"
								readonly
								v-bind="props"
								outlined
								dense
								required
							/>
						</template>
						<v-date-picker
							v-model="model.endDate"
							:min="minEndDate"
							@update:model-value="endMenu = false"
						/>
					</v-menu>
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
					<v-btn
						:loading="loading"
						type="submit"
						:disabled="
							!model.title ||
							!model.description ||
							!model.startDate ||
							!model.endDate ||
							!model.categoryId
						"
						color="primary"
					>
						저장
					</v-btn>
				</div>
			</v-form>
		</v-card>
	</v-container>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
	getChallengeById,
	updateChallenge,
	getCategories,
} from '@/services/challengeService'
import axios from 'axios'
import { handleApiError } from '@/utils/apiError'

const route = useRoute()
const router = useRouter()
const id = Number(route.params.id)

// 카테고리 목록
const categories = ref([])

// 유효성 및 로딩 상태
const valid = ref(false)
const loading = ref(false)
const form = ref(null)

// v-menu 열림/닫힘 제어용
const startMenu = ref(false)
const endMenu = ref(false)

// 원본 + 양방향 바인딩용 모델
const model = reactive({
	title: '',
	description: '',
	startDate: '',
	endDate: '',
	categoryId: null,
})

const today = new Date().toISOString().substr(0, 10)

// YYYY-MM-DD 텍스트 포맷
const startDateFormatted = computed(() =>
	model.startDate ? new Date(model.startDate).toLocaleDateString('sv-SE') : ''
)
const endDateFormatted = computed(() =>
	model.endDate ? new Date(model.endDate).toLocaleDateString('sv-SE') : ''
)

// 시작일 + 7일 = 최소 종료일
const minEndDate = computed(() => {
	if (!model.startDate) return null
	const d = new Date(model.startDate)
	d.setDate(d.getDate() + 7)
	return d.toISOString().substr(0, 10)
})

// 유효성 검사
const rules = {
	title: [
		(v) => !!v || '제목을 입력하세요.',
		(v) => v.length >= 2 || '2글자 이상 입력하세요.',
		(v) => v.length <= 50 || '50자 이내여야 합니다.',
	],
	description: [
		(v) => !!v || '설명을 입력하세요.',
		(v) => v.length >= 10 || '10자 이상 입력하세요.',
		(v) => v.length <= 500 || '500자 이내여야 합니다.',
	],
	categoryId: [(v) => !!v || '카테고리를 선택하세요.'],
	startDate: [(v) => !!v || '시작일을 선택하세요.'],
	endDate: [
		(v) => !!v || '종료일을 선택하세요.',
		(v) =>
			!startDate.value ||
			!v ||
			(new Date(v) - new Date(startDate.value)) / (1000 * 60 * 60 * 24) >=
				7 ||
			'종료일은 시작일로부터 최소 일주일 이상 뒤여야 합니다.',
	],
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
	const { valid: isValid } = await form.value.validate()
	if (!isValid) return
	loading.value = true
	try {
		await updateChallenge(id, { ...model })
		alert('수정이 완료되었습니다.')
		router.push({ name: 'challenge-overview', params: { id } })
	} catch (e) {
		if (axios.isAxiosError(e)) {
			const { status, data } = e.response || {}
			// 400 validation or business error
			if (status === 400) {
				alert(data.message || '수정에 실패했습니다.')
				return
			}
			handleApiError(e)
		} else {
			console.error(e)
			handleApiError(e)
		}
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
