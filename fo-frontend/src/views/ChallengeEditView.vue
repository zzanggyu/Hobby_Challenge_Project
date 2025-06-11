<template>
	<v-container class="pa-6" fluid>
		<v-card class="mx-auto" max-width="800" elevation="4">
			<!-- 카드 헤더 -->
			<v-card-title class="headline grey--text text--darken-1">
				<v-icon class="mr-3" size="36" color="orange">mdi-pencil</v-icon>
				챌린지 수정
			</v-card-title>
			<v-divider></v-divider>
			<v-card-text>
				<v-form ref="form" v-model="valid" @submit.prevent="onSubmit">
					<v-row dense>
						<!-- 제목 -->
						<v-col cols="12" md="6">
							<v-text-field
								v-model="model.title"
								:rules="rules.title"
								label="챌린지 제목"
								outlined
								dense
								required
								clearable
							/>
						</v-col>

						<!-- 카테고리 -->
						<v-col cols="12" md="6">
							<v-select
								v-model="model.categoryId"
								:items="categories"
								:rules="rules.categoryId"
								item-title="categoryName"
								item-value="categoryId"
								label="카테고리"
								outlined
								dense
								required
								clearable
								placeholder="카테고리를 선택하세요"
							/>
						</v-col>

						<!-- 설명 -->
						<v-col cols="12">
							<v-textarea
								v-model="model.description"
								:rules="rules.description"
								label="설명"
								clearable
								min-rows="10"
								max-rows="10"
								rows="10"
								counter="500"
								class="mb-4"
							/>
						</v-col>

						<!-- 시작일 -->
						<v-col cols="12" md="6">
							<v-menu
								v-model="startMenu"
								:close-on-content-click="false"
								transition="scale-transition"
								offset-y
							>
								<template #activator="{ props }">
									<v-text-field
										v-model="startDateFormatted"
										:rules="rules.startDate"
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
						</v-col>

						<!-- 종료일 -->
						<v-col cols="12" md="6">
							<v-menu
								v-model="endMenu"
								:close-on-content-click="false"
								transition="scale-transition"
								offset-y
							>
								<template #activator="{ props }">
									<v-text-field
										v-model="endDateFormatted"
										:rules="rules.endDate"
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
						</v-col>
					</v-row>
				</v-form>
			</v-card-text>

			<!-- 카드 액션: 버튼 우측 정렬 -->
			<v-divider></v-divider>
			<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn variant="outlined" class="mr-2" @click="router.back()">
					취소
				</v-btn>
				<v-btn
					color="primary"
					:loading="loading"
					:disabled="
						!model.title ||
						!model.description ||
						!model.categoryId ||
						!model.startDate ||
						!model.endDate
					"
					@click="onSubmit"
				>
					수정하기
				</v-btn>
			</v-card-actions>
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
			!model.startDate ||
			!v ||
			(new Date(v) - new Date(model.startDate)) / (1000 * 60 * 60 * 24) >=
				6 ||
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
