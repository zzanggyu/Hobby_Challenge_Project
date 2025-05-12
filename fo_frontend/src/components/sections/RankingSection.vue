<!-- src/components/sections/RankingSection.vue -->
<template>
	<v-container class="py-10">
		<!-- 헤더 + 기간 선택 -->
		<div class="d-flex justify-space-between align-center mb-4">
			<h2 class="text-h4 font-weight-bold">
				{{ titleMap[period] }} 인기 랭킹 🔥
			</h2>
			<v-select
				v-model="period"
				:items="periodOptions"
				item-title="label"
				item-value="value"
				hide-details
				density="compact"
				style="max-width: 120px"
			/>
		</div>

		<!-- 랭킹 리스트 -->
		<v-row v-if="rankings.length">
			<v-col cols="12" md="4" v-for="(c, i) in rankings" :key="c.id">
				<v-card :elevation="i < 3 ? 6 : 2" class="pa-4 rank-card">
					<div class="d-flex align-center">
						<v-avatar size="48" class="mr-4">
							<v-img v-if="c.thumbnail" :src="c.thumbnail" />
							<span v-else class="text-h5 font-weight-bold">{{
								i + 1
							}}</span>
						</v-avatar>
						<div>
							<h3 class="text-subtitle-1 font-weight-bold mb-1">
								#{{ i + 1 }} {{ c.title }}
							</h3>
							<p class="text-body-2">
								{{ c.participation_count }}명 참여
							</p>
						</div>
					</div>
				</v-card>
			</v-col>
		</v-row>

		<!-- 데이터 없을 때 안내 -->
		<div v-else class="text-center pa-10 text-body-1">
			아직 {{ titleMap[period] }} 랭킹 데이터가 없습니다.
		</div>
	</v-container>
</template>

<script setup lang="ts">
import { ref, watchEffect } from 'vue'

interface RankingItem {
	id: number
	title: string
	participation_count: number
	thumbnail?: string
}

type Period = 'weekly' | 'all'

// 주간 / 전체 옵션
const periodOptions = [
	{ label: '주간', value: 'weekly' as Period },
	{ label: '전체', value: 'all' as Period },
]

// 헤더용 맵핑
const titleMap: Record<Period, string> = {
	weekly: '주간',
	all: '전체',
}

const period = ref<Period>('weekly')
const rankings = ref<RankingItem[]>([])

watchEffect(async () => {
	try {
		// fetch 를 사용해서 주간/전체 랭킹 가져오기
		const res = await fetch(`/rankings?type=${period.value}&metric=count`)
		if (!res.ok) throw new Error(`HTTP ${res.status}`)
		rankings.value = await res.json()
	} catch (e) {
		console.error('랭킹 로드 실패', e)
		rankings.value = []
	}
})
</script>

<style scoped>
.rank-card {
	transition: transform 0.2s;
}
.rank-card:hover {
	transform: translateY(-4px);
}
</style>
