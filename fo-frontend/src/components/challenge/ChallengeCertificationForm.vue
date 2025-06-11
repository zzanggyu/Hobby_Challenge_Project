<template>
	<v-card outlined max-width="650" class="mx-auto">
		<v-card-title>오늘의 인증 등록</v-card-title>
		<!-- 권한 없는 사용자에게 안내 메시지 -->
		<v-alert
			v-if="!hasWritePermission"
			type="info"
			variant="tonal"
			class="ma-4"
		>
			<div>
				<strong>인증 등록 안내</strong><br />
				챌린지에 참여하고 승인을 받은 후 인증을 등록할 수 있습니다.
				<br />하루 하나의 사진과 코멘트로 인증을 등록해 나의 활동을
				공유하세요
			</div>
		</v-alert>
		<v-card-text>
			<!-- 텍스트 영역 개선 -->
			<v-textarea
				v-model="comment"
				label="한 줄 메시지 (선택)"
				:rules="[rules.maxLength]"
				:counter="50"
				rows="4"
				auto-grow
				clearable
				class="mb-4"
				placeholder="오늘의 운동 한마디를 남겨보세요! (선택사항, 50자 이내)"
			/>

			<!-- 압축 정보 표시 추가 -->
			<!-- <v-alert
				v-if="compressionInfo"
				type="success"
				variant="tonal"
				class="mb-4"
			>
				이미지 최적화: {{ compressionInfo.original }} →
				{{ compressionInfo.compressed }} ({{ compressionInfo.ratio }}% 절약)
			</v-alert> -->

			<!-- 처리 중 표시 -->
			<!-- <v-alert
				v-if="imageProcessing"
				type="info"
				variant="tonal"
				class="mb-4"
			>
				<div class="d-flex align-center">
					<v-progress-circular
						indeterminate
						size="20"
						width="2"
						class="mr-2"
					/>
					<span>이미지를 최적화하는 중...</span>
				</div>
			</v-alert> -->

			<!-- 이미지 업로드  -->
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
import { useAuthStore } from '@/stores/auth'
import { resizeImage, formatFileSize } from '@/utils/imageUtils'

const route = useRoute()
const challengeId = Number(route.params.id)

const comment = ref('')
const file = ref(null)
const processedFile = ref(null)
const busy = ref(false)
const imagePreview = ref(null)
const auth = useAuthStore()

const compressionInfo = ref(null)
const imageProcessing = ref(false)

const props = defineProps({
	canWrite: {
		type: Boolean,
		default: false,
	},
})

const hasWritePermission = computed(() => {
	// props로 받은 canWrite가 있으면 사용, 없으면 로그인 여부만 체크
	return props.canWrite ?? auth.isAuthenticated
})

// 유효성 검사 규칙
const rules = {
	maxLength: (v) => !v || v.length <= 50 || '50자 이내로 입력하세요.',
	fileSize: (v) =>
		!v || v.size <= 10 * 1024 * 1024 || '파일 크기는 10MB 이하여야 합니다.',
}

// 폼 유효성 체크
const valid = computed(() => {
	return (
		(!comment.value || comment.value.length <= 50) && // 빈값이거나 50자 이하
		processedFile.value &&
		(!processedFile.value || processedFile.value.size <= 5 * 1024 * 1024)
	)
})

// 파일 선택 시 미리보기
// 파일 선택 시 리사이징 적용
watch(file, async (newFile) => {
	// 파일이 제거되었을 때
	if (!newFile) {
		processedFile.value = null
		imagePreview.value = null
		compressionInfo.value = null
		return
	}

	try {
		imageProcessing.value = true
		console.log(' 리사이징 시작:', newFile.name)

		// 리사이징 수행
		const resizedFile = await resizeImage(newFile, {
			maxWidth: 800,
			maxHeight: 600,
			quality: 0.85,
			format: 'jpeg',
		})

		// 압축 정보 저장
		const compressionRatio = (
			((newFile.size - resizedFile.size) / newFile.size) *
			100
		).toFixed(1)
		compressionInfo.value = {
			original: formatFileSize(newFile.size),
			compressed: formatFileSize(resizedFile.size),
			ratio: compressionRatio,
		}

		//  리사이징된 파일을 별도 변수에 저장 (무한루프 방지)
		processedFile.value = resizedFile

		// 미리보기 생성
		const reader = new FileReader()
		reader.onload = (e) => {
			imagePreview.value = e.target.result
		}
		reader.readAsDataURL(resizedFile)

		console.log(' 리사이징 완료')
	} catch (error) {
		console.error(' 이미지 처리 실패:', error)
		alert(`이미지 처리 중 오류가 발생했습니다: ${error.message}`)
		processedFile.value = null
		imagePreview.value = null
		compressionInfo.value = null
	} finally {
		imageProcessing.value = false
	}
})

// 폼 초기화
function reset() {
	comment.value = ''
	file.value = null
	processedFile.value = null
	imagePreview.value = null
	compressionInfo.value = null
}

// 완료 이벤트
const emit = defineEmits(['submitted'])

async function save() {
	const auth = useAuthStore()
	if (!auth.isAuthenticated) {
		alert('로그인 후 인증을 등록할 수 있습니다.')
		return
	}

	if (!valid.value) return

	//  리사이징이 완료되지 않았다면 대기
	if (imageProcessing.value) {
		alert('이미지 처리가 완료될 때까지 잠시 기다려주세요.')
		return
	}

	//  리사이징된 파일이 없다면 에러
	if (!processedFile.value) {
		alert('이미지 처리에 실패했습니다. 다시 선택해주세요.')
		return
	}

	busy.value = true
	try {
		// FormData 생성
		const formData = new FormData()
		formData.append('comment', comment.value)
		formData.append('image', processedFile.value) // 리사이징된 파일 사용

		console.log(' 업로드 시작:', {
			comment: comment.value,
			imageSize: formatFileSize(processedFile.value.size),
			imageType: processedFile.value.type,
		})

		// 서버로 전송
		await submitCertification(challengeId, formData)

		emit('submitted')
		reset() // 성공 시 폼 초기화
		alert('인증이 등록되었습니다!')
	} catch (err) {
		const res = err.response?.data

		if (res?.errorCode === '400041') {
			alert(res.message || '오늘은 이미 인증을 등록했습니다.')
		} else if (res?.errorCode === '400049') {
			alert('파일 크기는 최대 5MB까지 가능합니다.')
		} else if (res?.errorCode === '400040') {
			alert('JPG, PNG, GIF 형식만 지원합니다.')
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
