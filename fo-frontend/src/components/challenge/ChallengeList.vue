<template>
	<v-container>
		<!-- 1. 검색 & 카테고리 필터 바 -->
		<v-row class="mb-4" align="center">
			<v-col cols="12" md="6">
				<v-text-field
					v-model="search"
					label="챌린지 제목 검색"
					clearable
					@keyup.enter="fetchChallenges"
				/>
			</v-col>
			<v-col cols="12" md="4">
				<v-select
					v-model="selectedCategory"
					:items="categories"
					item-title="name"
					item-value="id"
					label="카테고리 필터"
					clearable
				/>
			</v-col>
		</v-row>

		<!-- 2. 챌린지 카드 리스트 -->
		<v-row align="stretch">
			<v-col
				v-for="c in filteredChallenges"
				:key="c.challengeId"
				cols="12"
				md="6"
				lg="4"
				class="d-flex"
			>
				<v-card
					elevation="4"
					height="310"
					width="100%"
					rounded="xl"
					tile="false"
					variant="outlined"
					class="d-flex flex-column"
					outlined
					:style="{
						border: '1px solid rgba(165, 243, 212, 0.6)',
						backgroundColor: '#f9fdfc',
					}"
				>
					<!-- 카드 상단: 제목/설명 -->
					<v-card-title class="px-4">
						<div class="d-flex w-100 justify-space-between align-center">
							<span class="text-h6">{{ c.title }}</span>

							<v-icon
								:color="c.isFavorite ? 'red' : 'grey'"
								@click="onToggleFavorite(c)"
								class="mr-2"
								style="cursor: pointer"
							>
								{{ c.isFavorite ? 'mdi-heart' : 'mdi-heart-outline' }}
							</v-icon>
						</div>
					</v-card-title>

					<v-divider color="green lighten-2" />

					<v-card-text class="flex-grow-1">
						{{ c.description }}
					</v-card-text>

					<!-- 카드 하단: 날짜 / 카테고리 / 버튼 -->
					<v-card-subtitle>
						기간: {{ formatDate(c.startDate) }} ~
						{{ formatDate(c.endDate) }}
					</v-card-subtitle>

					<v-card-actions class="mt-auto">
						<!-- 카테고리  -->
						<v-chip small outlined color="green-accent-4">
							{{ categoryName(c.categoryId) }}
						</v-chip>
						<small class="grey--text text--darken-1"
							>by: {{ c.creatorNickname }}</small
						>
						<v-spacer />
						<!-- 참여하기 버튼 -->
						<v-btn small color="primary" @click="onJoin(c.challengeId)">
							참여하기
						</v-btn>
					</v-card-actions>
				</v-card>
			</v-col>
		</v-row>
	</v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import {
	getChallenges,
	joinChallenge,
	toggleFavoriteChallenge,
} from '@/services/challengeService'
import { getCategories } from '@/services/categoryService'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
// 챌린지 목록을 저장할 반응형 변수
const challenges = ref([])
const categories = ref([]) // 카테고리 목록
// 검색어 와 선택된 카테고리 를 위한 반응형 변수
const search = ref('')
const selectedCategory = ref(null)

// API 호출
async function fetchChallenges() {
	try {
		const data = await getChallenges()
		challenges.value = data.map((c) => ({
			...c,
			isFavorite: c.isFavorite ?? false,
		}))
	} catch (err) {
		if (
			axios.isAxiosError(err) &&
			(err.response.status === 401 || err.response.status === 403)
		) {
			alert('로그인해야 이용할 수 있습니다.')
			router.push({
				name: 'login',
				query: { redirect: router.currentRoute.value.fullPath },
			})
		} else {
			console.error(err)
		}
	}
}

// 카테고리 가져오기
async function fetchCategories() {
	try {
		categories.value = await getCategories()
	} catch (err) {
		console.error('카테고리 로드 실패', err)
	}
}

// 관심 챌린지 등록 하트 누르기
async function onToggleFavorite(challenge) {
	try {
		await toggleFavoriteChallenge(challenge.challengeId)
		challenge.isFavorite = !challenge.isFavorite
	} catch (err) {
		console.error(err)
		alert('관심챌린지 등록 중 오류가 발생했습니다.')
	}
}

// 검색 + 카테고리 필터 적용
const filteredChallenges = computed(() => {
	return challenges.value.filter((c) => {
		// 검색어 (챌린지 제목) 매칭
		const matchesText =
			c.title.includes(search.value) || c.description.includes(search.value)
		// 카테고리 선택 없으면 모두
		const matchesCategory =
			!selectedCategory.value || c.categoryId === selectedCategory.value
		return matchesText && matchesCategory
	})
})

// 카테고리 ID → 이름 매핑
function categoryName(id) {
	const cat = categories.value.find((x) => x.id === id)
	return cat ? cat.name : '알 수 없음'
}

// 참여하기 버튼 클릭 핸들러
async function onJoin(challengeId) {
	try {
		await joinChallenge(challengeId)
		alert('챌린지 참여 요청이 완료되었습니다!')
	} catch (err) {
		console.error(err)
		alert('참여 요청 중 오류가 발생했습니다.')
	}
}

// 날짜 포맷터
function formatDate(date) {
	return date ? new Date(date).toLocaleDateString() : '-'
}

// 컴포넌트 마운트 시 초기 로드
onMounted(async () => {
	await Promise.all([fetchCategories(), fetchChallenges()])
})
</script>

<style scoped></style>
