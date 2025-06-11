<template>
	<v-container class="py-10">
		<!-- 헤더 -->
		<div class="text-center mb-8">
			<h2 class="text-h4 font-weight-bold mb-2">🏆 인증왕 랭킹</h2>
			<p class="text-body-1 text-grey">
				가장 많은 인증을 올린 사용자들을 확인해보세요!
			</p>
		</div>

		<!-- 로딩 상태 -->
		<div v-if="loading" class="text-center py-8">
			<v-progress-circular indeterminate color="primary" size="64" />
			<p class="mt-4">랭킹을 불러오는 중...</p>
		</div>

		<!-- 랭킹 리스트 -->
		<v-row v-else-if="rankings.length" justify="center">
			<v-col
				cols="12"
				sm="6"
				md="4"
				v-for="(user, index) in rankings"
				:key="user.userId"
				class="d-flex"
			>
				<v-card
					:elevation="index < 3 ? 12 : 6"
					class="rank-card flex-grow-1"
					:class="getRankCardClass(index)"
				>
					<!-- 순위 배지 -->
					<div class="rank-badge">
						<v-chip
							:color="getRankColor(index)"
							size="small"
							class="font-weight-bold"
						>
							{{ index + 1 }}위
						</v-chip>
					</div>

					<v-card-text class="pa-6 text-center">
						<!-- 프로필 이미지 / 아바타 -->
						<div class="mb-4">
							<v-avatar
								:size="index < 3 ? 80 : 64"
								:class="
									index < 3 ? 'rank-avatar-special' : 'rank-avatar'
								"
							>
								<v-img
									v-if="user.imageUrl"
									:src="user.imageUrl"
									alt="프로필"
								/>
								<v-icon
									v-else
									:size="index < 3 ? 40 : 32"
									:color="index < 3 ? 'white' : 'grey'"
								>
									mdi-account
								</v-icon>
							</v-avatar>

							<!-- 왕관 (1위만) -->
							<v-icon
								v-if="index === 0"
								class="crown-icon"
								size="24"
								color="amber"
							>
								mdi-crown
							</v-icon>
						</div>

						<!-- 사용자 정보 -->
						<h3 class="text-h6 font-weight-bold mb-2">
							{{ user.nickname }}
						</h3>

						<!-- 레벨 표시 -->
						<v-chip
							size="small"
							:color="getLevelColor(user.level)"
							variant="outlined"
							class="mb-3"
						>
							<v-icon left size="16">mdi-star</v-icon>
							Lv.{{ user.level }}
						</v-chip>

						<!-- 인증 수 강조 -->
						<div class="cert-count mb-2">
							<v-icon
								size="24"
								class="mr-2"
								:color="getRankColor(index)"
							>
								mdi-camera
							</v-icon>
							<span
								class="font-weight-bold"
								:class="index < 3 ? 'text-h4' : 'text-h5'"
								:style="{ color: getRankColorHex(index) }"
							>
								{{ user.certificationCount }}
							</span>
							<span class="text-body-2 ml-1">개</span>
						</div>

						<!-- 포인트  -->
						<div v-if="user.points" class="text-caption text-grey">
							{{ user.points?.toLocaleString() }}P
						</div>

						<!-- 순위 아이콘 -->
						<div class="mt-3">
							<v-icon
								:size="index < 3 ? 32 : 24"
								:color="getRankColor(index)"
							>
								{{ getRankIcon(index) }}
							</v-icon>
						</div>
					</v-card-text>
				</v-card>
			</v-col>
		</v-row>

		<!-- 데이터 없을 때 -->
		<div v-else class="text-center py-10">
			<v-icon size="80" color="grey-lighten-2" class="mb-4">
				mdi-account-star-outline
			</v-icon>
			<h3 class="text-h5 mb-2">아직 랭킹 데이터가 없습니다</h3>
			<p class="text-body-1 text-grey">
				첫 번째 인증을 등록하고 랭킹에 도전해보세요!
			</p>
		</div>
	</v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchUserRankings } from '@/services/rankingService'

// 상태 관리
const rankings = ref([])
const loading = ref(true)

// 순위별 색상
function getRankColor(index) {
	if (index === 0) return 'amber' // 1위: 금색
	if (index === 1) return 'grey' // 2위: 은색
	if (index === 2) return '#CD7F32' // 3위: 동색
	return 'blue' // 4위 이하: 파란색
}

// 순위별 색상 (HEX)
function getRankColorHex(index) {
	if (index === 0) return '#FFC107' // 1위: 금색
	if (index === 1) return '#9E9E9E' // 2위: 은색
	if (index === 2) return '#CD7F32' // 3위: 동색
	return '#2196F3' // 4위 이하: 파란색
}

// 순위별 아이콘
function getRankIcon(index) {
	if (index === 0) return 'mdi-trophy' // 1위: 트로피
	if (index === 1) return 'mdi-medal' // 2위: 메달
	if (index === 2) return 'mdi-medal' // 3위: 메달
	return 'mdi-star' // 4위 이하: 별
}

// 순위별 카드 클래스
function getRankCardClass(index) {
	if (index === 0) return 'rank-first'
	if (index === 1) return 'rank-second'
	if (index === 2) return 'rank-third'
	return 'rank-normal'
}

// 레벨별 색상
function getLevelColor(level = 1) {
	if (level >= 40) return 'black'
	if (level >= 30) return 'purple'
	if (level >= 25) return 'deepblue'
	if (level >= 20) return 'blue'
	if (level >= 15) return 'green'
	if (level >= 10) return 'yellow'
	if (level >= 5) return 'orange'
	if (level >= 2) return 'red'
	return 'grey'
}
// 데이터 로드
async function loadRankings() {
	loading.value = true
	try {
		rankings.value = await fetchUserRankings(9) // 상위 9명만 표시
	} catch (error) {
		rankings.value = []
	} finally {
		loading.value = false
	}
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
	loadRankings()
})
</script>

<style scoped>
.rank-card {
	transition: all 0.3s ease;
	position: relative;
	height: 100%;
}

.rank-card:hover {
	transform: translateY(-8px) scale(1.02);
}

/* 순위 배지 */
.rank-badge {
	position: absolute;
	top: 12px;
	right: 12px;
	z-index: 2;
}

/* 왕관 아이콘 (1위) */
.crown-icon {
	position: absolute;
	top: -8px;
	left: 50%;
	transform: translateX(-50%);
	z-index: 3;
}

/* 아바타 스타일 */
.rank-avatar-special {
	border: 4px solid #ffc107;
	box-shadow: 0 4px 12px rgba(255, 193, 7, 0.4);
}

.rank-avatar {
	border: 2px solid #e0e0e0;
}

/* 순위별 특별 스타일 */
.rank-first {
	border: 3px solid #ffc107;
	background: linear-gradient(135deg, #fff8e1 0%, #ffffff 100%);
	box-shadow: 0 12px 32px rgba(255, 193, 7, 0.3) !important;
}

.rank-second {
	border: 2px solid #9e9e9e;
	background: linear-gradient(135deg, #f5f5f5 0%, #ffffff 100%);
	box-shadow: 0 8px 24px rgba(158, 158, 158, 0.2) !important;
}

.rank-third {
	border: 2px solid #ff9800;
	background: linear-gradient(135deg, #fff3e0 0%, #ffffff 100%);
	box-shadow: 0 8px 24px rgba(255, 152, 0, 0.2) !important;
}

.rank-normal {
	border: 1px solid #e0e0e0;
}

/* 인증 수 강조 */
.cert-count {
	display: flex;
	align-items: center;
	justify-content: center;
}

/* 애니메이션 */
.rank-card {
	animation: fadeInUp 0.6s ease-out;
}

.rank-card:nth-child(1) {
	animation-delay: 0.1s;
}
.rank-card:nth-child(2) {
	animation-delay: 0.2s;
}
.rank-card:nth-child(3) {
	animation-delay: 0.3s;
}

@keyframes fadeInUp {
	from {
		opacity: 0;
		transform: translateY(30px);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

/* 반응형 */
@media (max-width: 600px) {
	.rank-card {
		margin-bottom: 16px;
	}
}
</style>
