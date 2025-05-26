<template>
	<v-card outlined max-width="600" class="mx-auto">
		<v-card-title>오늘의 인증 등록</v-card-title>
		<v-card-text>
			<v-textarea
				v-model="form.comment"
				label="한 줄 메시지"
				rows="2"
				clearable
			/>
			<v-file-input
				v-model="form.file"
				label="사진 업로드 (선택)"
				accept="image/*"
			/>
		</v-card-text>
		<v-card-actions>
			<v-spacer />
			<v-btn
				:loading="busy"
				color="primary"
				:disabled="isSubmitting"
				@click="save"
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
const cid = Number(route.params.id)

const form = ref({ comment: '', file: null })
const busy = ref(false)

async function save() {
	if (!form.value.comment) return alert('메시지를 입력하세요.')
	busy.value = true
	try {
		const fd = new FormData()
		fd.append('comment', form.value.comment)
		if (form.value.file) fd.append('file', form.value.file)
		await submitCertification(cid, fd)
		// 저장 후엔 부모(Overview) 쪽에서 새로고침 이벤트를 받을 수도 있고
		// 그냥 페이지 reload 해버려도 OK
		location.reload()
	} finally {
		busy.value = false
	}
}
</script>
