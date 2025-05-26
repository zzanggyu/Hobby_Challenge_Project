<template>
	<v-card class="mx-auto" max-width="600">
		<v-card-title>{{ challenge.title }}</v-card-title>
		<v-card-text>
			<div><strong>설명:</strong> {{ challenge.description }}</div>
			<div>
				<strong>기간:</strong>
				{{ formatDate(challenge.startDate) }} ~
				{{ formatDate(challenge.endDate) }}
			</div>
		</v-card-text>
		<v-card-actions>
			<v-btn color="primary" @click="onEdit">수정</v-btn>
			<v-btn color="error" @click="$emit('delete')">삭제</v-btn>
		</v-card-actions>
	</v-card>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
	challenge: {
		type: Object,
		required: true,
	},
})
const emit = defineEmits(['delete'])
const router = useRouter()

function formatDate(d) {
	return d ? new Date(d).toLocaleDateString() : '-'
}

function onEdit() {
	router.push({
		name: 'challenge-edit',
		params: { id: props.challenge.challengeId },
	})
}
</script>
