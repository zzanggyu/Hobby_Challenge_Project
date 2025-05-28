<template>
	<v-card outlined max-width="600" class="mx-auto">
		<v-card-title>오늘의 인증 등록</v-card-title>
		<v-card-text>
			<v-textarea
				v-model="comment"
				label="한 줄 메시지"
				rows="2"
				clearable
			/>
			<v-file-input
				v-model="file"
				label="사진 업로드 (선택)"
				accept="image/*"
			/>
		</v-card-text>
		<v-card-actions>
			<v-spacer />
			<v-btn color="primary" :loading="busy" :disabled="busy" @click="save"
				>등록</v-btn
			>
		</v-card-actions>
	</v-card>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { submitCertification } from '@/services/certService'

const route = useRoute()
const challengeId = Number(route.params.id)

const comment = ref('')
const file = ref(null)
const busy = ref(false)

// 완료되면 부모에게 알려주기 위해 emits 선언
const emit = defineEmits(['submitted'])

async function save() {
	if (!comment.value) {
		return alert('메시지를 입력하세요.')
	}

	busy.value = true
	try {
		const fd = new FormData()
		fd.append('comment', comment.value)
		if (file.value) fd.append('file', file.value)

		await submitCertification(challengeId, fd)
		emit('submitted')
	} catch (err) {
		const res = err.response?.data

		// 하루 1회 초과 등록 중복 에러
		if (res?.errorCode === '400041') {
			alert(res.message || '오늘은 이미 인증을 등록했습니다.')
			return
		}

		console.error(err)
		alert(res?.message || '인증 등록 중 오류가 발생했습니다.')
	} finally {
		busy.value = false
	}
}
</script>
