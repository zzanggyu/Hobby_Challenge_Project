<!-- src/views/SignupView.vue -->
<template>
	<v-container fluid fill-height align="center">
		<v-card width="600" elevation="6" class="pa-8">
			<h2 class="text-h5 mb-4 text-center">회원가입</h2>

			<v-form ref="form" v-model="valid" lazy-validation>
				<!-- 아이디 -->
				<v-text-field
					v-model="credentials.loginid"
					:rules="rules.loginid"
					label="아이디"
					placeholder="영문·숫자 8~20자"
					prepend-inner-icon="mdi-account"
					required
				/>

				<!-- 비밀번호 -->
				<v-text-field
					v-model="credentials.password"
					:type="showPassword ? 'text' : 'password'"
					:append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
					@click:append-inner="showPassword = !showPassword"
					:rules="rules.password"
					label="비밀번호"
					placeholder="영문·숫자·특수문자 포함, 8~20자"
					required
				/>

				<!-- 비밀번호 확인 -->
				<v-text-field
					v-model="credentials.confirm"
					:type="showConfirm ? 'text' : 'password'"
					:append-inner-icon="showConfirm ? 'mdi-eye-off' : 'mdi-eye'"
					@click:append-inner="showConfirm = !showConfirm"
					:rules="rules.confirm"
					label="비밀번호 확인"
					placeholder="비밀번호를 다시 입력하세요"
					required
				/>

				<!-- 이메일 -->
				<!-- <v-text-field
					v-model="credentials.email"
					:rules="rules.email"
					label="이메일"
					placeholder="example@naver.com"
					prepend-inner-icon="mdi-email"
					required
				/> -->
				<!-- 이메일 + 인증 요청 -->
				<div>
					<div v-if="!emailVerified">
						<v-row class="align-center mb-4" dense>
							<v-col cols="8">
								<v-text-field
									v-model="credentials.email"
									:rules="rules.email"
									label="이메일"
									placeholder="example@naver.com"
									prepend-inner-icon="mdi-email"
									:disabled="emailSent"
									required
								/>
							</v-col>
							<v-col cols="4">
								<v-btn
									:disabled="
										!rules.email.every(
											(fn) => fn(credentials.email) === true
										) || emailSent
									"
									color="primary"
									block
									@click="sendEmailCode"
								>
									{{ emailSent ? timerDisplay : '인증 요청' }}
								</v-btn>
							</v-col>
						</v-row>

						<!-- 인증 코드 입력란 -->
						<v-row v-if="emailSent" class="align-center mb-4" dense>
							<v-col cols="8">
								<v-text-field
									v-model="emailCode"
									:rules="[(v) => !!v || '코드를 입력하세요.']"
									label="인증 코드"
									placeholder="메일로 받은 6자리"
									required
								/>
							</v-col>
							<v-col cols="4">
								<v-btn
									:disabled="emailVerified || emailCode.length !== 6"
									color="success"
									block
									@click="verifyEmailCode"
								>
									{{ emailVerified ? '인증 완료' : '인증 확인' }}
								</v-btn>
							</v-col>
						</v-row>
					</div>

					<!-- 인증 완료 -->
					<div v-else>
						<v-alert type="success" dense class="mb-4">
							이메일이 인증되었습니다!
						</v-alert>
					</div>
				</div>

				<!-- 이름(본명) -->
				<v-text-field
					v-model="credentials.username"
					:rules="rules.username"
					label="이름"
					placeholder="예) 홍길동"
					prepend-inner-icon="mdi-account-circle"
					required
				/>

				<!-- 닉네임 -->
				<v-text-field
					v-model="credentials.nickname"
					:rules="rules.nickname"
					label="닉네임(선택)"
					placeholder="예) 감자돌이"
					prepend-inner-icon="mdi-pencil"
				/>

				<!-- 생년월일 -->
				<v-date-input
					v-model="credentials.birthdate"
					:rules="rules.birthdate"
					format="yyyy.MM.dd"
					value-format="yyyy.MM.dd"
					:mask="['####.##.##']"
					:max="maxDate"
					locale="ko"
					label="생년월일"
					placeholder="예) 2000.01.01"
					editable
					open-on-focus
					required
					clearable
				/>

				<!-- 취미 (복수 선택) -->
				<!-- <v-select
							v-model="credentials.hobbies"
							:items="hobbyOptions"
							label="취미"
							multiple
							chips
							placeholder="여러 개 선택 가능"
						/> -->

				<!-- 프로필 이미지 -->
				<!-- 에러 / 성공 메시지 -->
				<v-alert v-if="error" type="error" dense class="mb-4">
					{{ error }}
				</v-alert>
				<v-alert v-if="message" type="success" dense class="mb-4">
					{{ message }}
				</v-alert>

				<!-- 회원가입 버튼 -->
				<v-btn
					:loading="loading"
					:disabled="!valid || !emailVerified"
					color="primary"
					block
					class="mt-2"
					@click="onSubmit"
				>
					회원가입
				</v-btn>
			</v-form>

			<div class="text-center mt-4">
				이미 계정이 있으신가요?
				<RouterLink to="/login">로그인으로 이동</RouterLink>
			</div>
		</v-card>
	</v-container>
</template>

<script setup>
import { ref, reactive, watch, computed, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import {
	signup,
	sendSignupCode,
	verifySignupCode,
} from '@/services/authService'
import { format } from 'date-fns'

const form = ref()
const valid = ref(false)
const loading = ref(false)
const error = ref('')
const message = ref('')
const showPassword = ref(false)
const showConfirm = ref(false)

const router = useRouter()

const emailSent = ref(false) // 이메일 인증 상태
const timer = ref(0) // 남은 재요청 대기 시간(초)
let timerId = null // setInterval ID
const emailCode = ref('') // 사용자가 입력한 코드
const emailVerified = ref(false) // 인증 성공 여부

const pad = (n) => n.toString().padStart(2, '0')
const today = new Date()
const maxDate = `${today.getFullYear()}.${pad(today.getMonth() + 1)}.${pad(
	today.getDate()
)}`

const credentials = reactive({
	loginid: '',
	password: '',
	confirm: '',
	email: '',
	username: '',
	nickname: '',
	birthdate: '',
})

// 유효성 규칙
const rules = {
	loginid: [
		(v) => !!v || '아이디를 입력하세요.',
		(v) =>
			/^[A-Za-z0-9]{8,20}$/.test(v) ||
			'아이디는 영문·숫자 8~20자여야 합니다.',
	],
	email: [
		(v) => !!v || '이메일을 입력하세요.',
		(v) =>
			/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ||
			'유효한 이메일 형식이 아닙니다.',
		(v) =>
			!emailSent.value ||
			emailVerified.value ||
			'이메일 인증을 먼저 해주세요.',
	],
	password: [
		(v) => !!v || '비밀번호를 입력하세요.',
		(v) =>
			/^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,20}$/.test(v) ||
			'비밀번호는 8~20자, 영문·숫자·특수문자 각각 1자 이상 포함해야 합니다.',
	],
	confirm: [
		(v) => !!v || '비밀번호 확인이 필요합니다.',
		(v) => v === credentials.password || '비밀번호가 일치하지 않습니다.',
	],
	username: [
		(v) => !!v || '이름을 입력하세요.',
		(v) => v.length <= 5 || '이름은 5자 이내여야 합니다.',
	],
	// 닉네임 (선택)
	nickname: [
		(v) =>
			v === '' ||
			(v.length >= 2 && v.length <= 10) ||
			'별명은 2~10자여야 합니다.',
	],
	// 3) 생년월일 – 필수, YYYY-MM-DD 형식
	birthdate: [
		(v) => !!v || '생년월일을 입력하세요.',
		(v) => /^\d{4}\.\d{2}\.\d{2}$/.test(v) || 'YYYY.MM.DD 형식이어야 합니다.',
		(v) => {
			const d = new Date(v)
			return !isNaN(d.getTime()) || '유효한 날짜가 아닙니다.'
		},
		// 오늘 이전 날짜만 허용
		(v) => {
			const d = new Date(v)
			const now = new Date()
			// 00시 00분 기준으로만 비교하면, 오늘은 입력 불가!
			d.setHours(0, 0, 0, 0)
			now.setHours(0, 0, 0, 0)
			return d < now || '생년월일은 오늘 이전 날짜만 가능합니다.'
		},
	],
}

// 1) 인증 요청
async function sendEmailCode() {
	if (emailSent.value) return
	// TODO: 실제 API 호출
	try {
		await sendSignupCode(credentials.email)
		emailSent.value = true
		timer.value = 300 // 5분 안에 인증증
		timerId = window.setInterval(() => {
			if (timer.value > 0) {
				timer.value--
			} else {
				clearInterval(timerId)
				emailSent.value = false
			}
		}, 1000)
		error.value = ''
	} catch (e) {
		error.value = e.response?.data?.message || '인증 요청에 실패했습니다.'
		message.value = ''
	}
}

// 남은 시간 → "m분 ss초" 포맷으로 변환
const timerDisplay = computed(() => {
	const m = Math.floor(timer.value / 60)
	const s = timer.value % 60
	// 초 앞에 1자리일 땐 0 붙이기 (선택)
	const ss = String(s).padStart(2, '0')
	return `${m}분 ${ss}초 후 만료`
})

// 2) 인증 코드 확인
async function verifyEmailCode() {
	// TODO: 실제 API 호출
	try {
		await verifySignupCode(credentials.email, emailCode.value)
		emailVerified.value = true
		clearInterval(timerId)
		error.value = ''
	} catch (e) {
		error.value =
			e.response?.data?.message || '인증 코드가 올바르지 않습니다.'
		message.value = ''
	}
}

// 비밀번호 일치시 confirm 필드 다시 검증
watch(
	() => credentials.password,
	() => {
		form.value.resetValidation('confirm')
	}
)

// 타이머 클린업
onBeforeUnmount(() => clearInterval(timerId))

// 3) 최종 제출
async function onSubmit() {
	if (!(await form.value.validate())) return
	if (!emailVerified.value) {
		alert('이메일 인증을 완료해주세요.')
		return
	}
	loading.value = true
	error.value = ''
	message.value = ''
	try {
		// TODO: 회원가입 API 호출
		await signup({
			loginId: credentials.loginid,
			password: credentials.password,
			email: credentials.email,
			username: credentials.username,
			nickname: credentials.nickname,
			birthDate: format(new Date(credentials.birthdate), 'yyyy.MM.dd'), // v-date-input 에서 자꾸 Date객체를 모델에 넣어서 보내기 직전 수동 포맷
		})
		message.value = '회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.'
		// 잠시 대기 후 이동
		setTimeout(() => router.push('/login'), 1000)
	} catch (e) {
		const msg = e.response?.data?.message || '회원가입에 실패했습니다.'
		// 이미 가입된 이메일/아이디인 경우
		if (msg.includes('아이디') || msg.includes('이메일')) {
			if (confirm(`${msg}\n로그인 페이지로 이동할까요?`)) {
				router.push('/login')
				return
			}
		}

		error.value = msg
		console.error('signup failed response:', e.response?.data)
	} finally {
		loading.value = false
	}
}
</script>

<style scoped>
/* ::v-deep 로 Vuetify 내부 마크업까지 도달하게 하기 */
/* ::v-deep .v-text-field.required .v-field__label::after {
	content: ' *';
	color: red;
} */
</style>
