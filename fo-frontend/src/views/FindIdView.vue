<!-- FindIdView.vue 수정 -->
<template>
	<v-container class="d-flex justify-center align-center" style="height: 80vh">
		<v-card width="400" elevation="6" class="pa-6">
			<h2 class="text-h5 mb-4 text-center">아이디 찾기</h2>

			<v-form ref="form" v-model="valid" lazy-validation>
				<!-- 이름 입력 필드 추가 -->
				<v-text-field
					v-model="username"
					:rules="rules.username"
					label="이름"
					placeholder="가입 시 등록한 이름"
					prepend-inner-icon="mdi-account"
					required
				/>

				<v-text-field
					v-model="email"
					:rules="rules.email"
					label="이메일"
					placeholder="example@domain.com"
					prepend-inner-icon="mdi-email"
					required
				/>

				<v-alert v-if="message" type="success" dense class="mb-4">
					{{ message }}
				</v-alert>
				<v-alert v-if="error" type="error" dense class="mb-4">
					{{ error }}
				</v-alert>

				<v-btn
					:loading="loading"
					:disabled="!valid"
					color="primary"
					block
					@click="onFindId"
				>
					아이디 확인하기
				</v-btn>
			</v-form>

			<div class="text-center mt-4">
				<RouterLink to="/login">로그인으로 돌아가기</RouterLink>
			</div>
		</v-card>
	</v-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { findId } from '@/services/authService'

const form = ref()
const valid = ref(false)
const username = ref('')
const email = ref('')
const loading = ref(false)
const error = ref('')
const message = ref('')

const router = useRouter()

const rules = {
	username: [
		(v) => !!v || '이름을 입력하세요.',
		(v) => v.length >= 2 || '이름은 2자 이상이어야 합니다.',
	],
	email: [
		(v) => !!v || '이메일을 입력하세요.',
		(v) =>
			/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ||
			'유효한 이메일 형식이 아닙니다.',
	],
}

async function onFindId() {
	const ok = await form.value.validate()
	if (!ok) return

	loading.value = true
	error.value = ''
	message.value = ''

	try {
		const res = await findId(username.value, email.value)
		message.value = `당신의 아이디는 "${res.data.loginId}" 입니다.`
		form.value.reset()
	} catch (e) {
		error.value =
			e.response?.data?.message || '아이디 찾기 중 오류가 발생했습니다.'
	} finally {
		loading.value = false
	}
}
</script>
