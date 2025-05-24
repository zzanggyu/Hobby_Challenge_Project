<template>
	<v-container class="d-flex justify-center align-center" style="height: 80vh">
		<v-card width="400" elevation="6" class="pa-6">
			<h2 class="text-h5 mb-4 text-center">비밀번호 찾기</h2>

			<v-form ref="form" v-model="valid" lazy-validation>
				<!-- Step 1: 코드 발송 -->
				<div v-if="step === 1">
					<v-text-field
						v-model="loginId"
						:rules="rules.loginId"
						label="아이디"
						prepend-inner-icon="mdi-account"
						required
					/>
					<v-text-field
						v-model="email"
						:rules="rules.email"
						label="가입 시 등록한 이메일"
						prepend-inner-icon="mdi-email"
						required
					/>
					<v-btn
						class="mt-4"
						:loading="loading"
						:disabled="!valid"
						color="primary"
						block
						@click="onSendCode"
					>
						인증 코드 발송
					</v-btn>
				</div>

				<!-- Step 2: 코드 검증 -->
				<div v-else-if="step === 2">
					<v-text-field
						v-model="code"
						:rules="[(v) => !!v || '코드를 입력하세요.']"
						label="인증 코드"
						prepend-inner-icon="mdi-shield-key"
						required
					/>
					<v-btn
						class="mt-4"
						:loading="loading"
						:disabled="!code"
						color="primary"
						block
						@click="onVerifyCode"
					>
						코드 확인
					</v-btn>
				</div>

				<!-- Step 3: 비밀번호 재설정 -->
				<div v-else>
					<v-text-field
						v-model="newPassword"
						:type="showPassword ? 'text' : 'password'"
						:append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
						@click:append-inner="showPassword = !showPassword"
						:rules="rules.password"
						label="새 비밀번호"
						placeholder="영문·숫자·특수문자 포함, 8~20자"
						required
					/>

					<v-text-field
						v-model="confirmPassword"
						:type="showPassword ? 'text' : 'password'"
						:append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
						@click:append-inner="showPassword = !showPassword"
						:rules="[
							(v) => !!v || '비밀번호 확인이 필요합니다.',
							(v) =>
								v === newPassword || '비밀번호가 일치하지 않습니다.',
						]"
						label="비밀번호 확인"
						required
					/>
					<v-btn
						class="mt-4"
						:loading="loading"
						:disabled="!newPassword || !confirmPassword"
						color="primary"
						block
						@click="onResetPassword"
					>
						비밀번호 재설정
					</v-btn>
				</div>
			</v-form>

			<v-alert v-if="message" type="success" dense class="mt-4">
				{{ message }}
			</v-alert>
			<v-alert v-if="error" type="error" dense class="mt-4">
				{{ error }}
			</v-alert>

			<div class="text-center mt-4">
				<RouterLink to="/login">로그인으로 돌아가기</RouterLink>
			</div>
		</v-card>
	</v-container>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
	sendPasswordResetCode,
	verifyPasswordCode,
	resetPassword,
} from '@/services/authService'

const router = useRouter()
const form = ref()
const valid = ref(false)
const step = ref(1)

const loginId = ref('')
const email = ref('')
const code = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const loading = ref(false)
const error = ref('')
const message = ref('')
const showPassword = ref(false)

const rules = {
	loginId: [
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
	],
	password: [
		(v) => !!v || '비밀번호를 입력하세요.',
		(v) =>
			/^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,20}$/.test(v) ||
			'영문·숫자·특수문자 포함 8~20자여야 합니다.',
	],
}

// 1. 이메일로 코드 발송
async function onSendCode() {
	if (!(await form.value.validate())) return
	loading.value = true
	error.value = ''
	try {
		await sendPasswordResetCode(loginId.value, email.value)
		message.value = '인증 코드가 이메일로 전송되었습니다.'
		step.value = 2
	} catch (e) {
		error.value = e.response?.data?.message || '코드 발송에 실패했습니다.'
	} finally {
		loading.value = false
	}
}

// 2. 이메일로 전송된 코드 검증
async function onVerifyCode() {
	loading.value = true
	error.value = ''
	try {
		await verifyPasswordCode(email.value, code.value)
		message.value = '코드가 확인되었습니다. 새 비밀번호를 입력해 주세요.'
		step.value = 3
	} catch (e) {
		error.value = e.response?.data?.message || '코드 검증에 실패했습니다.'
	} finally {
		loading.value = false
	}
}

// 3. 비밀번호 재설정
async function onResetPassword() {
	// 폼 전체 검증 (password 룰+confirm 룰 모두 체크)
	if (!(await form.value.validate())) return
	loading.value = true
	error.value = ''
	try {
		await resetPassword(loginId.value, newPassword.value)
		message.value =
			'비밀번호가 성공적으로 변경되었습니다. 로그인 페이지로 이동합니다.'
		setTimeout(() => router.push('/login'), 1500)
	} catch (e) {
		error.value = e.response?.data?.message || '재설정에 실패했습니다.'
	} finally {
		loading.value = false
	}
}

// 비밀번호 바뀔 때 폼 검증 리셋
watch(newPassword, () => {
	form.value.resetValidation('confirmPassword')
})
</script>

<style scoped></style>
