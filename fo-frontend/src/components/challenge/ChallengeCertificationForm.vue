<template>
	<v-card outlined max-width="600" class="mx-auto">
		<v-card-title>오늘의 인증 등록</v-card-title>
		<v-card-text>
			<!-- 텍스트 영역 개선 -->
			<v-textarea
				v-model="comment"
				label="한 줄 메시지"
				:rules="[rules.required, rules.maxLength]"
				:counter="100"
				rows="4"
				auto-grow
				clearable
				class="mb-4"
			/>

			<!-- 이미지 업로드 개선 -->
			<v-file-input
				v-model="file"
				label="사진 업로드"
				accept="image/*"
				prepend-icon="mdi-camera"
				show-size
				:rules="[rules.fileSize]"
				class="mb-2"
			>
				<template v-slot:selection="{ text }">
					<v-chip small label color="primary">
						{{ text }}
					</v-chip>
				</template>
			</v-file-input>

			<!-- 이미지 미리보기 추가 -->
			<v-img
				v-if="imagePreview"
				:src="imagePreview"
				max-height="200"
				class="mb-4 rounded"
			/>
		</v-card-text>

		<v-card-actions>
			<v-spacer />
			<v-btn text @click="reset">초기화</v-btn>
			<v-btn
				color="primary"
				:loading="busy"
				:disabled="!valid || busy"
				@click="save"
			>
				등록
			</v-btn>
		</v-card-actions>
	</v-card>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { submitCertification } from '@/services/certService'

const route = useRoute()
const challengeId = Number(route.params.id)

const comment = ref('')
const file = ref(null)
const busy = ref(false)
const imagePreview = ref(null)

// 유효성 검사 규칙
const rules = {
	required: (v) => !!v || '메시지를 입력하세요.',
	maxLength: (v) => (v && v.length <= 100) || '100자 이내로 입력하세요.',
	fileSize: (v) =>
		!v || v.size <= 10 * 1024 * 1024 || '파일 크기는 10MB 이하여야 합니다.',
}

// 폼 유효성 체크
const valid = computed(() => {
	return (
		comment.value &&
		comment.value.length <= 100 &&
		(!file.value || file.value.size <= 10 * 1024 * 1024)
	)
})

// 파일 선택 시 미리보기
watch(file, (newFile) => {
	if (newFile) {
		const reader = new FileReader()
		reader.onload = (e) => {
			imagePreview.value = e.target.result
		}
		reader.readAsDataURL(newFile)
	} else {
		imagePreview.value = null
	}
})

// 폼 초기화
function reset() {
	comment.value = ''
	file.value = null
	imagePreview.value = null
}

// 완료 이벤트
const emit = defineEmits(['submitted'])

async function save() {
	if (!valid.value) return

	busy.value = true
	try {
		const fd = new FormData()
		fd.append('comment', comment.value)
		if (file.value) {
			fd.append('file', file.value)
		}

		await submitCertification(challengeId, fd)
		emit('submitted')
		reset() // 성공 시 폼 초기화
	} catch (err) {
		const res = err.response?.data

		if (res?.errorCode === '400041') {
			alert(res.message || '오늘은 이미 인증을 등록했습니다.')
		} else {
			console.error(err)
			alert(res?.message || '인증 등록 중 오류가 발생했습니다.')
		}
	} finally {
		busy.value = false
	}
}
</script>

<style scoped>
/* 텍스트 영역 스타일 개선 */
.v-textarea {
	font-size: 16px;
}
</style>
