<!-- MyPageView.vue - 비밀번호 변경 UI 개선 -->
<template>
	<v-container class="pa-6">
		<v-row justify="center">
			<v-col cols="12" md="8" lg="6">
				<!-- 프로필 카드 -->
				<v-card class="mb-6">
					<v-card-title class="text-h5 font-weight-bold">
						내 프로필
					</v-card-title>

					<v-card-text>
						<!-- 프로필 정보 -->
						<v-list>
							<!-- 이름 (변경 불가) -->
							<v-list-item>
								<template v-slot:prepend>
									<v-icon>mdi-account</v-icon>
								</template>
								<v-list-item-title>이름</v-list-item-title>
								<v-list-item-subtitle>{{
									userInfo.username
								}}</v-list-item-subtitle>
							</v-list-item>
							<v-divider class="my-4" />

							<!-- 닉네임 (변경 가능) -->
							<v-list-item>
								<template v-slot:prepend>
									<v-icon>mdi-card-account-details</v-icon>
								</template>
								<v-list-item-title>닉네임</v-list-item-title>
								<v-list-item-subtitle v-if="!editingNickname">
									{{ userInfo.nickname }}
								</v-list-item-subtitle>
								<v-text-field
									v-else
									v-model="newNickname"
									:rules="nicknameRules"
									density="compact"
									hide-details="auto"
								/>
								<template v-slot:append>
									<v-btn
										v-if="!editingNickname"
										icon
										size="small"
										@click="startEditNickname"
									>
										<v-icon>mdi-pencil</v-icon>
									</v-btn>
									<div v-else>
										<v-btn
											icon
											size="small"
											color="primary"
											:disabled="
												!hasNicknameChanged || savingNickname
											"
											@click="saveNickname"
											:loading="savingNickname"
										>
											<v-icon>mdi-check</v-icon>
										</v-btn>
										<v-btn
											icon
											size="small"
											@click="cancelEditNickname"
										>
											<v-icon>mdi-close</v-icon>
										</v-btn>
									</div>
								</template>
							</v-list-item>
							<v-list-item
								v-if="
									editingNickname &&
									!hasNicknameChanged &&
									newNickname.trim()
								"
							>
								<v-alert type="info" variant="tonal" density="compact">
									현재 닉네임과 동일합니다. 변경사항이 없어요.
								</v-alert>
							</v-list-item>
							<v-divider class="my-4" />

							<!-- 아이디 (변경 불가) -->
							<v-list-item>
								<template v-slot:prepend>
									<v-icon>mdi-identifier</v-icon>
								</template>
								<v-list-item-title>아이디</v-list-item-title>
								<v-list-item-subtitle>{{
									userInfo.loginId
								}}</v-list-item-subtitle>
							</v-list-item>
							<v-divider class="my-4" />

							<!-- 이메일 (표시만) -->
							<v-list-item>
								<template v-slot:prepend>
									<v-icon>mdi-email</v-icon>
								</template>
								<v-list-item-title>이메일</v-list-item-title>
								<v-list-item-subtitle>{{
									userInfo.email
								}}</v-list-item-subtitle>
							</v-list-item>
							<v-divider class="my-4" />

							<!-- 레벨 및 포인트 -->
							<v-list-item>
								<template v-slot:prepend>
									<v-icon>mdi-trophy</v-icon>
								</template>
								<v-list-item-title>레벨 및 경험치</v-list-item-title>
								<v-list-item-subtitle>
									<v-chip
										small
										:color="getLevelColor(userInfo.level)"
										class="mr-2"
									>
										Lv.{{ userInfo.level }}
									</v-chip>
									<v-chip small :color="getLevelColor(userInfo.level)">
										{{ userInfo.points }} EXP
									</v-chip>
								</v-list-item-subtitle>
							</v-list-item>
						</v-list>
					</v-card-text>
				</v-card>
				<v-card class="mb-6" elevation="3">
					<v-card-title class="text-h5 font-weight-bold text-primary">
						<v-icon class="mr-2" color="primary"
							>mdi-trophy-variant</v-icon
						>
						내 챌린지 활동
					</v-card-title>

					<v-card-text>
						<!-- 통계 카드들 -->
						<v-row class="mb-4">
							<v-col cols="6" md="3">
								<v-card variant="outlined" class="text-center pa-3">
									<div class="text-h4 font-weight-bold text-primary">
										{{ challengeStats.totalChallenges }}
									</div>
									<div class="text-caption text-grey-darken-1">
										참여 챌린지
									</div>
								</v-card>
							</v-col>
							<v-col cols="6" md="3">
								<v-card variant="outlined" class="text-center pa-3">
									<div class="text-h4 font-weight-bold text-success">
										{{ challengeStats.activeChallenges }}
									</div>
									<div class="text-caption text-grey-darken-1">
										진행중
									</div>
								</v-card>
							</v-col>
							<v-col cols="6" md="3">
								<v-card variant="outlined" class="text-center pa-3">
									<div class="text-h4 font-weight-bold text-info">
										{{ challengeStats.completedChallenges }}
									</div>
									<div class="text-caption text-grey-darken-1">
										완료
									</div>
								</v-card>
							</v-col>
							<v-col cols="6" md="3">
								<v-card variant="outlined" class="text-center pa-3">
									<div class="text-h4 font-weight-bold text-orange">
										{{ challengeStats.totalCertifications }}
									</div>
									<div class="text-caption text-grey-darken-1">
										총 인증
									</div>
								</v-card>
							</v-col>
						</v-row>

						<!-- 버튼들 -->
						<v-row>
							<v-col cols="12">
								<v-btn
									block
									variant="outlined"
									color="primary"
									prepend-icon="mdi-format-list-bulleted"
									@click="showMyChallenges"
									:loading="loadingChallenges"
								>
									내 참여 챌린지 보기
								</v-btn>
							</v-col>
						</v-row>
					</v-card-text>
				</v-card>
				<!-- 비밀번호 변경 카드 - UI 개선 -->
				<v-card class="mb-6">
					<v-card-title>
						<div class="d-flex align-center">
							<span class="text-h6">비밀번호 변경</span>
						</div>
					</v-card-title>

					<v-card-text>
						<v-form ref="passwordForm" v-model="passwordValid">
							<!-- 현재 비밀번호 - 눈 모양 아이콘 추가 -->
							<v-text-field
								v-model="passwordData.currentPassword"
								:rules="currentPasswordRules"
								:type="showCurrentPassword ? 'text' : 'password'"
								:append-inner-icon="
									showCurrentPassword ? 'mdi-eye-off' : 'mdi-eye'
								"
								@click:append-inner="
									showCurrentPassword = !showCurrentPassword
								"
								label="현재 비밀번호"
								prepend-inner-icon="mdi-lock"
								variant="outlined"
								class="mb-4"
								placeholder="현재 사용 중인 비밀번호를 입력하세요"
							/>

							<!-- 새 비밀번호 - 눈 모양 아이콘 추가 -->
							<v-text-field
								v-model="passwordData.newPassword"
								:rules="passwordRules"
								:type="showNewPassword ? 'text' : 'password'"
								:append-inner-icon="
									showNewPassword ? 'mdi-eye-off' : 'mdi-eye'
								"
								@click:append-inner="showNewPassword = !showNewPassword"
								label="새 비밀번호"
								prepend-inner-icon="mdi-lock-plus"
								variant="outlined"
								class="mb-4"
								placeholder="영문, 숫자, 특수문자 포함 8~20자"
								hint="영문, 숫자, 특수문자를 각각 최소 1자 이상 포함해야 합니다"
								persistent-hint
							/>

							<!-- 새 비밀번호 확인 - 눈 모양 아이콘 추가 -->
							<v-text-field
								v-model="passwordData.confirmPassword"
								:rules="confirmPasswordRules"
								:type="showConfirmPassword ? 'text' : 'password'"
								:append-inner-icon="
									showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'
								"
								@click:append-inner="
									showConfirmPassword = !showConfirmPassword
								"
								label="새 비밀번호 확인"
								prepend-inner-icon="mdi-lock-check"
								variant="outlined"
								placeholder="새 비밀번호를 다시 입력하세요"
							/>
						</v-form>
					</v-card-text>

					<v-card-actions class="px-6 pb-6">
						<v-spacer />
						<v-btn
							color="secondary"
							variant="outlined"
							@click="resetPasswordForm"
							:disabled="changingPassword"
							class="mr-2"
						>
							초기화
						</v-btn>
						<v-btn
							color="primary"
							:disabled="!passwordValid"
							:loading="changingPassword"
							@click="changePassword"
							size="large"
						>
							<v-icon left size="18">mdi-check</v-icon>
							변경하기
						</v-btn>
					</v-card-actions>
				</v-card>

				<!-- 회원 탈퇴 카드 -->
				<v-card>
					<v-card-title class="text-h6 error--text">
						<div class="d-flex align-center">회원 탈퇴</div>
					</v-card-title>

					<v-card-text>
						<v-alert type="warning" variant="tonal" class="mb-4">
							<div class="d-flex align-center">
								<v-icon class="mr-2">mdi-alert-triangle</v-icon>
								<div>
									<strong>주의사항</strong><br />
									회원 탈퇴 시 모든 데이터가 삭제되며 복구할 수
									없습니다.<br />
									• 작성한 챌린지 및 인증 내역<br />
									• 참여 내역 및 포인트<br />
									• 개인정보 및 계정 정보
								</div>
							</div>
						</v-alert>

						<v-btn
							color="error"
							variant="outlined"
							@click="withdrawDialog = true"
							size="large"
						>
							<v-icon left>mdi-account-minus</v-icon>
							회원 탈퇴
						</v-btn>
					</v-card-text>
				</v-card>
			</v-col>
		</v-row>

		<!-- 회원 탈퇴 확인 -->
		<v-dialog v-model="withdrawDialog" max-width="450" persistent>
			<v-card>
				<v-card-title class="text-h6 error--text d-flex align-center">
					<v-icon class="mr-2" color="error">mdi-alert-circle</v-icon>
					정말 탈퇴하시겠습니까?
				</v-card-title>

				<v-card-text class="pb-0">
					<v-alert type="error" variant="tonal" class="mb-4">
						<div class="d-flex align-center">
							<!-- <v-icon class="mr-2">mdi-database-remove</v-icon> -->
							<div>
								<strong>경고!</strong><br />
								탈퇴 후에는 데이터를 복구할 수 없습니다.
							</div>
						</div>
					</v-alert>

					<!-- 탈퇴 확인 비밀번호 - 눈 모양 아이콘 추가 -->
					<v-text-field
						v-model="withdrawPassword"
						:type="showWithdrawPassword ? 'text' : 'password'"
						:append-inner-icon="
							showWithdrawPassword ? 'mdi-eye-off' : 'mdi-eye'
						"
						@click:append-inner="
							showWithdrawPassword = !showWithdrawPassword
						"
						label="비밀번호 확인"
						prepend-inner-icon="mdi-lock"
						variant="outlined"
						placeholder="현재 비밀번호를 입력하세요"
						:rules="[(v) => !!v || '비밀번호를 입력하세요.']"
						class="mb-2"
					/>
				</v-card-text>

				<v-card-actions class="px-6 pb-6">
					<v-spacer />
					<v-btn
						variant="outlined"
						@click="withdrawDialog = false"
						:disabled="withdrawing"
					>
						<v-icon left size="18">mdi-close</v-icon>
						취소
					</v-btn>
					<v-btn
						color="error"
						:loading="withdrawing"
						:disabled="!withdrawPassword"
						@click="deleteAccount"
						class="ml-2"
					>
						<v-icon left size="18">mdi-account-remove</v-icon>
						탈퇴하기
					</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
	</v-container>

	<MyChallengesDialog
		v-model="challengesDialog"
		:challenges="myChallenges"
		:loading="loadingChallenges"
		@challenge-selected="onChallengeSelected"
	/>

	<MyCertificationsDialog
		v-model="certificationsDialog"
		:challenge="selectedChallenge"
		:certifications="selectedCertifications"
		:loading="certificationsLoading"
	/>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
	getMyInfo,
	updateNickname,
	changePassword as apiChangePassword,
	deleteAccount as apiDeleteAccount,
	getMyCertificationCount,
	getUserStatistics,
	getMyParticipations,
} from '@/services/userService'
import { getCertifications } from '@/services/certService'
// 컴포넌트 임포트
import MyChallengesDialog from '@/components/mypage/MyChallengesDialog.vue'
import MyCertificationsDialog from '@/components/mypage/MyCertificationsDialog.vue'

const router = useRouter()
const auth = useAuthStore()

// 다이얼로그 관련 상태
const challengesDialog = ref(false)
const certificationsDialog = ref(false)
const selectedChallenge = ref(null)
const selectedCertifications = ref([])
const certificationsLoading = ref(false)

// 상태 관리
const userInfo = ref({})
const editingNickname = ref(false)
const newNickname = ref('')
const originalNickname = ref('')
const savingNickname = ref(false)
const passwordValid = ref(false)
const changingPassword = ref(false)
const withdrawDialog = ref(false)
const withdrawPassword = ref('')
const withdrawing = ref(false)

// 비밀번호 보이기/숨기기 상태 추가
const showCurrentPassword = ref(false) // 현재 비밀번호
const showNewPassword = ref(false) // 새 비밀번호
const showConfirmPassword = ref(false) // 새 비밀번호 확인
const showWithdrawPassword = ref(false) // 탈퇴 확인 비밀번호

// 챌린지 관련 상태
const challengeStats = ref({
	totalChallenges: 0,
	activeChallenges: 0,
	completedChallenges: 0,
	totalCertifications: 0,
})
const myChallenges = ref([])
const loadingStats = ref(false)
const loadingChallenges = ref(false)

// 폼 참조
const passwordForm = ref()

// 비밀번호 데이터
const passwordData = ref({
	currentPassword: '',
	newPassword: '',
	confirmPassword: '',
})

// 개별 오류 메시지
const passwordErrors = ref({
	currentPassword: '',
	newPassword: '',
	confirmPassword: '',
})

// 닉네임 변경 여부 체크
const hasNicknameChanged = computed(() => {
	return (
		newNickname.value.trim() !== '' &&
		newNickname.value.trim() !== originalNickname.value
	)
})

// 유효성 검사 규칙
const nicknameRules = [
	(v) => !!v || '닉네임을 입력하세요.',
	(v) =>
		(v && v.length >= 2 && v.length <= 10) || '닉네임은 2~10자여야 합니다.',
	(v) =>
		/^[가-힣a-zA-Z0-9_]+$/.test(v) ||
		'한글, 영문, 숫자, 언더스코어만 사용 가능합니다.',
]

// 현재 비밀번호호
const currentPasswordRules = [
	(v) => !!v || '현재 비밀번호를 입력하세요.',
	(v) =>
		/^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,20}$/.test(v) ||
		'영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.',
	(v) =>
		/^[A-Za-z0-9!@#$%^&*]{8,20}$/.test(v) ||
		'비밀번호: 영문, 숫자, 특수문자만 사용 가능합니다. (8~20자)',
]

const passwordRules = [
	(v) => !!v || '새 비밀번호를 입력하세요.',
	(v) =>
		/^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,20}$/.test(v) ||
		'영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.',
	(v) =>
		/^[A-Za-z0-9!@#$%^&*]{8,20}$/.test(v) ||
		'비밀번호: 영문, 숫자, 특수문자만 사용 가능합니다. (8~20자)',
	(v) =>
		v !== passwordData.value.currentPassword ||
		'새 비밀번호는 현재 비밀번호와 달라야 합니다.',
]

// 비밀번호 확인
const confirmPasswordRules = [
	(v) => !!v || '비밀번호 확인을 입력하세요.',
	(v) =>
		v === passwordData.value.newPassword || '비밀번호가 일치하지 않습니다.',
]

// 내 정보 조회
async function loadMyInfo() {
	try {
		userInfo.value = await getMyInfo()
		originalNickname.value = userInfo.value.nickname

		// 사용자 정보 로딩 완료 후 통계 로드
		if (userInfo.value.userId) {
			await loadChallengeStats()
		}
	} catch (error) {
		alert('사용자 정보를 불러오는데 실패했습니다.')
	}
}

// 닉네임 수정 시작
function startEditNickname() {
	newNickname.value = userInfo.value.nickname
	editingNickname.value = true
}

// 닉네임 수정 취소
function cancelEditNickname() {
	editingNickname.value = false
	newNickname.value = ''
}

// 닉네임 저장
async function saveNickname() {
	//  변경사항이 없으면 요청 차단
	if (!hasNicknameChanged.value) {
		alert('변경사항이 없습니다.')
		return
	}

	//  빈 값 체크
	if (!newNickname.value.trim()) {
		alert('닉네임을 입력해주세요.')
		return
	}

	//  동일한 값인지 재확인
	if (newNickname.value.trim() === originalNickname.value) {
		alert('현재 닉네임과 동일합니다.')
		return
	}
	// 유효성 검사
	const valid = nicknameRules.every((rule) => rule(newNickname.value) === true)
	if (!valid) return

	savingNickname.value = true
	try {
		const updated = await updateNickname(newNickname.value)
		userInfo.value = updated
		originalNickname.value = updated.nickname
		editingNickname.value = false

		// 스토어 업데이트
		auth.user.nickname = updated.nickname
		alert('닉네임이 성공적으로 변경되었습니다!')
	} catch (error) {
		alert(error.response?.data?.message || '닉네임 변경에 실패했습니다.')
	} finally {
		savingNickname.value = false
	}
}

//  비밀번호 폼 초기화 함수
function resetPasswordForm() {
	passwordData.value = {
		currentPassword: '',
		newPassword: '',
		confirmPassword: '',
	}
	// 비밀번호 보이기 상태도 초기화
	showCurrentPassword.value = false
	showNewPassword.value = false
	showConfirmPassword.value = false

	if (passwordForm.value) {
		passwordForm.value.reset()
	}
}

// 비밀번호 변경
async function changePassword() {
	const valid = await passwordForm.value.validate()
	if (!valid) return

	changingPassword.value = true
	try {
		await apiChangePassword({
			currentPassword: passwordData.value.currentPassword,
			newPassword: passwordData.value.newPassword,
		})

		alert('비밀번호가 성공적으로 변경되었습니다!')

		//  폼 초기화 함수 사용
		resetPasswordForm()
	} catch (error) {
		alert(error.response?.data?.message || '비밀번호 변경에 실패했습니다.')
	} finally {
		changingPassword.value = false
	}
}

// 회원 탈퇴
async function deleteAccount() {
	if (!withdrawPassword.value) return

	withdrawing.value = true
	try {
		//확인
		if (confirm('정말 탈퇴하시겠습니까??') == true) {
			await apiDeleteAccount(withdrawPassword.value)
			alert('회원 탈퇴가 완료되었습니다.')
		} else {
			return false //취소
		}

		// 로그아웃 처리
		await auth.logout()
		router.push('/')
	} catch (error) {
		alert(error.response?.data?.message || '회원 탈퇴에 실패했습니다.')
	} finally {
		withdrawing.value = false
		withdrawDialog.value = false
		// 탈퇴 실패 시 비밀번호 필드 초기화
		withdrawPassword.value = ''
		showWithdrawPassword.value = false
	}
}

// 레벨별 색상
function getLevelColor(level) {
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

//  챌린지 선택 핸들러
async function onChallengeSelected(challenge) {
	certificationsLoading.value = true
	selectedChallenge.value = challenge

	try {
		const result = await getCertifications(
			challenge.challengeId, // challengeId
			1, // page (첫 페이지)
			100, // size (충분히 많이)
			true // onlyMine (내 인증만)
		)

		// PageResponseDTO 구조에 따라 데이터 추출
		selectedCertifications.value = result.items || []

		// 날짜순 정렬 (최신순)
		selectedCertifications.value.sort(
			(a, b) => new Date(b.createdDate) - new Date(a.createdDate)
		)

		// 챌린지 목록 다이얼로그 닫고 인증 목록 다이얼로그 열기
		challengesDialog.value = false
		certificationsDialog.value = true
	} catch (error) {
		alert(
			'인증 목록을 불러오는데 실패했습니다: ' +
				(error.message || '알 수 없는 오류')
		)
	} finally {
		certificationsLoading.value = false
	}
}

// 챌린지 통계 로드
async function loadChallengeStats() {
	if (!userInfo.value.userId) return

	loadingStats.value = true
	try {
		const participations = await getMyParticipations(userInfo.value.userId)
		const approvedParticipations = participations.filter(
			(p) => p.status === 'APPROVED' || p.role === 'OWNER'
		)

		if (approvedParticipations.length === 0) {
			challengeStats.value = {
				totalChallenges: 0,
				activeChallenges: 0,
				completedChallenges: 0,
				totalCertifications: 0,
			}

			return
		}

		const now = new Date()

		// 🆕 디버깅 로그 추가
		const promises = approvedParticipations.map(
			async (participation, index) => {
				try {
					const challengeEndDate = participation.endDate
						? new Date(participation.endDate)
						: new Date('9999-12-31')

					const isActive = challengeEndDate >= now.setHours(0, 0, 0, 0)

					const certResponse = await getMyCertificationCount(
						userInfo.value.userId,
						participation.challengeId
					)
					const certResult = certResponse.data

					const finalCount = certResult.certificationCount || 0

					return {
						challengeId: participation.challengeId,
						challengeTitle: participation.challengeTitle, // 🆕 추가
						isActive,
						certificationCount: finalCount,
					}
				} catch (error) {
					return {
						challengeId: participation.challengeId,
						challengeTitle: participation.challengeTitle,
						isActive: false,
						certificationCount: 0,
					}
				}
			}
		)

		const results = await Promise.all(promises)

		// 결과 집계
		let activeChallenges = 0
		let completedChallenges = 0
		let totalCertifications = 0

		results.forEach((result, index) => {
			if (result.isActive) {
				activeChallenges++
			} else {
				completedChallenges++
			}
			totalCertifications += result.certificationCount
		})

		// 🆕 최종 통계 로그
		const finalStats = {
			totalChallenges: approvedParticipations.length,
			activeChallenges: activeChallenges,
			completedChallenges: completedChallenges,
			totalCertifications: totalCertifications,
		}

		challengeStats.value = finalStats
	} catch (error) {
		challengeStats.value = {
			totalChallenges: 0,
			activeChallenges: 0,
			completedChallenges: 0,
			totalCertifications: 0,
		}
	} finally {
		loadingStats.value = false
	}
}

// 내 챌린지 목록 보기
async function showMyChallenges() {
	loadingChallenges.value = true
	try {
		const participations = await getMyParticipations(userInfo.value.userId)
		const approvedParticipations = participations.filter(
			(p) => p.status === 'APPROVED' || p.role === 'OWNER'
		)

		if (approvedParticipations.length === 0) {
			alert('참여 중인 챌린지가 없습니다.')
			return
		}

		const now = new Date()
		const results = []

		for (const participation of approvedParticipations) {
			try {
				const certResponse = await getMyCertificationCount(
					userInfo.value.userId,
					participation.challengeId
				)
				const certResult = certResponse.data
				// 🆕 챌린지 상태 판단 개선
				const challengeEndDate = participation.endDate
					? new Date(participation.endDate)
					: new Date('9999-12-31')

				const isActive = challengeEndDate >= now.setHours(0, 0, 0, 0)

				results.push({
					challengeId: participation.challengeId,
					challengeTitle:
						participation.challengeTitle ||
						participation.title ||
						`챌린지 ${participation.challengeId}`,
					certificationCount: certResult.certificationCount || 0,
					status: participation.status,
					role: participation.role,
					isActive: isActive, // 🆕 추가
					endDate: participation.endDate,
					startDate: participation.startDate,
				})
			} catch (error) {
				// 에러 시에도 기본 정보는 표시
				results.push({
					challengeId: participation.challengeId,
					challengeTitle:
						participation.challengeTitle ||
						`챌린지 ${participation.challengeId}`,
					certificationCount: 0,
					status: participation.status,
					role: participation.role,
					isActive: false,
					endDate: participation.endDate,
				})
			}
		}

		//  활성 챌린지를 앞으로 정렬
		results.sort((a, b) => {
			if (a.isActive && !b.isActive) return -1
			if (!a.isActive && b.isActive) return 1
			return b.certificationCount - a.certificationCount
		})

		myChallenges.value = results

		// 다이얼로그 열기
		challengesDialog.value = true
	} catch (error) {
		alert('챌린지 목록을 불러오는데 실패했습니다.')
	} finally {
		loadingChallenges.value = false
	}
}

// 🆕 수동 새로고침 함수
async function refreshStats() {
	if (loadingStats.value) {
		return
	}

	await loadChallengeStats()
	alert('📊 통계가 갱신되었습니다!')
}

// 내 인증 내역 보기
async function showMyCertifications() {
	if (!userInfo.value.userId) {
		alert('사용자 정보를 불러오는 중입니다. 잠시 후 시도해주세요.')
		return
	}

	const total = challengeStats.value.totalCertifications
	if (total === 0) {
		alert(
			'아직 작성한 인증이 없습니다.\n챌린지에 참여해서 인증을 남겨보세요!'
		)
		return
	}

	const message =
		`📊 내 인증 현황\n\n` +
		`총 인증: ${total}개\n` +
		`참여 챌린지: ${challengeStats.value.totalChallenges}개\n` +
		`진행중: ${challengeStats.value.activeChallenges}개\n` +
		`완료: ${challengeStats.value.completedChallenges}개\n\n` +
		`💡 "내 참여 챌린지" 버튼을 눌러 각 챌린지별 인증을 확인할 수 있습니다.`

	alert(message)
}

onMounted(() => {
	loadMyInfo()
})
</script>

<style scoped></style>
