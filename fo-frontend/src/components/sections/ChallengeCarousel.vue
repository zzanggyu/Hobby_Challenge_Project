<template>
	<!-- ▣ 슬라이더 ▣ -->
	<!-- 슬라이드로 보여줄 챌린지가 1개 이상일 때만 슬라이더 렌더링 -->
	<div v-if="limited.length > 0">
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
			<!-- ▸ 슬라이드 카드 반복 출력 -->
			<v-slide-group-item
				v-for="item in limited"
				:key="item.challengeId"
				v-slot="{ isSelected }"
			>
				<v-card
					class="challenge-card mx-3 bg-primary"
					style="color: white; border-radius: 30px"
					:elevation="isSelected ? 6 : 2"
					width="320"
				>
					<!-- (1) 썸네일 이미지가 있을 때 이미지 카드 -->
					<template v-if="item.thumbnail">
						<v-responsive aspect-ratio="3/4">
							<v-img :src="item.thumbnail" cover />
						</v-responsive>
					</template>
					<!-- (2) 썸네일이 없으면 컬러 배경 카드 -->
					<template v-else>
						<div
							class="color-card d-flex flex-column justify-space-between pa-10 text-white"
							:style="{ background: item.bg || '#7e5bef' }"
						>
							<!-- 카테고리명 칩 -->
							<div class="d-flex align-center mb-2">
								<v-chip
									size="small"
									color="white"
									text-color="primary"
									class="font-weight-bold elevation-2 px-3 py-1"
									style="border-radius: 12px"
								>
									{{ getCategoryName(item.categoryId) }}
								</v-chip>
							</div>
							<div>
								<h2 class="text-h5 font-weight-bold mb-2">
									{{ item.title }}
								</h2>
								<p class="text-body-2">
									{{ item.description }}
								</p>
							</div>
							<v-btn color="white" variant="outlined" size="small">
								자세히 보기
							</v-btn>
						</div>
					</template>
				</v-card>
			</v-slide-group-item>
		</v-slide-group>
		<!-- ●●● 인디케이터 점 + 자동재생 토글 -->
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
			<v-btn icon color="primary" class="ml-4" @click="togglePlay">
				<v-icon>{{ playing ? 'mdi-pause' : 'mdi-play' }}</v-icon>
			</v-btn>
		</div>
	</div>

	<!-- 데이터 없을 때 안내 메시지 (슬라이드 배열이 비었을 때) -->
	<div v-else class="py-8 text-center text-grey">
		<v-icon size="64" color="grey lighten-2">mdi-emoticon-sad</v-icon>
		<p class="text-h6 grey--text mt-4">아직 등록된 인기 챌린지가 없습니다.</p>
	</div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
	challenges: { type: Array, required: true }, // 슬라이드로 보여줄 챌린지 배열
	maxItems: { type: Number, default: 20 }, // 최대 보여줄 카드 개수
	interval: { type: Number, default: 4000 }, // 자동 슬라이드 간격(ms)
	autoPlay: { type: Boolean, default: true }, // 자동재생 여부
	categories: { type: Array, required: true },
})

// 카테고리명 반환 함수 (카테고리 데이터 구조에 맞게 수정)
function getCategoryName(categoryId) {
	const found = props.categories.find((c) => c.categoryId === categoryId)
	return found ? found.categoryName : '기타'
}

// limited: 상위 maxItems개로 잘라서 실제 슬라이드로 보여줌
const limited = computed(() => props.challenges.slice(0, props.maxItems))
const active = ref(0) // 현재 활성 인덱스(슬라이드 위치)
const playing = ref(props.autoPlay) // 자동재생 on/off
let timer = null // setInterval 핸들 저장

// ▶ 슬라이드 한 장씩 이동 함수
function nextSlide() {
	active.value = (active.value + 1) % limited.value.length
}
function prevSlide() {
	active.value =
		(active.value - 1 + limited.value.length) % limited.value.length
}

// ▶ 자동 재생 컨트롤
function startAuto() {
	stopAuto()
	timer = setInterval(nextSlide, props.interval)
}
function stopAuto() {
	clearInterval(timer)
	timer = null
}
function togglePlay() {
	playing.value ? stopAuto() : startAuto()
	playing.value = !playing.value
}

// ▶ 라이프사이클(마운트/언마운트)에서 자동재생 관리
onMounted(() => {
	if (props.autoPlay) startAuto()
})
onBeforeUnmount(stopAuto)

// ⚠️ 절대 여기서 API 호출, 데이터 관리 하지 말 것!
//    (오직 props로 받은 데이터만 화면에 보여주기)
</script>

<style scoped>
.challenge-card {
	border-radius: 30px;
	overflow: hidden;
}
.color-card {
	height: calc(320px * 4 / 3);
}
.v-btn--size-8 {
	--v-btn-height: 8px;
	--v-btn-size: 8px;
}
</style>
