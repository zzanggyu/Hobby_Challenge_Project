<template>
	<v-footer color="grey-darken-3" height="64">
		<v-container
			class="d-flex align-center justify-space-between text-white"
			fluid
		>
			<div>&copy; 2025 Hobbyllenge</div>

			<div class="d-flex gap-4">
				<a href="mailto:help@hobbychallenge.com" class="text-white">
					help@hobbychallenge.com
				</a>
				<!-- SNS 아이콘 예시 -->
				<!-- <v-icon size="20">mdi-instagram</v-icon>
          <v-icon size="20">mdi-facebook</v-icon> -->
			</div>
		</v-container>
	</v-footer>
</template>

<script setup>
/* 특별한 로직 없음 */
</script>

<style scoped>
a:hover {
	text-decoration: underline;
}
.gap-4 {
	gap: 1rem;
}
</style>
