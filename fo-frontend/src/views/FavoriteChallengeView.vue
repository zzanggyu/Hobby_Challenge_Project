<!-- src/views/FavoriteListView.vue -->
<template>
	<v-container class="pa-6">
		<h2 class="text-h5 mb-4">내 관심 챌린지</h2>
		<!-- 컴포넌트는 components/challenge 아래에 만드신 것을 import -->
		<FavoriteChallenge />
	</v-container>
</template>

<script setup>
import FavoriteChallenge from '@/components/challenge/FavoriteChallenge.vue'
</script>

<style scoped>
/* 페이지 단위 스타일이 필요하다면 여기에 */
</style>
