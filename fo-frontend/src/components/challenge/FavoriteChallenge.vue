<template>
	<v-container>
		<v-row class="mb-4" align="center">
			<v-col cols="12">
				<h2 class="text-h5">관심 챌린지</h2>
			</v-col>
		</v-row>

		<v-row align="stretch">
			<template v-for="fav in favorites" :key="fav.challengeId">
				<v-col v-if="fav.challenge" cols="12" md="6" lg="4" class="d-flex">
					<v-card
						elevation="4"
						height="310"
						width="100%"
						rounded="xl"
						:style="{
							border: '1px solid rgba(165, 243, 212, 0.6)',
							backgroundColor: '#f9fdfc',
						}"
					>
						<v-card-title class="px-4">
							<div
								class="d-flex w-100 justify-space-between align-center"
							>
								<span class="text-h6">{{ fav.challenge.title }}</span>
								<v-icon
									color="red"
									@click="onToggleFavorite(fav.challenge.challengeId)"
									style="cursor: pointer"
									>mdi-heart</v-icon
								>
							</div>
						</v-card-title>

						<v-divider color="green lighten-2" />

						<v-card-text class="flex-grow-1">
							{{ fav.challenge.description }}
						</v-card-text>

						<v-card-subtitle>
							기간: {{ formatDate(fav.challenge.startDate) }} ~
							{{ formatDate(fav.challenge.endDate) }}
						</v-card-subtitle>

						<v-card-actions class="mt-auto">
							<v-chip small outlined color="green-accent-4">
								{{ categoryName(fav.challenge.categoryId) }}
							</v-chip>
							<small class="grey--text text--darken-1">
								by: {{ fav.challenge.creatorNickname }}
							</small>
							<v-spacer />
							<v-btn
								small
								color="primary"
								@click="onJoin(fav.challenge.challengeId)"
							>
								참여하기
							</v-btn>
						</v-card-actions>
					</v-card>
				</v-col>
			</template>
		</v-row>
	</v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import {
	getFavoriteChallenges,
	toggleFavoriteChallenge,
} from '@/services/challengeService'
import { getCategories } from '@/services/categoryService'

const router = useRouter()
const favorites = ref([])
const categories = ref([])

// 내 관심 챌린지 목록 가져오기
async function fetchFavorites() {
	try {
		const data = await getFavoriteChallenges()
		favorites.value = data
	} catch (e) {
		console.error('관심챌린지 로드 실패', e)
		if (
			axios.isAxiosError(e) &&
			(e.response.status === 401 || e.response.status === 403)
		) {
			alert('로그인 후 이용 가능합니다.')
			router.push({ name: 'login' })
		}
	}
}

// 즐겨찾기 토글(취소)
async function onToggleFavorite(challengeId) {
	try {
		await toggleFavoriteChallenge(challengeId)
		favorites.value = favorites.value.filter(
			(f) => f.challenge.challengeId !== challengeId
		)
	} catch (e) {
		console.error('관심챌린지 취소 실패', e)
		alert('취소 중 오류가 발생했습니다.')
	}
}

// 참여하기 버튼
async function onJoin(challengeId) {
	try {
		await axios.post(`/api/challenges/${challengeId}/join`)
		alert('참여 요청 완료!')
	} catch (e) {
		console.error('참여 요청 실패', e)
		alert('참여 중 오류 발생')
	}
}

// 카테고리 이름 매핑
async function loadCategories() {
	try {
		categories.value = await getCategories()
	} catch (e) {
		console.error('카테고리 로드 실패', e)
	}
}

function categoryName(id) {
	const cat = categories.value.find((x) => x.categoryId === id)
	return cat ? cat.name : '알 수 없음'
}

// 날짜 포맷
function formatDate(d) {
	return d ? new Date(d).toLocaleDateString() : '-'
}

// 마운트 시 초기 로드
onMounted(async () => {
	await Promise.all([fetchFavorites(), loadCategories()])
})
</script>

<style scoped></style>
