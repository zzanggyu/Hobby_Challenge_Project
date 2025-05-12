<template>
	<v-menu v-model="open" location="bottom end" offset-y>
		<template #activator="{ props }">
			<v-btn icon v-bind="props">
				<v-badge :content="unread" color="red" v-if="unread">
					<v-icon>mdi-bell-outline</v-icon>
				</v-badge>
				<v-icon v-else>mdi-bell-outline</v-icon>
			</v-btn>
		</template>

		<v-list style="min-width: 260px; max-height: 300px" class="py-0">
			<v-list-item
				v-for="n in notices"
				:key="n.id"
				@click="read(n.id)"
				:title="n.title"
				:subtitle="n.date"
				:class="{ 'font-weight-bold': !n.read }"
				density="compact"
			/>
			<v-list-item v-if="!notices.length" title="알림이 없습니다" />
		</v-list>
	</v-menu>
</template>

<script setup lang="js">
import { ref, computed } from 'vue'
import { useNotificationStore } from '@/stores/notification'

const store   = useNotificationStore()
const open    = ref(false)
const notices = computed(() => store.notifications)
const unread  = computed(() => store.unreadCount)

function read (id) {
  store.markAsRead(id)
}
</script>

<style scoped>
/* 필요하다면 스타일 추가 */
</style>
