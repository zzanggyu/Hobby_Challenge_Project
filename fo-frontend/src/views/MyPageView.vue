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
									<v-chip small color="primary" class="mr-2">
										Lv.{{ userInfo.level }}
									</v-chip>
									<v-chip small color="secondary">
										{{ userInfo.points }} EXP
									</v-chip>
								</v-list-item-subtitle>
							</v-list-item>
						</v-list>
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
								:rules="[(v) => !!v || '현재 비밀번호를 입력하세요.']"
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
							<v-icon class="mr-2">mdi-database-remove</v-icon>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
	getMyInfo,
	updateNickname,
	changePassword as apiChangePassword,
	deleteAccount as apiDeleteAccount,
} from '@/services/userService'

const router = useRouter()
const auth = useAuthStore()

// 상태 관리
const userInfo = ref({})
const editingNickname = ref(false)
const newNickname = ref('')
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

// 폼 참조
const passwordForm = ref()

// 비밀번호 데이터
const passwordData = ref({
	currentPassword: '',
	newPassword: '',
	confirmPassword: '',
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

const passwordRules = [
	(v) => !!v || '새 비밀번호를 입력하세요.',
	(v) =>
		/^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,20}$/.test(v) ||
		'영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.',
]

const confirmPasswordRules = [
	(v) => !!v || '비밀번호 확인을 입력하세요.',
	(v) =>
		v === passwordData.value.newPassword || '비밀번호가 일치하지 않습니다.',
]

// 내 정보 조회
async function loadMyInfo() {
	try {
		userInfo.value = await getMyInfo()
	} catch (error) {
		console.error('사용자 정보 조회 실패:', error)
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
	// 유효성 검사
	const valid = nicknameRules.every((rule) => rule(newNickname.value) === true)
	if (!valid) return

	savingNickname.value = true
	try {
		const updated = await updateNickname(newNickname.value)
		userInfo.value = updated
		editingNickname.value = false
		// 스토어 업데이트
		auth.user.nickname = updated.nickname
		alert('닉네임이 성공적으로 변경되었습니다!')
	} catch (error) {
		console.error('닉네임 변경 실패:', error)
		alert(error.response?.data?.message || '닉네임 변경에 실패했습니다.')
	} finally {
		savingNickname.value = false
	}
}

// 🆕 비밀번호 폼 초기화 함수 추가
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
		console.error('비밀번호 변경 실패:', error)
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
		await apiDeleteAccount(withdrawPassword.value)
		alert('회원 탈퇴가 완료되었습니다.')

		// 로그아웃 처리
		await auth.logout()
		router.push('/')
	} catch (error) {
		console.error('회원 탈퇴 실패:', error)
		alert(error.response?.data?.message || '회원 탈퇴에 실패했습니다.')
	} finally {
		withdrawing.value = false
		withdrawDialog.value = false
		// 탈퇴 실패 시 비밀번호 필드 초기화
		withdrawPassword.value = ''
		showWithdrawPassword.value = false
	}
}

onMounted(() => {
	loadMyInfo()
})
</script>

<style scoped></style>
