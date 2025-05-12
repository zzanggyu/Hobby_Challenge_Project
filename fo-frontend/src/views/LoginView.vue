<template>
	<v-container class="d-flex justify-center align-center" style="height: 80vh">
		<v-card width="400" elevation="6" class="pa-6">
			<h2 class="text-h5 mb-4 text-center">로그인</h2>

			<v-form ref="form" v-model="valid" lazy-validation>
				<!-- 아이디 -->
				<v-text-field
					v-model="credentials.loginid"
					:rules="rules.loginid"
					label="아이디"
					placeholder="영문·숫자 조합, 8~20자"
					prepend-inner-icon="mdi-account"
					maxlength="20"
					counter
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
					prepend-inner-icon="mdi-lock"
					type="password"
					maxlength="20"
					counter
					required
				/>

				<v-alert v-if="error" type="error" dense class="mb-4">
					{{ error }}
				</v-alert>

				<v-btn
					:disabled="!valid || loading"
					color="primary"
					class="mt-2"
					block
					@click="onSubmit"
				>
					<span v-if="!loading">로그인</span>
					<v-progress-circular
						v-else
						indeterminate
						size="20"
						width="2"
						color="white"
					/>
				</v-btn>
			</v-form>
			<!-- 회원가입,아이디 찾기, 비밀번호 찾기 이동동 -->
			<div class="text-center mt-4">
				아직 계정이 없으신가요?
				<RouterLink to="/signup">회원가입</RouterLink><br />
				<RouterLink to="/find-id">아이디 찾기</RouterLink>
				<span class="mx-2">|</span>
				<RouterLink to="/find-password">비밀번호 찾기</RouterLink>
			</div>
		</v-card>
	</v-container>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const valid = ref(false)
const form = ref()

const loading = ref(false)
const error = ref('')

const credentials = reactive({
	loginid: '',
	password: '',
})

// 비밀번호 보이기/숨기기 토글 상태
const showPassword = ref(false)

const router = useRouter()

// 유효성 규칙 정규식
const rules = {
	loginid: [
		(v) => !!v || '아이디를 입력하세요.',
		(v) =>
			/^[A-Za-z0-9]{8,20}$/.test(v) ||
			'아이디는 영문·숫자 조합, 8~20자여야 합니다.',
	],
	password: [
		(v) => !!v || '비밀번호를 입력하세요.',
		(v) =>
			/^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,20}$/.test(v) ||
			'비밀번호는 8~20자, 영문·숫자·특수문자 각각 1자 이상 포함해야 합니다.',
	],
}

async function onSubmit() {
	// 1) 폼 검증
	const ok = await form.value.validate()
	if (!ok) return

	loading.value = true
	error.value = ''

	try {
		// TODO: 실제 로그인 API 호출
		// const res = await fetch('/api/auth/login', { ... })
		// if (!res.ok) throw new Error(...)
		// const data = await res.json()
		// localStorage.setItem('auth_token', data.token)

		// 임시로 홈으로 리다이렉트
		router.push('/')
	} catch (e: any) {
		error.value = e.message || '로그인 중 오류가 발생했습니다.'
	} finally {
		loading.value = false
	}
}
</script>
