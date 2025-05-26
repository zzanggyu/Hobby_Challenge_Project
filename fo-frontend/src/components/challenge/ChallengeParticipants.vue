<template>
	<v-tabs v-model="tab" grow>
		<v-tab>요청 목록</v-tab>
		<v-tab>참여자</v-tab>

		<v-tab-item>
			<v-list two-line>
				<v-list-item v-for="r in requests" :key="r.participationId">
					<v-list-item-content>
						<v-list-item-title>{{
							r.nickname || r.userId
						}}</v-list-item-title>
						<v-list-item-subtitle>{{ r.status }}</v-list-item-subtitle>
					</v-list-item-content>
					<v-list-item-action>
						<v-btn
							small
							color="primary"
							@click="$emit('approve', r.participationId)"
							>승인</v-btn
						>
						<v-btn
							small
							color="error"
							@click="$emit('reject', r.participationId)"
							>거절</v-btn
						>
					</v-list-item-action>
				</v-list-item>
			</v-list>
		</v-tab-item>

		<v-tab-item>
			<v-list two-line>
				<v-list-item v-for="p in participants" :key="p.participationId">
					<v-list-item-content>
						<v-list-item-title>{{
							p.nickname || p.userId
						}}</v-list-item-title>
					</v-list-item-content>
					<v-list-item-action>
						<v-btn
							small
							color="error"
							@click="$emit('remove', p.participationId)"
							>제명</v-btn
						>
					</v-list-item-action>
				</v-list-item>
			</v-list>
		</v-tab-item>
	</v-tabs>
</template>

<script setup>
import { defineProps, defineEmits, ref } from 'vue'

const props = defineProps({
	requests: { type: Array, default: () => [] },
	participants: { type: Array, default: () => [] },
})
const emit = defineEmits(['approve', 'reject', 'remove'])
const tab = ref(0)
</script>
