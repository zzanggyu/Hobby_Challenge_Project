<template>
	<v-container class="pa-6" fluid>
		<v-card class="mx-auto" max-width="800" elevation="4">
			<!-- 카드 헤더 -->
			<v-card-title class="headline grey--text text--darken-1">
				<v-icon class="mr-3" size="36" color="yellow">mdi-trophy</v-icon
				>챌린지 생성
			</v-card-title>
			<v-divider></v-divider>

			<!-- 카드 본문: 그리드 레이아웃 -->
			<v-card-text>
				<v-row dense>
					<!-- 제목 -->
					<v-col cols="12" md="6">
						<v-text-field
							v-model="title"
							:rules="rules.title"
							label="챌린지 제목"
							outlined
							dense
							required
						/>
					</v-col>

					<!-- 카테고리 -->
					<v-col cols="12" md="6">
						<v-select
							v-model="categoryId"
							:items="categories"
							:rules="rules.categoryId"
							item-title="categoryName"
							item-value="categoryId"
							label="카테고리"
							outlined
							dense
							required
						/>
					</v-col>

					<!-- 설명 -->
					<v-col cols="12">
						<!-- 설명 -->
						<v-textarea
							v-model="description"
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
					</v-col>

					<!-- 시작일시 -->
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
								/>
							</template>
							<v-date-picker
								v-model="startDate"
								:min="today"
								@update:model-value="startMenu = false"
							/>
						</v-menu>
					</v-col>

					<!-- 종료일시 -->
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
								/>
							</template>
							<v-date-picker
								v-model="endDate"
								:min="minEndDate"
								:rules="endDate"
								@update:model-value="endMenu = false"
							/>
						</v-menu>
					</v-col>
				</v-row>
			</v-card-text>

			<!-- 카드 액션: 버튼 우측 정렬 -->
			<v-divider></v-divider>
			<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn
					color="secondary"
					depressed
					:disabled="
						!title ||
						!description ||
						!categoryId ||
						!startDate ||
						!endDate
					"
					@click="onSubmit"
				>
					생성하기
				</v-btn>
			</v-card-actions>
		</v-card>
	</v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { createChallenge, getCategories } from '@/services/challengeService'

import { useRouter } from 'vue-router'
import axios from 'axios'
import { handleApiError } from '@/utils/apiError'

const router = useRouter()
const title = ref('')
const description = ref('')
const startDate = ref(null)
const endDate = ref(null)
const categoryId = ref(null)
const startMenu = ref(false)
const endMenu = ref(false)
const today = new Date().toISOString().substr(0, 10)

const categories = ref([])

// TODO : API로 가져오기 카테고리리
onMounted(async () => {
	try {
		const cats = await getCategories()
		categories.value = cats
	} catch (e) {
		console.error('카테고리 불러오기 실패', e)
	}
})

// YYYY-MM-DD 형식으로 포맷팅
const startDateFormatted = computed(() =>
	startDate.value
		? startDate.value.toLocaleDateString('sv-SE') // "2025-05-20"
		: ''
)
const endDateFormatted = computed(() =>
	endDate.value ? endDate.value.toLocaleDateString('sv-SE') : ''
)

// 시작일 + 7일 계산 (문자열 "YYYY-MM-DD")
const minEndDate = computed(() => {
	if (!startDate.value) return null
	const d = new Date(startDate.value)
	d.setDate(d.getDate() + 7)
	return d.toISOString().substr(0, 10)
})

// // 룰: 시작일이 없거나, endDate ≥ startDate+7일
// const endDateRule = (v) => {
// 	if (!v || !startDate.value) return true
// 	const start = new Date(startDate.value)
// 	const end = new Date(v)
// 	return (
// 		(end - start) / (1000 * 60 * 60 * 24) >= 7 ||
// 		'종료일은 시작일로부터 최소 일주일 이상 뒤여야 합니다.'
// 	)
// }

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

// 최종 제출출
async function onSubmit() {
	try {
		await createChallenge({
			title: title.value,
			description: description.value,
			startDate: startDate.value
				? startDate.value.toLocaleDateString('sv-SE')
				: null,
			endDate: endDate.value
				? endDate.value.toLocaleDateString('sv-SE')
				: null,
			categoryId: categoryId.value,
		})
		alert('챌린지가 성공적으로 생성되었습니다!')
		router.push({ name: 'my-challenges' })
	} catch (e) {
		if (axios.isAxiosError(e)) {
			const { status, data } = e.response || {}
			console.log('>>> createChallenge error response:', data)
			// 1) 사용자당 챌린지 1개 제한
			if (status === 400 && data.errorCode === 'CHALLENGE_LIMIT_EXCEEDED') {
				alert(data.message || '하나의 챌린지만 생성할 수 있습니다.')
			}
			// 2) DTO 유효성 (detail 필드)
			else if (status === 400 && data.detail) {
				const msgs = data.detail
					.replace(/[{}]/g, '')
					.split(', ')
					.map((s) => s.split('=').pop())
				alert(msgs.join('\n'))
			}
			// 3) 그 외 예상치 못한 400+
			else {
				alert(data.message || '챌린지 생성에 실패했습니다.')
			}
		} else {
			console.error(e)
			handleApiError(e)
		}
	}
}
</script>

<style scoped>
/* 카드 그림자나 색상을 테마에 맞춰 변경하고 싶으면 Vuetify 테마 설정으로! */
</style>
