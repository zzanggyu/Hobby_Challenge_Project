<template>
	<!-- ▣ 슬라이더 ▣ -->
	<v-slide-group v-model="active" mandatory center-active class="pa-6">
		<!-- ▸ 왼쪽 화살표 슬롯 -->
		<template #prev>
			<v-btn icon variant="text" @click="prevSlide">
				<v-icon>mdi-chevron-left</v-icon>
			</v-btn>
		</template>

		<!-- ▸ 오른쪽 화살표 슬롯 -->
		<template #next>
			<v-btn icon variant="text" @click="nextSlide">
				<v-icon>mdi-chevron-right</v-icon>
			</v-btn>
		</template>

		<!-- ▸ 슬라이드 카드 -->
		<v-slide-group-item
			v-for="item in limited"
			:key="item.id"
			v-slot="{ isSelected }"
		>
			<v-card
				class="challenge-card mx-3"
				:elevation="isSelected ? 6 : 2"
				width="320"
			>
				<!-- (1) 썸네일 카드 -->
				<template v-if="item.thumbnail">
					<v-responsive aspect-ratio="3/4">
						<v-img :src="item.thumbnail" cover />
					</v-responsive>
				</template>

				<!-- (2) 컬러 카드 -->
				<template v-else>
					<div
						class="color-card d-flex flex-column justify-space-between pa-10 text-white"
						:style="{ background: item.bg || '#7e5bef' }"
					>
						<div>
							<h2 class="text-h5 font-weight-bold mb-2">
								{{ item.title }}
							</h2>
							<p class="text-body-2">{{ item.desc }}</p>
						</div>
						<v-btn color="white" variant="outlined" size="small"
							>자세히 보기</v-btn
						>
					</div>
				</template>
			</v-card>
		</v-slide-group-item>
	</v-slide-group>

	<!-- ●●● 인디케이터 점 -->
	<div class="text-center mt-4">
		<v-btn
			v-for="(dot, i) in limited.length"
			:key="i"
			size="8"
			icon
			class="mx-1"
			:color="active === i ? 'primary' : 'grey'"
			@click="active = i"
		/>

		<!--  자동재생 토글 -->
		<v-btn icon color="primary" class="ml-4" @click="togglePlay">
			<v-icon>{{ playing ? 'mdi-pause' : 'mdi-play' }}</v-icon>
		</v-btn>
	</div>
</template>

<script setup>
/* ────────────────────────────────────────────────
     1)  props 정의
     ──────────────────────────────────────────────── */
const props = defineProps({
	/* 모든 챌린지 배열 (필수) */
	challenges: { type: Array, required: true },

	/* 보여줄 최대 카드 수 */
	maxItems: { type: Number, default: 20 },

	/* 자동 재생 간격(ms) */
	interval: { type: Number, default: 4000 },

	/* 컴포넌트 마운트 시 자동 재생 시작 여부 */
	autoPlay: { type: Boolean, default: true },
})

/* ────────────────────────────────────────────────
     2)  리액티브 상태
     ──────────────────────────────────────────────── */
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

/* 상위 maxItems 개로 제한한 배열 */
const limited = computed(() => props.challenges.slice(0, props.maxItems))

/* 현재 활성 인덱스(슬라이드 위치) */
const active = ref(0)

/* 자동 재생 중인지 여부 (▶ / ❚❚ 토글용) */
const playing = ref(props.autoPlay)

/* setInterval 핸들 저장용 */
let timer = null

/* ────────────────────────────────────────────────
     3)  슬라이드 이동 함수 (한 장씩)
     ──────────────────────────────────────────────── */
function nextSlide() {
	active.value = (active.value + 1) % limited.value.length
}
function prevSlide() {
	active.value =
		(active.value - 1 + limited.value.length) % limited.value.length
}

/* ────────────────────────────────────────────────
     4)  자동 재생 컨트롤
     ──────────────────────────────────────────────── */
function startAuto() {
	stopAuto() // 기존 타이머 제거
	timer = setInterval(nextSlide, props.interval) // 새로 시작
}
function stopAuto() {
	clearInterval(timer)
	timer = null
}

/* ▶ / ❚❚  버튼 토글 */
function togglePlay() {
	playing.value ? stopAuto() : startAuto()
	playing.value = !playing.value
}

/* ────────────────────────────────────────────────
     5)  라이프사이클 훅
     ──────────────────────────────────────────────── */
onMounted(() => {
	if (props.autoPlay) startAuto()
})
onBeforeUnmount(stopAuto)
</script>

<style scoped>
.challenge-card {
	border-radius: 30px;
	overflow: hidden;
}
.color-card {
	height: calc(320px * 4 / 3);
} /* 3:4 비율 */

.v-btn--size-8 {
	--v-btn-height: 8px;
	--v-btn-size: 8px;
} /* 인디케이터 */
</style>
