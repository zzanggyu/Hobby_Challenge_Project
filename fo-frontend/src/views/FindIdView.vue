<!-- src/views/FindIdView.vue -->
<template>
	<v-container class="d-flex justify-center align-center" style="height: 80vh">
		<v-card width="400" elevation="6" class="pa-6">
			<h2 class="text-h5 mb-4 text-center">아이디 찾기</h2>

			<v-form ref="form" v-model="valid" lazy-validation>
				<v-text-field
					v-model="email"
					:rules="rules.email"
					label="가입 시 등록한 이메일"
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
					이메일로 아이디 전송
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

const form = ref()
const valid = ref(false)
const email = ref('')
const loading = ref(false)
const error = ref('')
const message = ref('')

const router = useRouter()

const rules = {
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
		// TODO: 실제 API 호출
		// await fetch('/api/auth/find-id', { method:'POST', body:JSON.stringify({ email: email.value }) })
		message.value = '가입된 이메일로 아이디를 전송했습니다.'
	} catch (e) {
		error.value = e.message || '아이디 찾기 중 오류가 발생했습니다.'
	} finally {
		loading.value = false
	}
}
</script>

<style scoped></style>
